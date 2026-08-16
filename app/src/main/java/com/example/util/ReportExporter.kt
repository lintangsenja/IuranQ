package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.KamarKasEntity
import com.example.data.TransaksiEntity
import com.example.data.WargaEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {

    private fun formatRupiah(amount: Long): String {
        return "Rp" + String.format(Locale.GERMANY, "%,d", amount)
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
    }

    // ==========================================
    // 1. EXCEL EXPORT (Padat & Efisien max col BK)
    // ==========================================
    fun exportExcel(
        context: Context,
        transaksiList: List<TransaksiEntity>,
        kasList: List<KamarKasEntity>,
        wargaList: List<WargaEntity>
    ) {
        try {
            val fileName = "Laporan_Keuangan_IuranQ_RT01RW03_${System.currentTimeMillis()}.xlsx"
            val file = File(context.cacheDir, fileName)

            val xmlBuilder = StringBuilder()
            xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            xmlBuilder.append("<?mso-application progid=\"Excel.Sheet\"?>\n")
            xmlBuilder.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
            xmlBuilder.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n")
            xmlBuilder.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n")
            xmlBuilder.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
            xmlBuilder.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n")

            // Styles
            xmlBuilder.append("<Styles>\n")
            xmlBuilder.append(" <Style ss:ID=\"Header\"><Font ss:Bold=\"1\" ss:Size=\"14\" ss:Color=\"#FFFFFF\"/><Interior ss:Color=\"#1E1B4B\" ss:Pattern=\"Solid\"/><Alignment ss:Horizontal=\"Center\"/></Style>\n")
            xmlBuilder.append(" <Style ss:ID=\"SubHeader\"><Font ss:Bold=\"1\" ss:Size=\"11\" ss:Color=\"#FFFFFF\"/><Interior ss:Color=\"#0F766E\" ss:Pattern=\"Solid\"/><Alignment ss:Horizontal=\"Center\"/></Style>\n")
            xmlBuilder.append(" <Style ss:ID=\"ColHeader\"><Font ss:Bold=\"1\" ss:Size=\"10\" ss:Color=\"#FFFFFF\"/><Interior ss:Color=\"#4338CA\" ss:Pattern=\"Solid\"/><Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\"/></Borders></Style>\n")
            xmlBuilder.append(" <Style ss:ID=\"Data\"><Font ss:Size=\"10\"/></Style>\n")
            xmlBuilder.append(" <Style ss:ID=\"Currency\"><Font ss:Size=\"10\"/><NumberFormat ss:Format=\"Rp#,##0\"/></Style>\n")
            xmlBuilder.append(" <Style ss:ID=\"Total\"><Font ss:Bold=\"1\" ss:Size=\"11\" ss:Color=\"#1E1B4B\"/><Interior ss:Color=\"#E0E7FF\" ss:Pattern=\"Solid\"/></Style>\n")
            xmlBuilder.append("</Styles>\n")

            // SHEET 1: Buku Kas Umum (Transactions)
            xmlBuilder.append("<Worksheet ss:Name=\"Buku Kas Umum\">\n")
            xmlBuilder.append("<Table ss:ExpandedColumnCount=\"20\">\n")

            // Col widths
            xmlBuilder.append("<Column ss:Width=\"40\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"110\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"110\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"140\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"120\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"70\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"90\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"90\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"90\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"220\"/>\n")
            xmlBuilder.append("<Column ss:Width=\"110\"/>\n")

            // Title Row
            xmlBuilder.append("<Row ss:StyleID=\"Header\">\n")
            xmlBuilder.append("<Cell ss:MergeAcross=\"10\"><Data ss:Type=\"String\">LAPORAN KEUANGAN KAS WARGA RT 01 RW 03 DESA PURBAYASA (IuranQ)</Data></Cell>\n")
            xmlBuilder.append("</Row>\n")

            // Subtitle
            xmlBuilder.append("<Row ss:StyleID=\"SubHeader\">\n")
            xmlBuilder.append("<Cell ss:MergeAcross=\"10\"><Data ss:Type=\"String\">Periode: ${SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(Date())} | Dicetak: ${formatDate(System.currentTimeMillis())}</Data></Cell>\n")
            xmlBuilder.append("</Row>\n")

            // Empty row
            xmlBuilder.append("<Row/>\n")

            // Table Headers (Columns up to dense layout)
            xmlBuilder.append("<Row ss:StyleID=\"ColHeader\">\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">No</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Waktu Transaksi</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Kode TRX</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Nama Warga</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Kamar Kas</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Jenis</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Nominal Total</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Alokasi Jimpitan</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Overflow Tabungan</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Keterangan</Data></Cell>\n")
            xmlBuilder.append("<Cell><Data ss:Type=\"String\">Petugas</Data></Cell>\n")
            xmlBuilder.append("</Row>\n")

            var totalMasuk = 0L
            var totalKeluar = 0L

            transaksiList.forEachIndexed { index, trx ->
                if (trx.jenisMutasi == "MASUK") totalMasuk += trx.nominal else totalKeluar += trx.nominal
                xmlBuilder.append("<Row ss:StyleID=\"Data\">\n")
                xmlBuilder.append("<Cell><Data ss:Type=\"Number\">${index + 1}</Data></Cell>\n")
                xmlBuilder.append("<Cell><Data ss:Type=\"String\">${formatDate(trx.timestamp)}</Data></Cell>\n")
                xmlBuilder.append("<Cell><Data ss:Type=\"String\">${trx.kodeTransaksi}</Data></Cell>\n")
                xmlBuilder.append("<Cell><Data ss:Type=\"String\">${trx.namaWarga ?: "-"}</Data></Cell>\n")
                xmlBuilder.append("<Cell><Data ss:Type=\"String\">${trx.kamarKasId}</Data></Cell>\n")
                xmlBuilder.append("<Cell><Data ss:Type=\"String\">${trx.jenisMutasi}</Data></Cell>\n")
                xmlBuilder.append("<Cell ss:StyleID=\"Currency\"><Data ss:Type=\"Number\">${trx.nominal}</Data></Cell>\n")
                xmlBuilder.append("<Cell ss:StyleID=\"Currency\"><Data ss:Type=\"Number\">${trx.nominalTargetJimpitan}</Data></Cell>\n")
                xmlBuilder.append("<Cell ss:StyleID=\"Currency\"><Data ss:Type=\"Number\">${trx.nominalOverflowTabungan}</Data></Cell>\n")
                xmlBuilder.append("<Cell><Data ss:Type=\"String\">${escapeXml(trx.keterangan)}</Data></Cell>\n")
                xmlBuilder.append("<Cell><Data ss:Type=\"String\">${trx.petugas}</Data></Cell>\n")
                xmlBuilder.append("</Row>\n")
            }

            // Total Summary Row
            xmlBuilder.append("<Row ss:StyleID=\"Total\">\n")
            xmlBuilder.append("<Cell ss:MergeAcross=\"5\"><Data ss:Type=\"String\">TOTAL PENERIMAAN KAS:</Data></Cell>\n")
            xmlBuilder.append("<Cell ss:StyleID=\"Currency\"><Data ss:Type=\"Number\">$totalMasuk</Data></Cell>\n")
            xmlBuilder.append("<Cell ss:MergeAcross=\"3\"><Data ss:Type=\"String\">Pengeluaran: ${formatRupiah(totalKeluar)} | Saldo Bersih: ${formatRupiah(totalMasuk - totalKeluar)}</Data></Cell>\n")
            xmlBuilder.append("</Row>\n")

            xmlBuilder.append("</Table>\n")
            xmlBuilder.append("</Worksheet>\n")

            // SHEET 2: Ringkasan Saldo Multi-Kamar Kas
            xmlBuilder.append("<Worksheet ss:Name=\"Saldo Kamar Kas\">\n")
            xmlBuilder.append("<Table ss:ExpandedColumnCount=\"10\">\n")
            xmlBuilder.append("<Column ss:Width=\"40\"/><Column ss:Width=\"180\"/><Column ss:Width=\"130\"/><Column ss:Width=\"140\"/><Column ss:Width=\"240\"/>\n")
            xmlBuilder.append("<Row ss:StyleID=\"Header\"><Cell ss:MergeAcross=\"4\"><Data ss:Type=\"String\">REKAP SALDO PER KAMAR KAS RT 01 RW 03</Data></Cell></Row>\n")
            xmlBuilder.append("<Row ss:StyleID=\"ColHeader\"><Cell><Data ss:Type=\"String\">No</Data></Cell><Cell><Data ss:Type=\"String\">Nama Kamar Kas</Data></Cell><Cell><Data ss:Type=\"String\">Kode Kategori</Data></Cell><Cell><Data ss:Type=\"String\">Saldo Saat Ini</Data></Cell><Cell><Data ss:Type=\"String\">Deskripsi Peruntukan</Data></Cell></Row>\n")
            var totalSemuaKas = 0L
            kasList.forEachIndexed { i, kas ->
                totalSemuaKas += kas.saldoTotal
                xmlBuilder.append("<Row ss:StyleID=\"Data\"><Cell><Data ss:Type=\"Number\">${i + 1}</Data></Cell><Cell><Data ss:Type=\"String\">${kas.namaKas}</Data></Cell><Cell><Data ss:Type=\"String\">${kas.id}</Data></Cell><Cell ss:StyleID=\"Currency\"><Data ss:Type=\"Number\">${kas.saldoTotal}</Data></Cell><Cell><Data ss:Type=\"String\">${escapeXml(kas.deskripsi)}</Data></Cell></Row>\n")
            }
            xmlBuilder.append("<Row ss:StyleID=\"Total\"><Cell ss:MergeAcross=\"2\"><Data ss:Type=\"String\">TOTAL AKUMULASI DANA RT:</Data></Cell><Cell ss:StyleID=\"Currency\"><Data ss:Type=\"Number\">$totalSemuaKas</Data></Cell><Cell><Data ss:Type=\"String\">Status: AMAN & TERVERIFIKASI</Data></Cell></Row>\n")
            xmlBuilder.append("</Table>\n")
            xmlBuilder.append("</Worksheet>\n")

            // SHEET 3: Rekap Warga & Status Kas Air Whitelist
            xmlBuilder.append("<Worksheet ss:Name=\"Data Warga & Kas Air\">\n")
            xmlBuilder.append("<Table ss:ExpandedColumnCount=\"10\">\n")
            xmlBuilder.append("<Column ss:Width=\"40\"/><Column ss:Width=\"90\"/><Column ss:Width=\"160\"/><Column ss:Width=\"130\"/><Column ss:Width=\"110\"/><Column ss:Width=\"120\"/><Column ss:Width=\"120\"/>\n")
            xmlBuilder.append("<Row ss:StyleID=\"Header\"><Cell ss:MergeAcross=\"6\"><Data ss:Type=\"String\">DAFTAR WARGA, SALDO TABUNGAN &amp; STATUS KAS AIR</Data></Cell></Row>\n")
            xmlBuilder.append("<Row ss:StyleID=\"ColHeader\"><Cell><Data ss:Type=\"String\">No</Data></Cell><Cell><Data ss:Type=\"String\">ID Warga</Data></Cell><Cell><Data ss:Type=\"String\">Nama Lengkap</Data></Cell><Cell><Data ss:Type=\"String\">No WhatsApp</Data></Cell><Cell><Data ss:Type=\"String\">RT / RW</Data></Cell><Cell><Data ss:Type=\"String\">Status Kas Air</Data></Cell><Cell><Data ss:Type=\"String\">Saldo Tabungan</Data></Cell></Row>\n")
            wargaList.forEachIndexed { i, w ->
                xmlBuilder.append("<Row ss:StyleID=\"Data\"><Cell><Data ss:Type=\"Number\">${i + 1}</Data></Cell><Cell><Data ss:Type=\"String\">${w.id}</Data></Cell><Cell><Data ss:Type=\"String\">${w.nama}</Data></Cell><Cell><Data ss:Type=\"String\">${w.noHpWa}</Data></Cell><Cell><Data ss:Type=\"String\">RT ${w.rt} / RW ${w.rw}</Data></Cell><Cell><Data ss:Type=\"String\">${w.statusAir}</Data></Cell><Cell ss:StyleID=\"Currency\"><Data ss:Type=\"Number\">${w.saldoTabungan}</Data></Cell></Row>\n")
            }
            xmlBuilder.append("</Table>\n")
            xmlBuilder.append("</Worksheet>\n")

            xmlBuilder.append("</Workbook>")

            FileOutputStream(file).use { out ->
                out.write(xmlBuilder.toString().toByteArray(Charsets.UTF_8))
            }

            shareFile(context, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Laporan Keuangan Excel (.xlsx)")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal ekspor Excel: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ==========================================
    // 2. WORD EXPORT (.docx Rich Document)
    // ==========================================
    fun exportWord(
        context: Context,
        transaksiList: List<TransaksiEntity>,
        kasList: List<KamarKasEntity>,
        wargaList: List<WargaEntity>
    ) {
        try {
            val fileName = "Laporan_Keuangan_IuranQ_RT01RW03_${System.currentTimeMillis()}.doc"
            val file = File(context.cacheDir, fileName)

            val totalMasuk = transaksiList.filter { it.jenisMutasi == "MASUK" }.sumOf { it.nominal }
            val totalKeluar = transaksiList.filter { it.jenisMutasi == "KELUAR" }.sumOf { it.nominal }
            val totalKasSemua = kasList.sumOf { it.saldoTotal }

            val html = """
                <html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
                <head>
                <meta charset="utf-8">
                <title>Laporan Keuangan RT 01 RW 03 Desa Purbayasa</title>
                <style>
                    body { font-family: 'Calibri', sans-serif; margin: 30px; color: #1e1b4b; }
                    .header { text-align: center; border-bottom: 3px double #1e1b4b; padding-bottom: 12px; margin-bottom: 20px; }
                    .title { font-size: 18pt; font-weight: bold; margin: 0; color: #1e1b4b; }
                    .subtitle { font-size: 12pt; margin: 4px 0; color: #4338ca; }
                    .address { font-size: 9.5pt; color: #64748b; }
                    table { width: 100%; border-collapse: collapse; margin-top: 15px; margin-bottom: 20px; }
                    th { background-color: #4338ca; color: white; border: 1px solid #312e81; padding: 8px; font-size: 10pt; text-align: left; }
                    td { border: 1px solid #cbd5e1; padding: 7px; font-size: 9.5pt; }
                    tr:nth-child(even) { background-color: #f8fafc; }
                    .summary-box { background: #f1f5f9; border-left: 5px solid #0d9488; padding: 14px; margin: 15px 0; }
                    .signature-table { width: 100%; margin-top: 40px; border: none; }
                    .signature-table td { border: none; text-align: center; width: 33%; }
                </style>
                </head>
                <body>
                    <div class="header">
                        <div class="title">RUKUN TETANGGA 01 RUKUN WARGA 03</div>
                        <div class="subtitle">DESA PURBAYASA KECAMATAN PADAMARA</div>
                        <div class="address">Aplikasi Manajemen Keuangan Komunitas IuranQ - Dicetak: ${formatDate(System.currentTimeMillis())}</div>
                    </div>

                    <div class="summary-box">
                        <h3 style="margin-top:0; color:#0f766e;">RINGKASAN EKSEKUTIF KAS</h3>
                        <p>• <b>Total Saldo Kas RT:</b> ${formatRupiah(totalKasSemua)}</p>
                        <p>• <b>Total Mutasi Masuk:</b> ${formatRupiah(totalMasuk)} | <b>Total Mutasi Keluar:</b> ${formatRupiah(totalKeluar)}</p>
                        <p>• <b>Jumlah Warga Terdaftar:</b> ${wargaList.size} KK</p>
                    </div>

                    <h3>1. RINCIAN SALDO PER KAMAR KAS</h3>
                    <table>
                        <tr>
                            <th>No</th>
                            <th>Nama Kamar Kas</th>
                            <th>Kategori</th>
                            <th>Saldo Terakhir</th>
                            <th>Peruntukan</th>
                        </tr>
                        ${kasList.mapIndexed { i, k ->
                            "<tr><td>${i+1}</td><td><b>${k.namaKas}</b></td><td>${k.id}</td><td>${formatRupiah(k.saldoTotal)}</td><td>${k.deskripsi}</td></tr>"
                        }.joinToString("")}
                    </table>

                    <h3>2. CATATAN MUTASI &amp; TRANSAKSI TERAKHIR</h3>
                    <table>
                        <tr>
                            <th>Waktu</th>
                            <th>Kode</th>
                            <th>Nama Warga</th>
                            <th>Kamar Kas</th>
                            <th>Jenis</th>
                            <th>Nominal</th>
                            <th>Keterangan</th>
                        </tr>
                        ${transaksiList.take(50).map { t ->
                            "<tr><td>${formatDate(t.timestamp)}</td><td>${t.kodeTransaksi}</td><td>${t.namaWarga ?: "-"}</td><td>${t.kamarKasId}</td><td><b>${t.jenisMutasi}</b></td><td>${formatRupiah(t.nominal)}</td><td>${t.keterangan}</td></tr>"
                        }.joinToString("")}
                    </table>

                    <table class="signature-table">
                        <tr>
                            <td>Mengetahui,<br><b>Ketua RT 01</b><br><br><br><br><u>( Bpk. Slamet Riyadi )</u></td>
                            <td><br><b>Petugas Lapangan</b><br><br><br><br><u>( Petugas Piket )</u></td>
                            <td>Purbayasa, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}<br><b>Bendahara RT</b><br><br><br><br><u>( Bpk. Ahmad Dahlan )</u></td>
                        </tr>
                    </table>
                </body>
                </html>
            """.trimIndent()

            FileOutputStream(file).use { out ->
                out.write(html.toByteArray(Charsets.UTF_8))
            }

            shareFile(context, file, "application/msword", "Laporan Keuangan Word (.doc)")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal ekspor Word: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ==========================================
    // 3. PDF EXPORT (Android Native PdfDocument)
    // ==========================================
    fun exportPdf(
        context: Context,
        transaksiList: List<TransaksiEntity>,
        kasList: List<KamarKasEntity>,
        wargaList: List<WargaEntity>
    ) {
        try {
            val fileName = "Laporan_Keuangan_IuranQ_RT01RW03_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)

            val document = PdfDocument()
            val pageWidth = 595 // A4 standard width
            val pageHeight = 842 // A4 standard height
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint().apply { isAntiAlias = true }

            // Header Background Bar
            paint.color = Color.rgb(30, 27, 75) // #1E1B4B
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, paint)

            // Header Text
            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 15f
            canvas.drawText("PEMERINTAH KABUPATEN PURBALINGGA", 30f, 32f, paint)
            paint.textSize = 13f
            canvas.drawText("RUKUN TETANGGA 01 RW 03 DESA PURBAYASA", 30f, 52f, paint)
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Laporan Keuangan Terpadu Multi-Kamar Kas (IuranQ) • Dicetak: ${formatDate(System.currentTimeMillis())}", 30f, 75f, paint)

            var yPos = 120f

            // Executive Summary Card
            paint.color = Color.rgb(241, 245, 249)
            canvas.drawRoundRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 65f, 8f, 8f, paint)

            val totalKas = kasList.sumOf { it.saldoTotal }
            val totalMasuk = transaksiList.filter { it.jenisMutasi == "MASUK" }.sumOf { it.nominal }
            val totalKeluar = transaksiList.filter { it.jenisMutasi == "KELUAR" }.sumOf { it.nominal }

            paint.color = Color.rgb(15, 118, 110)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 10.5f
            canvas.drawText("RINGKASAN TOTAL KAS: ${formatRupiah(totalKas)}", 45f, yPos + 22f, paint)

            paint.color = Color.rgb(51, 65, 85)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 9f
            canvas.drawText("Total Masuk: ${formatRupiah(totalMasuk)}   |   Total Keluar: ${formatRupiah(totalKeluar)}   |   Warga: ${wargaList.size} KK", 45f, yPos + 45f, paint)

            yPos += 85f

            // Section 1: Saldo Multi-Kamar Kas
            paint.color = Color.rgb(67, 56, 202)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 11f
            canvas.drawText("1. SALDO MULTI-KAMAR KAS", 30f, yPos, paint)

            yPos += 12f
            // Table Header
            paint.color = Color.rgb(224, 231, 255)
            canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 18f, paint)

            paint.color = Color.rgb(30, 27, 75)
            paint.textSize = 8.5f
            canvas.drawText("KAMAR KAS", 35f, yPos + 12f, paint)
            canvas.drawText("KATEGORI", 220f, yPos + 12f, paint)
            canvas.drawText("SALDO TERAKHIR", 430f, yPos + 12f, paint)

            yPos += 18f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            kasList.take(8).forEach { kas ->
                paint.color = Color.rgb(248, 250, 252)
                canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 16f, paint)
                paint.color = Color.rgb(30, 41, 59)
                canvas.drawText(kas.namaKas, 35f, yPos + 12f, paint)
                canvas.drawText(kas.id, 220f, yPos + 12f, paint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(formatRupiah(kas.saldoTotal), 430f, yPos + 12f, paint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                yPos += 17f
            }

            yPos += 15f
            // Section 2: Transaksi Terakhir
            paint.color = Color.rgb(67, 56, 202)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 11f
            canvas.drawText("2. DAFTAR MUTASI & TRANSAKSI TERAKHIR", 30f, yPos, paint)

            yPos += 12f
            paint.color = Color.rgb(224, 231, 255)
            canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 18f, paint)
            paint.color = Color.rgb(30, 27, 75)
            paint.textSize = 8f
            canvas.drawText("WAKTU / KODE", 35f, yPos + 12f, paint)
            canvas.drawText("WARGA / KAS", 170f, yPos + 12f, paint)
            canvas.drawText("JENIS", 340f, yPos + 12f, paint)
            canvas.drawText("NOMINAL", 400f, yPos + 12f, paint)
            canvas.drawText("KETERANGAN", 475f, yPos + 12f, paint)

            yPos += 18f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            transaksiList.take(16).forEach { trx ->
                paint.color = Color.rgb(30, 41, 59)
                paint.textSize = 7.5f
                canvas.drawText(formatDate(trx.timestamp).substring(0, 10) + " " + trx.kodeTransaksi, 35f, yPos + 11f, paint)
                val targetText = (trx.namaWarga ?: trx.kamarKasId).let { if (it.length > 22) it.take(20) + ".." else it }
                canvas.drawText(targetText, 170f, yPos + 11f, paint)
                canvas.drawText(trx.jenisMutasi, 340f, yPos + 11f, paint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(formatRupiah(trx.nominal), 400f, yPos + 11f, paint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                val ketText = trx.keterangan.let { if (it.length > 18) it.take(16) + ".." else it }
                canvas.drawText(ketText, 475f, yPos + 11f, paint)

                yPos += 15f
            }

            // Footer Signature Area
            yPos = 740f
            paint.color = Color.rgb(100, 116, 139)
            paint.textSize = 8.5f
            canvas.drawText("Mengetahui, Ketua RT 01", 60f, yPos, paint)
            canvas.drawText("Bendahara RT 01", 410f, yPos, paint)

            canvas.drawText("( Slamet Riyadi )", 60f, yPos + 55f, paint)
            canvas.drawText("( Ahmad Dahlan )", 410f, yPos + 55f, paint)

            document.finishPage(page)

            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            shareFile(context, file, "application/pdf", "Laporan Keuangan PDF")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal ekspor PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri: Uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan $title"))
    }

    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}
