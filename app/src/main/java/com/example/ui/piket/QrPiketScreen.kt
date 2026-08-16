package com.example.ui.piket

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.shadow
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
import androidx.core.content.ContextCompat
import com.example.data.PetugasEntity
import com.example.data.WargaEntity
import com.example.ui.IuranQViewModel
import com.example.ui.scanner.RealCameraPreview
import com.example.ui.theme.CardIndigoGradient
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TealAccent
import com.example.util.QrCodeGenerator
import com.example.util.WhatsAppGateway
import java.util.Locale

enum class QrPiketSubMenu(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SCAN_QR("Scan QR", Icons.Default.QrCodeScanner),
    GENERATE_QR("Generate QR", Icons.Default.QrCode),
    LIST_PETUGAS("List Petugas", Icons.Default.Group)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrPiketScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSubMenu by remember { mutableStateOf(QrPiketSubMenu.SCAN_QR) }
    val scannerState by viewModel.scannerState.collectAsState()
    val petugasList by viewModel.petugasList.collectAsState()
    val defaultPetugas = petugasList.find { it.isDefaultAktif }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroGradient)
                .padding(top = 16.dp, bottom = 12.dp, start = 18.dp, end = 18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Manajemen QR & Piket",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "RT 01 RW 03 Desa Purbayasa",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }

                    // Active Officer Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.2f))))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF34D399), CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = defaultPetugas?.nama?.take(14) ?: "Petugas Piket",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom 3-Tab Segmented Control
                TabRow(
                    selectedTabIndex = selectedSubMenu.ordinal,
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedSubMenu.ordinal]),
                            height = 3.dp,
                            color = Color(0xFFFCD34D)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    QrPiketSubMenu.values().forEach { tab ->
                        Tab(
                            selected = selectedSubMenu == tab,
                            onClick = { selectedSubMenu = tab },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(tab.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(tab.label, fontWeight = if (selectedSubMenu == tab) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Body Content based on selected Sub-Menu
        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubMenu) {
                QrPiketSubMenu.SCAN_QR -> ScanQrSubView(viewModel = viewModel)
                QrPiketSubMenu.GENERATE_QR -> GenerateQrSubView(viewModel = viewModel)
                QrPiketSubMenu.LIST_PETUGAS -> ListPetugasSubView(viewModel = viewModel)
            }
        }
    }

    // Payment Sheet Modal (Granular Dual-Form: Jimpitan Wajib + Tabungan Pribadi)
    if (scannerState.isOpen && scannerState.warga != null) {
        val warga = scannerState.warga!!
        ModalBottomSheet(
            onDismissRequest = { viewModel.closePaymentSheet() },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            DualFormPaymentSheetContent(
                warga = warga,
                defaultPetugas = defaultPetugas,
                viewModel = viewModel,
                onClose = { viewModel.closePaymentSheet() }
            )
        }
    }
}

// --------------------------------------------------------------------------------------
// SUB-VIEW 1: SCAN QR (Camera Scanner + Torch + Quick Selector Fallback)
// --------------------------------------------------------------------------------------
@Composable
fun ScanQrSubView(viewModel: IuranQViewModel) {
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
    val wargaList by viewModel.wargaList.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            RealCameraPreview(
                onQrDetected = { qrCode ->
                    viewModel.openPaymentSheetForWargaId(qrCode)
                },
                isTorchOn = isTorchOn,
                onTorchChanged = { isTorchOn = it }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Akses Kamera Diperlukan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Arahkan kamera ke stiker QR di pintu rumah warga untuk membuka form jimpitan instan.",
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Aktifkan Kamera")
                }
            }
        }

        // Top Scanner Floating Controls (Torch & Offline Mode)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isTorchOn) Color(0xFFF59E0B) else Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { isTorchOn = !isTorchOn }
                    .testTag("flash_toggle_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flashlight",
                        tint = if (isTorchOn) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Arahkan ke Stiker QR Pintu Warga",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Surface(
                shape = CircleShape,
                color = if (isOfflineMode) Color(0xFFDC2626) else Color(0xFF059669),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.toggleOfflineMode() }
                    .testTag("offline_toggle_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isOfflineMode) Icons.Default.WifiOff else Icons.Default.Wifi,
                        contentDescription = "Offline Mode",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Bottom Quick Warga Fallback Picker
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.94f)),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Atau Pilih Cepat Warga:",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${wargaList.size} Warga Terdaftar",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
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
}

