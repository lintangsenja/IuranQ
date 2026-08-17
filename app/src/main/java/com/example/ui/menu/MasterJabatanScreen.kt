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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JabatanEntity
import com.example.ui.IuranQViewModel
import com.example.ui.MainTab
import com.example.ui.UserRole
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterJabatanScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentRole by viewModel.currentRole.collectAsState()
    val jabatanList by viewModel.jabatanList.collectAsState()
    val pengurusList by viewModel.pengurusList.collectAsState()

    var editingJabatan by remember { mutableStateOf<JabatanEntity?>(null) }
    var isNewJabatan by remember { mutableStateOf(false) }
    var deleteCandidate by remember { mutableStateOf<JabatanEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (currentRole != UserRole.WARGA) {
                FloatingActionButton(
                    onClick = {
                        editingJabatan = JabatanEntity(
                            namaJabatan = "",
                            deskripsi = "",
                            levelAkses = "ADMIN_PENGURUS",
                            urutan = jabatanList.size + 1
                        )
                        isNewJabatan = true
                    },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 72.dp)
                        .testTag("fab_tambah_jabatan")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tambah Jabatan", fontWeight = FontWeight.Bold)
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
                                text = "Master Data Jabatan RT",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Struktur jabatan dinamis terintegrasi profil pengurus",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            // Info Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Semua pilihan jabatan pada form Profil Pengurus ditarik secara otomatis & dinamis dari daftar master ini.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Jabatan Items
            items(jabatanList, key = { it.id }) { item ->
                val usedCount = pengurusList.count { it.jabatan.equals(item.namaJabatan, ignoreCase = true) }
                JabatanItemCard(
                    item = item,
                    usedCount = usedCount,
                    canEdit = currentRole != UserRole.WARGA,
                    onEdit = {
                        editingJabatan = item
                        isNewJabatan = false
                    },
                    onDelete = {
                        deleteCandidate = item
                    }
                )
            }
        }
    }

    // Dialog Tambah / Edit Jabatan
    if (editingJabatan != null) {
        JabatanEditDialog(
            initial = editingJabatan!!,
            isNew = isNewJabatan,
            onDismiss = { editingJabatan = null },
            onSave = { entity ->
                viewModel.simpanJabatan(entity, isNewJabatan)
                editingJabatan = null
                Toast.makeText(context, "Data jabatan tersimpan", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog Konfirmasi Hapus Jabatan
    if (deleteCandidate != null) {
        val target = deleteCandidate!!
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
            title = { Text("Hapus Jabatan '${target.namaJabatan}'?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Menghapus jabatan ini akan menghilangkannya dari opsi pilihan form profil pengurus.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hapusJabatan(target.id)
                        deleteCandidate = null
                        Toast.makeText(context, "Jabatan berhasil dihapus", Toast.LENGTH_SHORT).show()
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
private fun JabatanItemCard(
    item: JabatanEntity,
    usedCount: Int,
    canEdit: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.namaJabatan,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
                if (item.deskripsi.isNotBlank()) {
                    Text(
                        text = item.deskripsi,
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFEEF2FF)
                    ) {
                        Text(
                            text = "Level: ${item.levelAkses}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (usedCount > 0) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = "$usedCount Pengurus",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (usedCount > 0) Color(0xFF15803D) else Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (canEdit) {
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp).testTag("btn_edit_jabatan_${item.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp).testTag("btn_delete_jabatan_${item.id}")
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
private fun JabatanEditDialog(
    initial: JabatanEntity,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (JabatanEntity) -> Unit
) {
    var namaJabatan by remember { mutableStateOf(initial.namaJabatan) }
    var deskripsi by remember { mutableStateOf(initial.deskripsi) }
    var levelAkses by remember { mutableStateOf(initial.levelAkses) }
    var urutanText by remember { mutableStateOf(initial.urutan.toString()) }

    var expandedRoleDropdown by remember { mutableStateOf(false) }
    val roleOptions = listOf("SUPER_ADMIN", "ADMIN_PENGURUS", "WARGA")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "Tambah Master Jabatan" else "Edit Jabatan",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = namaJabatan,
                    onValueChange = { namaJabatan = it },
                    label = { Text("Nama Jabatan *") },
                    placeholder = { Text("Contoh: Ketua RT 01") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_nama_jabatan")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi & Tugas Pokok") },
                    placeholder = { Text("Contoh: Pengambil kebijakan & koordinasi warga") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Dropdown Level Akses
                ExposedDropdownMenuBox(
                    expanded = expandedRoleDropdown,
                    onExpandedChange = { expandedRoleDropdown = it }
                ) {
                    OutlinedTextField(
                        value = levelAkses,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Level Akses Default") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoleDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRoleDropdown,
                        onDismissRequest = { expandedRoleDropdown = false }
                    ) {
                        roleOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    levelAkses = opt
                                    expandedRoleDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (namaJabatan.isNotBlank()) {
                        val urutan = urutanText.toIntOrNull() ?: 1
                        onSave(
                            initial.copy(
                                namaJabatan = namaJabatan.trim(),
                                deskripsi = deskripsi.trim(),
                                levelAkses = levelAkses,
                                urutan = urutan
                            )
                        )
                    }
                },
                enabled = namaJabatan.isNotBlank(),
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
