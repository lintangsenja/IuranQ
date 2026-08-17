package com.example.ui.kas

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KamarKasEntity
import com.example.data.TransaksiEntity
import com.example.ui.IuranQViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TealAccent
import com.example.util.WhatsAppGateway
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasMultiKamarScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val kamarKasList by viewModel.kamarKasList.collectAsState()
    val transaksiList by viewModel.transaksiList.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var selectedKasId by remember { mutableStateOf("ALL") }
    var showMutasiDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf<TransaksiEntity?>(null) }
    var deleteReasonText by remember { mutableStateOf("Koreksi salah input nominal") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (currentRole != com.example.ui.UserRole.WARGA) {
                FloatingActionButton(
                    onClick = { showMutasiDialog = true },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("fab_tambah_mutasi")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Input Mutasi",
                        modifier = Modifier.size(24.dp)
                    )
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
            // Header with minimalist Add Icon on Top-Right
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeroGradient)
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = "Multi-Kamar Kas Berelasi",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Sistem pembagian kamar kas transparan RT 01 RW 03 Desa Purbayasa",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                        }

                        if (currentRole != com.example.ui.UserRole.WARGA) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    .clickable { showMutasiDialog = true }
                                    .testTag("header_add_mutasi_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Input Mutasi",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Kas Selector Chips
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChipKas(
                            title = "Semua Kamar (${kamarKasList.size})",
                            isSelected = selectedKasId == "ALL",
                            onClick = { selectedKasId = "ALL" }
                        )
                    }
                    items(kamarKasList) { kas ->
                        FilterChipKas(
                            title = kas.namaKas,
                            isSelected = selectedKasId == kas.id,
                            onClick = { selectedKasId = kas.id }
                        )
                    }
                }
            }

            // Special Modul: Kas Air Whitelist / Amnesty Panel (If Kas Air is active/selected)
            if (selectedKasId == "ALL" || selectedKasId == "KAS_AIR") {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF86EFAC), Color(0xFF3B82F6))))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0284C7))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Kontrol Status Kran Air Bersih RT",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Tarif Rp5.000/bln saat hidup • Otomatis Rp0 saat air mati (Tutup)",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            wargaList.forEach { warga ->
                                val isAirHidup = warga.statusAir == "AKTIF" || warga.statusAir == "AIR_HIDUP"
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(warga.nama, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = Color(0xFF0F172A))
                                        Text(
                                            if (isAirHidup) "Status: AIR HIDUP (Mengalir)" else "Status: AIR MATI (Tutup)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isAirHidup) EmeraldSuccess else Color(0xFFD97706)
                                        )
                                        if (!isAirHidup) {
                                            Text(
                                                "Tagihan Bulan Ini: Rp0 (Bebas Iuran)",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // WhatsApp Reminder Button (active only if Air Hidup)
                                        IconButton(
                                            onClick = {
                                                if (isAirHidup) {
                                                    val msg = WhatsAppGateway.buildKasAirReminder(warga, "Agustus 2026", 5000L)
                                                    WhatsAppGateway.sendWhatsApp(context, warga.noHpWa, msg)
                                                }
                                            },
                                            enabled = isAirHidup,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Send,
                                                contentDescription = "Kirim WA Tagihan Air",
                                                tint = if (isAirHidup) EmeraldSuccess else Color.LightGray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Switch(
                                            checked = isAirHidup,
                                            onCheckedChange = {
                                                viewModel.toggleAmnestyAir(warga.id, warga.statusAir)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = EmeraldSuccess,
                                                checkedTrackColor = Color(0xFFBBF7D0),
                                                uncheckedThumbColor = Color(0xFF94A3B8),
                                                uncheckedTrackColor = Color(0xFFE2E8F0)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section: Daftar Mutasi Transaksi
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Riwayat Mutasi & Pembukuan Kas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Pencatatan kas masuk dan kas keluar",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (currentRole != com.example.ui.UserRole.WARGA) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary)
                                .clickable { showMutasiDialog = true }
                                .testTag("btn_input_mutasi_icon"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Input Mutasi",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            val filteredTrans = if (selectedKasId == "ALL") {
                transaksiList
            } else {
                transaksiList.filter { it.kamarKasId == selectedKasId }
            }

            if (filteredTrans.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Belum ada catatan mutasi untuk kamar kas ini", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(filteredTrans) { trx ->
                    TransaksiCardItem(
                        transaksi = trx,
                        canDelete = currentRole != com.example.ui.UserRole.WARGA,
                        onDeleteClick = { showDeleteConfirmDialog = trx }
                    )
                }
            }
        }
    }

    // Modal Input Mutasi
    if (showMutasiDialog) {
        InputMutasiDialog(
            kamarKasList = kamarKasList,
            wargaList = wargaList,
            onDismiss = { showMutasiDialog = false },
            onSubmit = { kamarId, jenis, nominal, kategori, keterangan, wargaId ->
                viewModel.catatMutasiUmum(kamarId, jenis, nominal, kategori, keterangan, wargaId)
                showMutasiDialog = false
            }
        )
    }

    // Modal Soft Delete Confirmation with Audit Reason
    if (showDeleteConfirmDialog != null) {
        val trx = showDeleteConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Hapus Mutasi (Soft Delete)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Data tidak akan hilang permanen melainkan masuk ke Log Jejak Audit. Transaksi: ${trx.kodeTransaksi} (${trx.keterangan})",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Alasan Pembatalan / Hapus:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = deleteReasonText,
                        onValueChange = { deleteReasonText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.softDeleteTransaksi(trx.id, deleteReasonText)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDanger)
                ) {
                    Text("Tandai Dihapus (Audit Log)")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun FilterChipKas(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) IndigoPrimary else Color.White,
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("filter_kas_${title}")
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color(0xFF334155),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun TransaksiCardItem(
    transaksi: TransaksiEntity,
    canDelete: Boolean,
    onDeleteClick: () -> Unit
) {
    val isMasuk = transaksi.jenisMutasi == "MASUK"
    val dateStr = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id", "ID")).format(Date(transaksi.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
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
                    .size(44.dp)
                    .background(if (isMasuk) Color(0xFFDCFCE7) else Color(0xFFFFE4E6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMasuk) Icons.Default.LocalAtm else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isMasuk) EmeraldSuccess else RoseDanger,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaksi.namaWarga ?: transaksi.kamarKasId,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = transaksi.kamarKasId,
                            fontSize = 9.5.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaksi.keterangan,
                    fontSize = 11.5.sp,
                    color = Color(0xFF475569)
                )
                Text(
                    text = "$dateStr • Petugas: ${transaksi.petugas}",
                    fontSize = 10.5.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isMasuk) "+" else "-"}Rp" + String.format(Locale.GERMANY, "%,d", transaksi.nominal),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (isMasuk) EmeraldSuccess else RoseDanger
                )

                if (canDelete) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Soft Delete",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputMutasiDialog(
    kamarKasList: List<KamarKasEntity>,
    wargaList: List<com.example.data.WargaEntity>,
    onDismiss: () -> Unit,
    onSubmit: (kamarKasId: String, jenis: String, nominal: Long, kategori: String, keterangan: String, wargaId: String?) -> Unit
) {
    var selectedKamar by remember { mutableStateOf(kamarKasList.firstOrNull()?.id ?: "KAS_JIMPITAN") }
    var jenisMutasi by remember { mutableStateOf("MASUK") }
    var nominalText by remember { mutableStateOf("") }
    var keteranganText by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Input Mutasi Kas Manual", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                // Jenis Mutasi Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { jenisMutasi = "MASUK" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (jenisMutasi == "MASUK") EmeraldSuccess else Color(0xFFE2E8F0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pemasukan (+)", color = if (jenisMutasi == "MASUK") Color.White else Color(0xFF334155), fontSize = 12.sp)
                    }
                    Button(
                        onClick = { jenisMutasi = "KELUAR" },
                        colors = ButtonDefaults.buttonColors(containerColor = if (jenisMutasi == "KELUAR") RoseDanger else Color(0xFFE2E8F0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pengeluaran (-)", color = if (jenisMutasi == "KELUAR") Color.White else Color(0xFF334155), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pilih Kamar Kas Dropdown
                Text("Pilih Kamar Kas Tujuan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedKamar,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        kamarKasList.forEach { kas ->
                            DropdownMenuItem(
                                text = { Text("${kas.namaKas} (${kas.id})") },
                                onClick = {
                                    selectedKamar = kas.id
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Nominal (Rp):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = nominalText,
                    onValueChange = { nominalText = it.filter { ch -> ch.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Contoh: 50000") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Keterangan Mutasi:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = keteranganText,
                    onValueChange = { keteranganText = it },
                    placeholder = { Text("Contoh: Pembelian kain mori / Sumbangan warga") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nom = nominalText.toLongOrNull() ?: 0L
                    if (nom > 0) {
                        onSubmit(selectedKamar, jenisMutasi, nom, selectedKamar, keteranganText.ifBlank { "Mutasi manual $jenisMutasi" }, null)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Simpan Mutasi")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
