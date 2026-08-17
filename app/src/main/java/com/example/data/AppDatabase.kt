package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WargaEntity::class,
        KamarKasEntity::class,
        TransaksiEntity::class,
        KelompokKurbanEntity::class,
        AnggotaKurbanEntity::class,
        InventarisEntity::class,
        KegiatanRtEntity::class,
        AuditLogEntity::class,
        AppConfigEntity::class,
        PetugasEntity::class,
        JabatanEntity::class,
        PengurusEntity::class,
        BackupHistoryEntity::class,
        UserAccountEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wargaDao(): WargaDao
    abstract fun kasDao(): KasDao
    abstract fun transaksiDao(): TransaksiDao
    abstract fun kurbanDao(): KurbanDao
    abstract fun inventarisDao(): InventarisDao
    abstract fun kegiatanDao(): KegiatanDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun appConfigDao(): AppConfigDao
    abstract fun petugasDao(): PetugasDao
    abstract fun jabatanDao(): JabatanDao
    abstract fun pengurusDao(): PengurusDao
    abstract fun backupHistoryDao(): BackupHistoryDao
    abstract fun userAccountDao(): UserAccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "iuranq_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    seedDatabase(database)
                                }
                            }
                        }
                    }).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDatabase(db: AppDatabase) {
            // Seed Kamar Kas
            val kasList = listOf(
                KamarKasEntity(
                    id = "KAS_JIMPITAN",
                    namaKas = "Kas Jimpitan RT",
                    kategori = "JIMPITAN",
                    deskripsi = "Pencatatan harian jimpitan target Rp1.000/hari (Rp365rb/thn)",
                    saldoTotal = 3_450_000L,
                    colorHex = "#059669",
                    iconName = "LocalAtm"
                ),
                KamarKasEntity(
                    id = "KAS_TABUNGAN",
                    namaKas = "Tabungan Warga",
                    kategori = "TABUNGAN_OVERFLOW",
                    deskripsi = "Akumulasi saldo sisa lebih/overflow dari setoran harian jimpitan",
                    saldoTotal = 2_150_000L,
                    colorHex = "#4F46E5",
                    iconName = "Savings"
                ),
                KamarKasEntity(
                    id = "KAS_KURBAN",
                    namaKas = "Tabungan Kurban",
                    kategori = "KURBAN",
                    deskripsi = "Patungan kurban sapi (max 7 orang per ekor) Idul Adha 1447H",
                    saldoTotal = 14_800_000L,
                    colorHex = "#D97706",
                    iconName = "Pets"
                ),
                KamarKasEntity(
                    id = "KAS_SEDEKAH_SUBUH",
                    namaKas = "Sedekah Subuh",
                    kategori = "SEDEKAH_SUBUH",
                    deskripsi = "Infaq sedekah subuh berkah untuk santunan dhuafa & anak yatim",
                    saldoTotal = 1_850_000L,
                    colorHex = "#0891B2",
                    iconName = "VolunteerActivism"
                ),
                KamarKasEntity(
                    id = "KAS_MARDILAYON",
                    namaKas = "Dana Sosial & Mardilayon",
                    kategori = "MARDILAYON",
                    deskripsi = "Penyediaan kain mori gratis, mobil ambulans, & santunan kematian",
                    saldoTotal = 2_700_000L,
                    colorHex = "#9333EA",
                    iconName = "Favorite"
                ),
                KamarKasEntity(
                    id = "KAS_AIR",
                    namaKas = "Kas Air Bersih RT",
                    kategori = "KAS_AIR",
                    deskripsi = "Iuran pengelolaan air bersih bulanan tetap Rp5.000/KK",
                    saldoTotal = 980_000L,
                    colorHex = "#0284C7",
                    iconName = "WaterDrop"
                ),
                KamarKasEntity(
                    id = "KAS_PEMUDA",
                    namaKas = "Kas Pemuda & Olahraga",
                    kategori = "PEMUDA",
                    deskripsi = "Dana pembinaan karang taruna RT, voli, & event kepemudaan",
                    saldoTotal = 1_250_000L,
                    colorHex = "#EA580C",
                    iconName = "SportsSoccer"
                ),
                KamarKasEntity(
                    id = "KAS_INVENTARIS",
                    namaKas = "Kas Sewa Inventaris",
                    kategori = "SEWA_INVENTARIS",
                    deskripsi = "Penerimaan sewa tratag, kursi lipat, & sound system warga",
                    saldoTotal = 890_000L,
                    colorHex = "#475569",
                    iconName = "Chair"
                )
            )
            db.kasDao().insertAllKas(kasList)

            // Seed Warga
            val wargaList = listOf(
                WargaEntity(
                    id = "WRG-001",
                    noKk = "3303011204900001",
                    nik = "3303011204900001",
                    nama = "Bambang Santoso",
                    rt = "01",
                    rw = "03",
                    alamat = "Jl. Melati No. 01, Purbayasa",
                    noHpWa = "081234567801",
                    statusAir = "AKTIF",
                    saldoTabungan = 85_000L
                ),
                WargaEntity(
                    id = "WRG-002",
                    noKk = "3303011204900002",
                    nik = "3303011204900002",
                    nama = "Siti Rohmah",
                    rt = "01",
                    rw = "03",
                    alamat = "Jl. Melati No. 03, Purbayasa",
                    noHpWa = "081234567802",
                    statusAir = "AKTIF",
                    saldoTabungan = 120_000L
                ),
                WargaEntity(
                    id = "WRG-003",
                    noKk = "3303011204900003",
                    nik = "3303011204900003",
                    nama = "Joko Widodo",
                    rt = "01",
                    rw = "03",
                    alamat = "Jl. Melati No. 05, Purbayasa",
                    noHpWa = "081234567803",
                    statusAir = "AMNESTI_OFF", // Air off - tidak tertagih
                    saldoTabungan = 50_000L
                ),
                WargaEntity(
                    id = "WRG-004",
                    noKk = "3303011204900004",
                    nik = "3303011204900004",
                    nama = "Ahmad Dahlan",
                    rt = "01",
                    rw = "03",
                    alamat = "Jl. Melati No. 07, Purbayasa",
                    noHpWa = "081234567804",
                    statusAir = "AKTIF",
                    saldoTabungan = 240_000L
                ),
                WargaEntity(
                    id = "WRG-005",
                    noKk = "3303011204900005",
                    nik = "3303011204900005",
                    nama = "Tri Wahyuni",
                    rt = "01",
                    rw = "03",
                    alamat = "Jl. Melati No. 09, Purbayasa",
                    noHpWa = "081234567805",
                    statusAir = "AKTIF",
                    saldoTabungan = 95_000L
                ),
                WargaEntity(
                    id = "WRG-006",
                    noKk = "3303011204900006",
                    nik = "3303011204900006",
                    nama = "Slamet Riyadi",
                    rt = "01",
                    rw = "03",
                    alamat = "Jl. Kenanga No. 02, Purbayasa",
                    noHpWa = "081234567806",
                    statusAir = "AKTIF",
                    saldoTabungan = 160_000L
                ),
                WargaEntity(
                    id = "WRG-007",
                    noKk = "3303011204900007",
                    nik = "3303011204900007",
                    nama = "Hendra Gunawan",
                    rt = "01",
                    rw = "03",
                    alamat = "Jl. Kenanga No. 04, Purbayasa",
                    noHpWa = "081234567807",
                    statusAir = "TIDAK_TERPASANG",
                    saldoTabungan = 70_000L
                )
            )
            db.wargaDao().insertAllWarga(wargaList)

            // Seed Kurban
            val kelompok1 = KelompokKurbanEntity(
                id = "KRB-SAPI-1",
                namaKelompok = "Kelompok Sapi Al-Falah (1)",
                tahunHijriah = "1447 H / 2026 M",
                targetHargaPerEkor = 21_000_000L,
                maksimalAnggota = 7
            )
            val kelompok2 = KelompokKurbanEntity(
                id = "KRB-SAPI-2",
                namaKelompok = "Kelompok Sapi Barokah (2)",
                tahunHijriah = "1447 H / 2026 M",
                targetHargaPerEkor = 21_000_000L,
                maksimalAnggota = 7
            )
            db.kurbanDao().insertAllKelompok(listOf(kelompok1, kelompok2))

            val anggotaKurbanList = listOf(
                AnggotaKurbanEntity(kelompokId = "KRB-SAPI-1", wargaId = "WRG-001", namaWarga = "Bambang Santoso", targetPerOrang = 3_000_000L, terkumpul = 2_400_000L, batasBawahIuran = 100_000L),
                AnggotaKurbanEntity(kelompokId = "KRB-SAPI-1", wargaId = "WRG-002", namaWarga = "Siti Rohmah", targetPerOrang = 3_000_000L, terkumpul = 3_000_000L, batasBawahIuran = 100_000L),
                AnggotaKurbanEntity(kelompokId = "KRB-SAPI-1", wargaId = "WRG-004", namaWarga = "Ahmad Dahlan", targetPerOrang = 3_000_000L, terkumpul = 2_100_000L, batasBawahIuran = 100_000L),
                AnggotaKurbanEntity(kelompokId = "KRB-SAPI-1", wargaId = "WRG-005", namaWarga = "Tri Wahyuni", targetPerOrang = 3_000_000L, terkumpul = 1_800_000L, batasBawahIuran = 100_000L),
                AnggotaKurbanEntity(kelompokId = "KRB-SAPI-1", wargaId = "WRG-006", namaWarga = "Slamet Riyadi", targetPerOrang = 3_000_000L, terkumpul = 2_700_000L, batasBawahIuran = 100_000L),
                AnggotaKurbanEntity(kelompokId = "KRB-SAPI-2", wargaId = "WRG-007", namaWarga = "Hendra Gunawan", targetPerOrang = 3_000_000L, terkumpul = 1_500_000L, batasBawahIuran = 100_000L),
                AnggotaKurbanEntity(kelompokId = "KRB-SAPI-2", wargaId = "WRG-003", namaWarga = "Joko Widodo", targetPerOrang = 3_000_000L, terkumpul = 1_300_000L, batasBawahIuran = 100_000L)
            )
            db.kurbanDao().insertAllAnggota(anggotaKurbanList)

            // Seed Inventaris
            val inventarisList = listOf(
                InventarisEntity(namaBarang = "Tratag Pipa Besi 4x6 Meter", jumlahTotal = 4, jumlahTersedia = 3, tarifSewa = 75_000L, satuan = "Hari"),
                InventarisEntity(namaBarang = "Kursi Plastik Napolly Hijau", jumlahTotal = 150, jumlahTersedia = 120, tarifSewa = 1_000L, satuan = "Buah/Hari"),
                InventarisEntity(namaBarang = "Sound System Portable & Mic Wireless", jumlahTotal = 2, jumlahTersedia = 2, tarifSewa = 50_000L, satuan = "Hari"),
                InventarisEntity(namaBarang = "Panggung Kayu Mini 3x4 Meter", jumlahTotal = 1, jumlahTersedia = 1, tarifSewa = 100_000L, satuan = "Acara"),
                InventarisEntity(namaBarang = "Terpal Biru Tebal 6x8 Meter", jumlahTotal = 5, jumlahTersedia = 5, tarifSewa = 15_000L, satuan = "Hari")
            )
            db.inventarisDao().insertAllInventaris(inventarisList)

            // Seed Kegiatan RT
            val kegiatanList = listOf(
                KegiatanRtEntity(
                    namaKegiatan = "Kerja Bakti Bersih Selokan & Fogging",
                    tanggal = System.currentTimeMillis() - 86400000L * 3,
                    kamarKasSumber = "KAS_PEMUDA",
                    biaya = 250_000L,
                    penanggungJawab = "Seksi Kebersihan RT",
                    status = "SELESAI",
                    keterangan = "Beli konsumsi & obat fogging selokan warga"
                ),
                KegiatanRtEntity(
                    namaKegiatan = "Santunan Mardilayon (Kain Mori & Perlengkapan)",
                    tanggal = System.currentTimeMillis() - 86400000L * 7,
                    kamarKasSumber = "KAS_MARDILAYON",
                    biaya = 350_000L,
                    penanggungJawab = "Seksi Kematian & Sosial",
                    status = "SELESAI",
                    keterangan = "Pemberian kain mori gratis keluarga almarhum"
                )
            )
            db.kegiatanDao().insertAllKegiatan(kegiatanList)

            // Seed App Config
            db.appConfigDao().setConfig(AppConfigEntity("APP_NAME", "IuranQ RT 01 RW 03 Desa Purbayasa"))
            db.appConfigDao().setConfig(AppConfigEntity("TARGET_JIMPITAN_HARIAN", "1000"))
            db.appConfigDao().setConfig(AppConfigEntity("TARIF_KAS_AIR_BULANAN", "5000"))
            db.appConfigDao().setConfig(AppConfigEntity("RT_NAME", "RT 01"))
            db.appConfigDao().setConfig(AppConfigEntity("RW_NAME", "RW 03"))
            db.appConfigDao().setConfig(AppConfigEntity("DESA_NAME", "Desa Purbayasa"))

            // Seed Petugas Piket
            val initialPetugas = listOf(
                PetugasEntity(nama = "Bpk. Bambang Santoso", noHp = "081234567801", jadwalPiket = "Senin Malam", isDefaultAktif = true),
                PetugasEntity(nama = "Bpk. Sutrisno", noHp = "081234567808", jadwalPiket = "Selasa Malam", isDefaultAktif = false),
                PetugasEntity(nama = "Bpk. Joko Widodo", noHp = "081234567803", jadwalPiket = "Rabu Malam", isDefaultAktif = false),
                PetugasEntity(nama = "Bpk. Ahmad Dahlan", noHp = "081234567804", jadwalPiket = "Kamis Malam", isDefaultAktif = false),
                PetugasEntity(nama = "Bpk. Hendra Gunawan", noHp = "081234567807", jadwalPiket = "Jumat Malam", isDefaultAktif = false),
                PetugasEntity(nama = "Bpk. Slamet Riyadi", noHp = "081234567806", jadwalPiket = "Sabtu Malam", isDefaultAktif = false),
                PetugasEntity(nama = "Bpk. Agus Prasetyo", noHp = "081234567809", jadwalPiket = "Minggu Malam", isDefaultAktif = false)
            )
            db.petugasDao().insertAllPetugas(initialPetugas)

            // Seed Sample Transactions
            val now = System.currentTimeMillis()
            val sampleTrans = listOf(
                TransaksiEntity(
                    kodeTransaksi = "TRX-JMP-001",
                    wargaId = "WRG-001",
                    namaWarga = "Bambang Santoso",
                    kamarKasId = "KAS_JIMPITAN",
                    jenisMutasi = "MASUK",
                    kategori = "JIMPITAN",
                    nominal = 1_000L,
                    nominalTargetJimpitan = 1_000L,
                    nominalOverflowTabungan = 4_000L,
                    keterangan = "Scan QR Jimpitan Harian (Total Setor: Rp5.000 -> Rp1.000 Jimpitan, Rp4.000 Masuk Tabungan)",
                    petugas = "Petugas Piket Malam",
                    timestamp = now - 3600000L * 2
                ),
                TransaksiEntity(
                    kodeTransaksi = "TRX-TAB-001",
                    wargaId = "WRG-001",
                    namaWarga = "Bambang Santoso",
                    kamarKasId = "KAS_TABUNGAN",
                    jenisMutasi = "MASUK",
                    kategori = "TABUNGAN_OVERFLOW",
                    nominal = 4_000L,
                    nominalTargetJimpitan = 1_000L,
                    nominalOverflowTabungan = 4_000L,
                    keterangan = "Auto-Overflow Tabungan Warga dari Jimpitan",
                    petugas = "Sistem IuranQ",
                    timestamp = now - 3600000L * 2
                ),
                TransaksiEntity(
                    kodeTransaksi = "TRX-AIR-002",
                    wargaId = "WRG-002",
                    namaWarga = "Siti Rohmah",
                    kamarKasId = "KAS_AIR",
                    jenisMutasi = "MASUK",
                    kategori = "KAS_AIR",
                    nominal = 5_000L,
                    keterangan = "Iuran Bulanan Air Bersih RT",
                    petugas = "Bendahara RT",
                    timestamp = now - 3600000L * 24
                ),
                TransaksiEntity(
                    kodeTransaksi = "TRX-KRB-003",
                    wargaId = "WRG-001",
                    namaWarga = "Bambang Santoso",
                    kamarKasId = "KAS_KURBAN",
                    jenisMutasi = "MASUK",
                    kategori = "KURBAN",
                    nominal = 200_000L,
                    keterangan = "Cicilan Tabungan Kurban Sapi Al-Falah",
                    petugas = "Bendahara Kurban",
                    timestamp = now - 3600000L * 48
                )
            )
            db.transaksiDao().insertAllTransaksi(sampleTrans)

            // Seed Master Data Jabatan
            val defaultJabatan = listOf(
                JabatanEntity(namaJabatan = "Super Admin RT", deskripsi = "Penanggung jawab utama sistem & IT RT", levelAkses = "SUPER_ADMIN", urutan = 1),
                JabatanEntity(namaJabatan = "Ketua RW 03", deskripsi = "Pimpinan wilayah RW 03 Purbayasa", levelAkses = "ADMIN_PENGURUS", urutan = 2),
                JabatanEntity(namaJabatan = "Ketua RT 01", deskripsi = "Pimpinan warga & pengambil kebijakan RT 01", levelAkses = "ADMIN_PENGURUS", urutan = 3),
                JabatanEntity(namaJabatan = "Bendahara Umum RT", deskripsi = "Pengelola mutasi kas & pembukuan", levelAkses = "ADMIN_PENGURUS", urutan = 4),
                JabatanEntity(namaJabatan = "Sekretaris RT", deskripsi = "Pencatatan data warga & surat menyurat", levelAkses = "ADMIN_PENGURUS", urutan = 5),
                JabatanEntity(namaJabatan = "Seksi Keamanan / Piket Ronda", deskripsi = "Koordinator ronda malam & jimpitan", levelAkses = "ADMIN_PENGURUS", urutan = 6),
                JabatanEntity(namaJabatan = "Seksi Kerohanian & Kurban", deskripsi = "Pengelola tabungan kurban & ibadah", levelAkses = "ADMIN_PENGURUS", urutan = 7),
                JabatanEntity(namaJabatan = "Seksi Sosial & Mardilayon", deskripsi = "Santunan dhuafa & rukun kematian", levelAkses = "ADMIN_PENGURUS", urutan = 8),
                JabatanEntity(namaJabatan = "Seksi Pengelola Air Bersih", deskripsi = "Pencatatan iuran & meteran air warga", levelAkses = "ADMIN_PENGURUS", urutan = 9)
            )
            db.jabatanDao().insertAllJabatan(defaultJabatan)

            // Seed Profil Pengurus
            val defaultPengurus = listOf(
                PengurusEntity(nama = "H. Supriyanto, S.Pd", jabatan = "Ketua RT 01", noWa = "081234567890", fotoAvatar = "avatar_1", email = "rt01.purbayasa@gmail.com", catatan = "Periode 2024-2029", isUtama = true),
                PengurusEntity(nama = "Drs. H. Mulyono", jabatan = "Ketua RW 03", noWa = "081398765432", fotoAvatar = "avatar_2", email = "rw03.purbayasa@gmail.com", catatan = "Pembina Wilayah", isUtama = true),
                PengurusEntity(nama = "Hj. Endang Tri Wahyuni", jabatan = "Bendahara Umum RT", noWa = "081543219876", fotoAvatar = "avatar_3", email = "bendahara.rt01@gmail.com", catatan = "Pengelola Kas Utama", isUtama = true),
                PengurusEntity(nama = "Ahmad Fajar, S.Kom", jabatan = "Super Admin RT", noWa = "085712345678", fotoAvatar = "avatar_4", email = "admin.iuranq@gmail.com", catatan = "Administrator Sistem & Keamanan", isUtama = true)
            )
            db.pengurusDao().insertAllPengurus(defaultPengurus)

            // Seed User Accounts (3 Roles)
            val defaultUsers = listOf(
                UserAccountEntity(username = "admin_super", namaLengkap = "Ahmad Fajar (Admin IT)", role = "SUPER_ADMIN", noWa = "085712345678", pinPassword = "123", isAktif = true),
                UserAccountEntity(username = "rt01_ketua", namaLengkap = "H. Supriyanto (Ketua RT)", role = "ADMIN_PENGURUS", noWa = "081234567890", pinPassword = "123", isAktif = true),
                UserAccountEntity(username = "bendahara_rt", namaLengkap = "Hj. Endang Tri Wahyuni", role = "ADMIN_PENGURUS", noWa = "081543219876", pinPassword = "123", isAktif = true),
                UserAccountEntity(username = "bambang_s", namaLengkap = "Bambang Santoso", role = "WARGA", wargaId = "WRG-001", noWa = "081234567890", pinPassword = "123", isAktif = true)
            )
            db.userAccountDao().insertAllUsers(defaultUsers)

            // Seed Initial Backup Record
            db.backupHistoryDao().insertBackupHistory(
                BackupHistoryEntity(
                    fileName = "backup_iuranq_initial_auto.json",
                    fileSizeFormatted = "28.5 KB",
                    totalRecords = 35,
                    status = "BERHASIL",
                    jsonContent = """{"appName":"IuranQ","version":"1.0","community":"RT 01 RW 03 Desa Purbayasa"}"""
                )
            )

            db.auditLogDao().insertAuditLog(
                AuditLogEntity(
                    aksi = "INITIALIZE_SYSTEM",
                    userRole = "SUPER_ADMIN",
                    detail = "Inisialisasi Master Data & Database IuranQ RT 01 RW 03 Desa Purbayasa"
                )
            )
        }
    }
}
