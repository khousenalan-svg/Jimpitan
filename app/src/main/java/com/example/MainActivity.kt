package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.AppDatabase
import com.example.data.model.UserRole
import com.example.data.model.Warga
import com.example.data.repository.JimpitanRepository
import com.example.ui.components.CatatJimpitanDialog
import com.example.ui.components.WargaQrCardDialog
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PetugasDashboardScreen
import com.example.ui.theme.JimpitanTheme
import com.example.ui.viewmodel.JimpitanViewModel
import com.example.ui.viewmodel.JimpitanViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

  private val database by lazy { AppDatabase.getInstance(applicationContext) }
  private val repository by lazy {
    JimpitanRepository(
      userDao = database.userDao(),
      wargaDao = database.wargaDao(),
      transactionDao = database.jimpitanTransactionDao(),
      pengeluaranDao = database.pengeluaranKasDao(),
      reconciliationDao = database.reconciliationDao()
    )
  }

  private val viewModel: JimpitanViewModel by viewModels {
    JimpitanViewModelFactory(repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      JimpitanTheme {
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
          color = MaterialTheme.colorScheme.background
        ) {
          JimpitanApp(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun JimpitanApp(viewModel: JimpitanViewModel) {
  val context = LocalContext.current

  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val activeUsers by viewModel.activeUsers.collectAsStateWithLifecycle()
  val wargaList by viewModel.wargaList.collectAsStateWithLifecycle()
  val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
  val todayTransactions by viewModel.transactionsByDate.collectAsStateWithLifecycle()
  val monthlyTransactions by viewModel.transactionsByMonth.collectAsStateWithLifecycle()
  val monthlyPengeluaran by viewModel.pengeluaranByMonth.collectAsStateWithLifecycle()
  val allPengeluaran by viewModel.allPengeluaran.collectAsStateWithLifecycle()
  val reconciliations by viewModel.reconciliations.collectAsStateWithLifecycle()
  val totalIncomeAllTime by viewModel.totalIncomeAllTime.collectAsStateWithLifecycle()
  val totalPengeluaranAllTime by viewModel.totalPengeluaranAllTime.collectAsStateWithLifecycle()
  val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
  val scannedWarga by viewModel.scannedWarga.collectAsStateWithLifecycle()

  var viewingQrWarga by remember { mutableStateOf<Warga?>(null) }

  // Listen to UI messages
  LaunchedEffect(Unit) {
    viewModel.uiMessage.collectLatest { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    val user = currentUser
    if (user == null) {
      LoginScreen(
        users = activeUsers,
        onLogin = { targetUser, pin ->
          viewModel.login(targetUser, pin)
        },
        onQuickLogin = { role ->
          viewModel.quickLoginAs(role)
        }
      )
    } else {
      BackHandler {
        viewModel.logout()
      }

      when (user.role) {
        UserRole.PETUGAS -> {
          PetugasDashboardScreen(
            currentUser = user,
            wargaList = wargaList,
            todayTransactions = todayTransactions,
            onScanBarcode = { code ->
              viewModel.handleBarcodeScanned(code)
            },
            onSelectWarga = { selected ->
              viewModel.selectWargaManual(selected)
            },
            onShowQrCard = { target ->
              viewingQrWarga = target
            },
            onLogout = {
              viewModel.logout()
            }
          )
        }

        UserRole.ADMIN -> {
          AdminDashboardScreen(
            currentUser = user,
            wargaList = wargaList,
            allTransactions = allTransactions,
            monthlyTransactions = monthlyTransactions,
            monthlyPengeluaran = monthlyPengeluaran,
            allPengeluaran = allPengeluaran,
            reconciliations = reconciliations,
            totalIncomeAllTime = totalIncomeAllTime,
            totalPengeluaranAllTime = totalPengeluaranAllTime,
            selectedMonth = selectedMonth,
            onMonthChange = { newMonth ->
              viewModel.setSelectedMonth(newMonth)
            },
            onAddPengeluaran = { title, amount, category, notes ->
              viewModel.addPengeluaran(title, amount, category, notes)
            },
            onEditPengeluaran = { target, title, amount, category, notes, pin, reason, onSuccess ->
              viewModel.editPengeluaranWithVerification(
                target, title, amount, category, notes, pin, reason, onSuccess
              )
            },
            onDeletePengeluaran = { target, pin, reason, onSuccess ->
              viewModel.deletePengeluaranWithVerification(target, pin, reason, onSuccess)
            },
            onSaveWarga = { newWarga, onSuccess ->
              viewModel.saveOrUpdateWarga(newWarga, onSuccess)
            },
            onDeleteWarga = { target ->
              viewModel.deleteWarga(target)
            },
            onShowQrCard = { target ->
              viewingQrWarga = target
            },
            onSaveReconciliation = { income, expense, cash, notes ->
              viewModel.saveReconciliation(income, expense, cash, notes)
            },
            onExportPdf = {
              viewModel.exportPdfReport(context)
            },
            onExportExcel = {
              viewModel.exportExcelReport(context)
            },
            onLogout = {
              viewModel.logout()
            }
          )
        }
      }
    }

    // Modal: Catat Jimpitan (when a warga is scanned or picked)
    scannedWarga?.let { warga ->
      CatatJimpitanDialog(
        warga = warga,
        petugasName = currentUser?.fullName ?: "Petugas",
        onDismiss = { viewModel.dismissScannedWarga() },
        onSave = { amount, category, notes, method ->
          viewModel.recordJimpitan(warga, amount, category, notes, method)
        }
      )
    }

    // Modal: Digital Citizen Card with Barcode & QR
    viewingQrWarga?.let { warga ->
      WargaQrCardDialog(
        warga = warga,
        onDismiss = { viewingQrWarga = null }
      )
    }
  }
}
