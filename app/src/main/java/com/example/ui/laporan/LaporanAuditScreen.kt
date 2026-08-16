package com.example.ui.laporan

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.IuranQViewModel
import com.example.ui.theme.CardEmeraldGradient
import com.example.ui.theme.CardIndigoGradient
import com.example.ui.theme.CardRoseGradient
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TealAccent
import com.example.util.ReportExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LaporanAuditScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Cetak Laporan Presisi, 1: Audit Log & Restore

    val transaksiList by viewModel.transaksiList.collectAsState()
    val transaksiAuditList by viewModel.transaksiAuditList.collectAsState()
    val kamarKasList by viewModel.kamarKasList.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val auditLogs by viewModel.auditLogList.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()

    val deletedTransactions = transaksiAuditList.filter { it.isDeleted }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(bottom = 80.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeroGradient)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Laporan Presisi & Jejak Audit",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Cetak Excel (Max Kolom BK), Word, PDF & Pelacakan Soft Delete",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Sub Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = IndigoPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Cetak Laporan", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Jejak Audit & Restore (${deletedTransactions.size})", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Section: Offline Sync Banner if any unsynced
            if (unsyncedCount > 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "$unsyncedCount Transaksi Offline Belum Disinkron",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF92400E)
                                )
                                Text("Data tersimpan aman di HP, ketuk sinkron untuk memperbarui server", fontSize = 11.sp, color = Color(0xFFB45309))
                            }
                            Button(
                                onClick = {
                                    viewModel.syncDataOffline()
                                    Toast.makeText(context, "Sinkronisasi offline berhasil!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sinkron", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Export Cards
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pilih Format Ekspor Laporan Keuangan:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Excel Button
                    ExportFormatCard(
                        title = "1. Ekspor Dokumen Excel (.xlsx)",
                        subtitle = "Format spreadsheet padat & efisien (Maksimal Kolom BK). Multi-sheet buku kas umum, rekap saldo kamar kas, data tabungan jimpitan warga & kas air.",
                        badgeText = "Excel 2020-2026",
                        badgeColor = EmeraldSuccess,
                        icon = Icons.Default.TableChart,
                        buttonLabel = "Download Excel (.xlsx)",
                        buttonColor = EmeraldSuccess,
                        testTag = "btn_export_excel"
                    ) {
                        ReportExporter.exportExcel(context, transaksiList, kamarKasList, wargaList)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Word Button
                    ExportFormatCard(
                        title = "2. Ekspor Dokumen Word (.doc / .docx)",
                        subtitle = "Buku kas umum resmi ber-kop surat RT 01 RW 03 Desa Purbayasa, ringkasan peruntukan dana, serta kolom tanda tangan Ketua & Bendahara.",
                        badgeText = "MS Word Document",
                        badgeColor = IndigoPrimary,
                        icon = Icons.Default.Description,
                        buttonLabel = "Download Word (.doc)",
                        buttonColor = IndigoPrimary,
                        testTag = "btn_export_word"
                    ) {
                        ReportExporter.exportWord(context, transaksiList, kamarKasList, wargaList)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. PDF Button
                    ExportFormatCard(
                        title = "3. Ekspor Dokumen PDF Resmi (.pdf)",
                        subtitle = "Format cetak A4 siap print dengan grafis tabel rapi, watermark keaslian IuranQ, dan ringkasan eksekutif saldo kas warga.",
                        badgeText = "PDF Cetak Presisi",
                        badgeColor = RoseDanger,
                        icon = Icons.Default.PictureAsPdf,
                        buttonLabel = "Download PDF (.pdf)",
                        buttonColor = RoseDanger,
                        testTag = "btn_export_pdf"
                    ) {
                        ReportExporter.exportPdf(context, transaksiList, kamarKasList, wargaList)
                    }
                }
            }
        } else {
            // Tab 1: Audit Log & Soft Delete Restore
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daftar Transaksi Dibatalkan (Soft Delete):",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Data yang dihapus tetap tersimpan di database dan dapat dipulihkan kapan saja.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            if (deletedTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Tidak ada transaksi yang dibatalkan", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(deletedTransactions) { deletedTrx ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFECDD3), Color(0xFFFDA4AF))))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${deletedTrx.kodeTransaksi} • ${deletedTrx.namaWarga ?: deletedTrx.kamarKasId}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF9F1239)
                                )
                                Text(
                                    text = "Nominal: Rp${String.format(Locale.GERMANY, "%,d", deletedTrx.nominal)} (${deletedTrx.keterangan})",
                                    fontSize = 12.sp,
                                    color = Color(0xFF881337)
                                )
                                Text(
                                    text = "Alasan Hapus: ${deletedTrx.deletedReason ?: "Koreksi input"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFBE123C)
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.restoreTransaksi(deletedTrx.id)
                                    Toast.makeText(context, "Transaksi berhasil dipulihkan!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(34.dp).testTag("restore_btn_${deletedTrx.id}")
                            ) {
                                Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pulihkan", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // System Audit Trail Timeline
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aktivitas Log Sistem Terkini:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            items(auditLogs.take(15)) { log ->
                val dateStr = SimpleDateFormat("dd MMM HH:mm", Locale("id", "ID")).format(Date(log.timestamp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 3.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateStr,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "[${log.aksi}] ${log.detail} (${log.userRole})",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExportFormatCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonLabel: String,
    buttonColor: Color,
    testTag: String,
    onExportClick: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(badgeColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onExportClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag(testTag),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(buttonLabel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
