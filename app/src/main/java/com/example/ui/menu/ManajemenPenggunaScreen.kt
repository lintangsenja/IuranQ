package com.example.ui.menu

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccountEntity
import com.example.ui.IuranQViewModel
import com.example.ui.MainTab
import com.example.ui.UserRole
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManajemenPenggunaScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentRole by viewModel.currentRole.collectAsState()
    val usersList by viewModel.userAccountList.collectAsState()

    var editingUser by remember { mutableStateOf<UserAccountEntity?>(null) }
    var isNewUser by remember { mutableStateOf(false) }
    var deleteCandidate by remember { mutableStateOf<UserAccountEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (currentRole == UserRole.SUPER_ADMIN) {
                FloatingActionButton(
                    onClick = {
                        editingUser = UserAccountEntity(
                            username = "",
                            namaLengkap = "",
                            role = "ADMIN_PENGURUS",
                            noWa = "081234567890",
                            pinPassword = "123",
                            isAktif = true
                        )
                        isNewUser = true
                    },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 72.dp)
                        .testTag("fab_tambah_user")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah Pengguna", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(bottom = 96.dp)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.switchTab(MainTab.MENU_PENGATURAN) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Manajemen Hak Akses & Akun",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Kelola pengguna berbasis 3 Role Utama",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            // 3 Role Matrix Overview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3 TINGKATAN HAK AKSES UTAMA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        RolePill(
                            title = "1. Super Admin",
                            desc = "Akses penuh sistem, master data, restore database, dan audit log",
                            color = Color(0xFF7C3AED),
                            icon = Icons.Default.Shield
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RolePill(
                            title = "2. Admin RT / RW / Bendahara",
                            desc = "Akses operasional keuangan, pencatatan kas, iuran, jimpitan, dan warga",
                            color = Color(0xFF4F46E5),
                            icon = Icons.Default.SupervisorAccount
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RolePill(
                            title = "3. Warga RT",
                            desc = "Akses mandiri melihat transparansi kas & histori saldo pribadi",
                            color = Color(0xFF059669),
                            icon = Icons.Default.Person
                        )
                    }
                }
            }

            // List of User Accounts Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAFTAR AKUN TERDAFTAR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.8.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFEEF2FF)
                    ) {
                        Text(
                            text = "${usersList.size} Pengguna",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // List of Accounts
            items(usersList, key = { it.id }) { user ->
                UserAccountCard(
                    user = user,
                    canManage = currentRole == UserRole.SUPER_ADMIN,
                    onEdit = {
                        editingUser = user
                        isNewUser = false
                    },
                    onDelete = {
                        deleteCandidate = user
                    },
                    onToggleActive = {
                        viewModel.simpanUserAccount(user.copy(isAktif = !user.isAktif), false)
                    }
                )
            }
        }
    }

    // Dialog Tambah / Edit User
    if (editingUser != null) {
        UserEditDialog(
            initial = editingUser!!,
            isNew = isNewUser,
            onDismiss = { editingUser = null },
            onSave = { entity ->
                viewModel.simpanUserAccount(entity, isNewUser)
                editingUser = null
                Toast.makeText(context, "Akun pengguna berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog Konfirmasi Hapus
    if (deleteCandidate != null) {
        val target = deleteCandidate!!
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
            title = { Text("Hapus Akun '${target.username}'?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Akun ${target.namaLengkap} (${target.role}) akan dihapus permanen dari sistem.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hapusUserAccount(target.id)
                        deleteCandidate = null
                        Toast.makeText(context, "Akun berhasil dihapus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun RolePill(
    title: String,
    desc: String,
    color: Color,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.08f),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.3f))))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
                Text(text = desc, fontSize = 11.sp, color = Color(0xFF475569), lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun UserAccountCard(
    user: UserAccountEntity,
    canManage: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    val roleColor = when (user.role) {
        "SUPER_ADMIN" -> Color(0xFF7C3AED)
        "ADMIN_PENGURUS" -> IndigoPrimary
        else -> EmeraldSuccess
    }

    val roleLabel = when (user.role) {
        "SUPER_ADMIN" -> "Super Admin"
        "ADMIN_PENGURUS" -> "Admin RT/RW/Bendahara"
        else -> "Warga RT"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(roleColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (user.role) {
                        "SUPER_ADMIN" -> Icons.Default.Shield
                        "ADMIN_PENGURUS" -> Icons.Default.SupervisorAccount
                        else -> Icons.Default.Person
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.namaLengkap.ifBlank { user.username },
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
                Text(
                    text = "@${user.username} • WA: ${user.noWa}",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = roleColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = roleLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = roleColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    if (!user.isAktif) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = "Nonaktif",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (canManage) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(34.dp).testTag("btn_edit_user_${user.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp).testTag("btn_delete_user_${user.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserEditDialog(
    initial: UserAccountEntity,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (UserAccountEntity) -> Unit
) {
    var username by remember { mutableStateOf(initial.username) }
    var namaLengkap by remember { mutableStateOf(initial.namaLengkap) }
    var role by remember { mutableStateOf(initial.role) }
    var noWa by remember { mutableStateOf(initial.noWa) }
    var pinPassword by remember { mutableStateOf(initial.pinPassword) }
    var isAktif by remember { mutableStateOf(initial.isAktif) }

    var expandedRoleDropdown by remember { mutableStateOf(false) }
    val roleList = listOf(
        Pair("SUPER_ADMIN", "Super Admin (Akses Penuh)"),
        Pair("ADMIN_PENGURUS", "Admin RT/RW/Bendahara (Operasional Kas)"),
        Pair("WARGA", "Warga RT (Akses Mandiri/Lihat Saldo)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "Tambah Akun Pengguna" else "Edit Pengguna",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = namaLengkap,
                    onValueChange = { namaLengkap = it },
                    label = { Text("Nama Lengkap *") },
                    placeholder = { Text("Contoh: Bambang Santoso") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_nama_lengkap_user")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username Login *") },
                    placeholder = { Text("bambang_s") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_username_user")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedRoleDropdown,
                    onExpandedChange = { expandedRoleDropdown = it }
                ) {
                    OutlinedTextField(
                        value = roleList.find { it.first == role }?.second ?: role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hak Akses (Role) *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoleDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth().testTag("select_role_user")
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRoleDropdown,
                        onDismissRequest = { expandedRoleDropdown = false }
                    ) {
                        roleList.forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    role = code
                                    expandedRoleDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noWa,
                    onValueChange = { noWa = it },
                    label = { Text("No. WhatsApp") },
                    placeholder = { Text("081234567890") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pinPassword,
                    onValueChange = { pinPassword = it },
                    label = { Text("PIN / Password") },
                    placeholder = { Text("123456") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Status Akun Aktif", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isAktif,
                        onCheckedChange = { isAktif = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isNotBlank() && namaLengkap.isNotBlank()) {
                        onSave(
                            initial.copy(
                                username = username.trim(),
                                namaLengkap = namaLengkap.trim(),
                                role = role,
                                noWa = noWa.trim(),
                                pinPassword = pinPassword.trim(),
                                isAktif = isAktif
                            )
                        )
                    }
                },
                enabled = username.isNotBlank() && namaLengkap.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
