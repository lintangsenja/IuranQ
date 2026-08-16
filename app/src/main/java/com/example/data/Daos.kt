package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WargaDao {
    @Query("SELECT * FROM warga WHERE isDeleted = 0 ORDER BY nama ASC")
    fun getAllWarga(): Flow<List<WargaEntity>>

    @Query("SELECT * FROM warga WHERE id = :id AND isDeleted = 0 LIMIT 1")
    suspend fun getWargaById(id: String): WargaEntity?

    @Query("SELECT * FROM warga WHERE id = :id LIMIT 1")
    suspend fun getAnyWargaById(id: String): WargaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWarga(warga: WargaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWarga(list: List<WargaEntity>)

    @Query("UPDATE warga SET saldoTabungan = saldoTabungan + :amount WHERE id = :wargaId")
    suspend fun addSaldoTabungan(wargaId: String, amount: Long)

    @Query("UPDATE warga SET statusAir = :statusAir WHERE id = :wargaId")
    suspend fun updateStatusAir(wargaId: String, statusAir: String)

    @Query("UPDATE warga SET isDeleted = 1 WHERE id = :wargaId")
    suspend fun softDeleteWarga(wargaId: String)

    @Query("UPDATE warga SET isDeleted = 0 WHERE id = :wargaId")
    suspend fun restoreWarga(wargaId: String)

    @Query("SELECT COUNT(*) FROM warga WHERE isDeleted = 0")
    fun getJumlahWarga(): Flow<Int>
}

@Dao
interface KasDao {
    @Query("SELECT * FROM kamar_kas WHERE isDeleted = 0")
    fun getAllKamarKas(): Flow<List<KamarKasEntity>>

    @Query("SELECT * FROM kamar_kas WHERE id = :id LIMIT 1")
    suspend fun getKasById(id: String): KamarKasEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateKas(kas: KamarKasEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKas(list: List<KamarKasEntity>)

    @Query("UPDATE kamar_kas SET saldoTotal = saldoTotal + :nominal WHERE id = :id")
    suspend fun updateSaldoKas(id: String, nominal: Long)
}

@Dao
interface TransaksiDao {
    @Query("SELECT * FROM transaksi WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllTransaksi(): Flow<List<TransaksiEntity>>

    @Query("SELECT * FROM transaksi ORDER BY timestamp DESC")
    fun getAllTransaksiIncludingDeleted(): Flow<List<TransaksiEntity>>

    @Query("SELECT * FROM transaksi WHERE wargaId = :wargaId AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getTransaksiByWarga(wargaId: String): Flow<List<TransaksiEntity>>

    @Query("SELECT * FROM transaksi WHERE kamarKasId = :kamarKasId AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getTransaksiByKas(kamarKasId: String): Flow<List<TransaksiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaksi(transaksi: TransaksiEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransaksi(list: List<TransaksiEntity>)

    @Query("UPDATE transaksi SET isDeleted = 1, deletedBy = :deletedBy, deletedReason = :reason WHERE id = :id")
    suspend fun softDeleteTransaksi(id: Long, deletedBy: String, reason: String)

    @Query("UPDATE transaksi SET isDeleted = 0, deletedBy = NULL, deletedReason = NULL WHERE id = :id")
    suspend fun restoreTransaksi(id: Long)

    @Query("UPDATE transaksi SET isSync = 1 WHERE isSync = 0")
    suspend fun markAllAsSynced()

    @Query("SELECT COUNT(*) FROM transaksi WHERE isSync = 0")
    fun getUnsyncedCount(): Flow<Int>
}

@Dao
interface KurbanDao {
    @Query("SELECT * FROM kelompok_kurban WHERE isDeleted = 0")
    fun getAllKelompok(): Flow<List<KelompokKurbanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKelompok(kelompok: KelompokKurbanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKelompok(list: List<KelompokKurbanEntity>)

    @Query("SELECT * FROM anggota_kurban WHERE isDeleted = 0")
    fun getAllAnggota(): Flow<List<AnggotaKurbanEntity>>

    @Query("SELECT * FROM anggota_kurban WHERE kelompokId = :kelompokId AND isDeleted = 0")
    fun getAnggotaByKelompok(kelompokId: String): Flow<List<AnggotaKurbanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnggota(anggota: AnggotaKurbanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnggota(list: List<AnggotaKurbanEntity>)

    @Query("UPDATE anggota_kurban SET terkumpul = terkumpul + :nominal WHERE id = :anggotaId")
    suspend fun tambahTabunganAnggota(anggotaId: Long, nominal: Long)
}

@Dao
interface InventarisDao {
    @Query("SELECT * FROM inventaris WHERE isDeleted = 0")
    fun getAllInventaris(): Flow<List<InventarisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventaris(item: InventarisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllInventaris(list: List<InventarisEntity>)

    @Update
    suspend fun updateInventaris(item: InventarisEntity)
}

@Dao
interface KegiatanDao {
    @Query("SELECT * FROM kegiatan_rt WHERE isDeleted = 0 ORDER BY tanggal DESC")
    fun getAllKegiatan(): Flow<List<KegiatanRtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKegiatan(kegiatan: KegiatanRtEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKegiatan(list: List<KegiatanRtEntity>)

    @Update
    suspend fun updateKegiatan(kegiatan: KegiatanRtEntity)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_log ORDER BY timestamp DESC LIMIT 200")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_config")
    fun getAllConfigs(): Flow<List<AppConfigEntity>>

    @Query("SELECT value FROM app_config WHERE `key` = :key LIMIT 1")
    suspend fun getConfig(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: AppConfigEntity)
}

@Dao
interface PetugasDao {
    @Query("SELECT * FROM petugas WHERE isDeleted = 0 ORDER BY isDefaultAktif DESC, nama ASC")
    fun getAllPetugas(): Flow<List<PetugasEntity>>

    @Query("SELECT * FROM petugas WHERE isDefaultAktif = 1 AND isDeleted = 0 LIMIT 1")
    suspend fun getDefaultPetugas(): PetugasEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPetugas(petugas: PetugasEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPetugas(list: List<PetugasEntity>)

    @Update
    suspend fun updatePetugas(petugas: PetugasEntity)

    @Query("UPDATE petugas SET isDefaultAktif = 0")
    suspend fun clearDefaultPetugas()

    @Query("UPDATE petugas SET isDefaultAktif = 1 WHERE id = :id")
    suspend fun setDefaultPetugas(id: Long)

    @Query("UPDATE petugas SET isDeleted = 1 WHERE id = :id")
    suspend fun deletePetugas(id: Long)
}
