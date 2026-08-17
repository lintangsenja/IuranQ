package com.example.ui.menu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.BackupHistoryEntity
import com.example.ui.IuranQViewModel
import com.example.ui.MainTab
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val backupList by viewModel.backupHistoryList.collectAsState()

    var pendingBackupJson by remember { mutableStateOf("") }
    var pendingRecordCount by remember { mutableStateOf(0) }
    var deleteCandidate by remember { mutableStateOf<BackupHistoryEntity?>(null) }
    var restoreCandidate by remember { mutableStateOf<BackupHistoryEntity?>(null) }
    var showRestoreSuccessDialog by remember { mutableStateOf<Int?>(null) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }

    // SAF File Creator Launcher (Backup/Export)
    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null && pendingBackupJson.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.write(pendingBackupJson.toByteArray(Charsets.UTF_8))
                        }
                    }
                    val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "backup_iuranq.json"
                    val sizeKb = (pendingBackupJson.toByteArray().size / 1024.0)
                    val sizeFormatted = String.format(Locale.getDefault(), "%.1f KB", sizeKb)

                    viewModel.saveBackupHistoryRecord(
                        fileName = fileName,
                        fileSizeFormatted = sizeFormatted,
                        totalRecords = pendingRecordCount,
                        jsonContent = pendingBackupJson
                    )

                    Toast.makeText(context, "Backup berhasil disimpan ke perangkat!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal menulis file: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // SAF File Picker Launcher (Restore/Import)
    val openDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val jsonContent = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).readText()
                        }
                    }
                    if (!jsonContent.isNullOrBlank()) {
                        viewModel.restoreBackupFromJson(jsonContent) { count ->
                            showRestoreSuccessDialog = count
                        }
                    } else {
                        Toast.makeText(context, "File JSON kosong atau tidak valid", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal membaca file: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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
                                text = "Backup & Restore Database",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Format JSON • Storage Access Framework (SAF)",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            // 2 Main Action Buttons: Backup & Restore
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "AKSI DATA UTAMA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Tombol Backup
                        Button(
                            onClick = {
                                viewModel.generateBackupJson { jsonStr, count ->
                                    pendingBackupJson = jsonStr
                                    pendingRecordCount = count
                                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                    val suggestedName = "iuranq_backup_$timeStamp.json"
                                    createDocLauncher.launch(suggestedName)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_action_backup_json"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Backup Database (.json)", fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tombol Restore
                        OutlinedButton(
                            onClick = {
                                openDocLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_action_restore_json"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF059669)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder().copy(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                            )
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Restore Database (.json)", fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Menggunakan File Picker (SAF) bawaan Android sehingga Anda bebas menentukan folder penyimpanan (Google Drive, SD Card, atau Internal).",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Sub-Menu: Riwayat Backup
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Riwayat Backup",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFE0E7FF)
                    ) {
                        Text(
                            text = "${backupList.size} Catatan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (backupList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Belum ada riwayat backup tersimpan", color = Color(0xFF64748B), fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(backupList, key = { it.id }) { item ->
                    BackupHistoryCard(
                        item = item,
                        formattedDate = dateFormat.format(Date(item.createdAt)),
                        onShare = {
                            shareBackupJson(context, item.fileName, item.jsonContent)
                        },
                        onRestore = {
                            restoreCandidate = item
                        },
                        onDelete = {
                            deleteCandidate = item
                        }
                    )
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus Riwayat
    if (deleteCandidate != null) {
        val target = deleteCandidate!!
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            icon = {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
            },
            title = {
                Text("Hapus Riwayat Backup?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Riwayat backup '${target.fileName}' akan dihapus dari daftar histori aplikasi.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hapusBackupHistory(target.id)
                        deleteCandidate = null
                        Toast.makeText(context, "Riwayat backup dihapus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Konfirmasi Restore dari Riwayat
    if (restoreCandidate != null) {
        val target = restoreCandidate!!
        AlertDialog(
            onDismissRequest = { restoreCandidate = null },
            icon = {
                Icon(Icons.Default.Restore, contentDescription = null, tint = IndigoPrimary)
            },
            title = {
                Text("Pulihkan dari Backup Ini?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Database akan diperbarui menggunakan data dari snapshot '${target.fileName}' (${target.totalRecords} entri). Lanjutkan?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreBackupFromJson(target.jsonContent) { count ->
                            restoreCandidate = null
                            showRestoreSuccessDialog = count
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Pulihkan Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { restoreCandidate = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Sukses Restore
    if (showRestoreSuccessDialog != null) {
        val count = showRestoreSuccessDialog!!
        AlertDialog(
            onDismissRequest = { showRestoreSuccessDialog = null },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Restore Database Berhasil", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Sebanyak $count entri data (Warga, Master Jabatan, Profil Pengurus, & Akun) telah berhasil dipulihkan ke dalam sistem IuranQ.")
            },
            confirmButton = {
                Button(
                    onClick = { showRestoreSuccessDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("Selesai")
                }
            }
        )
    }
}

@Composable
private fun BackupHistoryCard(
    item: BackupHistoryEntity,
    formattedDate: String,
    onShare: () -> Unit,
    onRestore: () -> Unit,
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.fileName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "$formattedDate • ${item.fileSizeFormatted}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = "${item.totalRecords} Data",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row: Share, Restore, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Share
                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_share_backup_${item.id}")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Bagikan", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 11.5.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Restore
                Button(
                    onClick = onRestore,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_restore_history_${item.id}")
                ) {
                    Icon(Icons.Default.Restore, contentDescription = "Pulihkan", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pulihkan", fontSize = 11.5.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Delete
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2))
                        .testTag("btn_delete_backup_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Riwayat",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun shareBackupJson(context: Context, fileName: String, jsonContent: String) {
    try {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, jsonContent)
            putExtra(Intent.EXTRA_SUBJECT, "Backup Database IuranQ - $fileName")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan File Backup JSON via")
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal membagikan file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
