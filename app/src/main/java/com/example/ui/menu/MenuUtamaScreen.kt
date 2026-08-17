package com.example.ui.menu

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.CardEmeraldGradient
import com.example.ui.theme.CardIndigoGradient
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.LivestockKurban
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuUtamaScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val pengurusList by viewModel.pengurusList.collectAsState()
    val jabatanList by viewModel.jabatanList.collectAsState()
    val backupList by viewModel.backupHistoryList.collectAsState()
    val usersList by viewModel.userAccountList.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // 1. Header Hero
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeroGradient)
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Menu & Pengaturan",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "IuranQ RT 01 RW 03 Desa Purbayasa",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentRole.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Profil Ringkas Pengurus Kunci Card
                        val ketuaRt = pengurusList.find { it.jabatan.contains("Ketua RT", ignoreCase = true) }
                            ?: pengurusList.firstOrNull()

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color.White.copy(alpha = 0.12f)
                            )
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
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SupervisorAccount,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ketuaRt?.nama ?: "H. Supriyanto, S.Pd",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${ketuaRt?.jabatan ?: "Ketua RT 01"} • WA: ${ketuaRt?.noWa ?: "081234567890"}",
                                        fontSize = 12.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewModel.switchTab(MainTab.PROFIL_PENGURUS) }
                                )
                            }
                        }
                    }
                }
            }

            // 2. Section: MODUL PENGATURAN & MASTER DATA (Requested Features)
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "PENGATURAN & MASTER DATA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // 2a. Backup & Restore
                    MenuActionCard(
                        title = "Backup & Restore (.json)",
                        subtitle = "Ekspor & pulihkan data sistem via SAF Storage File Picker",
                        badgeText = "${backupList.size} Riwayat",
                        icon = Icons.Default.Backup,
                        iconBg = Color(0xFF4F46E5),
                        testTag = "menu_btn_backup_restore",
                        onClick = { viewModel.switchTab(MainTab.BACKUP_RESTORE) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2b. Profil Pengurus
                    MenuActionCard(
                        title = "Profil Pengurus Kunci",
                        subtitle = "Super Admin, Ketua RT, RW, Bendahara, Seksi Kurban & Ronda",
                        badgeText = "${pengurusList.size} Pengurus",
                        icon = Icons.Default.Badge,
                        iconBg = Color(0xFF059669),
                        testTag = "menu_btn_profil_pengurus",
                        onClick = { viewModel.switchTab(MainTab.PROFIL_PENGURUS) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2c. Master Data Jabatan
                    MenuActionCard(
                        title = "Master Data Jabatan",
                        subtitle = "Kelola struktur jabatan dinamis untuk form profil pengurus",
                        badgeText = "${jabatanList.size} Jabatan",
                        icon = Icons.Default.Work,
                        iconBg = Color(0xFFD97706),
                        testTag = "menu_btn_master_jabatan",
                        onClick = { viewModel.switchTab(MainTab.MASTER_JABATAN) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2d. Manajemen Pengguna (3 Role)
                    MenuActionCard(
                        title = "Manajemen Pengguna (3 Role)",
                        subtitle = "Warga, Super Admin, & Admin RT/RW/Bendahara",
                        badgeText = "${usersList.size} Akun",
                        icon = Icons.Default.ManageAccounts,
                        iconBg = Color(0xFF7C3AED),
                        testTag = "menu_btn_manajemen_user",
                        onClick = { viewModel.switchTab(MainTab.MANAJEMEN_USER) }
                    )
                }
            }

            // 3. Section: AKSES CEPAT OPERASIONAL RT
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Text(
                        text = "MODUL OPERASIONAL RT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickTile(
                            modifier = Modifier.weight(1f),
                            title = "Data Warga",
                            subtitle = "${wargaList.size} KK",
                            icon = Icons.Default.Group,
                            color = Color(0xFF4F46E5),
                            onClick = { viewModel.switchTab(MainTab.WARGA) }
                        )
                        QuickTile(
                            modifier = Modifier.weight(1f),
                            title = "Kamar Kas",
                            subtitle = "Multi-Kamar",
                            icon = Icons.Default.AccountBalanceWallet,
                            color = Color(0xFF059669),
                            onClick = { viewModel.switchTab(MainTab.KAS_KAMAR) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Kurban Tile using clean livestock silhouette icon
                        QuickTile(
                            modifier = Modifier.weight(1f),
                            title = "Tabungan Kurban",
                            subtitle = "7 Org / Sapi",
                            icon = Icons.Default.LivestockKurban,
                            color = Color(0xFFD97706),
                            onClick = { viewModel.switchTab(MainTab.KURBAN) }
                        )
                        QuickTile(
                            modifier = Modifier.weight(1f),
                            title = "QR & Ronda",
                            subtitle = "Piket Malam",
                            icon = Icons.Default.QrCodeScanner,
                            color = Color(0xFF0284C7),
                            onClick = { viewModel.switchTab(MainTab.QR_PIKET) }
                        )
                    }
                }
            }

            // 4. Role Switcher Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Beralih Role Akses Demo",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pilih hak akses untuk menguji tampilan Super Admin, Admin RT/RW/Bendahara, atau Warga:",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        UserRole.values().forEach { role ->
                            val isSelected = currentRole == role
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.switchRole(role) }
                                    .testTag("switch_role_${role.name.lowercase()}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(
                                        if (isSelected) listOf(IndigoPrimary, IndigoPrimary)
                                        else listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0))
                                    )
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = role.label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.5.sp,
                                            color = if (isSelected) IndigoPrimary else Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = role.subtitle,
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = IndigoPrimary,
                                            modifier = Modifier.size(20.dp)
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
}

@Composable
private fun MenuActionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    iconBg: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = iconBg.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconBg,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun QuickTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
