package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AnggotaKurbanEntity
import com.example.data.AppDatabase
import com.example.data.BackupHistoryEntity
import com.example.data.InventarisEntity
import com.example.data.IuranQRepository
import com.example.data.JabatanEntity
import com.example.data.KamarKasEntity
import com.example.data.KelompokKurbanEntity
import com.example.data.PengurusEntity
import com.example.data.PetugasEntity
import com.example.data.TransaksiEntity
import com.example.data.UserAccountEntity
import com.example.data.WargaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole(val label: String, val subtitle: String, val code: String) {
    SUPER_ADMIN("Super Admin", "Akses Penuh Sistem & Master Data", "SUPER_ADMIN"),
    ADMIN_PENGURUS("Admin RT/RW/Bendahara", "Akses Operasional Keuangan & Warga", "ADMIN_PENGURUS"),
    WARGA("Warga RT 01", "Akses Mandiri & Transparansi Saldo", "WARGA")
}

enum class MainTab(val title: String) {
    DASHBOARD("Beranda"),
    JIMPITAN("Jimpitan"),
    QR_PIKET("Piket & QR"),
    KAS_KAMAR("Kamar Kas"),
    WARGA("Data Warga"),
    KURBAN("Kurban"),
    KEGIATAN_INVENTARIS("Kegiatan & Aset"),
    LAPORAN("Laporan & Audit"),
    MENU_PENGATURAN("Menu & Pengaturan"),
    BACKUP_RESTORE("Backup & Restore"),
    PROFIL_PENGURUS("Profil Pengurus"),
    MASTER_JABATAN("Master Jabatan"),
    MANAJEMEN_USER("Manajemen Pengguna")
}

data class ScannerPaymentDialogState(
    val isOpen: Boolean = false,
    val warga: WargaEntity? = null,
    val initialNominal: String = "5000",
    val isSuccess: Boolean = false,
    val lastJimpitanTrx: TransaksiEntity? = null,
    val lastTabunganTrx: TransaksiEntity? = null
)

class IuranQViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: IuranQRepository = IuranQRepository(AppDatabase.getDatabase(application))

    private val _currentRole = MutableStateFlow(UserRole.SUPER_ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.DASHBOARD)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _appName = MutableStateFlow("IuranQ RT 01 RW 03 Desa Purbayasa")
    val appName: StateFlow<String> = _appName.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _scannerState = MutableStateFlow(ScannerPaymentDialogState())
    val scannerState: StateFlow<ScannerPaymentDialogState> = _scannerState.asStateFlow()

    val wargaList = repository.allWarga.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val kamarKasList = repository.allKamarKas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val transaksiList = repository.allTransaksi.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val transaksiAuditList = repository.allTransaksiAudit.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val kelompokKurbanList = repository.allKelompokKurban.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val anggotaKurbanList = repository.allAnggotaKurban.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val inventarisList = repository.allInventaris.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val kegiatanList = repository.allKegiatan.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val auditLogList = repository.allAuditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val petugasList = repository.allPetugas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val jabatanList = repository.allJabatan.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pengurusList = repository.allPengurus.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val backupHistoryList = repository.allBackupHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val userAccountList = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val unsyncedCount = repository.unsyncedCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun switchRole(role: UserRole) {
        _currentRole.value = role
    }

    fun switchTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setAppName(name: String) {
        _appName.value = name
    }

    fun toggleOfflineMode() {
        _isOfflineMode.value = !_isOfflineMode.value
    }

    private fun getActiveOperatorName(): String {
        val defaultPetugas = petugasList.value.find { it.isDefaultAktif }
        return defaultPetugas?.nama ?: _currentRole.value.label
    }

    // Scanner & Payment Sheet
    fun openPaymentSheetForWargaId(wargaId: String) {
        viewModelScope.launch {
            val cleanId = wargaId.trim().replace("IURANQ:", "")
            val found = repository.findWargaById(cleanId)
            if (found != null) {
                _scannerState.value = ScannerPaymentDialogState(
                    isOpen = true,
                    warga = found,
                    initialNominal = "5000",
                    isSuccess = false
                )
            }
        }
    }

    fun closePaymentSheet() {
        _scannerState.value = ScannerPaymentDialogState(isOpen = false)
    }

    fun submitJimpitanPayment(wargaId: String, totalNominal: Long, onCompleted: (WargaEntity, Long, Long, Long) -> Unit) {
        viewModelScope.launch {
            val isOffline = _isOfflineMode.value
            val operator = getActiveOperatorName()
            val result = repository.recordJimpitanWithOverflow(
                wargaId = wargaId,
                totalNominal = totalNominal,
                targetJimpitanHarian = 1000L,
                petugas = operator,
                isOfflineCreated = isOffline
            )
            val updatedWarga = repository.findWargaById(wargaId)
            if (updatedWarga != null) {
                val jimpitanPart = result.first.nominal
                val overflowPart = result.second?.nominal ?: 0L
                _scannerState.value = _scannerState.value.copy(
                    isSuccess = true,
                    lastJimpitanTrx = result.first,
                    lastTabunganTrx = result.second,
                    warga = updatedWarga
                )
                onCompleted(updatedWarga, totalNominal, jimpitanPart, overflowPart)
            }
        }
    }

    fun submitDualJimpitanPayment(
        wargaId: String,
        nominalJimpitan: Long,
        nominalTabungan: Long,
        onCompleted: (WargaEntity, Long, Long, Long) -> Unit
    ) {
        viewModelScope.launch {
            val totalNominal = nominalJimpitan + nominalTabungan
            val isOffline = _isOfflineMode.value
            val operator = getActiveOperatorName()
            val result = repository.recordJimpitanWithOverflow(
                wargaId = wargaId,
                totalNominal = totalNominal,
                targetJimpitanHarian = nominalJimpitan,
                petugas = operator,
                isOfflineCreated = isOffline
            )
            val updatedWarga = repository.findWargaById(wargaId)
            if (updatedWarga != null) {
                _scannerState.value = _scannerState.value.copy(
                    isSuccess = true,
                    lastJimpitanTrx = result.first,
                    lastTabunganTrx = result.second,
                    warga = updatedWarga
                )
                onCompleted(updatedWarga, totalNominal, nominalJimpitan, nominalTabungan)
            }
        }
    }

    fun bayarKasAir(wargaId: String, nominal: Long, bulanTahun: String) {
        viewModelScope.launch {
            repository.bayarKasAir(wargaId, nominal, bulanTahun, getActiveOperatorName())
        }
    }

    fun toggleAmnestyAir(wargaId: String, currentStatus: String) {
        viewModelScope.launch {
            repository.toggleStatusAir(wargaId, currentStatus, getActiveOperatorName())
        }
    }

    fun simpanPetugas(petugas: PetugasEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.simpanPetugas(petugas, isNew, _currentRole.value.label)
        }
    }

    fun setDefaultPetugas(id: Long) {
        viewModelScope.launch {
            repository.setDefaultPetugas(id, _currentRole.value.label)
        }
    }

    fun hapusPetugas(id: Long) {
        viewModelScope.launch {
            repository.hapusPetugas(id, _currentRole.value.label)
        }
    }

    fun bayarCicilanKurban(anggotaId: Long, nominal: Long) {
        viewModelScope.launch {
            repository.bayarCicilanKurban(anggotaId, nominal, _currentRole.value.label)
        }
    }

    fun catatMutasiUmum(kamarKasId: String, jenis: String, nominal: Long, kategori: String, keterangan: String, wargaId: String? = null) {
        viewModelScope.launch {
            repository.recordMutasiKas(
                kamarKasId = kamarKasId,
                jenisMutasi = jenis,
                nominal = nominal,
                kategori = kategori,
                keterangan = keterangan,
                wargaId = wargaId,
                petugas = _currentRole.value.label
            )
        }
    }

    fun catatKegiatanRt(nama: String, kamarKas: String, biaya: Long, pj: String, keterangan: String) {
        viewModelScope.launch {
            repository.tambahKegiatanRt(nama, kamarKas, biaya, pj, keterangan, _currentRole.value.label)
        }
    }

    fun softDeleteTransaksi(id: Long, reason: String) {
        viewModelScope.launch {
            repository.softDeleteTransaksi(id, reason, _currentRole.value.label)
        }
    }

    fun restoreTransaksi(id: Long) {
        viewModelScope.launch {
            repository.restoreTransaksi(id, _currentRole.value.label)
        }
    }

    fun simpanWarga(warga: WargaEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.simpanWarga(warga, isNew, _currentRole.value.label)
        }
    }

    fun softDeleteWarga(wargaId: String) {
        viewModelScope.launch {
            repository.softDeleteWarga(wargaId, _currentRole.value.label)
        }
    }

    fun syncDataOffline() {
        viewModelScope.launch {
            repository.syncOfflineTransactions(_currentRole.value.label)
        }
    }

    fun simpanInventaris(item: InventarisEntity) {
        viewModelScope.launch {
            repository.simpanInventaris(item, _currentRole.value.label)
        }
    }

    fun tambahKelompokKurban(kelompok: KelompokKurbanEntity) {
        viewModelScope.launch {
            repository.tambahKelompokKurban(kelompok, _currentRole.value.label)
        }
    }

    fun tambahAnggotaKurban(anggota: AnggotaKurbanEntity) {
        viewModelScope.launch {
            repository.tambahAnggotaKurban(anggota, _currentRole.value.label)
        }
    }

    // Master Jabatan CRUD
    fun simpanJabatan(jabatan: JabatanEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.simpanJabatan(jabatan, isNew, _currentRole.value.label)
        }
    }

    fun hapusJabatan(id: Long) {
        viewModelScope.launch {
            repository.hapusJabatan(id, _currentRole.value.label)
        }
    }

    // Profil Pengurus CRUD
    fun simpanPengurus(pengurus: PengurusEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.simpanPengurus(pengurus, isNew, _currentRole.value.label)
        }
    }

    fun hapusPengurus(id: Long) {
        viewModelScope.launch {
            repository.hapusPengurus(id, _currentRole.value.label)
        }
    }

    // User Account CRUD (3 Roles)
    fun simpanUserAccount(user: UserAccountEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.simpanUser(user, isNew, _currentRole.value.label)
        }
    }

    fun hapusUserAccount(id: Long) {
        viewModelScope.launch {
            repository.hapusUser(id, _currentRole.value.label)
        }
    }

    // Backup & Restore Engine
    fun generateBackupJson(onDone: (jsonString: String, totalRecords: Int) -> Unit) {
        viewModelScope.launch {
            val (json, count) = repository.exportCompleteDatabaseJson()
            onDone(json, count)
        }
    }

    fun saveBackupHistoryRecord(fileName: String, fileSizeFormatted: String, totalRecords: Int, jsonContent: String) {
        viewModelScope.launch {
            repository.simpanBackupHistory(fileName, fileSizeFormatted, totalRecords, jsonContent)
        }
    }

    fun restoreBackupFromJson(jsonString: String, onDone: (restoredCount: Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.restoreDatabaseFromJson(jsonString, _currentRole.value.label)
            onDone(count)
        }
    }

    fun hapusBackupHistory(id: Long) {
        viewModelScope.launch {
            repository.hapusBackupHistory(id)
        }
    }
}

