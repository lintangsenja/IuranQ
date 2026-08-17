package com.example.ui.inventaris

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InventarisEntity
import com.example.data.KegiatanRtEntity
import com.example.ui.IuranQViewModel
import com.example.ui.theme.CardCyanGradient
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TealAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KegiatanInventarisScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Inventaris RT (Tratag & Kursi), 1: Event & Kegiatan RT

    val inventarisList by viewModel.inventarisList.collectAsState()
    val kegiatanList by viewModel.kegiatanList.collectAsState()
    val kamarKasList by viewModel.kamarKasList.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var showAddEventDialog by remember { mutableStateOf(false) }
    var showRentDialog by remember { mutableStateOf<InventarisEntity?>(null) }
    var rentQtyText by remember { mutableStateOf("1") }
    var rentPeminjamText by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (currentRole != com.example.ui.UserRole.WARGA && selectedSubTab == 1) {
                FloatingActionButton(
                    onClick = { showAddEventDialog = true },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 72.dp)
                        .testTag("fab_tambah_kegiatan")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah Event", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeroGradient)
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "Kegiatan & Inventaris Aset RT",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Penyewaan Tratag/Kursi & Pembiayaan Event Komunitas RT 01 RW 03",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Sub Tab Row
            item {
                TabRow(
                    selectedTabIndex = selectedSubTab,
                    containerColor = Color.White,
                    contentColor = IndigoPrimary
                ) {
                    Tab(
                        selected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 },
                        text = { Text("Inventaris & Sewa", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        text = { Text("Event & Kegiatan RT", fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (selectedSubTab == 0) {
                // Section: Inventaris RT
                items(inventarisList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color(0xFFEEF2FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = IndigoPrimary)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.namaBarang,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Total Stok: ${item.jumlahTotal} ${item.satuan} • Tersedia: ${item.jumlahTersedia} ${item.satuan}",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Tarif Sewa: Rp${String.format(Locale.GERMANY, "%,d", item.tarifSewa)} / hari",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldSuccess
                                )
                            }

                            if (currentRole != com.example.ui.UserRole.WARGA) {
                                Button(
                                    onClick = { showRentDialog = item },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Sewa", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                // Section: Event & Kegiatan RT
                items(kegiatanList) { keg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = keg.namaKegiatan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = keg.status,
                                        color = EmeraldSuccess,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Biaya Terpotong: Rp${String.format(Locale.GERMANY, "%,d", keg.biaya)} dari ${keg.kamarKasSumber}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = IndigoPrimary
                            )
                            Text(
                                text = "PJ: ${keg.penanggungJawab} • ${keg.keterangan}",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Add Event
    if (showAddEventDialog) {
        var eventName by remember { mutableStateOf("") }
        var eventCostText by remember { mutableStateOf("150000") }
        var eventPj by remember { mutableStateOf("") }
        var eventKas by remember { mutableStateOf(kamarKasList.firstOrNull()?.id ?: "KAS_JIMPITAN") }
        var eventKet by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            title = { Text("Tambah Kegiatan RT Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = { Text("Nama Kegiatan") },
                        placeholder = { Text("Contoh: Kerja Bakti / Peringatan 17 Agustus") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = eventCostText,
                        onValueChange = { eventCostText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Anggaran Biaya (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = eventPj,
                        onValueChange = { eventPj = it },
                        label = { Text("Penanggung Jawab") },
                        placeholder = { Text("Contoh: Bpk. Slamet") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = eventKet,
                        onValueChange = { eventKet = it },
                        label = { Text("Keterangan Singkat") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cost = eventCostText.toLongOrNull() ?: 0L
                        if (eventName.isNotBlank()) {
                            viewModel.catatKegiatanRt(eventName, eventKas, cost, eventPj.ifBlank { "Pengurus RT" }, eventKet.ifBlank { "Kegiatan warga" })
                            showAddEventDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Simpan & Potong Kas")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddEventDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal Sewa Inventaris
    if (showRentDialog != null) {
        val item = showRentDialog!!
        AlertDialog(
            onDismissRequest = { showRentDialog = null },
            title = { Text("Penyewaan ${item.namaBarang}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Tarif: Rp${String.format(Locale.GERMANY, "%,d", item.tarifSewa)} / hari", fontSize = 12.sp, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rentPeminjamText,
                        onValueChange = { rentPeminjamText = it },
                        label = { Text("Nama Warga Peminjam") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rentQtyText,
                        onValueChange = { rentQtyText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Jumlah (${item.satuan})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = rentQtyText.toIntOrNull() ?: 1
                        val newTersedia = (item.jumlahTersedia - qty).coerceAtLeast(0)
                        val updated = item.copy(
                            jumlahTersedia = newTersedia,
                            status = if (newTersedia <= 0) "DISEWA" else "TERSEDIA"
                        )
                        viewModel.simpanInventaris(updated)
                        // Record rental income
                        val totalIncome = item.tarifSewa * qty
                        viewModel.catatMutasiUmum("KAS_INVENTARIS", "MASUK", totalIncome, "SEWA_INVENTARIS", "Sewa ${item.namaBarang} x$qty oleh $rentPeminjamText")
                        showRentDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Catat Sewa & Masukkan Kas")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRentDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
