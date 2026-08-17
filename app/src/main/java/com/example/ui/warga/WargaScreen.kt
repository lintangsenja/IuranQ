package com.example.ui.warga

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WargaEntity
import com.example.ui.IuranQViewModel
import com.example.ui.theme.CardIndigoGradient
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TealAccent
import com.example.util.QrCodeGenerator
import com.example.util.WhatsAppGateway
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WargaScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val wargaList by viewModel.wargaList.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedQrWarga by remember { mutableStateOf<WargaEntity?>(null) }
    var editingWarga by remember { mutableStateOf<WargaEntity?>(null) }
    var isNewWarga by remember { mutableStateOf(false) }

    val filteredWarga = wargaList.filter {
        it.nama.contains(searchQuery, ignoreCase = true) ||
                it.id.contains(searchQuery, ignoreCase = true) ||
                it.alamat.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (currentRole != com.example.ui.UserRole.WARGA) {
                FloatingActionButton(
                    onClick = {
                        val nextNumber = wargaList.size + 1
                        val newId = "WRG-" + String.format(Locale.getDefault(), "%03d", nextNumber)
                        editingWarga = WargaEntity(
                            id = newId,
                            noKk = "330305010100${String.format(Locale.getDefault(), "%04d", nextNumber)}",
                            nik = "330305101000${String.format(Locale.getDefault(), "%04d", nextNumber)}",
                            nama = "",
                            alamat = "RT 01 RW 03 Desa Purbayasa",
                            noHpWa = "081234567890",
                            statusAir = "AKTIF",
                            saldoTabungan = 0L,
                            rt = "01",
                            rw = "03"
                        )
                        isNewWarga = true
                    },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 72.dp)
                        .testTag("fab_tambah_warga")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Warga")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah Warga", fontWeight = FontWeight.Bold)
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
                            text = "Database Kependudukan & QR",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Daftar Kepala Keluarga RT 01 RW 03 Desa Purbayasa & Cetak Stiker QR",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama, ID Warga, atau alamat...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Warga Cards List
            items(filteredWarga) { warga ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
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
                        // Avatar / Initials
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(CardIndigoGradient, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = warga.nama.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = warga.nama,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEEF2FF)
                                ) {
                                    Text(
                                        text = warga.id,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tabungan Overflow: Rp${String.format(Locale.GERMANY, "%,d", warga.saldoTabungan)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = "${warga.alamat} • WA: ${warga.noHpWa}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Actions
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // QR Stiker Button
                            IconButton(
                                onClick = { selectedQrWarga = warga },
                                modifier = Modifier.size(36.dp).testTag("qr_btn_${warga.id}")
                            ) {
                                Icon(Icons.Default.QrCode2, contentDescription = "Lihat Stiker QR", tint = IndigoPrimary)
                            }

                            // Edit Button
                            if (currentRole != com.example.ui.UserRole.WARGA) {
                                IconButton(
                                    onClick = {
                                        editingWarga = warga
                                        isNewWarga = false
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Warga", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Stiker QR Code Warga
    if (selectedQrWarga != null) {
        val qrWarga = selectedQrWarga!!
        val qrBitmap = remember(qrWarga.id) {
            QrCodeGenerator.generateQrBitmap("IURANQ:${qrWarga.id}", 600)
        }

        AlertDialog(
            onDismissRequest = { selectedQrWarga = null },
            title = {
                Text(
                    text = "Stiker QR Door-to-Door Warga",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "IuranQ - RT 01 RW 03",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = IndigoPrimary
                            )
                            Text(
                                text = "DESA PURBAYASA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "QR Code Stiker",
                                    modifier = Modifier
                                        .size(190.dp)
                                        .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = qrWarga.nama,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "ID: ${qrWarga.id}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = qrWarga.alamat,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareText = "Stiker QR Code Pembayaran IuranQ:\nNama: ${qrWarga.nama}\nID: ${qrWarga.id}\nKode QR: IURANQ:${qrWarga.id}\nRT 01 RW 03 Desa Purbayasa"
                        WhatsAppGateway.sendWhatsApp(context, qrWarga.noHpWa, shareText)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kirim ke WA")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedQrWarga = null }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Modal Add / Edit Warga
    if (editingWarga != null) {
        var formWarga by remember { mutableStateOf(editingWarga!!) }
        AlertDialog(
            onDismissRequest = { editingWarga = null },
            title = { Text(if (isNewWarga) "Tambah Warga Baru" else "Edit Data Warga", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = formWarga.nama,
                        onValueChange = { formWarga = formWarga.copy(nama = it) },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formWarga.noHpWa,
                        onValueChange = { formWarga = formWarga.copy(noHpWa = it) },
                        label = { Text("Nomor WhatsApp") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formWarga.alamat,
                        onValueChange = { formWarga = formWarga.copy(alamat = it) },
                        label = { Text("Alamat Rumah") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formWarga.nama.isNotBlank()) {
                            viewModel.simpanWarga(formWarga, isNewWarga)
                            editingWarga = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Simpan Data")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingWarga = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