// --------------------------------------------------------------------------------------
// DUAL-FORM PAYMENT MODAL: Form 1 Jimpitan Wajib + Form 2 Tabungan Pribadi
// --------------------------------------------------------------------------------------
@Composable
fun DualFormPaymentSheetContent(
    warga: WargaEntity,
    defaultPetugas: PetugasEntity?,
    viewModel: IuranQViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    // Form 1: Jimpitan Wajib (Kas RT) -> Default 1000
    var jimpitanText by remember { mutableStateOf("1000") }
    // Form 2: Tabungan Pribadi (Optional / Overflow) -> Default 4000
    var tabunganText by remember { mutableStateOf("4000") }

    val nominalJimpitan = jimpitanText.toLongOrNull() ?: 0L
    val nominalTabungan = tabunganText.toLongOrNull() ?: 0L
    val totalSetor = nominalJimpitan + nominalTabungan
    val estimasiTotalSaldo = warga.saldoTabungan + nominalTabungan

    val isAirHidup = warga.statusAir == "AKTIF" || warga.statusAir == "AIR_HIDUP"
    var isSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp)
            .padding(bottom = 32.dp)
    ) {
        // Warga Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    Text(
                        text = warga.nama,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "ID: ${warga.id} • ${warga.alamat}",
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569)
                    )
                }

                // Air Status Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAirHidup) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = if (isAirHidup) "AIR HIDUP" else "AIR MATI",
                        color = if (isAirHidup) Color(0xFF15803D) else Color(0xFFB45309),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!isSubmitted) {
            // Dual-Form Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Form 1: Jimpitan Wajib
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "1. Jimpitan Wajib (RT):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF059669)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = jimpitanText,
                        onValueChange = { jimpitanText = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_jimpitan_wajib"),
                        shape = RoundedCornerShape(12.dp),
                        prefix = { Text("Rp", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        singleLine = true
                    )
                }

                // Form 2: Tabungan Pribadi
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "2. Tabungan Warga:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = tabunganText,
                        onValueChange = { tabunganText = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_tabungan_pribadi"),
                        shape = RoundedCornerShape(12.dp),
                        prefix = { Text("Rp", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Shortcut Chips for Total Setor
            Text(
                text = "Preset Cepat Total Uang:",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val presets = listOf(1000L, 2000L, 5000L, 10000L, 20000L, 50000L)
                items(presets) { presetTotal ->
                    val jimpitanPart = if (presetTotal >= 1000L) 1000L else presetTotal
                    val tabunganPart = if (presetTotal > 1000L) presetTotal - 1000L else 0L
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (totalSetor == presetTotal) Color(0xFFEEF2FF) else Color(0xFFF1F5F9),
                        border = if (totalSetor == presetTotal) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(IndigoPrimary, IndigoPrimary))) else null,
                        modifier = Modifier
                            .clickable {
                                jimpitanText = jimpitanPart.toString()
                                tabunganText = tabunganPart.toString()
                            }
                    ) {
                        Text(
                            text = "Rp" + String.format(Locale.GERMANY, "%,d", presetTotal),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (totalSetor == presetTotal) IndigoPrimary else Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Breakdown Calculation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Alokasi Kas Jimpitan RT:", fontSize = 12.sp, color = Color(0xFF475569))
                        Text("Rp" + String.format(Locale.GERMANY, "%,d", nominalJimpitan), fontWeight = FontWeight.Bold, color = Color(0xFF059669), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Alokasi Tabungan Warga:", fontSize = 12.sp, color = Color(0xFF475569))
                        Text("+Rp" + String.format(Locale.GERMANY, "%,d", nominalTabungan), fontWeight = FontWeight.Bold, color = IndigoPrimary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE2E8F0)))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Uang Diterima:", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("Rp" + String.format(Locale.GERMANY, "%,d", totalSetor), fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Akumulasi Saldo Tabungan:", fontSize = 11.5.sp, color = Color(0xFF64748B))
                        Text("Rp" + String.format(Locale.GERMANY, "%,d", estimasiTotalSaldo), fontWeight = FontWeight.SemiBold, color = IndigoPrimary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (totalSetor > 0) {
                        viewModel.submitDualJimpitanPayment(warga.id, nominalJimpitan, nominalTabungan) { updatedWarga, total, jimp, over ->
                            isSubmitted = true
                            val msg = WhatsAppGateway.buildJimpitanReceipt(
                                warga = updatedWarga,
                                totalSetor = total,
                                jimpitan = jimp,
                                overflow = over,
                                totalSaldoTabungan = updatedWarga.saldoTabungan,
                                petugas = defaultPetugas?.nama ?: "Petugas Piket RT 01"
                            )
                            WhatsAppGateway.sendWhatsApp(context, updatedWarga.noHpWa, msg)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_dual_payment_wa"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan & Kirim Struk WhatsApp", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    if (totalSetor > 0) {
                        viewModel.submitDualJimpitanPayment(warga.id, nominalJimpitan, nominalTabungan) { _, _, _, _ ->
                            isSubmitted = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("submit_dual_payment_only"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Simpan Saja (Cepat)")
            }
        } else {
            // Success Confirmation View
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(56.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("Setoran Berhasil Disimpan!", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kas Jimpitan +Rp${String.format(Locale.GERMANY, "%,d", nominalJimpitan)} • Tabungan +Rp${String.format(Locale.GERMANY, "%,d", nominalTabungan)}",
                    color = Color(0xFF475569),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Selesai / Scan Berikutnya")
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SUB-VIEW 2: GENERATE QR (Pilih Warga / Input Warga Baru + Interactive Door Sticker)
// --------------------------------------------------------------------------------------
@Composable
fun GenerateQrSubView(viewModel: IuranQViewModel) {
    val context = LocalContext.current
    val wargaList by viewModel.wargaList.collectAsState()

    var generateMode by remember { mutableStateOf(0) } // 0 = Pilih Warga Terdaftar, 1 = Input Warga Baru
    var selectedWargaId by remember { mutableStateOf(wargaList.firstOrNull()?.id ?: "WRG-001") }

    // Input Warga Baru Fields
    var namaBaru by remember { mutableStateOf("") }
    var alamatBaru by remember { mutableStateOf("") }
    var noKkBaru by remember { mutableStateOf("") }
    var nikBaru by remember { mutableStateOf("") }
    var noHpBaru by remember { mutableStateOf("") }
    var statusAirBaru by remember { mutableStateOf("AIR_HIDUP") }

    val activeWarga = wargaList.find { it.id == selectedWargaId } ?: wargaList.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Toggle Source: Pilih Warga Terdaftar vs Input Warga Baru
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (generateMode == 0) IndigoPrimary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { generateMode = 0 }
                            .testTag("tab_pilih_warga")
                    ) {
                        Text(
                            text = "Pilih Warga Terdaftar",
                            color = if (generateMode == 0) Color.White else Color(0xFF475569),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (generateMode == 1) IndigoPrimary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { generateMode = 1 }
                            .testTag("tab_input_warga_baru")
                    ) {
                        Text(
                            text = "+ Input Warga Baru",
                            color = if (generateMode == 1) Color.White else Color(0xFF475569),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }

        if (generateMode == 0) {
            // Mode 0: Pilih dari warga terdaftar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Pilih Warga untuk Digenerate Stiker QR:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(wargaList) { w ->
                                val isSel = w.id == selectedWargaId
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSel) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                                    border = if (isSel) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(IndigoPrimary, IndigoPrimary))) else CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.clickable { selectedWargaId = w.id }
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                        Text(w.nama, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isSel) IndigoPrimary else Color(0xFF1E293B))
                                        Text(w.id, fontSize = 10.sp, color = Color(0xFF64748B))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Mode 1: Form Input Warga Baru
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Form Pendaftaran Warga Baru & Buat QR:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = namaBaru,
                            onValueChange = { namaBaru = it },
                            label = { Text("Nama Kepala Keluarga (KK)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = alamatBaru,
                            onValueChange = { alamatBaru = it },
                            label = { Text("Alamat / No. Rumah") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = noHpBaru,
                                onValueChange = { noHpBaru = it },
                                label = { Text("No. WhatsApp") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = noKkBaru,
                                onValueChange = { noKkBaru = it },
                                label = { Text("No. KK") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Air Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Status Aliran Kran Air:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    if (statusAirBaru == "AIR_HIDUP") "AIR HIDUP (Mengalir)" else "AIR MATI (Tutup)",
                                    fontSize = 11.sp,
                                    color = if (statusAirBaru == "AIR_HIDUP") EmeraldSuccess else Color(0xFFD97706)
                                )
                            }
                            Switch(
                                checked = statusAirBaru == "AIR_HIDUP",
                                onCheckedChange = { statusAirBaru = if (it) "AIR_HIDUP" else "AIR_MATI" }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (namaBaru.isNotBlank() && alamatBaru.isNotBlank()) {
                                    val newId = "WRG-" + String.format(Locale.getDefault(), "%03d", wargaList.size + 1)
                                    val newWarga = WargaEntity(
                                        id = newId,
                                        nama = namaBaru,
                                        alamat = alamatBaru,
                                        noHpWa = noHpBaru.ifBlank { "08123456789" },
                                        noKk = noKkBaru.ifBlank { "3303010000000001" },
                                        nik = nikBaru.ifBlank { "3303010000000001" },
                                        statusAir = statusAirBaru,
                                        saldoTabungan = 0L
                                    )
                                    viewModel.simpanWarga(newWarga, isNew = true)
                                    selectedWargaId = newId
                                    generateMode = 0
                                    Toast.makeText(context, "Warga berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Nama dan alamat wajib diisi", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Warga & Generate QR", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Preview Stiker Pintu QR Code Canvas
        if (activeWarga != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF818CF8), Color(0xFFC084FC))))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Door Sticker Top Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81))))
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "RUKUN TETANGGA 01 / RW 03",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "DESA PURBAYASA • STIKER RESMI JIMPITAN",
                                    color = Color(0xFFFCD34D),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Code Bitmap Display
                        val qrContent = "IURANQ:${activeWarga.id}"
                        val qrBitmap: Bitmap? = remember(qrContent) {
                            QrCodeGenerator.generateQrBitmap(qrContent, 400)
                        }

                        if (qrBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(3.dp, Color(0xFF1E1B4B), RoundedCornerShape(16.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "QR Stiker Warga",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = activeWarga.nama,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = activeWarga.alamat,
                            fontSize = 12.5.sp,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEEF2FF)
                        ) {
                            Text(
                                text = "KODE ID: ${activeWarga.id}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Sticker Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val msg = WhatsAppGateway.buildQrStickerShareMessage(activeWarga)
                                    WhatsAppGateway.sendWhatsApp(context, activeWarga.noHpWa, msg)
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kirim WA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("IuranQ QR", "IURANQ:${activeWarga.id}")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Kode QR disalin: IURANQ:${activeWarga.id}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Salin Kode", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SUB-VIEW 3: LIST PETUGAS (CRUD Petugas Piket + Toggle Default Petugas Aktif)
// --------------------------------------------------------------------------------------
@Composable
fun ListPetugasSubView(viewModel: IuranQViewModel) {
    val context = LocalContext.current
    val petugasList by viewModel.petugasList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPetugas by remember { mutableStateOf<PetugasEntity?>(null) }
    var deleteConfirmPetugas by remember { mutableStateOf<PetugasEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary & Add Button Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daftar Petugas Ronda / Piket",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Petugas aktif otomatis dicatat di transaksi & audit log",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            modifier = Modifier.testTag("add_petugas_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // List of Petugas
        items(petugasList) { petugas ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (petugas.isDefaultAktif) Color(0xFFF0FDF4) else Color.White
                ),
                border = if (petugas.isDefaultAktif) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF86EFAC), Color(0xFF10B981)))) else CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (petugas.isDefaultAktif) EmeraldSuccess else Color(0xFF64748B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = petugas.nama,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )
                            if (petugas.isDefaultAktif) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "AKTIF",
                                        color = Color(0xFF15803D),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Jadwal: ${petugas.jadwalPiket} • HP: ${petugas.noHp}",
                            fontSize = 11.5.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    // Action Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Switch / Toggle to set as default active
                        IconButton(
                            onClick = { viewModel.setDefaultPetugas(petugas.id) },
                            modifier = Modifier.size(36.dp).testTag("set_active_petugas_${petugas.id}")
                        ) {
                            Icon(
                                imageVector = if (petugas.isDefaultAktif) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Set Aktif",
                                tint = if (petugas.isDefaultAktif) EmeraldSuccess else Color(0xFF94A3B8)
                            )
                        }

                        IconButton(
                            onClick = { editingPetugas = petugas },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { deleteConfirmPetugas = petugas },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = RoseDanger, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal Add / Edit Petugas Dialog
    if (showAddDialog || editingPetugas != null) {
        val isEdit = editingPetugas != null
        var formNama by remember { mutableStateOf(editingPetugas?.nama ?: "") }
        var formNoHp by remember { mutableStateOf(editingPetugas?.noHp ?: "") }
        var formJadwal by remember { mutableStateOf(editingPetugas?.jadwalPiket ?: "Senin Malam") }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingPetugas = null
            },
            title = {
                Text(
                    text = if (isEdit) "Edit Data Petugas Piket" else "Tambah Petugas Piket Baru",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = formNama,
                        onValueChange = { formNama = it },
                        label = { Text("Nama Petugas / Bpk.") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = formNoHp,
                        onValueChange = { formNoHp = it },
                        label = { Text("Nomor WhatsApp") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = formJadwal,
                        onValueChange = { formJadwal = it },
                        label = { Text("Jadwal Piket (Contoh: Senin Malam)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formNama.isNotBlank()) {
                            val newPetugas = PetugasEntity(
                                id = editingPetugas?.id ?: 0L,
                                nama = formNama,
                                noHp = formNoHp,
                                jadwalPiket = formJadwal,
                                isDefaultAktif = editingPetugas?.isDefaultAktif ?: false
                            )
                            viewModel.simpanPetugas(newPetugas, isNew = !isEdit)
                            showAddDialog = false
                            editingPetugas = null
                            Toast.makeText(context, "Data petugas berhasil disimpan", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAddDialog = false
                    editingPetugas = null
                }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal Delete Petugas Confirmation
    if (deleteConfirmPetugas != null) {
        val pet = deleteConfirmPetugas!!
        AlertDialog(
            onDismissRequest = { deleteConfirmPetugas = null },
            title = { Text("Hapus Petugas", fontWeight = FontWeight.Bold) },
            text = {
                Text("Apakah Anda yakin ingin menghapus petugas ${pet.nama}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hapusPetugas(pet.id)
                        deleteConfirmPetugas = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDanger)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteConfirmPetugas = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
