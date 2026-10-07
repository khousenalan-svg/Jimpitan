package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserAccount
import com.example.data.model.UserRole

@Composable
fun LoginScreen(
  users: List<UserAccount>,
  onLogin: (user: UserAccount, pin: String) -> Boolean,
  onQuickLogin: (role: UserRole) -> Unit
) {
  var selectedUser by remember(users) {
    mutableStateOf(users.firstOrNull { it.role == UserRole.PETUGAS } ?: users.firstOrNull())
  }
  var pinInput by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(28.dp))

      // App Emblem / Banner
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(
            Brush.verticalGradient(
              listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.QrCodeScanner,
          contentDescription = "Jimpitan Logo",
          tint = Color.White,
          modifier = Modifier.size(44.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "JIMPITAN DIGITAL RT",
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 0.5.sp
      )
      Text(
        text = "Sistem Scan Barcode & Rekonsiliasi Kas RT 04 / RW 08",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Quick Role Switch Chips
      Text(
        text = "Pilih Akun Pengguna:",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        modifier = Modifier.align(Alignment.Start)
      )

      Spacer(modifier = Modifier.height(10.dp))

      users.forEach { user ->
        val isSelected = selectedUser?.id == user.id
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
              selectedUser = user
              pinInput = ""
              errorMessage = null
            }
            .testTag("user_card_${user.username}"),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.surface
          ),
          border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
          else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(
                  if (user.role == UserRole.ADMIN) Color(0xFFFDE68A) else Color(0xFFBAE6FD),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Nightlight,
                contentDescription = null,
                tint = if (user.role == UserRole.ADMIN) Color(0xFF92400E) else Color(0xFF0369A1),
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = user.fullName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = if (user.role == UserRole.ADMIN) "Admin / Bendahara / Ketua RT" else "Petugas Ronda Malam",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            if (isSelected) {
              Icon(
                Icons.Default.Check,
                contentDescription = "Terpilih",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // PIN Input Card
      selectedUser?.let { currentUser ->
        ElevatedCard(
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Verifikasi PIN Akun",
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Petunjuk uji coba: PIN untuk '${currentUser.username}' adalah '${currentUser.pin}'",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = pinInput,
              onValueChange = {
                if (it.length <= 6) {
                  pinInput = it
                  errorMessage = null
                }
              },
              label = { Text("Masukkan PIN") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
              visualTransformation = PasswordVisualTransformation(),
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_pin_input"),
              shape = RoundedCornerShape(10.dp)
            )

            if (errorMessage != null) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = {
                val ok = onLogin(currentUser, pinInput)
                if (!ok) {
                  errorMessage = "PIN salah! Gunakan PIN: ${currentUser.pin}"
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("login_submit_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Masuk ke Sistem", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Instant Quick-Test Buttons
      Text(
        text = "⚡ Akses Cepat Mode:",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = { onQuickLogin(UserRole.PETUGAS) },
          modifier = Modifier
            .weight(1f)
            .testTag("quick_login_petugas"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Nightlight, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Petugas Ronda", fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = { onQuickLogin(UserRole.ADMIN) },
          modifier = Modifier
            .weight(1f)
            .testTag("quick_login_admin"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Admin RT", fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}
