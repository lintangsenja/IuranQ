package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.IuranQViewModel
import com.example.ui.MainTab
import com.example.ui.UserRole
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.inventaris.KegiatanInventarisScreen
import com.example.ui.jimpitan.JimpitanTabunganScreen
import com.example.ui.kas.KasMultiKamarScreen
import com.example.ui.kurban.TabunganKurbanScreen
import com.example.ui.laporan.LaporanAuditScreen
import com.example.ui.piket.QrPiketScreen
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.LightBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.warga.WargaScreen

class MainActivity : ComponentActivity() {
    private val viewModel: IuranQViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                IuranQApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IuranQApp(viewModel: IuranQViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()

    var showMenuSheet by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            VibrantBottomBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.switchTab(it) },
                onMenuClick = { showMenuSheet = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBg)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    MainTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    MainTab.JIMPITAN -> JimpitanTabunganScreen(viewModel = viewModel)
                    MainTab.QR_PIKET -> QrPiketScreen(viewModel = viewModel)
                    MainTab.KAS_KAMAR -> KasMultiKamarScreen(viewModel = viewModel)
                    MainTab.WARGA -> WargaScreen(viewModel = viewModel)
                    MainTab.KURBAN -> TabunganKurbanScreen(viewModel = viewModel)
                    MainTab.KEGIATAN_INVENTARIS -> KegiatanInventarisScreen(viewModel = viewModel)
                    MainTab.LAPORAN -> LaporanAuditScreen(viewModel = viewModel)
                }
            }

            // Quick Role Switcher Floating Pill in Top-Right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .clickable { showRoleMenu = true }
                        .testTag("role_switcher_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(IndigoPrimary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentRole.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showRoleMenu,
                    onDismissRequest = { showRoleMenu = false }
                ) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = role.label,
                                        fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal,
                                        color = if (role == currentRole) IndigoPrimary else Color(0xFF1E293B)
                                    )
                                    if (role == currentRole) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = IndigoPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                viewModel.switchRole(role)
                                showRoleMenu = false
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet for Quick Navigation to all modules
    if (showMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Menu Lengkap IuranQ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Kelola seluruh keuangan & kegiatan RT 01 RW 03",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuTile(
                        title = "Data Warga",
                        subtitle = "Daftar KK, NIK & Status Air",
                        icon = Icons.Default.People,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.WARGA)
                        showMenuSheet = false
                    }

                    MenuTile(
                        title = "Kamar Kas",
                        subtitle = "Multi-Kamar & Mutasi",
                        icon = Icons.Default.AccountBalanceWallet,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.KAS_KAMAR)
                        showMenuSheet = false
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuTile(
                        title = "Tabungan Kurban",
                        subtitle = "Kelompok 7 Org/Sapi",
                        icon = Icons.Default.Pets,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.KURBAN)
                        showMenuSheet = false
                    }

                    MenuTile(
                        title = "Inventaris & Acara",
                        subtitle = "Peminjaman Alat RT",
                        icon = Icons.Default.Inventory2,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.KEGIATAN_INVENTARIS)
                        showMenuSheet = false
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MenuTile(
                        title = "Laporan & Audit",
                        subtitle = "Ekspor PDF/Excel RT",
                        icon = Icons.Default.Assessment,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.LAPORAN)
                        showMenuSheet = false
                    }

                    MenuTile(
                        title = "Manajemen QR & Piket",
                        subtitle = "Scan, Buat QR & Petugas",
                        icon = Icons.Default.QrCodeScanner,
                        color = Color(0xFFEC4899),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.QR_PIKET)
                        showMenuSheet = false
                    }
                }
            }
        }
    }
}

@Composable
fun VibrantBottomBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        // Bottom Bar Background Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    spotColor = Color(0x1A000000)
                ),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Beranda (Dashboard)
                VibrantNavItem(
                    label = "Beranda",
                    icon = Icons.Default.Home,
                    isSelected = currentTab == MainTab.DASHBOARD,
                    onClick = { onTabSelected(MainTab.DASHBOARD) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 2: Jimpitan & Tabungan
                VibrantNavItem(
                    label = "Jimpitan",
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = currentTab == MainTab.JIMPITAN,
                    onClick = { onTabSelected(MainTab.JIMPITAN) },
                    modifier = Modifier.weight(1f)
                )

                // Center Spacer for Raised FAB
                Spacer(modifier = Modifier.width(64.dp))

                // Tab 4: Kas RT / Air
                VibrantNavItem(
                    label = "Kas RT",
                    icon = Icons.Default.People,
                    isSelected = currentTab == MainTab.KAS_KAMAR,
                    onClick = { onTabSelected(MainTab.KAS_KAMAR) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 5: Menu / Laporan
                VibrantNavItem(
                    label = "Menu",
                    icon = Icons.Default.GridView,
                    isSelected = currentTab == MainTab.LAPORAN || currentTab == MainTab.KEGIATAN_INVENTARIS || currentTab == MainTab.KURBAN || currentTab == MainTab.WARGA,
                    onClick = onMenuClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center Raised Floating Action Button for Quick QR & Piket Scanner
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(62.dp)
                .clip(CircleShape)
                .border(4.dp, LightBg, CircleShape)
                .background(HeroGradient)
                .clickable { onTabSelected(MainTab.QR_PIKET) }
                .testTag("center_scan_fab"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Manajemen QR & Piket",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun VibrantNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6366F1).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF64748B).copy(alpha = 0.6f),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) IndigoPrimary else Color(0xFF64748B).copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MenuTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
