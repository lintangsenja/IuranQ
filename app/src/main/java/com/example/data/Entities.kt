package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warga")
data class WargaEntity(
    @PrimaryKey
    val id: String, // e.g. "WRG-001"
    val noKk: String,
    val nik: String,
    val nama: String,
    val rt: String = "01",
    val rw: String = "03",
    val alamat: String,
    val noHpWa: String,
    val statusAir: String = "AKTIF", // AKTIF, AMNESTI_OFF, TIDAK_TERPASANG
    val saldoTabungan: Long = 0L, // Saldo tabungan dari overflow jimpitan
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "kamar_kas")
data class KamarKasEntity(
    @PrimaryKey
    val id: String, // KAS_JIMPITAN, KAS_TABUNGAN, KAS_KURBAN, KAS_SEDEKAH_SUBUH, KAS_MARDILAYON, KAS_AIR, KAS_PEMUDA, KAS_INVENTARIS
    val namaKas: String,
    val kategori: String,
    val deskripsi: String,
    val saldoTotal: Long = 0L,
    val colorHex: String,
    val iconName: String,
    val isDeleted: Boolean = false
)

@Entity(tableName = "transaksi")
data class TransaksiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val kodeTransaksi: String,
    val wargaId: String? = null,
    val namaWarga: String? = null,
    val kamarKasId: String,
    val jenisMutasi: String, // MASUK, KELUAR
    val kategori: String, // JIMPITAN, TABUNGAN_OVERFLOW, KAS_AIR, KURBAN, SEDEKAH_SUBUH, MARDILAYON, PEMUDA, SEWA_INVENTARIS, EVENT_RT, PENGELUARAN_SOSIAL
    val nominal: Long,
    val nominalTargetJimpitan: Long = 0L,
    val nominalOverflowTabungan: Long = 0L,
    val keterangan: String,
    val petugas: String = "Petugas Piket",
    val isSync: Boolean = true,
    val isDeleted: Boolean = false,
    val deletedBy: String? = null,
    val deletedReason: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "kelompok_kurban")
data class KelompokKurbanEntity(
    @PrimaryKey
    val id: String, // KRB-SAPI-1
    val namaKelompok: String,
    val tahunHijriah: String = "1447 H / 2026 M",
    val targetHargaPerEkor: Long = 21_000_000L, // Rp 21.000.000 per sapi (Rp 3.000.000 / orang)
    val maksimalAnggota: Int = 7,
    val isDeleted: Boolean = false
)

@Entity(tableName = "anggota_kurban")
data class AnggotaKurbanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val kelompokId: String,
    val wargaId: String,
    val namaWarga: String,
    val targetPerOrang: Long = 3_000_000L,
    val terkumpul: Long = 0L,
    val batasBawahIuran: Long = 100_000L, // Batas bawah per cicilan
    val isDeleted: Boolean = false
)

@Entity(tableName = "inventaris")
data class InventarisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val namaBarang: String,
    val jumlahTotal: Int,
    val jumlahTersedia: Int,
    val tarifSewa: Long,
    val satuan: String = "Hari",
    val status: String = "TERSEDIA", // TERSEDIA, DISEWA, PERBAIKAN
    val isDeleted: Boolean = false
)

@Entity(tableName = "kegiatan_rt")
data class KegiatanRtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val namaKegiatan: String,
    val tanggal: Long = System.currentTimeMillis(),
    val kamarKasSumber: String = "KAS_PEMUDA",
    val biaya: Long = 0L,
    val penanggungJawab: String = "Ketua RT 01",
    val status: String = "RENCANA", // RENCANA, SELESAI, DIBATALKAN
    val keterangan: String = "",
    val isDeleted: Boolean = false
)

@Entity(tableName = "audit_log")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val aksi: String,
    val userRole: String,
    val detail: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey
    val key: String,
    val value: String
)

@Entity(tableName = "petugas")
data class PetugasEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama: String,
    val noHp: String = "",
    val jadwalPiket: String = "Senin Malam",
    val isDefaultAktif: Boolean = false,
    val isDeleted: Boolean = false
)

@Entity(tableName = "master_jabatan")
data class JabatanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val namaJabatan: String,
    val deskripsi: String = "",
    val levelAkses: String = "ADMIN_PENGURUS", // SUPER_ADMIN, ADMIN_PENGURUS, WARGA
    val urutan: Int = 1,
    val isDeleted: Boolean = false
)

@Entity(tableName = "profil_pengurus")
data class PengurusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama: String,
    val jabatan: String, // Dynamic from Master Jabatan
    val noWa: String,
    val fotoAvatar: String = "avatar_1", // avatar id or photo uri
    val email: String = "",
    val catatan: String = "",
    val isUtama: Boolean = false,
    val isDeleted: Boolean = false
)

@Entity(tableName = "backup_history")
data class BackupHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val fileSizeFormatted: String,
    val totalRecords: Int,
    val status: String = "BERHASIL",
    val jsonContent: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val namaLengkap: String,
    val role: String = "ADMIN_PENGURUS", // SUPER_ADMIN, ADMIN_PENGURUS, WARGA
    val noWa: String = "",
    val pinPassword: String = "123456",
    val wargaId: String? = null,
    val isAktif: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

