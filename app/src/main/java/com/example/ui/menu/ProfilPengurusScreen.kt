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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JabatanEntity
import com.example.data.PengurusEntity
import com.example.ui.IuranQViewModel
import com.example.ui.MainTab
import com.example.ui.UserRole
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.util.WhatsAppGateway

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilPengurusScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentRole by viewModel.currentRole.collectAsState()
    val pengurusList by viewModel.pengurusList.collectAsState()
    val jabatanList by viewModel.jabatanList.collectAsState()

    var editingPengurus by remember { mutableStateOf<PengurusEntity?>(null) }
    var isNewPengurus by remember { mutableStateOf(false) }
    var deleteCandidate by remember { mutableStateOf<PengurusEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (currentRole != UserRole.WARGA) {
                FloatingActionButton(
                    onClick = {
                        val defaultJabatan = jabatanList.firstOrNull()?.namaJabatan ?: "Pengurus RT"
                        editingPengurus = PengurusEntity(
                            nama = "",
                            jabatan = defaultJabatan,
                            noWa = "081234567890",
                            fotoAvatar = "avatar_1",
                            email = "",
                            catatan = "Periode 2024-2029",
                            isUtama = false
                        )
                        isNewPengurus = true
                    },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 72.dp)
                        .testTag("fab_tambah_pengurus")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah Pengurus", fontWeight = FontWeight.Bold)
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
                                text = "Struktur & Profil Pengurus",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Super Admin, Ketua RT, RW, Bendahara & Seksi",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            // Quick Link to Master Jabatan
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .clickable { viewModel.switchTab(MainTab.MASTER_JABATAN) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(IndigoPrimary, IndigoPrimary)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Pilihan Jabatan Dinamis", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = IndigoPrimary)
                                Text("Tersedia ${jabatanList.size} pilihan jabatan dari Master Data", fontSize = 11.sp, color = Color(0xFF475569))
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = IndigoPrimary
                        ) {
                            Text("Kelola", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            // List of Pengurus Cards
            items(pengurusList, key = { it.id }) { pengurus ->
                PengurusCard(
                    pengurus = pengurus,
                    canEdit = currentRole != UserRole.WARGA,
                    onChatWa = {
                        WhatsAppGateway.openWhatsAppChat(context, pengurus.noWa, "Halo Pak/Bu ${pengurus.nama} (${pengurus.jabatan}), mohon info terkait administrasi RT 01.")
                    },
                    onEdit = {
                        editingPengurus = pengurus
                        isNewPengurus = false
                    },
                    onDelete = {
                        deleteCandidate = pengurus
                    }
                )
            }
        }
    }

    // Dialog Edit / Tambah Pengurus
    if (editingPengurus != null) {
        PengurusEditDialog(
            initial = editingPengurus!!,
            isNew = isNewPengurus,
            availableJabatan = jabatanList,
            onDismiss = { editingPengurus = null },
            onSave = { entity ->
                viewModel.simpanPengurus(entity, isNewPengurus)
                editingPengurus = null
                Toast.makeText(context, "Profil pengurus berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog Konfirmasi Hapus
    if (deleteCandidate != null) {
        val target = deleteCandidate!!
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
            title = { Text("Hapus Pengurus?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Profil '${target.nama}' (${target.jabatan}) akan dihapus dari daftar pengurus aktif.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hapusPengurus(target.id)
                        deleteCandidate = null
                        Toast.makeText(context, "Pengurus berhasil dihapus", Toast.LENGTH_SHORT).show()
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
private fun PengurusCard(
    pengurus: PengurusEntity,
    canEdit: Boolean,
    onChatWa: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val avatarBg = when (pengurus.fotoAvatar) {
        "avatar_1" -> Color(0xFF4F46E5)
        "avatar_2" -> Color(0xFF059669)
        "avatar_3" -> Color(0xFFD97706)
        "avatar_4" -> Color(0xFF7C3AED)
        else -> Color(0xFF0284C7)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(avatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pengurus.nama.take(2).uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = pengurus.nama,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        if (pengurus.isUtama) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Star, contentDescription = "Utama", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFEEF2FF)
                    ) {
                        Text(
                            text = pengurus.jabatan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    if (pengurus.catatan.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = pengurus.catatan,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // WA Direct Action
                OutlinedButton(
                    onClick = onChatWa,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF059669)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat WA", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = pengurus.noWa, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (canEdit) {
                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(36.dp).testTag("btn_edit_pengurus_${pengurus.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(36.dp).testTag("btn_delete_pengurus_${pengurus.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PengurusEditDialog(
    initial: PengurusEntity,
    isNew: Boolean,
    availableJabatan: List<JabatanEntity>,
    onDismiss: () -> Unit,
    onSave: (PengurusEntity) -> Unit
) {
    var nama by remember { mutableStateOf(initial.nama) }
    var selectedJabatan by remember { mutableStateOf(initial.jabatan) }
    var noWa by remember { mutableStateOf(initial.noWa) }
    var fotoAvatar by remember { mutableStateOf(initial.fotoAvatar) }
    var email by remember { mutableStateOf(initial.email) }
    var catatan by remember { mutableStateOf(initial.catatan) }
    var isUtama by remember { mutableStateOf(initial.isUtama) }

    var expandedJabatanDropdown by remember { mutableStateOf(false) }
    val avatarList = listOf("avatar_1", "avatar_2", "avatar_3", "avatar_4", "avatar_5")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "Tambah Profil Pengurus" else "Edit Profil Pengurus",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    // Avatar selector
                    Text("Pilih Avatar / Foto Profil", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(avatarList) { av ->
                            val isSelected = fotoAvatar == av
                            val avBg = when (av) {
                                "avatar_1" -> Color(0xFF4F46E5)
                                "avatar_2" -> Color(0xFF059669)
                                "avatar_3" -> Color(0xFFD97706)
                                "avatar_4" -> Color(0xFF7C3AED)
                                else -> Color(0xFF0284C7)
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(avBg)
                                    .clickable { fotoAvatar = av },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Form Nama Lengkap
                    OutlinedTextField(
                        value = nama,
                        onValueChange = { nama = it },
                        label = { Text("Nama Lengkap & Gelar *") },
                        placeholder = { Text("Contoh: H. Supriyanto, S.Pd") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_nama_pengurus")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form Jabatan (Dinamis dari Master Data Jabatan)
                    ExposedDropdownMenuBox(
                        expanded = expandedJabatanDropdown,
                        onExpandedChange = { expandedJabatanDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = selectedJabatan,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jabatan (Dari Master Data) *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJabatanDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth().testTag("select_jabatan_pengurus")
                        )
                        ExposedDropdownMenu(
                            expanded = expandedJabatanDropdown,
                            onDismissRequest = { expandedJabatanDropdown = false }
                        ) {
                            if (availableJabatan.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Pengurus RT (Default)") },
                                    onClick = {
                                        selectedJabatan = "Pengurus RT"
                                        expandedJabatanDropdown = false
                                    }
                                )
                            } else {
                                availableJabatan.forEach { jab ->
                                    DropdownMenuItem(
                                        text = { Text(jab.namaJabatan) },
                                        onClick = {
                                            selectedJabatan = jab.namaJabatan
                                            expandedJabatanDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form No WA
                    OutlinedTextField(
                        value = noWa,
                        onValueChange = { noWa = it },
                        label = { Text("No. WhatsApp *") },
                        placeholder = { Text("Contoh: 081234567890") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_nowa_pengurus")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (Opsional)") },
                        placeholder = { Text("rt01@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form Catatan / Periode
                    OutlinedTextField(
                        value = catatan,
                        onValueChange = { catatan = it },
                        label = { Text("Masa Bakti / Catatan") },
                        placeholder = { Text("Periode 2024-2029") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isNotBlank() && selectedJabatan.isNotBlank()) {
                        onSave(
                            initial.copy(
                                nama = nama.trim(),
                                jabatan = selectedJabatan.trim(),
                                noWa = noWa.trim(),
                                fotoAvatar = fotoAvatar,
                                email = email.trim(),
                                catatan = catatan.trim(),
                                isUtama = isUtama
                            )
                        )
                    }
                },
                enabled = nama.isNotBlank() && selectedJabatan.isNotBlank(),
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
