package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.WargaEntity
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WhatsAppGateway {

    fun formatRupiah(amount: Long): String {
        return "Rp" + String.format(Locale.GERMANY, "%,d", amount)
    }

    fun buildJimpitanReceipt(
        warga: WargaEntity,
        totalSetor: Long,
        jimpitan: Long,
        overflow: Long,
        totalSaldoTabungan: Long,
        petugas: String
    ): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID"))
        val now = dateFormat.format(Date())

        return """
*BUKTI SETORAN JIMPITAN & TABUNGAN*
*IuranQ - RT 01 RW 03 DESA PURBAYASA*
━━━━━━━━━━━━━━━━━━━━━
Kepada Yth: *${warga.nama}*
ID Warga: `${warga.id}`
Alamat: ${warga.alamat}
Waktu: $now
Petugas: $petugas
━━━━━━━━━━━━━━━━━━━━━
*RINCIAN SETORAN:*
• Total Uang Diterima: *${formatRupiah(totalSetor)}*
• Alokasi Kas Jimpitan: *${formatRupiah(jimpitan)}*
• Overflow Masuk Tabungan: *${formatRupiah(overflow)}*

*TOTAL SALDO TABUNGAN ANDA:*
 *${formatRupiah(totalSaldoTabungan)}*
━━━━━━━━━━━━━━━━━━━━━
_Terima kasih atas partisipasi aktif Bapak/Ibu dalam memajukan lingkungan RT 01 RW 03 Desa Purbayasa._
_Pesan otomatis tercatat di sistem IuranQ._
        """.trimIndent()
    }

    fun buildKasAirReminder(
        warga: WargaEntity,
        bulan: String,
        nominal: Long = 5000L
    ): String {
        val isAirHidup = warga.statusAir == "AKTIF" || warga.statusAir == "AIR_HIDUP"
        val statusText = if (isAirHidup) "AIR HIDUP (Mengalir)" else "AIR MATI (Tutup - Rp0)"
        val nominalText = if (isAirHidup) formatRupiah(nominal) else "Rp0 (Bebas Iuran)"

        return """
*PEMBERITAHUAN KAS AIR BERSIH*
*RT 01 RW 03 DESA PURBAYASA*
━━━━━━━━━━━━━━━━━━━━━
Yth. *${warga.nama}* (${warga.id})
Alamat: ${warga.alamat}

Informasi status kran & tagihan air RT periode *Bulan $bulan*:
• Status Aliran: *${statusText}*
• Tagihan Bulan Ini: *${nominalText}*

${if (isAirHidup) "_Pembayaran dapat dilakukan melalui scan QR petugas piket atau transfer ke Bendahara RT._" else "_Saat ini kran Anda terdata MATI/TUTUP sehingga tagihan otomatis Rp0._"}
_Matur nuwun atas kerjasamanya._
        """.trimIndent()
    }

    fun buildMonthlyRekapMessage(
        warga: WargaEntity,
        bulanTahun: String,
        totalJimpitanBulanIni: Long,
        totalTabunganBulanIni: Long,
        totalSaldoAkumulasi: Long,
        hariHadir: Int
    ): String {
        return """
*BUKU HARIAN JIMPITAN & TABUNGAN*
*RT 01 RW 03 DESA PURBAYASA*
━━━━━━━━━━━━━━━━━━━━━
Kepada Yth: *${warga.nama}*
ID Warga: `${warga.id}`
Alamat: ${warga.alamat}
Periode: *${bulanTahun}*
━━━━━━━━━━━━━━━━━━━━━
*REKAP BULANAN:*
• Kehadiran Ronda/Piket: *${hariHadir} Hari Terisi*
• Total Jimpitan Kas RT: *${formatRupiah(totalJimpitanBulanIni)}*
• Tabungan Masuk Bulan Ini: *${formatRupiah(totalTabunganBulanIni)}*

*TOTAL SALDO AKUMULASI TABUNGAN ANDA:*
 *${formatRupiah(totalSaldoAkumulasi)}*
━━━━━━━━━━━━━━━━━━━━━
_Transparansi pencatatan digital IuranQ RT 01 RW 03 Desa Purbayasa._
        """.trimIndent()
    }

    fun buildQrStickerShareMessage(
        warga: WargaEntity
    ): String {
        return """
*STIKER DIGITAL QR CODE JIMPITAN WARGA*
*RT 01 RW 03 DESA PURBAYASA*
━━━━━━━━━━━━━━━━━━━━━
Yth. *${warga.nama}*
ID Warga: `${warga.id}`
Alamat: ${warga.alamat}
Kode QR: `IURANQ:${warga.id}`

_Stiker QR ini dapat ditempel di depan pintu/teras rumah untuk memudahkan petugas ronda malam memindai setoran Jimpitan & Tabungan harian secara instan._
        """.trimIndent()
    }

    fun buildKurbanProgressMessage(
        warga: WargaEntity,
        namaKelompok: String,
        terkumpul: Long,
        target: Long
    ): String {
        val persen = if (target > 0) (terkumpul * 100 / target).toInt() else 0
        val sisa = if (target > terkumpul) target - terkumpul else 0L

        return """
*UPDATE TABUNGAN KURBAN 1447 H*
*RT 01 RW 03 DESA PURBAYASA*
━━━━━━━━━━━━━━━━━━━━━
Yth. *${warga.nama}*
Kelompok: *${namaKelompok}*

*Progress Tabungan Kurban Anda:*
• Terkumpul: *${formatRupiah(terkumpul)}*
• Target Per Orang: *${formatRupiah(target)}*
• Pencapaian: *${persen}%*
• Sisa Kekurangan: *${formatRupiah(sisa)}*

_Semoga dimudahkan dalam menunaikan ibadah kurban tahun ini._
        """.trimIndent()
    }

    fun openWhatsAppChat(context: Context, rawPhoneNumber: String, message: String = "") {
        sendWhatsApp(context, rawPhoneNumber, message)
    }

    fun sendWhatsApp(context: Context, rawPhoneNumber: String, message: String) {
        try {
            var formattedPhone = rawPhoneNumber.replace(Regex("[^0-9]"), "")
            if (formattedPhone.startsWith("0")) {
                formattedPhone = "62" + formattedPhone.substring(1)
            } else if (!formattedPhone.startsWith("62")) {
                formattedPhone = "62$formattedPhone"
            }

            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback generic share sheet
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(Intent.createChooser(shareIntent, "Kirim via WhatsApp / Bagikan"))
            } catch (err: Exception) {
                Toast.makeText(context, "Tidak dapat membuka WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
