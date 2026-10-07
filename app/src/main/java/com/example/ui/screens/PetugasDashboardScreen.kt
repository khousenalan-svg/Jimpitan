package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JimpitanTransaction
import com.example.data.model.UserAccount
import com.example.data.model.Warga
import com.example.ui.components.CameraScannerView
import com.example.ui.components.StatCard
import com.example.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetugasDashboardScreen(
  currentUser: UserAccount,
  wargaList: List<Warga>,
  todayTransactions: List<JimpitanTransaction>,
  onScanBarcode: (String) -> Unit,
  onSelectWarga: (Warga) -> Unit,
  onShowQrCard: (Warga) -> Unit,
  onLogout: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val todayString = CurrencyUtils.getCurrentDateString()

  // Calculate statistics for today
  val todayTotalIncome = remember(todayTransactions) {
    todayTransactions.sumOf { it.amount }
  }

  // Warga who paid today
  val paidWargaIds = remember(todayTransactions) {
    todayTransactions.map { it.wargaId }.toSet()
  }

  val totalWargaCount = wargaList.size
  val paidCount = paidWargaIds.size
  val remainingCount = (totalWargaCount - paidCount).coerceAtLeast(0)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Jimpitan Ronda Malam",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
            Text(
              text = "${currentUser.fullName} • ${CurrencyUtils.formatDateDisplay(todayString)}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          IconButton(
            onClick = onLogout,
            modifier = Modifier.testTag("petugas_logout_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Keluar Akun")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan") },
          label = { Text("Scan Barcode") },
          modifier = Modifier.testTag("tab_scanner")
        )
        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = { Icon(Icons.Default.ListAlt, contentDescription = "Checklist Warga") },
          label = { Text("Rumah ($remainingCount)") },
          modifier = Modifier.testTag("tab_checklist")
        )
        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = { Icon(Icons.Default.History, contentDescription = "Riwayat") },
          label = { Text("Riwayat (${todayTransactions.size})") },
          modifier = Modifier.testTag("tab_history")
        )
      }
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      when (selectedTab) {
        0 -> {
          // SCANNER TAB
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
          ) {
            item {
              // Shift Progress Cards
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                StatCard(
                  title = "Terkumpul Malam Ini",
                  value = CurrencyUtils.formatRupiah(todayTotalIncome),
                  icon = Icons.Default.Paid,
                  subtitle = "${todayTransactions.size} kali setoran",
                  containerColor = MaterialTheme.colorScheme.primaryContainer,
                  contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.weight(1f)
                )
                StatCard(
                  title = "Status Kunjungan",
                  value = "$paidCount / $totalWargaCount KK",
                  icon = Icons.Default.Home,
                  subtitle = "Sisa $remainingCount belum",
                  containerColor = MaterialTheme.colorScheme.secondaryContainer,
                  contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                text = "Kamera Scanner Jimpitan:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.height(8.dp))

              CameraScannerView(
                wargaList = wargaList,
                onBarcodeDetected = onScanBarcode
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Quick Action Card
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.Nightlight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Petunjuk Ronda Jimpitan",
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                    Text(
                      text = "Arahkan scanner ke barcode rumah atau gunakan tab 'Rumah' untuk memilih rumah warga secara manual.",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }

        1 -> {
          // CHECKLIST WARGA TAB
          var filterStatus by remember { mutableStateOf("Semua") } // "Semua", "Belum Diambil", "Sudah Bayar"

          val filteredWarga = remember(wargaList, paidWargaIds, filterStatus) {
            when (filterStatus) {
              "Belum Diambil" -> wargaList.filter { !paidWargaIds.contains(it.id) }
              "Sudah Bayar" -> wargaList.filter { paidWargaIds.contains(it.id) }
              else -> wargaList
            }
          }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf("Semua (${totalWargaCount})", "Belum Diambil ($remainingCount)", "Sudah Bayar ($paidCount)").forEach { filter ->
                  val key = filter.substringBefore(" ")
                  val isSelected = filterStatus == key || (key == "Semua" && filterStatus == "Semua")
                  FilterChip(
                    selected = isSelected,
                    onClick = { filterStatus = key },
                    label = { Text(filter, fontSize = 11.sp) },
                    modifier = Modifier.testTag("filter_warga_$key")
                  )
                }
              }
              Spacer(modifier = Modifier.height(12.dp))
            }

            items(filteredWarga) { warga ->
              val isPaidToday = paidWargaIds.contains(warga.id)
              val residentTransactions = todayTransactions.filter { it.wargaId == warga.id }
              val totalPaidToday = residentTransactions.sumOf { it.amount }

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 5.dp)
                  .testTag("warga_card_${warga.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isPaidToday) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                  width = 1.dp,
                  color = if (isPaidToday) Color(0xFF86EFAC) else MaterialTheme.colorScheme.outlineVariant
                )
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(42.dp)
                      .background(
                        if (isPaidToday) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                        CircleShape
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (isPaidToday) Icons.Default.CheckCircle else Icons.Default.Home,
                      contentDescription = null,
                      tint = if (isPaidToday) Color(0xFF16A34A) else Color(0xFFD97706),
                      modifier = Modifier.size(24.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = warga.name,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp
                    )
                    Text(
                      text = "${warga.houseNumber} • ${warga.rtRw}",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (isPaidToday) {
                      Text(
                        text = "Sudah bayar: ${CurrencyUtils.formatRupiah(totalPaidToday)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF16A34A)
                      )
                    } else {
                      Text(
                        text = "Tarif default: ${CurrencyUtils.formatRupiah(warga.defaultTarif)}",
                        fontSize = 11.sp,
                        color = Color(0xFFB45309)
                      )
                    }
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Button(
                      onClick = { onSelectWarga(warga) },
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaidToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                      ),
                      modifier = Modifier.testTag("catat_btn_${warga.id}")
                    ) {
                      Text(
                        text = if (isPaidToday) "+ Tambah" else "Catat",
                        fontSize = 11.sp
                      )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedButton(
                      onClick = { onShowQrCard(warga) },
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      modifier = Modifier.testTag("qr_btn_${warga.id}")
                    ) {
                      Icon(Icons.Default.QrCode, contentDescription = "Lihat Barcode", modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Barcode", fontSize = 10.sp)
                    }
                  }
                }
              }
            }
          }
        }

        2 -> {
          // RIWAYAT TRANSAKSI HARIAN TAB
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
          ) {
            item {
              ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "Total Uang Jimpitan Hari Ini",
                      fontSize = 12.sp,
                      color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                      text = CurrencyUtils.formatRupiah(todayTotalIncome),
                      fontSize = 20.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  }
                  Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                  ) {
                    Text(
                      text = "${todayTransactions.size} Transaksi",
                      color = Color.White,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "Rincian Transaksi (${CurrencyUtils.formatDateDisplay(todayString)}):",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
            }

            if (todayTransactions.isEmpty()) {
              item {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                      Icons.Default.Paid,
                      contentDescription = null,
                      tint = Color.Gray,
                      modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      text = "Belum ada transaksi jimpitan hari ini.",
                      color = Color.Gray,
                      fontSize = 13.sp
                    )
                  }
                }
              }
            } else {
              items(todayTransactions) { tx ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFDCFCE7), CircleShape),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        Icons.Default.Paid,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(20.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = tx.wargaName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                      Text(
                        text = "${tx.houseNumber} • ${CurrencyUtils.formatTimeOnly(tx.timestamp)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      if (tx.notes.isNotBlank()) {
                        Text(
                          text = "Ket: ${tx.notes}",
                          fontSize = 11.sp,
                          color = MaterialTheme.colorScheme.outline,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                      }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        text = CurrencyUtils.formatRupiah(tx.amount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF16A34A)
                      )
                      Text(
                        text = tx.category,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
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
