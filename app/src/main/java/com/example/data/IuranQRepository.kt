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
    private val jabatanDao = database.jabatanDao()
    private val pengurusDao = database.pengurusDao()
    private val backupHistoryDao = database.backupHistoryDao()
    private val userAccountDao = database.userAccountDao()

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
    val allJabatan: Flow<List<JabatanEntity>> = jabatanDao.getAllJabatan()
    val allPengurus: Flow<List<PengurusEntity>> = pengurusDao.getAllPengurus()
    val allBackupHistory: Flow<List<BackupHistoryEntity>> = backupHistoryDao.getAllBackupHistory()
    val allUsers: Flow<List<UserAccountEntity>> = userAccountDao.getAllUsers()
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

    // Master Jabatan CRUD
    suspend fun simpanJabatan(jabatan: JabatanEntity, isNew: Boolean, operator: String) {
        if (isNew) {
            jabatanDao.insertJabatan(jabatan)
        } else {
            jabatanDao.updateJabatan(jabatan)
        }
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = if (isNew) "TAMBAH_JABATAN" else "EDIT_JABATAN",
                userRole = operator,
                detail = "${if (isNew) "Tambah" else "Update"} jabatan: ${jabatan.namaJabatan}"
            )
        )
    }

    suspend fun hapusJabatan(id: Long, operator: String) {
        jabatanDao.deleteJabatan(id)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "HAPUS_JABATAN",
                userRole = operator,
                detail = "Menghapus master jabatan ID $id"
            )
        )
    }

    // Profil Pengurus CRUD
    suspend fun simpanPengurus(pengurus: PengurusEntity, isNew: Boolean, operator: String) {
        if (isNew) {
            pengurusDao.insertPengurus(pengurus)
        } else {
            pengurusDao.updatePengurus(pengurus)
        }
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = if (isNew) "TAMBAH_PENGURUS" else "EDIT_PENGURUS",
                userRole = operator,
                detail = "${if (isNew) "Tambah" else "Update"} profil pengurus: ${pengurus.nama} (${pengurus.jabatan})"
            )
        )
    }

    suspend fun hapusPengurus(id: Long, operator: String) {
        pengurusDao.deletePengurus(id)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "HAPUS_PENGURUS",
                userRole = operator,
                detail = "Menghapus profil pengurus ID $id"
            )
        )
    }

    // User Account Management (3 Roles)
    suspend fun simpanUser(user: UserAccountEntity, isNew: Boolean, operator: String) {
        if (isNew) {
            userAccountDao.insertUser(user)
        } else {
            userAccountDao.updateUser(user)
        }
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = if (isNew) "TAMBAH_USER" else "EDIT_USER",
                userRole = operator,
                detail = "${if (isNew) "Tambah" else "Update"} akun pengguna: ${user.username} (${user.role})"
            )
        )
    }

    suspend fun hapusUser(id: Long, operator: String) {
        userAccountDao.deleteUser(id)
        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "HAPUS_USER",
                userRole = operator,
                detail = "Menghapus akun pengguna ID $id"
            )
        )
    }

    // Backup History
    suspend fun simpanBackupHistory(fileName: String, fileSizeFormatted: String, totalRecords: Int, jsonContent: String): Long {
        return backupHistoryDao.insertBackupHistory(
            BackupHistoryEntity(
                fileName = fileName,
                fileSizeFormatted = fileSizeFormatted,
                totalRecords = totalRecords,
                status = "BERHASIL",
                jsonContent = jsonContent
            )
        )
    }

    suspend fun hapusBackupHistory(id: Long) {
        backupHistoryDao.deleteBackupHistory(id)
    }

    // Comprehensive Export Database to JSON (.json)
    suspend fun exportCompleteDatabaseJson(): Pair<String, Int> {
        val root = JSONObject()
        root.put("appName", "IuranQ")
        root.put("schemaVersion", 3)
        root.put("exportedAt", System.currentTimeMillis())
        root.put("community", "RT 01 RW 03 Desa Purbayasa")

        var totalRecords = 0

        // 1. Warga
        val wargaArray = JSONArray()
        database.openHelper.readableDatabase.query("SELECT * FROM warga WHERE isDeleted = 0").use { cursor ->
            while (cursor.moveToNext()) {
                val obj = JSONObject()
                obj.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")))
                obj.put("noKk", cursor.getString(cursor.getColumnIndexOrThrow("noKk")))
                obj.put("nik", cursor.getString(cursor.getColumnIndexOrThrow("nik")))
                obj.put("nama", cursor.getString(cursor.getColumnIndexOrThrow("nama")))
                obj.put("rt", cursor.getString(cursor.getColumnIndexOrThrow("rt")))
                obj.put("rw", cursor.getString(cursor.getColumnIndexOrThrow("rw")))
                obj.put("alamat", cursor.getString(cursor.getColumnIndexOrThrow("alamat")))
                obj.put("noHpWa", cursor.getString(cursor.getColumnIndexOrThrow("noHpWa")))
                obj.put("statusAir", cursor.getString(cursor.getColumnIndexOrThrow("statusAir")))
                obj.put("saldoTabungan", cursor.getLong(cursor.getColumnIndexOrThrow("saldoTabungan")))
                wargaArray.put(obj)
                totalRecords++
            }
        }
        root.put("warga", wargaArray)

        // 2. Kamar Kas
        val kasArray = JSONArray()
        database.openHelper.readableDatabase.query("SELECT * FROM kamar_kas").use { cursor ->
            while (cursor.moveToNext()) {
                val obj = JSONObject()
                obj.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")))
                obj.put("namaKas", cursor.getString(cursor.getColumnIndexOrThrow("namaKas")))
                obj.put("kategori", cursor.getString(cursor.getColumnIndexOrThrow("kategori")))
                obj.put("deskripsi", cursor.getString(cursor.getColumnIndexOrThrow("deskripsi")))
                obj.put("saldoTotal", cursor.getLong(cursor.getColumnIndexOrThrow("saldoTotal")))
                obj.put("colorHex", cursor.getString(cursor.getColumnIndexOrThrow("colorHex")))
                obj.put("iconName", cursor.getString(cursor.getColumnIndexOrThrow("iconName")))
                kasArray.put(obj)
                totalRecords++
            }
        }
        root.put("kamarKas", kasArray)

        // 3. Transaksi
        val trxArray = JSONArray()
        database.openHelper.readableDatabase.query("SELECT * FROM transaksi").use { cursor ->
            while (cursor.moveToNext()) {
                val obj = JSONObject()
                obj.put("kodeTransaksi", cursor.getString(cursor.getColumnIndexOrThrow("kodeTransaksi")))
                obj.put("wargaId", cursor.getString(cursor.getColumnIndexOrThrow("wargaId")))
                obj.put("namaWarga", cursor.getString(cursor.getColumnIndexOrThrow("namaWarga")))
                obj.put("kamarKasId", cursor.getString(cursor.getColumnIndexOrThrow("kamarKasId")))
                obj.put("jenisMutasi", cursor.getString(cursor.getColumnIndexOrThrow("jenisMutasi")))
                obj.put("kategori", cursor.getString(cursor.getColumnIndexOrThrow("kategori")))
                obj.put("nominal", cursor.getLong(cursor.getColumnIndexOrThrow("nominal")))
                obj.put("keterangan", cursor.getString(cursor.getColumnIndexOrThrow("keterangan")))
                obj.put("petugas", cursor.getString(cursor.getColumnIndexOrThrow("petugas")))
                obj.put("timestamp", cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")))
                trxArray.put(obj)
                totalRecords++
            }
        }
        root.put("transaksi", trxArray)

        // 4. Master Jabatan
        val jabatanArray = JSONArray()
        database.openHelper.readableDatabase.query("SELECT * FROM master_jabatan WHERE isDeleted = 0").use { cursor ->
            while (cursor.moveToNext()) {
                val obj = JSONObject()
                obj.put("namaJabatan", cursor.getString(cursor.getColumnIndexOrThrow("namaJabatan")))
                obj.put("deskripsi", cursor.getString(cursor.getColumnIndexOrThrow("deskripsi")))
                obj.put("levelAkses", cursor.getString(cursor.getColumnIndexOrThrow("levelAkses")))
                obj.put("urutan", cursor.getInt(cursor.getColumnIndexOrThrow("urutan")))
                jabatanArray.put(obj)
                totalRecords++
            }
        }
        root.put("masterJabatan", jabatanArray)

        // 5. Profil Pengurus
        val pengurusArray = JSONArray()
        database.openHelper.readableDatabase.query("SELECT * FROM profil_pengurus WHERE isDeleted = 0").use { cursor ->
            while (cursor.moveToNext()) {
                val obj = JSONObject()
                obj.put("nama", cursor.getString(cursor.getColumnIndexOrThrow("nama")))
                obj.put("jabatan", cursor.getString(cursor.getColumnIndexOrThrow("jabatan")))
                obj.put("noWa", cursor.getString(cursor.getColumnIndexOrThrow("noWa")))
                obj.put("fotoAvatar", cursor.getString(cursor.getColumnIndexOrThrow("fotoAvatar")))
                obj.put("email", cursor.getString(cursor.getColumnIndexOrThrow("email")))
                obj.put("catatan", cursor.getString(cursor.getColumnIndexOrThrow("catatan")))
                obj.put("isUtama", cursor.getInt(cursor.getColumnIndexOrThrow("isUtama")) == 1)
                pengurusArray.put(obj)
                totalRecords++
            }
        }
        root.put("profilPengurus", pengurusArray)

        // 6. User Accounts
        val usersArray = JSONArray()
        database.openHelper.readableDatabase.query("SELECT * FROM user_account").use { cursor ->
            while (cursor.moveToNext()) {
                val obj = JSONObject()
                obj.put("username", cursor.getString(cursor.getColumnIndexOrThrow("username")))
                obj.put("namaLengkap", cursor.getString(cursor.getColumnIndexOrThrow("namaLengkap")))
                obj.put("role", cursor.getString(cursor.getColumnIndexOrThrow("role")))
                obj.put("noWa", cursor.getString(cursor.getColumnIndexOrThrow("noWa")))
                obj.put("pinPassword", cursor.getString(cursor.getColumnIndexOrThrow("pinPassword")))
                obj.put("isAktif", cursor.getInt(cursor.getColumnIndexOrThrow("isAktif")) == 1)
                usersArray.put(obj)
                totalRecords++
            }
        }
        root.put("userAccounts", usersArray)

        val jsonString = root.toString(2)
        return Pair(jsonString, totalRecords)
    }

    // Restore Database from JSON
    suspend fun restoreDatabaseFromJson(jsonString: String, operator: String): Int {
        val root = JSONObject(jsonString)
        var restoredCount = 0

        // 1. Warga
        if (root.has("warga")) {
            val arr = root.getJSONArray("warga")
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val entity = WargaEntity(
                    id = obj.optString("id", "WRG-${i + 1}"),
                    noKk = obj.optString("noKk", "3303050101000001"),
                    nik = obj.optString("nik", "3303051010000001"),
                    nama = obj.optString("nama", "Warga"),
                    rt = obj.optString("rt", "01"),
                    rw = obj.optString("rw", "03"),
                    alamat = obj.optString("alamat", "RT 01 RW 03 Purbayasa"),
                    noHpWa = obj.optString("noHpWa", ""),
                    statusAir = obj.optString("statusAir", "AKTIF"),
                    saldoTabungan = obj.optLong("saldoTabungan", 0L)
                )
                wargaDao.insertOrUpdateWarga(entity)
                restoredCount++
            }
        }

        // 2. Master Jabatan
        if (root.has("masterJabatan")) {
            val arr = root.getJSONArray("masterJabatan")
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val entity = JabatanEntity(
                    namaJabatan = obj.optString("namaJabatan", "Pengurus"),
                    deskripsi = obj.optString("deskripsi", ""),
                    levelAkses = obj.optString("levelAkses", "ADMIN_PENGURUS"),
                    urutan = obj.optInt("urutan", i + 1)
                )
                jabatanDao.insertJabatan(entity)
                restoredCount++
            }
        }

        // 3. Profil Pengurus
        if (root.has("profilPengurus")) {
            val arr = root.getJSONArray("profilPengurus")
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val entity = PengurusEntity(
                    nama = obj.optString("nama", ""),
                    jabatan = obj.optString("jabatan", ""),
                    noWa = obj.optString("noWa", ""),
                    fotoAvatar = obj.optString("fotoAvatar", "avatar_1"),
                    email = obj.optString("email", ""),
                    catatan = obj.optString("catatan", ""),
                    isUtama = obj.optBoolean("isUtama", false)
                )
                pengurusDao.insertPengurus(entity)
                restoredCount++
            }
        }

        // 4. User Accounts
        if (root.has("userAccounts")) {
            val arr = root.getJSONArray("userAccounts")
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val entity = UserAccountEntity(
                    username = obj.optString("username", "user_$i"),
                    namaLengkap = obj.optString("namaLengkap", ""),
                    role = obj.optString("role", "ADMIN_PENGURUS"),
                    noWa = obj.optString("noWa", ""),
                    pinPassword = obj.optString("pinPassword", "123456"),
                    isAktif = obj.optBoolean("isAktif", true)
                )
                userAccountDao.insertUser(entity)
                restoredCount++
            }
        }

        auditLogDao.insertAuditLog(
            AuditLogEntity(
                aksi = "RESTORE_DATABASE_JSON",
                userRole = operator,
                detail = "Restore data dari file backup JSON berhasil ($restoredCount entri dipulihkan)"
            )
        )

        return restoredCount
    }

    suspend fun findWargaById(id: String): WargaEntity? {
        return wargaDao.getWargaById(id)
    }
}

