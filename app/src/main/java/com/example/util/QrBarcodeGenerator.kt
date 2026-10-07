package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.util.EnumMap

object QrBarcodeGenerator {

  fun generateQrCodeBitmap(
    text: String,
    width: Int = 512,
    height: Int = 512
  ): Bitmap? {
    return try {
      val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
        put(EncodeHintType.MARGIN, 1)
      }
      val bitMatrix: BitMatrix = MultiFormatWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        width,
        height,
        hints
      )
      createBitmapFromMatrix(bitMatrix)
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun generateBarcode128Bitmap(
    text: String,
    width: Int = 600,
    height: Int = 200
  ): Bitmap? {
    return try {
      val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
        put(EncodeHintType.CHARACTER_SET, "UTF-8")
        put(EncodeHintType.MARGIN, 1)
      }
      val bitMatrix: BitMatrix = MultiFormatWriter().encode(
        text,
        BarcodeFormat.CODE_128,
        width,
        height,
        hints
      )
      createBitmapFromMatrix(bitMatrix)
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  private fun createBitmapFromMatrix(matrix: BitMatrix): Bitmap {
    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
      val offset = y * width
      for (x in 0 until width) {
        pixels[offset + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
      }
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
  }
}
