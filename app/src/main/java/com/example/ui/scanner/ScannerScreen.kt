package com.example.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.WargaEntity
import com.example.ui.IuranQViewModel
import com.example.ui.theme.CardEmeraldGradient
import com.example.ui.theme.CardIndigoGradient
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TealAccent
import com.example.util.WhatsAppGateway
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Izin kamera diperlukan untuk scan QR stiker warga", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var isTorchOn by remember { mutableStateOf(false) }
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val scannerState by viewModel.scannerState.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            RealCameraPreview(
                onQrDetected = { qrCode ->
                    viewModel.openPaymentSheetForWargaId(qrCode)
                },
                isTorchOn = isTorchOn,
                onTorchChanged = { isTorchOn = it }
            )
        } else {
            // Permission request screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Camera Permission",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Akses Kamera Diperlukan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "IuranQ menggunakan kamera real untuk scan QR stiker door-to-door dan flash terintegrasi saat malam hari.",
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    modifier = Modifier.testTag("request_camera_button")
                ) {
                    Text("Izinkan Akses Kamera")
                }
            }
        }

        // Top Control Bar Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flash / Torch Toggle Button
            Surface(
                shape = CircleShape,
                color = if (isTorchOn) Color(0xFFF59E0B) else Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { isTorchOn = !isTorchOn }
                    .testTag("flash_toggle_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Toggle Flash",
                        tint = if (isTorchOn) Color.Black else Color.White
                    )
                }
            }

            // Scanner Title & Info
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.65f)),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(IndigoPrimary, TealAccent)))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Arahkan ke Stiker QR",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Offline Mode Switcher
            Surface(
                shape = CircleShape,
                color = if (isOfflineMode) Color(0xFFDC2626) else Color(0xFF059669),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.toggleOfflineMode() }
                    .testTag("offline_toggle_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isOfflineMode) Icons.Default.WifiOff else Icons.Default.Wifi,
                        contentDescription = "Offline Mode",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom Quick Warga Picker Bar (for quick test/selection in case QR is unreadable)
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.92f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilih Cepat Nama Warga (RT 01):",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (isOfflineMode) {
                        Text(
                            text = "OFFLINE MODE AKTIF",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(wargaList) { warga ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier
                                .clickable { viewModel.openPaymentSheetForWargaId(warga.id) }
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                .testTag("quick_warga_${warga.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = TealAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = warga.nama,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = warga.id,
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Sheet Modal
    if (scannerState.isOpen && scannerState.warga != null) {
        val warga = scannerState.warga!!
        ModalBottomSheet(
            onDismissRequest = { viewModel.closePaymentSheet() },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            PaymentSheetContent(
                warga = warga,
                viewModel = viewModel,
                onClose = { viewModel.closePaymentSheet() }
            )
        }
    }
}

@Composable
fun PaymentSheetContent(
    warga: WargaEntity,
    viewModel: IuranQViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var inputNominalText by remember { mutableStateOf("5000") }
    val totalNominal = inputNominalText.toLongOrNull() ?: 0L
    val targetJimpitan = 1000L

    val jimpitanPortion = if (totalNominal <= targetJimpitan) totalNominal else targetJimpitan
    val overflowPortion = if (totalNominal > targetJimpitan) totalNominal - targetJimpitan else 0L
    val estimasiTotalTabungan = warga.saldoTabungan + overflowPortion

    var paymentDone by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 32.dp)
    ) {
        // Warga Profile Card Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(CardIndigoGradient, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = warga.nama.take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = warga.nama,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "ID: ${warga.id} • RT ${warga.rt}/RW ${warga.rw}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = warga.alamat,
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569)
                    )
                }
                // Air Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (warga.statusAir == "AKTIF") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = if (warga.statusAir == "AKTIF") "Air: Aktif" else "Amnesti Air",
                        color = if (warga.statusAir == "AKTIF") Color(0xFF15803D) else Color(0xFFB45309),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!paymentDone) {
            Text(
                text = "Nominal Uang Diterima (Rp):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputNominalText,
                onValueChange = { inputNominalText = it.filter { ch -> ch.isDigit() } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_nominal_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                ),
                shape = RoundedCornerShape(14.dp),
                prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Nominal Buttons
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val nominalPresets = listOf(1000L, 2000L, 5000L, 10000L, 20000L, 50000L)
                items(nominalPresets) { nom ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (totalNominal == nom) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                        border = if (totalNominal == nom) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(IndigoPrimary, IndigoPrimary))) else CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .clickable { inputNominalText = nom.toString() }
                            .testTag("preset_${nom}")
                    ) {
                        Text(
                            text = "Rp" + String.format(Locale.GERMANY, "%,d", nom),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (totalNominal == nom) IndigoPrimary else Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overflow Calculation Visual Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sistem Alokasi Multi-Kamar (Overflow):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(EmeraldSuccess, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1. Kas Jimpitan RT:", fontSize = 13.sp, color = Color(0xFF475569))
                        }
                        Text(
                            text = "Rp" + String.format(Locale.GERMANY, "%,d", jimpitanPortion),
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(IndigoPrimary, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("2. Overflow Masuk Tabungan:", fontSize = 13.sp, color = Color(0xFF475569))
                        }
                        Text(
                            text = "+Rp" + String.format(Locale.GERMANY, "%,d", overflowPortion),
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE2E8F0)))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Saldo Tabungan Warga:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                        Text(
                            text = "Rp" + String.format(Locale.GERMANY, "%,d", estimasiTotalTabungan),
                            fontWeight = FontWeight.ExtraBold,
                            color = IndigoPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (totalNominal > 0) {
                        viewModel.submitJimpitanPayment(warga.id, totalNominal) { updatedWarga, total, jimp, over ->
                            paymentDone = true
                            // Auto open WhatsApp receipt
                            val msg = WhatsAppGateway.buildJimpitanReceipt(
                                warga = updatedWarga,
                                totalSetor = total,
                                jimpitan = jimp,
                                overflow = over,
                                totalSaldoTabungan = updatedWarga.saldoTabungan,
                                petugas = "Petugas Piket RT 01"
                            )
                            WhatsAppGateway.sendWhatsApp(context, updatedWarga.noHpWa, msg)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_payment_and_wa_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan & Kirim Struk WhatsApp", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    if (totalNominal > 0) {
                        viewModel.submitJimpitanPayment(warga.id, totalNominal) { _, _, _, _ ->
                            paymentDone = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("submit_payment_only_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Simpan Saja (Tanpa WhatsApp)")
            }
        } else {
            // Success view
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Transaksi Berhasil Dicatat!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Kas Jimpitan +Rp${String.format(Locale.GERMANY, "%,d", jimpitanPortion)} | Tabungan +Rp${String.format(Locale.GERMANY, "%,d", overflowPortion)}",
                    color = Color(0xFF475569),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Selesai / Scan Warga Lainnya")
                }
            }
        }
    }
}
