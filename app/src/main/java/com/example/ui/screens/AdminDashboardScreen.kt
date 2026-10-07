package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JimpitanTransaction
import com.example.data.model.PengeluaranKas
import com.example.data.model.ReconciliationRecord
import com.example.data.model.UserAccount
import com.example.data.model.Warga
import com.example.ui.components.StatCard
import com.example.ui.components.VerifikasiKhususDialog
import com.example.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  currentUser: UserAccount,
  wargaList: List<Warga>,
  allTransactions: List<JimpitanTransaction>,
  monthlyTransactions: List<JimpitanTransaction>,
  monthlyPengeluaran: List<PengeluaranKas>,
  allPengeluaran: List<PengeluaranKas>,
  reconciliations: List<ReconciliationRecord>,
  totalIncomeAllTime: Long,
  totalPengeluaranAllTime: Long,
  selectedMonth: String,
  onMonthChange: (String) -> Unit,
  onAddPengeluaran: (title: String, amount: Long, category: String, notes: String) -> Unit,
  onEditPengeluaran: (target: PengeluaranKas, title: String, amount: Long, category: String, notes: String, pin: String, reason: String, onSuccess: () -> Unit) -> Unit,
  onDeletePengeluaran: (target: PengeluaranKas, pin: String, reason: String, onSuccess: () -> Unit) -> Unit,
  onSaveWarga: (Warga, () -> Unit) -> Unit,
  onDeleteWarga: (Warga) -> Unit,
  onShowQrCard: (Warga) -> Unit,
  onSaveReconciliation: (income: Long, expense: Long, cash: Long, notes: String) -> Unit,
  onExportPdf: () -> Unit,
  onExportExcel: () -> Unit,
  onLogout: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }

  // Dialog States
  var showAddPengeluaranDialog by remember { mutableStateOf(false) }
  var editingPengeluaran by remember { mutableStateOf<PengeluaranKas?>(null) }
  var pendingVerifyAction by remember { mutableStateOf<Pair<String, PengeluaranKas>?>(null) } // "EDIT" or "DELETE"
  var verifiedReasonAndPin by remember { mutableStateOf<Pair<String, String>?>(null) }

  var showAddWargaDialog by remember { mutableStateOf(false) }
  var editingWarga by remember { mutableStateOf<Warga?>(null) }
  var showReconciliationDialog by remember { mutableStateOf(false) }

  // Monthly Calculations
  val monthlyIncomeSum = remember(monthlyTransactions) {
    monthlyTransactions.sumOf { it.amount }
  }
  val monthlyPengeluaranSum = remember(monthlyPengeluaran) {
    monthlyPengeluaran.sumOf { it.amount }
  }
  val monthlyNetBalance = monthlyIncomeSum - monthlyPengeluaranSum
  val currentTotalKasBalance = totalIncomeAllTime - totalPengeluaranAllTime

  // Warga Participation Rate
  val uniqueWargaContributed = remember(monthlyTransactions) {
    monthlyTransactions.map { it.wargaId }.toSet().size
  }
  val participationRate = if (wargaList.isNotEmpty()) {
    (uniqueWargaContributed.toFloat() / wargaList.size.toFloat() * 100).toInt()
  } else 0

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Panel Pengurus & Kas RT",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
            Text(
              text = "${currentUser.fullName} • RT 04 / RW 08",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          IconButton(
            onClick = onLogout,
            modifier = Modifier.testTag("admin_logout_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Keluar")
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
          icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Kas & Laporan") },
          label = { Text("Kas & Laporan") },
          modifier = Modifier.testTag("admin_tab_kas")
        )
        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = { Icon(Icons.Default.PriceChange, contentDescription = "Pengeluaran") },
          label = { Text("Pengeluaran") },
          modifier = Modifier.testTag("admin_tab_pengeluaran")
        )
        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = { Icon(Icons.Default.People, contentDescription = "Data Warga") },
          label = { Text("Warga & Barcode") },
          modifier = Modifier.testTag("admin_tab_warga")
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
          // KAS & LAPORAN BULANAN
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
          ) {
            item {
              // Master Saldo Kas RT
              ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = "TOTAL SALDO KAS RT SAAT INI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 0.8.sp
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = CurrencyUtils.formatRupiah(currentTotalKasBalance),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                      )
                    }
                    Box(
                      modifier = Modifier
                        .size(46.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(14.dp))

                  // Sub-breakdown: Total Pemasukan Jimpitan All-time vs Pengeluaran All-time
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .background(Color.Black.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                      .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column {
                      Text("Total Jimpitan Masuk", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                      Text(CurrencyUtils.formatRupiah(totalIncomeAllTime), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                      Text("Total Pengeluaran Kas", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                      Text(CurrencyUtils.formatRupiah(totalPengeluaranAllTime), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Month Filter Header
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Laporan Bulanan Kas:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier.clickable {
                    // Toggle current or previous month for testing
                    val currentM = CurrencyUtils.getCurrentMonthPrefix()
                    onMonthChange(if (selectedMonth == currentM) "2026-09" else currentM)
                  }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = CurrencyUtils.formatMonthYearDisplay(selectedMonth),
                      fontSize = 12.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Monthly Stats Grid
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                StatCard(
                  title = "Pemasukan Jimpitan",
                  value = CurrencyUtils.formatRupiah(monthlyIncomeSum),
                  icon = Icons.Default.TrendingUp,
                  subtitle = "${monthlyTransactions.size} kali setoran",
                  containerColor = MaterialTheme.colorScheme.primaryContainer,
                  contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.weight(1f)
                )
                StatCard(
                  title = "Pengeluaran Kas",
                  value = CurrencyUtils.formatRupiah(monthlyPengeluaranSum),
                  icon = Icons.Default.TrendingDown,
                  subtitle = "${monthlyPengeluaran.size} catatan pengeluaran",
                  containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                  contentColor = MaterialTheme.colorScheme.error,
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                StatCard(
                  title = "Sisa Kas Periode Ini",
                  value = CurrencyUtils.formatRupiah(monthlyNetBalance),
                  icon = Icons.Default.Paid,
                  subtitle = if (monthlyNetBalance >= 0) "Surplus Kas" else "Defisit Kas",
                  containerColor = if (monthlyNetBalance >= 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                  contentColor = if (monthlyNetBalance >= 0) Color(0xFF166534) else Color(0xFF991B1B),
                  modifier = Modifier.weight(1f)
                )
                StatCard(
                  title = "Partisipasi Warga",
                  value = "$participationRate%",
                  icon = Icons.Default.Groups,
                  subtitle = "$uniqueWargaContributed dari ${wargaList.size} KK",
                  containerColor = MaterialTheme.colorScheme.secondaryContainer,
                  contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Export Buttons Section (PDF & Excel)
              Text(
                text = "Unduh Laporan Otomatis (PDF & Excel):",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = onExportPdf,
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE123C)), // Crimson Red for PDF
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("download_pdf_button")
                ) {
                  Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Unduh PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                  onClick = onExportExcel,
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)), // Forest Green for Excel
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("download_excel_button")
                ) {
                  Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Unduh Excel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Rekonsiliasi Real-Time Section
              ElevatedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "Rekonsiliasi Kas Fisik Real-Time",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                    }
                    OutlinedButton(
                      onClick = { showReconciliationDialog = true },
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.testTag("open_reconciliation_button")
                    ) {
                      Text("Hitung Selisih", fontSize = 11.sp)
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Bandingkan uang fisik di kaleng/tas bendahara dengan pencatatan digital untuk memastikan akurasi 100%.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  if (reconciliations.isNotEmpty()) {
                    val lastRec = reconciliations.first()
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                      color = MaterialTheme.colorScheme.surface,
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column {
                          Text(
                            text = "Rekonsiliasi Terakhir (${CurrencyUtils.formatDateTimeDisplay(lastRec.timestamp)})",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                          )
                          Text(
                            text = "Fisik: ${CurrencyUtils.formatRupiah(lastRec.physicalCash)} | Sistem: ${CurrencyUtils.formatRupiah(lastRec.recordedIncome - lastRec.recordedExpense)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                          )
                        }
                        Text(
                          text = if (lastRec.difference == 0L) "Cocok (Pas)" else "Selisih: ${CurrencyUtils.formatRupiah(lastRec.difference)}",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (lastRec.difference == 0L) Color(0xFF16A34A) else Color(0xFFDC2626)
                        )
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Daftar Transaksi Terkini
              Text(
                text = "Transaksi Jimpitan Terkini (${monthlyTransactions.size}):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
            }

            items(monthlyTransactions.take(8)) { tx ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      Icons.Default.Paid,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(tx.wargaName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("${tx.houseNumber} • ${tx.dateString} ${CurrencyUtils.formatTimeOnly(tx.timestamp)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                  Text(
                    CurrencyUtils.formatRupiah(tx.amount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }
            }
          }
        }

        1 -> {
          // KELOLA PENGELUARAN KAS RT (DENGAN EDIT & VERIFIKASI KHUSUS)
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Daftar Pengeluaran Kas RT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                  )
                  Text(
                    text = "Total Pengeluaran: ${CurrencyUtils.formatRupiah(monthlyPengeluaranSum)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                  )
                }
                Button(
                  onClick = { showAddPengeluaranDialog = true },
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.testTag("tambah_pengeluaran_button")
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Catat Pengeluaran", fontSize = 12.sp)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Fitur Edit & Hapus dilindungi Verifikasi Khusus PIN Pengurus untuk menjaga audit transparansi kas warga.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
            }

            if (monthlyPengeluaran.isEmpty()) {
              item {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("Belum ada catatan pengeluaran kas bulan ini.", color = Color.Gray)
                }
              }
            } else {
              items(monthlyPengeluaran) { item ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .testTag("pengeluaran_card_${item.id}"),
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Surface(
                          color = Color(0xFFFEE2E2),
                          shape = RoundedCornerShape(6.dp)
                        ) {
                          Text(
                            text = item.category,
                            color = Color(0xFF991B1B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = item.dateString,
                          fontSize = 11.sp,
                          color = MaterialTheme.colorScheme.outline
                        )
                      }

                      Text(
                        text = CurrencyUtils.formatRupiah(item.amount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.error
                      )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                      text = item.title,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp
                    )

                    if (item.notes.isNotBlank()) {
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = item.notes,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Audit / Modified Info Badge
                    if (item.lastModifiedBy != null) {
                      Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Row(
                          modifier = Modifier.padding(6.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(14.dp))
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = "Diedit oleh ${item.lastModifiedBy}: \"${item.modificationReason}\"",
                            fontSize = 10.sp,
                            color = Color(0xFF92400E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                          )
                        }
                      }
                      Spacer(modifier = Modifier.height(6.dp))
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "Otorisasi: ${item.authorizedByAdmin}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                      )

                      Row {
                        // Button Edit dengan Verifikasi Khusus
                        OutlinedButton(
                          onClick = {
                            pendingVerifyAction = Pair("EDIT", item)
                          },
                          shape = RoundedCornerShape(8.dp),
                          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                          modifier = Modifier.testTag("edit_pengeluaran_${item.id}")
                        ) {
                          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("Edit (Verifikasi)", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Button Hapus dengan Verifikasi Khusus
                        IconButton(
                          onClick = {
                            pendingVerifyAction = Pair("DELETE", item)
                          },
                          modifier = Modifier
                            .size(32.dp)
                            .testTag("hapus_pengeluaran_${item.id}")
                        ) {
                          Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        2 -> {
          // KELOLA WARGA & KARTU BARCODE
          var searchQuery by remember { mutableStateOf("") }
          val filteredWarga = remember(wargaList, searchQuery) {
            if (searchQuery.isBlank()) wargaList
            else wargaList.filter {
              it.name.contains(searchQuery, ignoreCase = true) ||
              it.houseNumber.contains(searchQuery, ignoreCase = true) ||
              it.barcodeData.contains(searchQuery, ignoreCase = true)
            }
          }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Data Warga & Barcode RT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                  )
                  Text(
                    text = "Total ${wargaList.size} Kepala Keluarga terdaftar",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Button(
                  onClick = { showAddWargaDialog = true },
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.testTag("tambah_warga_button")
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Tambah Warga", fontSize = 12.sp)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari nama, rumah, atau kode barcode...", fontSize = 12.sp) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("search_warga_input")
              )

              Spacer(modifier = Modifier.height(12.dp))
            }

            items(filteredWarga) { warga ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .testTag("admin_warga_card_${warga.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(40.dp)
                      .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(warga.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${warga.houseNumber} • ${warga.rtRw}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Kode: ${warga.barcodeData} | Tarif: ${CurrencyUtils.formatRupiah(warga.defaultTarif)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                  }

                  Row {
                    // Tombol Barcode QR Card
                    Button(
                      onClick = { onShowQrCard(warga) },
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      modifier = Modifier.testTag("cetak_barcode_warga_${warga.id}")
                    ) {
                      Icon(Icons.Default.QrCode, contentDescription = "Cetak Barcode", modifier = Modifier.size(15.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Barcode", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                      onClick = { editingWarga = warga },
                      modifier = Modifier.size(36.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = "Edit Warga", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
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

  // --- DIALOG: VERIFIKASI KHUSUS UNTUK EDIT / HAPUS PENGELUARAN ---
  pendingVerifyAction?.let { (action, targetExpense) ->
    VerifikasiKhususDialog(
      actionTitle = if (action == "EDIT") "Edit Pengeluaran Kas" else "Hapus Pengeluaran Kas",
      expenseTitle = targetExpense.title,
      currentAmount = targetExpense.amount,
      onDismiss = { pendingVerifyAction = null },
      onVerified = { pin, reason ->
        pendingVerifyAction = null
        if (action == "EDIT") {
          verifiedReasonAndPin = Pair(pin, reason)
          editingPengeluaran = targetExpense
        } else {
          // DELETE action
          onDeletePengeluaran(targetExpense, pin, reason) {
            // Deleted successfully
          }
        }
      }
    )
  }

  // --- DIALOG: FORM EDIT PENGELUARAN (SETELAH VERIFIKASI KHUSUS BERHASIL) ---
  editingPengeluaran?.let { targetExpense ->
    var title by remember { mutableStateOf(targetExpense.title) }
    var amountText by remember { mutableStateOf(targetExpense.amount.toString()) }
    var category by remember { mutableStateOf(targetExpense.category) }
    var notes by remember { mutableStateOf(targetExpense.notes) }

    AlertDialog(
      onDismissRequest = {
        editingPengeluaran = null
        verifiedReasonAndPin = null
      },
      title = { Text("Edit Pengeluaran (Terverifikasi)", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Keperluan Pengeluaran") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Nominal (Rp)") },
            prefix = { Text("Rp ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Kategori") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Keterangan Tambahan") },
            singleLine = false,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val amount = amountText.toLongOrNull() ?: 0L
            val reason = verifiedReasonAndPin?.second ?: "Koreksi data pengurus"
            val pin = verifiedReasonAndPin?.first ?: ""
            if (title.isNotBlank() && amount > 0) {
              onEditPengeluaran(targetExpense, title, amount, category, notes, pin, reason) {
                editingPengeluaran = null
                verifiedReasonAndPin = null
              }
            }
          }
        ) {
          Text("Simpan Perubahan")
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            editingPengeluaran = null
            verifiedReasonAndPin = null
          }
        ) {
          Text("Batal")
        }
      }
    )
  }

  // --- DIALOG: TAMBAH PENGELUARAN BARU ---
  if (showAddPengeluaranDialog) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Konsumsi Ronda") }
    var notes by remember { mutableStateOf("") }
    val categories = listOf("Konsumsi Ronda", "Penerangan Jalan", "Sosial Warga", "Pemeliharaan Pos", "ATK RT", "Lainnya")

    AlertDialog(
      onDismissRequest = { showAddPengeluaranDialog = false },
      title = { Text("Catat Pengeluaran Kas RT", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Keperluan Pengeluaran *") },
            placeholder = { Text("Contoh: Beli Bohlam Lampu Gang 2", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_judul_pengeluaran")
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Nominal Pengeluaran *") },
            prefix = { Text("Rp ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_nominal_pengeluaran")
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text("Kategori:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            categories.take(3).forEach { cat ->
              FilterChip(
                selected = category == cat,
                onClick = { category = cat },
                label = { Text(cat, fontSize = 10.sp) }
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Keterangan / Nomor Nota") },
            singleLine = false,
            maxLines = 2,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_catatan_pengeluaran")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val amt = amountText.toLongOrNull() ?: 0L
            if (title.isNotBlank() && amt > 0) {
              onAddPengeluaran(title.trim(), amt, category, notes.trim())
              showAddPengeluaranDialog = false
            }
          },
          modifier = Modifier.testTag("simpan_pengeluaran_button")
        ) {
          Text("Simpan Pengeluaran")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddPengeluaranDialog = false }) {
          Text("Batal")
        }
      }
    )
  }

  // --- DIALOG: TAMBAH / EDIT WARGA ---
  if (showAddWargaDialog || editingWarga != null) {
    val isEdit = editingWarga != null
    var id by remember { mutableStateOf(editingWarga?.id ?: "WRG-RT04-${wargaList.size + 1}") }
    var name by remember { mutableStateOf(editingWarga?.name ?: "") }
    var house by remember { mutableStateOf(editingWarga?.houseNumber ?: "Blok A") }
    var phone by remember { mutableStateOf(editingWarga?.phone ?: "") }
    var tarifText by remember { mutableStateOf(editingWarga?.defaultTarif?.toString() ?: "2000") }
    var notes by remember { mutableStateOf(editingWarga?.notes ?: "") }

    AlertDialog(
      onDismissRequest = {
        showAddWargaDialog = false
        editingWarga = null
      },
      title = { Text(if (isEdit) "Edit Data Warga" else "Tambah Warga Baru", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          OutlinedTextField(
            value = id,
            onValueChange = { if (!isEdit) id = it },
            enabled = !isEdit,
            label = { Text("ID Barcode Warga") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Kepala Keluarga *") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_nama_warga")
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = house,
            onValueChange = { house = it },
            label = { Text("Nomor Rumah / Blok *") },
            placeholder = { Text("Contoh: Blok B3 No. 12") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_rumah_warga")
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = tarifText,
            onValueChange = { tarifText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Tarif Jimpitan Default (Rp)") },
            prefix = { Text("Rp ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("No. WhatsApp / Telepon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Catatan Posisi Kotak Jimpitan") },
            placeholder = { Text("Contoh: Kaleng di tiang pagar besi") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val defaultTarif = tarifText.toLongOrNull() ?: 2000L
            if (name.isNotBlank() && house.isNotBlank()) {
              val warga = Warga(
                id = id.trim(),
                name = name.trim(),
                houseNumber = house.trim(),
                rtRw = "RT 04 / RW 08",
                phone = phone.trim(),
                barcodeData = id.trim(),
                defaultTarif = defaultTarif,
                notes = notes.trim()
              )
              onSaveWarga(warga) {
                showAddWargaDialog = false
                editingWarga = null
              }
            }
          },
          modifier = Modifier.testTag("simpan_data_warga_button")
        ) {
          Text(if (isEdit) "Simpan Perubahan" else "Tambah Warga")
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            showAddWargaDialog = false
            editingWarga = null
          }
        ) {
          Text("Batal")
        }
      }
    )
  }

  // --- DIALOG: REKONSILIASI KAS REAL-TIME ---
  if (showReconciliationDialog) {
    var physicalCashText by remember { mutableStateOf(currentTotalKasBalance.toString()) }
    var notes by remember { mutableStateOf("") }

    val physicalCash = physicalCashText.toLongOrNull() ?: 0L
    val difference = physicalCash - currentTotalKasBalance

    AlertDialog(
      onDismissRequest = { showReconciliationDialog = false },
      title = { Text("Rekonsiliasi Kas Real-Time", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = "Hitung uang fisik di kotak/rekening kas bendahara RT dan masukkan nominalnya di bawah ini:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))

          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Saldo Sistem Tercatat:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
              Text(
                CurrencyUtils.formatRupiah(currentTotalKasBalance),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = physicalCashText,
            onValueChange = { physicalCashText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Total Uang Fisik Dihitung (Rp)") },
            prefix = { Text("Rp ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_uang_fisik_rekonsiliasi")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Selisih Result
          Surface(
            color = if (difference == 0L) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = if (difference == 0L) "Status: PAS / COCOK 100%"
                else if (difference > 0) "Status: SELISIH LEBIH (+)"
                else "Status: SELISIH KURANG (-)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (difference == 0L) Color(0xFF166534) else Color(0xFF991B1B)
              )
              Text(
                text = "Selisih: ${CurrencyUtils.formatRupiah(difference)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (difference == 0L) Color(0xFF166534) else Color(0xFF991B1B)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Catatan Rekonsiliasi (Opsional)") },
            placeholder = { Text("Contoh: Uang koin belum sempat dihitung", fontSize = 11.sp) },
            singleLine = false,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onSaveReconciliation(totalIncomeAllTime, totalPengeluaranAllTime, physicalCash, notes.trim())
            showReconciliationDialog = false
          },
          modifier = Modifier.testTag("simpan_rekonsiliasi_button")
        ) {
          Text("Simpan Rekonsiliasi")
        }
      },
      dismissButton = {
        TextButton(onClick = { showReconciliationDialog = false }) {
          Text("Batal")
        }
      }
    )
  }
}
