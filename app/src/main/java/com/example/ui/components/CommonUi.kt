package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Warga
import com.example.util.CurrencyUtils
import com.example.util.QrBarcodeGenerator

@Composable
fun StatCard(
  title: String,
  value: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
  ElevatedCard(
    modifier = modifier,
    colors = CardDefaults.elevatedCardColors(
      containerColor = containerColor,
      contentColor = contentColor
    ),
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = contentColor.copy(alpha = 0.8f)
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .background(contentColor.copy(alpha = 0.15f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = contentColor
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = contentColor.copy(alpha = 0.7f)
        )
      }
    }
  }
}

@Composable
fun CatatJimpitanDialog(
  warga: Warga,
  petugasName: String,
  onDismiss: () -> Unit,
  onSave: (amount: Long, category: String, notes: String, paymentMethod: String) -> Unit
) {
  var selectedAmount by remember { mutableLongStateOf(warga.defaultTarif) }
  var customAmountText by remember { mutableStateOf(warga.defaultTarif.toString()) }
  var selectedCategory by remember { mutableStateOf("Jimpitan Harian") }
  var selectedPaymentMethod by remember { mutableStateOf("Tunai / Koin") }
  var notes by remember { mutableStateOf("") }
  val presetAmounts = listOf(1000L, 2000L, 5000L, 10000L, 20000L, 50000L)

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
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Paid,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Pencatatan Jimpitan",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "Scan Berhasil Terverifikasi",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_catat_jimpitan_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "Tutup")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Warga Info Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = warga.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${warga.houseNumber} (${warga.rtRw})",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            if (warga.notes.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Catatan: ${warga.notes}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Nominal Buttons
        Text(
          text = "Pilih Nominal Uang Masuk:",
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          presetAmounts.forEach { preset ->
            val isSelected = selectedAmount == preset
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedAmount = preset
                customAmountText = preset.toString()
              },
              label = {
                Text(CurrencyUtils.formatRupiah(preset), fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
              ),
              modifier = Modifier.testTag("preset_amount_${preset}")
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Custom Nominal Input
        OutlinedTextField(
          value = customAmountText,
          onValueChange = { input ->
            val filtered = input.filter { it.isDigit() }
            customAmountText = filtered
            selectedAmount = filtered.toLongOrNull() ?: 0L
          },
          label = { Text("Nominal Khusus (Rp)") },
          prefix = { Text("Rp ") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_nominal_jimpitan"),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Kategori & Metode
        Text(
          text = "Kategori Iuran:",
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Jimpitan Harian", "Titipan Ronda", "Sukarela").forEach { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontSize = 11.sp) },
              modifier = Modifier.testTag("cat_$cat")
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Catatan Tambahan
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Keterangan (Opsional)") },
          placeholder = { Text("Contoh: Koin kaleng, titip 2 hari", fontSize = 12.sp) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_catatan_jimpitan"),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("batal_catat_jimpitan_button")
          ) {
            Text("Batal")
          }
          Spacer(modifier = Modifier.width(10.dp))
          Button(
            onClick = {
              if (selectedAmount > 0) {
                onSave(selectedAmount, selectedCategory, notes, selectedPaymentMethod)
              }
            },
            enabled = selectedAmount > 0,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("simpan_jimpitan_button")
          ) {
            Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Simpan ${CurrencyUtils.formatRupiah(selectedAmount)}")
          }
        }
      }
    }
  }
}

/**
 * Special Security Verification Dialog for editing/deleting kas expenditures
 * As mandated: "tambahkan fitur edit uang uang pengeluaran dan ada verifikasi khusus"
 */
@Composable
fun VerifikasiKhususDialog(
  actionTitle: String,
  expenseTitle: String,
  currentAmount: Long,
  onDismiss: () -> Unit,
  onVerified: (adminPin: String, reason: String) -> Unit
) {
  var pinInput by remember { mutableStateOf("") }
  var reasonInput by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Box(
        modifier = Modifier
          .size(48.dp)
          .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.Security,
          contentDescription = "Verifikasi Keamanan",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(28.dp)
        )
      }
    },
    title = {
      Text(
        text = "Verifikasi Khusus Pengurus RT",
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontSize = 17.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "Perubahan pengeluaran kas memerlukan otorisasi PIN pengurus/admin dan pencatatan alasan audit transparansi warga.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "Pengeluaran: $expenseTitle", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(text = "Nominal Terdaftar: ${CurrencyUtils.formatRupiah(currentAmount)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = pinInput,
          onValueChange = {
            if (it.length <= 6) {
              pinInput = it
              errorMessage = null
            }
          },
          label = { Text("Masukkan PIN Keamanan Admin (Default: 123456)") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          visualTransformation = PasswordVisualTransformation(),
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pin_verifikasi_khusus")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = reasonInput,
          onValueChange = {
            reasonInput = it
            errorMessage = null
          },
          label = { Text("Alasan Perubahan / Koreksi *") },
          placeholder = { Text("Contoh: Koreksi nominal bon nota pembelian", fontSize = 11.sp) },
          singleLine = false,
          maxLines = 3,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("alasan_verifikasi_khusus")
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = errorMessage!!,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (pinInput.isBlank()) {
            errorMessage = "PIN keamanan tidak boleh kosong!"
          } else if (reasonInput.trim().length < 4) {
            errorMessage = "Mohon isi alasan perubahan secara jelas (min. 4 karakter)"
          } else {
            onVerified(pinInput.trim(), reasonInput.trim())
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("konfirmasi_verifikasi_khusus_button")
      ) {
        Text("Verifikasi & Lanjutkan")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("batal_verifikasi_khusus_button")
      ) {
        Text("Batal")
      }
    }
  )
}
