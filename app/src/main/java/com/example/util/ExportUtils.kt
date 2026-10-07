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
import com.example.data.model.JimpitanTransaction
import com.example.data.model.PengeluaranKas
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {

  fun exportPdfReport(
    context: Context,
    monthTitle: String,
    totalIncome: Long,
    totalExpense: Long,
    netBalance: Long,
    transactions: List<JimpitanTransaction>,
    pengeluaranList: List<PengeluaranKas>,
    adminName: String
  ): File? {
    try {
      val document = PdfDocument()
      val pageWidth = 595 // A4 standard point width
      val pageHeight = 842 // A4 standard point height
      var pageNumber = 1

      var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
      var page = document.startPage(pageInfo)
      var canvas = page.canvas

      val paint = Paint().apply {
        isAntiAlias = true
      }

      var currentY = 40f

      // Header RT
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      paint.textSize = 15f
      paint.color = Color.rgb(15, 118, 110) // Teal RT
      canvas.drawText("PENGURUS RUKUN TETANGGA (RT 04 / RW 08)", 40f, currentY, paint)
      currentY += 18f

      paint.textSize = 12f
      paint.color = Color.BLACK
      canvas.drawText("LAPORAN PERTANGGUNGJAWABAN KAS & IURAN JIMPITAN WARGA", 40f, currentY, paint)
      currentY += 15f

      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      paint.textSize = 9f
      paint.color = Color.DKGRAY
      val printDate = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID")).format(Date())
      canvas.drawText("Periode: $monthTitle | Dicetak pada: $printDate | Petugas/Admin: $adminName", 40f, currentY, paint)
      currentY += 10f

      // Dividing Line
      paint.strokeWidth = 1.5f
      paint.color = Color.rgb(15, 118, 110)
      canvas.drawLine(40f, currentY, (pageWidth - 40).toFloat(), currentY, paint)
      currentY += 18f

      // Summary Box
      paint.color = Color.rgb(240, 253, 250) // Light teal background
      paint.style = Paint.Style.FILL
      canvas.drawRoundRect(40f, currentY, (pageWidth - 40).toFloat(), currentY + 55f, 6f, 6f, paint)

      paint.style = Paint.Style.STROKE
      paint.strokeWidth = 1f
      paint.color = Color.rgb(153, 246, 228)
      canvas.drawRoundRect(40f, currentY, (pageWidth - 40).toFloat(), currentY + 55f, 6f, 6f, paint)

      paint.style = Paint.Style.FILL
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      paint.textSize = 9f
      paint.color = Color.rgb(17, 94, 89)
      canvas.drawText("RINGKASAN KEUANGAN KAS BULAN INI", 50f, currentY + 16f, paint)

      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      paint.textSize = 9f
      paint.color = Color.BLACK
      canvas.drawText("Total Pemasukan Jimpitan: ${CurrencyUtils.formatRupiah(totalIncome)}", 50f, currentY + 32f, paint)
      canvas.drawText("Total Pengeluaran Kas RT: ${CurrencyUtils.formatRupiah(totalExpense)}", 50f, currentY + 46f, paint)

      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      paint.color = if (netBalance >= 0) Color.rgb(22, 101, 52) else Color.RED
      canvas.drawText("Sisa Saldo Kas Bersih: ${CurrencyUtils.formatRupiah(netBalance)}", 300f, currentY + 32f, paint)
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      paint.color = Color.BLACK
      canvas.drawText("Total Transaksi: ${transactions.size} setoran", 300f, currentY + 46f, paint)

      currentY += 75f

      // Section 1: Daftar Pemasukan Jimpitan
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      paint.textSize = 10f
      paint.color = Color.rgb(15, 118, 110)
      canvas.drawText("A. DAFTAR PEMASUKAN JIMPITAN WARGA", 40f, currentY, paint)
      currentY += 14f

      // Table Header
      paint.color = Color.rgb(226, 232, 240)
      paint.style = Paint.Style.FILL
      canvas.drawRect(40f, currentY, (pageWidth - 40).toFloat(), currentY + 16f, paint)

      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      paint.textSize = 8f
      paint.color = Color.BLACK
      canvas.drawText("No", 45f, currentY + 11f, paint)
      canvas.drawText("Tanggal", 65f, currentY + 11f, paint)
      canvas.drawText("Nama Warga", 125f, currentY + 11f, paint)
      canvas.drawText("Rumah / Blok", 240f, currentY + 11f, paint)
      canvas.drawText("Nominal", 330f, currentY + 11f, paint)
      canvas.drawText("Petugas Ronda", 400f, currentY + 11f, paint)
      canvas.drawText("Keterangan", 480f, currentY + 11f, paint)
      currentY += 18f

      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      paint.textSize = 7.5f

      for (i in transactions.indices) {
        if (currentY > pageHeight - 80) {
          document.finishPage(page)
          pageNumber++
          pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
          page = document.startPage(pageInfo)
          canvas = page.canvas
          currentY = 40f

          // Re-draw table subheader
          paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
          paint.textSize = 8f
          paint.color = Color.BLACK
          canvas.drawText("Daftar Pemasukan Jimpitan (Lanjutan Hal. $pageNumber)", 40f, currentY, paint)
          currentY += 16f
          paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
          paint.textSize = 7.5f
        }

        val tx = transactions[i]
        if (i % 2 == 1) {
          paint.color = Color.rgb(248, 250, 252)
          paint.style = Paint.Style.FILL
          canvas.drawRect(40f, currentY - 2f, (pageWidth - 40).toFloat(), currentY + 11f, paint)
        }
        paint.color = Color.BLACK
        canvas.drawText("${i + 1}", 45f, currentY + 9f, paint)
        canvas.drawText(tx.dateString, 65f, currentY + 9f, paint)
        val shortName = if (tx.wargaName.length > 20) tx.wargaName.take(18) + ".." else tx.wargaName
        canvas.drawText(shortName, 125f, currentY + 9f, paint)
        canvas.drawText(tx.houseNumber, 240f, currentY + 9f, paint)
        canvas.drawText(CurrencyUtils.formatRupiah(tx.amount), 330f, currentY + 9f, paint)
        val shortPetugas = if (tx.petugasName.length > 15) tx.petugasName.take(13) + ".." else tx.petugasName
        canvas.drawText(shortPetugas, 400f, currentY + 9f, paint)
        val noteText = if (tx.notes.isNotBlank()) tx.notes.take(15) else "-"
        canvas.drawText(noteText, 480f, currentY + 9f, paint)

        currentY += 13f
      }

      currentY += 15f

      // Section 2: Pengeluaran Kas RT
      if (currentY > pageHeight - 160) {
        document.finishPage(page)
        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        page = document.startPage(pageInfo)
        canvas = page.canvas
        currentY = 40f
      }

      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      paint.textSize = 10f
      paint.color = Color.rgb(180, 83, 9) // Amber/Dark Orange for expenses
      canvas.drawText("B. DAFTAR PENGELUARAN KAS RT (DIVERIFIKASI)", 40f, currentY, paint)
      currentY += 14f

      // Table Header Pengeluaran
      paint.color = Color.rgb(254, 243, 199)
      paint.style = Paint.Style.FILL
      canvas.drawRect(40f, currentY, (pageWidth - 40).toFloat(), currentY + 16f, paint)

      paint.color = Color.BLACK
      paint.textSize = 8f
      canvas.drawText("No", 45f, currentY + 11f, paint)
      canvas.drawText("Tanggal", 65f, currentY + 11f, paint)
      canvas.drawText("Keperluan Pengeluaran", 125f, currentY + 11f, paint)
      canvas.drawText("Kategori", 280f, currentY + 11f, paint)
      canvas.drawText("Nominal", 380f, currentY + 11f, paint)
      canvas.drawText("Otorisasi Admin", 470f, currentY + 11f, paint)
      currentY += 18f

      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      paint.textSize = 7.5f

      if (pengeluaranList.isEmpty()) {
        paint.color = Color.GRAY
        canvas.drawText("Tidak ada pengeluaran pada periode ini.", 45f, currentY + 10f, paint)
        currentY += 18f
      } else {
        for (i in pengeluaranList.indices) {
          if (currentY > pageHeight - 80) {
            document.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            currentY = 40f
          }
          val exp = pengeluaranList[i]
          paint.color = Color.BLACK
          canvas.drawText("${i + 1}", 45f, currentY + 9f, paint)
          canvas.drawText(exp.dateString, 65f, currentY + 9f, paint)
          val titleText = if (exp.title.length > 28) exp.title.take(26) + ".." else exp.title
          canvas.drawText(titleText, 125f, currentY + 9f, paint)
          canvas.drawText(exp.category, 280f, currentY + 9f, paint)
          canvas.drawText(CurrencyUtils.formatRupiah(exp.amount), 380f, currentY + 9f, paint)
          canvas.drawText(exp.authorizedByAdmin.take(15), 470f, currentY + 9f, paint)
          currentY += 13f
        }
      }

      currentY += 25f
      if (currentY > pageHeight - 90) {
        document.finishPage(page)
        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        page = document.startPage(pageInfo)
        canvas = page.canvas
        currentY = 40f
      }

      // Signature Section
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      paint.textSize = 8.5f
      paint.color = Color.BLACK

      canvas.drawText("Mengetahui,", 80f, currentY, paint)
      canvas.drawText("Bendahara RT 04,", 420f, currentY, paint)
      currentY += 12f
      canvas.drawText("Ketua RT 04 / RW 08", 80f, currentY, paint)
      canvas.drawText("Pengelola Kas Jimpitan", 420f, currentY, paint)

      currentY += 45f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("( H. Bambang Sudiro )", 75f, currentY, paint)
      canvas.drawText("( $adminName )", 415f, currentY, paint)

      document.finishPage(page)

      val reportsDir = File(context.cacheDir, "reports")
      if (!reportsDir.exists()) reportsDir.mkdirs()

      val fileName = "Laporan_Jimpitan_RT04_${System.currentTimeMillis()}.pdf"
      val file = File(reportsDir, fileName)
      val fos = FileOutputStream(file)
      document.writeTo(fos)
      fos.close()
      document.close()

      return file
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }

  fun exportExcelCsvReport(
    context: Context,
    monthTitle: String,
    totalIncome: Long,
    totalExpense: Long,
    netBalance: Long,
    transactions: List<JimpitanTransaction>,
    pengeluaranList: List<PengeluaranKas>
  ): File? {
    try {
      val reportsDir = File(context.cacheDir, "reports")
      if (!reportsDir.exists()) reportsDir.mkdirs()

      val fileName = "Laporan_Kas_Jimpitan_RT04_${System.currentTimeMillis()}.csv"
      val file = File(reportsDir, fileName)
      val fos = FileOutputStream(file)
      val writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)

      // Write UTF-8 BOM so Excel opens indonesian special characters and accents perfectly
      writer.write("\uFEFF")

      writer.write("LAPORAN KAS & IURAN JIMPITAN WARGA RT 04 / RW 08\n")
      writer.write("Periode:,\"$monthTitle\"\n")
      writer.write("Tanggal Unduh:,\"${CurrencyUtils.formatDateTimeDisplay(System.currentTimeMillis())}\"\n\n")

      writer.write("RINGKASAN KEUANGAN\n")
      writer.write("Total Pemasukan Jimpitan,${totalIncome}\n")
      writer.write("Total Pengeluaran Kas RT,${totalExpense}\n")
      writer.write("Sisa Saldo Kas Bersih,${netBalance}\n\n")

      writer.write("DATA TRANSAKSI JIMPITAN MASUK\n")
      writer.write("No,ID Transaksi,Tanggal,Waktu,ID Warga,Nama Warga,Blok/Rumah,Nominal (Rp),Petugas Ronda,Metode,Kategori,Keterangan\n")

      for (i in transactions.indices) {
        val tx = transactions[i]
        val timeOnly = CurrencyUtils.formatTimeOnly(tx.timestamp)
        val cleanNote = tx.notes.replace("\"", "\"\"")
        writer.write("${i + 1},${tx.id},\"${tx.dateString}\",\"$timeOnly\",\"${tx.wargaId}\",\"${tx.wargaName}\",\"${tx.houseNumber}\",${tx.amount},\"${tx.petugasName}\",\"${tx.paymentMethod}\",\"${tx.category}\",\"$cleanNote\"\n")
      }

      writer.write("\n")
      writer.write("DATA PENGELUARAN KAS RT (DIVERIFIKASI)\n")
      writer.write("No,ID Pengeluaran,Tanggal,Waktu,Keperluan,Kategori,Nominal (Rp),Otorisasi Admin,Alasan Koreksi/Status,Terakhir Diubah\n")

      for (i in pengeluaranList.indices) {
        val exp = pengeluaranList[i]
        val timeOnly = CurrencyUtils.formatTimeOnly(exp.timestamp)
        val cleanTitle = exp.title.replace("\"", "\"\"")
        val cleanReason = (exp.modificationReason ?: "-").replace("\"", "\"\"")
        val modifiedInfo = if (exp.lastModifiedBy != null) "${exp.lastModifiedBy} (${CurrencyUtils.formatDateTimeDisplay(exp.lastModifiedTimestamp ?: 0)})" else "Asli"
        writer.write("${i + 1},${exp.id},\"${exp.dateString}\",\"$timeOnly\",\"$cleanTitle\",\"${exp.category}\",${exp.amount},\"${exp.authorizedByAdmin}\",\"$cleanReason\",\"$modifiedInfo\"\n")
      }

      writer.flush()
      writer.close()
      fos.close()

      return file
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }

  fun shareExportedFile(context: Context, file: File, mimeType: String, title: String) {
    try {
      val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title - RT 04 / RW 08")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      val chooser = Intent.createChooser(intent, "Bagikan $title")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Gagal membagikan file: ${e.message}", Toast.LENGTH_LONG).show()
    }
  }
}
