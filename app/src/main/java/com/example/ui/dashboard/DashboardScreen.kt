package com.example.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KamarKasEntity
import com.example.ui.IuranQViewModel
import com.example.ui.MainTab
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CardAmberGradient
import com.example.ui.theme.CardCyanGradient
import com.example.ui.theme.CardEmeraldGradient
import com.example.ui.theme.CardIndigoGradient
import com.example.ui.theme.CardRoseGradient
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroDarkGradient
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.LightBg
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TealAccent
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val kamarKasList by viewModel.kamarKasList.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val kelompokKurbanList by viewModel.kelompokKurbanList.collectAsState()
    val anggotaKurbanList by viewModel.anggotaKurbanList.collectAsState()
    val transaksiList by viewModel.transaksiList.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val appName by viewModel.appName.collectAsState()
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()

    val totalKasSemua = kamarKasList.sumOf { it.saldoTotal }
    val totalTabunganWarga = wargaList.sumOf { it.saldoTabungan }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(bottom = 90.dp)
    ) {
        // 1. Vibrant Palette Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(HeroGradient)
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 44.dp)
            ) {
                Column {
                    // Top App Header & Role Profile Avatar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RT 1 RW 3 Purbayasa",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Halo, ${currentRole.label}",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Avatar Box with border & amber circle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFFCD34D), CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Glassmorphic Balance Metric Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(28.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "TOTAL SALDO KAS RT",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Rp " + String.format(Locale.GERMANY, "%,d", totalKasSemua),
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFF34D399)
                                ) {
                                    Text(
                                        text = "+12% BLN INI",
                                        color = Color(0xFF064E3B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Target Progress Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                ) {
                                    val progressFraction = (totalKasSemua.toFloat() / 18_000_000f).coerceIn(0f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(if (progressFraction > 0f) progressFraction else 0.68f)
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399))
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Target: 18jt",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Overlapping 7-Day Jimpitan Trend Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-18).dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tren Jimpitan (7 Hari)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEEF2FF)
                            ) {
                                Text(
                                    text = "UPDATE 2J LALU",
                                    color = IndigoPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        VibrantWeeklyBarChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }
                }
            }
        }

        // 3. Vibrant 4-Column Action Grid
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Jimpitan (Orange)
                VibrantActionTile(
                    label = "Jimpitan",
                    iconColor = Color(0xFFF97316),
                    iconBgColor = Color(0xFFFFEDD5),
                    icon = Icons.Default.Savings,
                    modifier = Modifier.weight(1f).testTag("action_jimpitan")
                ) {
                    viewModel.switchTab(MainTab.JIMPITAN)
                }

                // Kas Air (Blue)
                VibrantActionTile(
                    label = "Kas Air",
                    iconColor = Color(0xFF3B82F6),
                    iconBgColor = Color(0xFFDBEAFE),
                    icon = Icons.Default.WaterDrop,
                    modifier = Modifier.weight(1f).testTag("action_kas_air")
                ) {
                    viewModel.switchTab(MainTab.KAS_KAMAR)
                }

                // Kurban (Emerald)
                VibrantActionTile(
                    label = "Kurban",
                    iconColor = Color(0xFF10B981),
                    iconBgColor = Color(0xFFD1FAE5),
                    icon = Icons.Default.Pets,
                    modifier = Modifier.weight(1f).testTag("action_kurban")
                ) {
                    viewModel.switchTab(MainTab.KURBAN)
                }

                // Sosial (Rose)
                VibrantActionTile(
                    label = "Sosial",
                    iconColor = Color(0xFFF43F5E),
                    iconBgColor = Color(0xFFFFE4E6),
                    icon = Icons.Default.LocalAtm,
                    modifier = Modifier.weight(1f).testTag("action_sosial")
                ) {
                    viewModel.switchTab(MainTab.KAS_KAMAR)
                }
            }
        }

        // 4. Dark Slate Offline / Realtime Status Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { viewModel.toggleOfflineMode() }
                    .testTag("dark_status_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeroDarkGradient)
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isOfflineMode) Icons.Default.WifiOff else Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isOfflineMode) "Offline Mode Aktif" else "Online RT Database",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (unsyncedCount > 0) "$unsyncedCount Data tersimpan di HP" else "Semua data kas tersinkronisasi",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Pulse Dot Indicator
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (isOfflineMode) Color(0xFFF59E0B) else Color(0xFF34D399), CircleShape)
                            )
                        }
                    }
                }
            }
        }

        // 4. Tabungan Kurban Circular / Donut Progress Cards
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Progress Tabungan Kurban (Max 7 Org/Sapi)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Target Rp21.000.000 / Ekor (Rp3.000.000/org)",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    kelompokKurbanList.forEach { kelompok ->
                        val anggotaList = anggotaKurbanList.filter { it.kelompokId == kelompok.id }
                        val terkumpulKelompok = anggotaList.sumOf { it.terkumpul }
                        val targetKelompok = kelompok.targetHargaPerEkor
                        val persenKelompok = if (targetKelompok > 0) (terkumpulKelompok.toFloat() / targetKelompok.toFloat()) else 0f

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFDE68A), Color(0xFFFBBF24))))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Circular Donut Chart
                                Box(
                                    modifier = Modifier.size(56.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressDonut(
                                        progress = persenKelompok,
                                        color = AmberWarning,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Text(
                                        text = "${(persenKelompok * 100).toInt()}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF92400E)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = kelompok.namaKelompok,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF78350F)
                                    )
                                    Text(
                                        text = "Terkumpul: Rp${String.format(Locale.GERMANY, "%,d", terkumpulKelompok)} / Rp${String.format(Locale.GERMANY, "%,d", targetKelompok)}",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "Peserta: ${anggotaList.size}/7 Orang",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Multi-Kamar Kas Overview Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Multi-Kamar Kas Berelasi RT 01",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(10.dp))

                kamarKasList.chunked(2).forEach { rowKas ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowKas.forEach { kas ->
                            KamarKasMiniCard(
                                kas = kas,
                                modifier = Modifier.weight(1f)
                            ) {
                                viewModel.switchTab(MainTab.KAS_KAMAR)
                            }
                        }
                        if (rowKas.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VibrantActionTile(
    label: String,
    iconColor: Color,
    iconBgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = Color(0xFF475569),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun VibrantWeeklyBarChart(modifier: Modifier = Modifier) {
    var selectedDayIndex by remember { mutableStateOf(4) } // Default active day: Jum (index 4)
    val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val heights = listOf(0.40f, 0.60f, 0.45f, 0.80f, 1.00f, 0.65f, 0.30f)
    val dayAmounts = listOf(42_000L, 58_000L, 46_000L, 78_000L, 95_000L, 64_000L, 35_000L)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                val fraction = heights[index]
                val isSelected = index == selectedDayIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedDayIndex = index }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .fillMaxHeight(fraction)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF818CF8), Color(0xFF6366F1))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFFE0E7FF), Color(0xFFC7D2FE))
                                    )
                                }
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, day ->
                val isSelected = index == selectedDayIndex
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) IndigoPrimary else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(vertical = 12.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InteractiveJimpitanBarChart(modifier: Modifier = Modifier) {
    var selectedMonthIndex by remember { mutableStateOf(5) } // default active month: Agustus

    val months = listOf("Mar", "Apr", "Mei", "Jun", "Jul", "Agt")
    val jimpitanAmounts = listOf(280_000L, 310_000L, 295_000L, 340_000L, 325_000L, 365_000L)
    val tabunganOverflow = listOf(140_000L, 190_000L, 160_000L, 220_000L, 210_000L, 260_000L)

    val maxAmount = 400_000f

    Column(modifier = modifier) {
        // Selected Month Tooltip Card
        val selectedMonth = months[selectedMonthIndex]
        val selectedJmp = jimpitanAmounts[selectedMonthIndex]
        val selectedTab = tabunganOverflow[selectedMonthIndex]

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bulan $selectedMonth 2026:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = IndigoPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Jimpitan: Rp${String.format(Locale.GERMANY, "%,d", selectedJmp)}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldSuccess
                )
                Text(
                    text = "Tabungan: Rp${String.format(Locale.GERMANY, "%,d", selectedTab)}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = IndigoPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Interactive Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val slotWidth = size.width / months.size
                        val index = (offset.x / slotWidth).toInt().coerceIn(0, months.size - 1)
                        selectedMonthIndex = index
                    }
                }
        ) {
            val barWidth = 18.dp.toPx()
            val spacing = size.width / months.size
            val chartBottom = size.height - 24.dp.toPx()

            // Draw horizontal reference lines
            val lineCount = 3
            for (i in 0..lineCount) {
                val y = chartBottom - (chartBottom / lineCount) * i
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            months.forEachIndexed { i, _ ->
                val x = spacing * i + (spacing - barWidth) / 2f
                val jmpHeight = (jimpitanAmounts[i] / maxAmount) * (chartBottom - 10.dp.toPx())
                val isSelected = i == selectedMonthIndex

                // Background highlight for selected column
                if (isSelected) {
                    drawRoundRect(
                        color = Color(0xFF6366F1).copy(alpha = 0.08f),
                        topLeft = Offset(spacing * i, 0f),
                        size = Size(spacing, chartBottom),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }

                // Jimpitan Bar (Emerald Gradient)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            if (isSelected) Color(0xFF059669) else Color(0xFF34D399),
                            if (isSelected) Color(0xFF047857) else Color(0xFF10B981)
                        )
                    ),
                    topLeft = Offset(x, chartBottom - jmpHeight),
                    size = Size(barWidth, jmpHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }

        // X Axis Month Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            months.forEachIndexed { idx, m ->
                Text(
                    text = m,
                    fontSize = 11.sp,
                    fontWeight = if (idx == selectedMonthIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (idx == selectedMonthIndex) IndigoPrimary else Color(0xFF64748B),
                    modifier = Modifier.clickable { selectedMonthIndex = idx }
                )
            }
        }
    }
}

@Composable
fun CircularProgressDonut(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 5.5.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)

        // Track
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )

        // Progress
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun KamarKasMiniCard(
    kas: KamarKasEntity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            try {
                                Color(android.graphics.Color.parseColor(kas.colorHex))
                            } catch (e: Exception) {
                                IndigoPrimary
                            },
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = kas.namaKas,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Rp" + String.format(Locale.GERMANY, "%,d", kas.saldoTotal),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = kas.id,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
