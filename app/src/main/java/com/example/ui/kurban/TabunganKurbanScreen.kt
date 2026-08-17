package com.example.ui.kurban

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnggotaKurbanEntity
import com.example.data.KelompokKurbanEntity
import com.example.ui.IuranQViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CardAmberGradient
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.HeroGradient
import com.example.ui.theme.IndigoPrimary
import com.example.util.WhatsAppGateway
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabunganKurbanScreen(
    viewModel: IuranQViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val kelompokList by viewModel.kelompokKurbanList.collectAsState()
    val anggotaList by viewModel.anggotaKurbanList.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var showSetorCicilanDialog by remember { mutableStateOf<AnggotaKurbanEntity?>(null) }
    var nominalCicilanText by remember { mutableStateOf("250000") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(bottom = 16.dp)
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
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Pets, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tabungan Kurban 1447 H",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Format 7 Orang per 1 Ekor Sapi • Tracking Cicilan Transparan",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Overview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFFDE68A), Color(0xFFF59E0B))))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        val totalTerkumpulKurban = anggotaList.sumOf { it.terkumpul }
                        val totalTargetKurban = kelompokList.sumOf { it.targetHargaPerEkor }

                        Text(
                            text = "TOTAL AKUMULASI TABUNGAN KURBAN",
                            color = Color(0xFFB45309),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp" + String.format(Locale.GERMANY, "%,d", totalTerkumpulKurban),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "Dari total target Rp${String.format(Locale.GERMANY, "%,d", totalTargetKurban)} (${kelompokList.size} Ekor Sapi)",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            // Kelompok List
            items(kelompokList) { kelompok ->
                val anggotaKelompok = anggotaList.filter { it.kelompokId == kelompok.id }
                val terkumpulKelompok = anggotaKelompok.sumOf { it.terkumpul }
                val targetKelompok = kelompok.targetHargaPerEkor
                val progressKelompok = if (targetKelompok > 0) (terkumpulKelompok.toFloat() / targetKelompok.toFloat()) else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                    text = kelompok.namaKelompok,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Target: Rp${String.format(Locale.GERMANY, "%,d", targetKelompok)} • ${kelompok.tahunHijriah}",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "${(progressKelompok * 100).toInt()}% Selesai",
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progressKelompok.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AmberWarning,
                            trackColor = Color(0xFFFEF3C7)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Daftar Peserta Saham (Maksimal 7 Orang):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        anggotaKelompok.forEachIndexed { idx, anggota ->
                            val warga = wargaList.find { it.id == anggota.wargaId }
                            val sisaCicilan = if (anggota.targetPerOrang > anggota.terkumpul) anggota.targetPerOrang - anggota.terkumpul else 0L
                            val persenAnggota = if (anggota.targetPerOrang > 0) (anggota.terkumpul.toFloat() / anggota.targetPerOrang.toFloat()) else 0f

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${idx + 1}. ${anggota.namaWarga}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Terkumpul: Rp${String.format(Locale.GERMANY, "%,d", anggota.terkumpul)} / Rp${String.format(Locale.GERMANY, "%,d", anggota.targetPerOrang)}",
                                            fontSize = 11.5.sp,
                                            color = if (anggota.terkumpul >= anggota.targetPerOrang) EmeraldSuccess else Color(0xFF64748B)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // WhatsApp Progress Button
                                        if (warga != null) {
                                            IconButton(
                                                onClick = {
                                                    val msg = WhatsAppGateway.buildKurbanProgressMessage(
                                                        warga = warga,
                                                        namaKelompok = kelompok.namaKelompok,
                                                        terkumpul = anggota.terkumpul,
                                                        target = anggota.targetPerOrang
                                                    )
                                                    WhatsAppGateway.sendWhatsApp(context, warga.noHpWa, msg)
                                                },
                                                modifier = Modifier.size(34.dp)
                                            ) {
                                                Icon(Icons.Default.Send, contentDescription = "Kirim WA Progress", tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                                            }
                                        }

                                        // Setor Cicilan Button
                                        if (currentRole != com.example.ui.UserRole.WARGA) {
                                            Button(
                                                onClick = { showSetorCicilanDialog = anggota },
                                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(32.dp).testTag("setor_kurban_${anggota.id}")
                                            ) {
                                                Text("Setor", fontSize = 11.sp)
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
    }

    // Setor Cicilan Dialog
    if (showSetorCicilanDialog != null) {
        val anggota = showSetorCicilanDialog!!
        AlertDialog(
            onDismissRequest = { showSetorCicilanDialog = null },
            title = { Text("Setor Cicilan Kurban", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Peserta: ${anggota.namaWarga}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Terkumpul saat ini: Rp${String.format(Locale.GERMANY, "%,d", anggota.terkumpul)}", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Nominal Setoran (Rp):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = nominalCicilanText,
                        onValueChange = { nominalCicilanText = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nom = nominalCicilanText.toLongOrNull() ?: 0L
                        if (nom > 0) {
                            viewModel.bayarCicilanKurban(anggota.id, nom)
                            showSetorCicilanDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Konfirmasi Setoran")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSetorCicilanDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
