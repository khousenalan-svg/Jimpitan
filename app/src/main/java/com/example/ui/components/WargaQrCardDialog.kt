package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.data.model.Warga
import com.example.util.CurrencyUtils
import com.example.util.QrBarcodeGenerator
import java.io.File
import java.io.FileOutputStream

@Composable
fun WargaQrCardDialog(
  warga: Warga,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val qrBitmap = remember(warga.barcodeData) {
    QrBarcodeGenerator.generateQrCodeBitmap(warga.barcodeData, 400, 400)
  }
  val barcode128Bitmap = remember(warga.barcodeData) {
    QrBarcodeGenerator.generateBarcode128Bitmap(warga.barcodeData, 500, 160)
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Kartu Barcode Warga",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_qr_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "Tutup")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Printable / Displayable Card Container
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
        ) {
          Column(
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Card Header
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "KARTU IURAN JIMPITAN WARGA",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
                Text(
                  text = warga.rtRw,
                  color = Color.White.copy(alpha = 0.9f),
                  fontSize = 10.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resident Info
            Text(
              text = warga.name,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color(0xFF0F172A),
              textAlign = TextAlign.Center
            )
            Text(
              text = warga.houseNumber,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "Tarif Default: ${CurrencyUtils.formatRupiah(warga.defaultTarif)} / malam",
              fontSize = 11.sp,
              color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // QR Code Box
            Box(
              modifier = Modifier
                .size(190.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(8.dp),
              contentAlignment = Alignment.Center
            ) {
              if (qrBitmap != null) {
                Image(
                  bitmap = qrBitmap.asImageBitmap(),
                  contentDescription = "QR Code Warga ${warga.name}",
                  modifier = Modifier.size(174.dp)
                )
              } else {
                Text("Gagal membuat QR", fontSize = 12.sp, color = Color.Red)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1D Barcode
            if (barcode128Bitmap != null) {
              Image(
                bitmap = barcode128Bitmap.asImageBitmap(),
                contentDescription = "Barcode 128",
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
              )
            }

            Text(
              text = warga.barcodeData,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "★ Tempelkan di dekat pintu rumah, pagar, atau kotak jimpitan ★",
              fontSize = 9.sp,
              color = Color.Gray,
              textAlign = TextAlign.Center
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .testTag("tutup_qr_card_button")
          ) {
            Text("Tutup")
          }

          Button(
            onClick = {
              shareQrCard(context, warga, qrBitmap)
            },
            modifier = Modifier
              .weight(1f)
              .testTag("bagikan_qr_card_button")
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Bagikan")
          }
        }
      }
    }
  }
}

private fun shareQrCard(context: Context, warga: Warga, qrBitmap: Bitmap?) {
  if (qrBitmap == null) return
  try {
    val cacheDir = File(context.cacheDir, "reports")
    if (!cacheDir.exists()) cacheDir.mkdirs()
    val file = File(cacheDir, "Kartu_Jimpitan_${warga.id}.png")
    val fos = FileOutputStream(file)
    qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos.close()

    val uri: Uri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "image/png"
      putExtra(Intent.EXTRA_STREAM, uri)
      putExtra(
        Intent.EXTRA_TEXT,
        "Kartu Jimpitan Digital RT 04 / RW 08\nNama: ${warga.name}\nRumah: ${warga.houseNumber}\nID Barcode: ${warga.barcodeData}"
      )
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Bagikan Kartu Jimpitan"))
  } catch (e: Exception) {
    e.printStackTrace()
    Toast.makeText(context, "Gagal membagikan: ${e.message}", Toast.LENGTH_SHORT).show()
  }
}
