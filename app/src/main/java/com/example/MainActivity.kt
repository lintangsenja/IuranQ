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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Work
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
import com.example.ui.menu.BackupRestoreScreen
import com.example.ui.menu.ManajemenPenggunaScreen
import com.example.ui.menu.MasterJabatanScreen
import com.example.ui.menu.MenuUtamaScreen
import com.example.ui.menu.ProfilPengurusScreen
import com.example.ui.piket.QrPiketScreen
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.LightBg
import com.example.ui.theme.LivestockKurban
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                .padding(bottom = innerPadding.calculateBottomPadding())
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
                    MainTab.MENU_PENGATURAN -> MenuUtamaScreen(viewModel = viewModel)
                    MainTab.BACKUP_RESTORE -> BackupRestoreScreen(viewModel = viewModel)
                    MainTab.PROFIL_PENGURUS -> ProfilPengurusScreen(viewModel = viewModel)
                    MainTab.MASTER_JABATAN -> MasterJabatanScreen(viewModel = viewModel)
                    MainTab.MANAJEMEN_USER -> ManajemenPenggunaScreen(viewModel = viewModel)
                }
            }

            // Quick Role Switcher Floating Pill in Top-Right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 10.dp, end = 16.dp)
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
                    .padding(bottom = 36.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Menu & Pengaturan IuranQ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Kelola seluruh modul, profil, dan data RT 01 RW 03",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEEF2FF),
                        modifier = Modifier.clickable {
                            viewModel.switchTab(MainTab.MENU_PENGATURAN)
                            showMenuSheet = false
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buka Hub", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = IndigoPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Pengaturan & Master
                Text(
                    text = "PENGATURAN & MASTER DATA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.6.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuTile(
                        title = "Backup & Restore",
                        subtitle = "Ekspor/Impor .json",
                        icon = Icons.Default.Backup,
                        color = Color(0xFF4F46E5),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.BACKUP_RESTORE)
                        showMenuSheet = false
                    }

                    MenuTile(
                        title = "Profil Pengurus",
                        subtitle = "Struktur & WA RT",
                        icon = Icons.Default.Badge,
                        color = Color(0xFF059669),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.PROFIL_PENGURUS)
                        showMenuSheet = false
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuTile(
                        title = "Master Jabatan",
                        subtitle = "Jabatan Dinamis",
                        icon = Icons.Default.Work,
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.MASTER_JABATAN)
                        showMenuSheet = false
                    }

                    MenuTile(
                        title = "Manajemen Pengguna",
                        subtitle = "3 Tingkat Role",
                        icon = Icons.Default.ManageAccounts,
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.MANAJEMEN_USER)
                        showMenuSheet = false
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Modul Operasional RT
                Text(
                    text = "MODUL OPERASIONAL RT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.6.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuTile(
                        title = "Data Warga",
                        subtitle = "Daftar KK & NIK",
                        icon = Icons.Default.People,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.WARGA)
                        showMenuSheet = false
                    }

                    MenuTile(
                        title = "Kamar Kas",
                        subtitle = "Multi-Kamar Kas",
                        icon = Icons.Default.AccountBalanceWallet,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.switchTab(MainTab.KAS_KAMAR)
                        showMenuSheet = false
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuTile(
                        title = "Tabungan Kurban",
                        subtitle = "Kelompok 7 Org/Sapi",
                        icon = LivestockKurban,
                        color = Color(0xFFD97706),
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

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        subtitle = "Scan & Jadwal Ronda",
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
    val isMenuSelected = currentTab == MainTab.MENU_PENGATURAN ||
            currentTab == MainTab.BACKUP_RESTORE ||
            currentTab == MainTab.PROFIL_PENGURUS ||
            currentTab == MainTab.MASTER_JABATAN ||
            currentTab == MainTab.MANAJEMEN_USER ||
            currentTab == MainTab.LAPORAN ||
            currentTab == MainTab.KEGIATAN_INVENTARIS ||
            currentTab == MainTab.KURBAN ||
            currentTab == MainTab.WARGA

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(Color.Transparent)
    ) {
        // Bottom Bar Background Card (Super Slim & Compact: 56dp)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = Color(0x20000000)
                ),
            shape = RoundedCornerShape(18.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
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
                Spacer(modifier = Modifier.width(46.dp))

                // Tab 4: Kas RT / Air
                VibrantNavItem(
                    label = "Kas RT",
                    icon = Icons.Default.People,
                    isSelected = currentTab == MainTab.KAS_KAMAR,
                    onClick = { onTabSelected(MainTab.KAS_KAMAR) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 5: Menu / Pengaturan
                VibrantNavItem(
                    label = "Menu",
                    icon = Icons.Default.GridView,
                    isSelected = isMenuSelected,
                    onClick = { onTabSelected(MainTab.MENU_PENGATURAN) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center Raised Floating Action Button for Quick QR & Piket Scanner (Slim Proportional: 46dp)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp)
                .size(46.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .background(HeroGradient)
                .clickable { onTabSelected(MainTab.QR_PIKET) }
                .testTag("center_scan_fab"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Manajemen QR & Piket",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
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
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6366F1).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(15.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF64748B).copy(alpha = 0.65f),
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) IndigoPrimary else Color(0xFF64748B).copy(alpha = 0.8f),
            maxLines = 1
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
