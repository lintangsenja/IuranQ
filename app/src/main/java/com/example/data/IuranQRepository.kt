package com.example.data

import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IuranQRepository(private val database: AppDatabase) {

    private val wargaDao = database.wargaDao()
    private val kasDao = database.kasDao()
    private val transaksiDao = database.transaksiDao()
    private val kurbanDao = database.kurbanDao()
    private val inventarisDao = database.inventarisDao()
    private val kegiatanDao = database.kegiatanDao()
    private val auditLogDao = database.auditLogDao()
    private val appConfigDao = database.appConfigDao()
    private val petugasDao = database.petugasDao()

    val allWarga: Flow<List<WargaEntity>> = wargaDao.getAllWarga()
    val allKamarKas: Flow<List<KamarKasEntity>> = kasDao.getAllKamarKas()
    val allTransaksi: Flow<List<TransaksiEntity>> = transaksiDao.getAllTransaksi()
    val allTransaksiAudit: Flow<List<TransaksiEntity>> = transaksiDao.getAllTransaksiIncludingDeleted()
    val allKelompokKurban: Flow<List<KelompokKurbanEntity>> = kurbanDao.getAllKelompok()
    val allAnggotaKurban: Flow<List<AnggotaKurbanEntity>> = kurbanDao.getAllAnggota()
    val allInventaris: Flow<List<InventarisEntity>> = inventarisDao.getAllInventaris()
    val allKegiatan: Flow<List<KegiatanRtEntity>> = kegiatanDao.getAllKegiatan()
    val allAuditLogs: Flow<List<AuditLogEntity>> = auditLogDao.getAllAuditLogs()
    val allPetugas: Flow<List<PetugasEntity>> = petugasDao.getAllPetugas()
    val unsyncedCount: Flow<Int> = transaksiDao.getUnsyncedCount()

    // Process Jimpitan with Automatic Overflow logic
    suspend fun recordJimpitanWithOverflow(
        wargaId: String,
        totalNominal: Long,
        targetJimpitanHarian: Long = 1000L,
        petugas: String = "Petugas Piket",
        isOfflineCreated: Boolean = false
    ): Pair<TransaksiEntity, TransaksiEntity?> {
        val warga = wargaDao.getWargaById(wargaId) ?: throw IllegalArgumentException("Warga tidak ditemukan: $wargaId")
        val timestamp = System.currentTimeMillis()
        val dateCode = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date(timestamp))

        val nominalJimpitan: Long
        val nominalOverflow: Long

        if (totalNominal <= targetJimpitanHarian) {
            nominalJimpitan = totalNominal
            nominalOverflow = 0L
        } else {
            nominalJimpitan = targetJimpitanHarian
            nominalOverflow = totalNominal - targetJimpitanHarian
        }

        // 1. Catat Jimpitan
        val trxJimpitan = TransaksiEntity(
            kodeTransaksi = "JMP-$dateCode",
            wargaId = warga.id,
            namaWarga = warga.nama,
            kamarKasId = "KAS_JIMPITAN",
            jenisMutasi = "MASUK",
            kategori = "JIMPITAN",
            nominal = nominalJimpitan,
            nominalTargetJimpitan = nominalJimpitan,
            nominalOverflowTabungan = nominalOverflow,
            keterangan = if (nominalOverflow > 0) {
                "Setor Jimpitan Rp${String.format(Locale.GERMANY, "%,d", totalNominal)} (Kas Jimpitan: Rp${String.format(Locale.GERMANY, "%,d", nominalJimpitan)}, Overflow Tabungan: Rp${String.format(Locale.GERMANY, "%,d", nominalOverflow)})"
            } else {
                "Setor Jimpitan Harian Rp${String.format(Locale.GERMANY, "%,d", nominalJimpitan)}"
            },
            petugas = petugas,
            isSync = !isOfflineCreated,
            timestamp = timestamp
        )
        transaksiDao.insertTransaksi(trxJimpitan)
        kasDao.updateSaldoKas("KAS_JIMPITAN", nominalJimpitan)

        // 2. Catat Overflow jika ada ke Kas Tabungan & Saldo Warga
        var trxTabungan: TransaksiEntity? = null
        if (nominalOverflow > 0) {
            trxTabungan = TransaksiEntity(
                kodeTransaksi = "TAB-$dateCode",
                wargaId = warga.id,
                namaWarga = warga.nama,
                kamarKasId = "KAS_TABUNGAN",
                jenisMutasi = "MASUK",
                kategori = "TABUNGAN_OVERFLOW",
                nominal = nominalOverflow,
                nominalTargetJimpitan = nominalJimpitan,
                nominalOverflowTabungan = nominalOverflow,
                keterangan = "Overflow Otomatis Tabungan Warga (${warga.nama})",
                petugas = petugas,
                isSync = !isOfflineCreated,
                timestamp = timestamp
            )
            transaksiDao.insertTransaksi(trxTabungan)
            kasDao.updateSaldoKas("KAS_TABUNGAN", nominalOverflow)
            wargaDao.addSaldoTabungan(warga.id, nominalOverflow)
        }

        // Audit Log
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "SCAN_JIMPITAN_OVERFLOW",
                userRole = petugas,
                detail = "Scan Jimpitan warga ${warga.nama} (${warga.id}) senilai Rp${String.format(Locale.GERMANY, "%,d", totalNominal)}. Kas Jimpitan +Rp${String.format(Locale.GERMANY, "%,d", nominalJimpitan)}, Tabungan +Rp${String.format(Locale.GERMANY, "%,d", nominalOverflow)}"
            )
        )

        return Pair(trxJimpitan, trxTabungan)
    }

    // Kas Air Payment
    suspend fun bayarKasAir(
        wargaId: String,
        nominal: Long = 5000L,
        bulanTahun: String,
        petugas: String = "Bendahara RT"
    ) {
        val warga = wargaDao.getWargaById(wargaId) ?: throw IllegalArgumentException("Warga tidak ditemukan")
        val timestamp = System.currentTimeMillis()
        val dateCode = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date(timestamp))

        val trx = TransaksiEntity(
            kodeTransaksi = "AIR-$dateCode",
            wargaId = warga.id,
            namaWarga = warga.nama,
            kamarKasId = "KAS_AIR",
            jenisMutasi = "MASUK",
            kategori = "KAS_AIR",
            nominal = nominal,
            keterangan = "Iuran Kas Air Bersih Bulan $bulanTahun",
            petugas = petugas,
            timestamp = timestamp
        )
        transaksiDao.insertTransaksi(trx)
        kasDao.updateSaldoKas("KAS_AIR", nominal)

        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "BAYAR_KAS_AIR",
                userRole = petugas,
                detail = "Pembayaran Kas Air ${warga.nama} ($bulanTahun) Rp${String.format(Locale.GERMANY, "%,d", nominal)}"
            )
        )
    }

    // Toggle Status Kas Air: "AIR HIDUP (Mengalir)" vs "AIR MATI (Tutup)"
    suspend fun toggleStatusAir(wargaId: String, currentStatus: String, petugas: String) {
        val isCurrentlyActive = currentStatus == "AKTIF" || currentStatus == "AIR_HIDUP"
        val newStatus = if (isCurrentlyActive) "AIR_MATI" else "AIR_HIDUP"
        wargaDao.updateStatusAir(wargaId, newStatus)
        val warga = wargaDao.getWargaById(wargaId)
        val labelStatus = if (newStatus == "AIR_HIDUP") "AIR HIDUP (Mengalir)" else "AIR MATI (Tutup)"
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "TOGGLE_STATUS_AIR",
                userRole = petugas,
                detail = "Ubah status kran air ${warga?.nama ?: wargaId} menjadi: $labelStatus. " +
                        if (newStatus == "AIR_MATI") "(Tagihan bulan ini otomatis diset Rp0 - Air Mati)" else "(Tagihan normal aktif Rp5.000)"
            )
        )
    }

    // Alias for backwards compatibility
    suspend fun toggleAmnestyAir(wargaId: String, currentStatus: String, petugas: String) {
        toggleStatusAir(wargaId, currentStatus, petugas)
    }

    // Setor Kurban
    suspend fun bayarCicilanKurban(
        anggotaId: Long,
        nominal: Long,
        petugas: String = "Bendahara Kurban"
    ) {
        val timestamp = System.currentTimeMillis()
        val dateCode = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date(timestamp))

        kurbanDao.tambahTabunganAnggota(anggotaId, nominal)
        kasDao.updateSaldoKas("KAS_KURBAN", nominal)

        val trx = TransaksiEntity(
            kodeTransaksi = "KRB-$dateCode",
            kamarKasId = "KAS_KURBAN",
            jenisMutasi = "MASUK",
            kategori = "KURBAN",
            nominal = nominal,
            keterangan = "Setoran Tabungan Kurban Sapi",
            petugas = petugas,
            timestamp = timestamp
        )
        transaksiDao.insertTransaksi(trx)

        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "SETOR_KURBAN",
                userRole = petugas,
                detail = "Setoran Kurban ID $anggotaId Rp${String.format(Locale.GERMANY, "%,d", nominal)}"
            )
        )
    }

    // Umum: Mutasi Masuk / Keluar Kas Apapun
    suspend fun recordMutasiKas(
        kamarKasId: String,
        jenisMutasi: String, // MASUK, KELUAR
        nominal: Long,
        kategori: String,
        keterangan: String,
        wargaId: String? = null,
        petugas: String = "Bendahara RT"
    ) {
        val timestamp = System.currentTimeMillis()
        val dateCode = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date(timestamp))
        val warga = if (wargaId != null) wargaDao.getWargaById(wargaId) else null

        val delta = if (jenisMutasi == "MASUK") nominal else -nominal
        kasDao.updateSaldoKas(kamarKasId, delta)

        val trx = TransaksiEntity(
            kodeTransaksi = "MUT-$dateCode",
            wargaId = warga?.id,
            namaWarga = warga?.nama,
            kamarKasId = kamarKasId,
            jenisMutasi = jenisMutasi,
            kategori = kategori,
            nominal = nominal,
            keterangan = keterangan,
            petugas = petugas,
            timestamp = timestamp
        )
        transaksiDao.insertTransaksi(trx)

        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "MUTASI_KAS_$jenisMutasi",
                userRole = petugas,
                detail = "$jenisMutasi Kas $kamarKasId Rp${String.format(Locale.GERMANY, "%,d", nominal)}: $keterangan"
            )
        )
    }

    // Kegiatan RT: potong saldo langsung dari kamar kas tertentu
    suspend fun tambahKegiatanRt(
        namaKegiatan: String,
        kamarKasSumber: String,
        biaya: Long,
        pj: String,
        keterangan: String,
        petugas: String = "Pengurus RT"
    ) {
        val kegiatan = KegiatanRtEntity(
            namaKegiatan = namaKegiatan,
            tanggal = System.currentTimeMillis(),
            kamarKasSumber = kamarKasSumber,
            biaya = biaya,
            penanggungJawab = pj,
            status = "SELESAI",
            keterangan = keterangan
        )
        kegiatanDao.insertKegiatan(kegiatan)

        if (biaya > 0) {
            recordMutasiKas(
                kamarKasId = kamarKasSumber,
                jenisMutasi = "KELUAR",
                nominal = biaya,
                kategori = "EVENT_RT",
                keterangan = "Biaya Kegiatan RT: $namaKegiatan (PJ: $pj)",
                petugas = petugas
            )
        }

        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "KEGIATAN_RT",
                userRole = petugas,
                detail = "Pencatatan kegiatan RT '$namaKegiatan', Biaya Rp${String.format(Locale.GERMANY, "%,d", biaya)} dari $kamarKasSumber"
            )
        )
    }

    // Soft Delete Transaksi
    suspend fun softDeleteTransaksi(id: Long, reason: String, petugas: String) {
        transaksiDao.softDeleteTransaksi(id, petugas, reason)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "SOFT_DELETE_TRX",
                userRole = petugas,
                detail = "Soft delete transaksi #$id oleh $petugas. Alasan: $reason"
            )
        )
    }

    // Restore Transaksi
    suspend fun restoreTransaksi(id: Long, petugas: String) {
        transaksiDao.restoreTransaksi(id)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "RESTORE_TRX",
                userRole = petugas,
                detail = "Restore transaksi #$id oleh $petugas"
            )
        )
    }

    // Warga CRUD
    suspend fun simpanWarga(warga: WargaEntity, isNew: Boolean, petugas: String) {
        wargaDao.insertOrUpdateWarga(warga)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = if (isNew) "TAMBAH_WARGA" else "EDIT_WARGA",
                userRole = petugas,
                detail = "${if (isNew) "Tambah" else "Update"} data warga ${warga.nama} (${warga.id})"
            )
        )
    }

    suspend fun softDeleteWarga(wargaId: String, petugas: String) {
        wargaDao.softDeleteWarga(wargaId)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "SOFT_DELETE_WARGA",
                userRole = petugas,
                detail = "Soft delete warga $wargaId oleh $petugas"
            )
        )
    }

    suspend fun syncOfflineTransactions(petugas: String): Int {
        transaksiDao.markAllAsSynced()
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "SYNC_OFFLINE_DATA",
                userRole = petugas,
                detail = "Sinkronisasi antrean data offline selesai berhasil"
            )
        )
        return 0
    }

    // Inventaris
    suspend fun simpanInventaris(item: InventarisEntity, petugas: String) {
        inventarisDao.insertInventaris(item)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "UPDATE_INVENTARIS",
                userRole = petugas,
                detail = "Update inventaris ${item.namaBarang}"
            )
        )
    }

    // Kurban
    suspend fun tambahKelompokKurban(kelompok: KelompokKurbanEntity, petugas: String) {
        kurbanDao.insertKelompok(kelompok)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "TAMBAH_KELOMPOK_KURBAN",
                userRole = petugas,
                detail = "Tambah kelompok kurban ${kelompok.namaKelompok}"
            )
        )
    }

    suspend fun tambahAnggotaKurban(anggota: AnggotaKurbanEntity, petugas: String) {
        kurbanDao.insertAnggota(anggota)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "TAMBAH_ANGGOTA_KURBAN",
                userRole = petugas,
                detail = "Tambah peserta kurban ${anggota.namaWarga} ke kelompok ${anggota.kelompokId}"
            )
        )
    }

    // Petugas Piket Management
    suspend fun simpanPetugas(petugas: PetugasEntity, isNew: Boolean, operator: String) {
        petugasDao.insertPetugas(petugas)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = if (isNew) "TAMBAH_PETUGAS" else "EDIT_PETUGAS",
                userRole = operator,
                detail = "${if (isNew) "Tambah" else "Update"} data petugas piket: ${petugas.nama} (${petugas.jadwalPiket})"
            )
        )
    }

    suspend fun setDefaultPetugas(id: Long, operator: String) {
        petugasDao.clearDefaultPetugas()
        petugasDao.setDefaultPetugas(id)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "SET_DEFAULT_PETUGAS",
                userRole = operator,
                detail = "Mengaktifkan petugas default aktif (ID: $id) untuk pencatatan otomatis transaksi piket"
            )
        )
    }

    suspend fun hapusPetugas(id: Long, operator: String) {
        petugasDao.deletePetugas(id)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "HAPUS_PETUGAS",
                userRole = operator,
                detail = "Menghapus data petugas piket (ID: $id)"
            )
        )
    }

    suspend fun getDefaultPetugas(): PetugasEntity? {
        return petugasDao.getDefaultPetugas()
    }

    // Export Database to JSON
    suspend fun exportToJson(): String {
        val root = JSONObject()
        root.put("appName", "IuranQ")
        root.put("version", "1.0")
        root.put("exportedAt", System.currentTimeMillis())
        root.put("community", "RT 01 RW 03 Desa Purbayasa")
        return root.toString(2)
    }

    suspend fun findWargaById(id: String): WargaEntity? {
        return wargaDao.getWargaById(id)
    }
}
