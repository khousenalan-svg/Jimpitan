package com.example.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.JimpitanTransaction
import com.example.data.model.PengeluaranKas
import com.example.data.model.ReconciliationRecord
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.model.Warga
import com.example.data.repository.JimpitanRepository
import com.example.util.CurrencyUtils
import com.example.util.ExportUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JimpitanViewModel(
  private val repository: JimpitanRepository
) : ViewModel() {

  // Current User & Session
  private val _currentUser = MutableStateFlow<UserAccount?>(null)
  val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

  val activeUsers: StateFlow<List<UserAccount>> = repository.activeUsers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected date & month filters
  private val _selectedDate = MutableStateFlow(CurrencyUtils.getCurrentDateString())
  val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

  private val _selectedMonth = MutableStateFlow(CurrencyUtils.getCurrentMonthPrefix())
  val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

  // Warga List
  val wargaList: StateFlow<List<Warga>> = repository.allWarga
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // All Transactions
  val allTransactions: StateFlow<List<JimpitanTransaction>> = repository.allTransactions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Transactions by selected Date
  val transactionsByDate: StateFlow<List<JimpitanTransaction>> = _selectedDate
    .flatMapLatest { date -> repository.getTransactionsByDate(date) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Transactions by selected Month
  val transactionsByMonth: StateFlow<List<JimpitanTransaction>> = _selectedMonth
    .flatMapLatest { month -> repository.getTransactionsByMonth(month) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Pengeluaran Kas
  val allPengeluaran: StateFlow<List<PengeluaranKas>> = repository.allPengeluaran
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pengeluaranByMonth: StateFlow<List<PengeluaranKas>> = _selectedMonth
    .flatMapLatest { month -> repository.getPengeluaranByMonth(month) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Reconciliations
  val reconciliations: StateFlow<List<ReconciliationRecord>> = repository.allReconciliations
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Active Scanned Warga for Dialog
  private val _scannedWarga = MutableStateFlow<Warga?>(null)
  val scannedWarga: StateFlow<Warga?> = _scannedWarga.asStateFlow()

  // UI Toast / Event Messages
  private val _uiMessage = MutableSharedFlow<String>()
  val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

  // Real-time Kas Calculations
  val totalIncomeAllTime: StateFlow<Long> = repository.totalIncomeAllTime
    .flatMapLatest { flowOfVal(it ?: 0L) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

  val totalPengeluaranAllTime: StateFlow<Long> = repository.totalPengeluaranAllTime
    .flatMapLatest { flowOfVal(it ?: 0L) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

  init {
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
    }
  }

  private fun <T> flowOfVal(value: T) = kotlinx.coroutines.flow.flowOf(value)

  fun login(user: UserAccount, enteredPin: String): Boolean {
    return if (user.pin == enteredPin.trim()) {
      _currentUser.value = user
      viewModelScope.launch {
        _uiMessage.emit("Selamat datang, ${user.fullName}")
      }
      true
    } else {
      viewModelScope.launch {
        _uiMessage.emit("PIN Keamanan salah! Silakan coba lagi.")
      }
      false
    }
  }

  fun quickLoginAs(role: UserRole) {
    viewModelScope.launch {
      val user = activeUsers.value.firstOrNull { it.role == role }
      if (user != null) {
        _currentUser.value = user
        _uiMessage.emit("Masuk sebagai ${user.fullName}")
      }
    }
  }

  fun logout() {
    _currentUser.value = null
  }

  fun setSelectedDate(date: String) {
    _selectedDate.value = date
  }

  fun setSelectedMonth(monthPrefix: String) {
    _selectedMonth.value = monthPrefix
  }

  fun handleBarcodeScanned(code: String) {
    viewModelScope.launch {
      val cleanCode = code.trim()
      val matched = repository.getWargaByBarcode(cleanCode)
        ?: repository.getWargaById(cleanCode)

      if (matched != null) {
        _scannedWarga.value = matched
      } else {
        _uiMessage.emit("Barcode '$cleanCode' tidak terdaftar sebagai warga RT!")
      }
    }
  }

  fun selectWargaManual(warga: Warga) {
    _scannedWarga.value = warga
  }

  fun dismissScannedWarga() {
    _scannedWarga.value = null
  }

  fun recordJimpitan(
    warga: Warga,
    amount: Long,
    category: String,
    notes: String,
    paymentMethod: String
  ) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val transaction = JimpitanTransaction(
        wargaId = warga.id,
        wargaName = warga.name,
        houseNumber = warga.houseNumber,
        amount = amount,
        dateString = CurrencyUtils.getCurrentDateString(),
        timestamp = System.currentTimeMillis(),
        petugasId = user.id,
        petugasName = user.fullName,
        category = category,
        notes = notes,
        paymentMethod = paymentMethod
      )
      repository.recordJimpitan(transaction)
      _scannedWarga.value = null
      _uiMessage.emit("Jimpitan ${CurrencyUtils.formatRupiah(amount)} untuk ${warga.name} berhasil dicatat!")
    }
  }

  fun addPengeluaran(
    title: String,
    amount: Long,
    category: String,
    notes: String
  ) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val pengeluaran = PengeluaranKas(
        title = title,
        amount = amount,
        dateString = CurrencyUtils.getCurrentDateString(),
        timestamp = System.currentTimeMillis(),
        category = category,
        notes = notes,
        authorizedByAdmin = user.fullName
      )
      repository.addPengeluaran(pengeluaran)
      _uiMessage.emit("Pengeluaran kas ${CurrencyUtils.formatRupiah(amount)} berhasil ditambahkan")
    }
  }

  /**
   * Edit Pengeluaran dengan Verifikasi Khusus:
   * Requires Admin verification PIN and Audit Reason.
   */
  fun editPengeluaranWithVerification(
    target: PengeluaranKas,
    newTitle: String,
    newAmount: Long,
    newCategory: String,
    newNotes: String,
    adminPin: String,
    reason: String,
    onSuccess: () -> Unit
  ) {
    val user = _currentUser.value
    // Verify admin PIN: accepts either current user's PIN if admin or default RT master PIN 123456
    val isValid = (user?.role == UserRole.ADMIN && user.pin == adminPin) || adminPin == "123456"
    if (!isValid) {
      viewModelScope.launch {
        _uiMessage.emit("Gagal: PIN Verifikasi Khusus Pengurus salah!")
      }
      return
    }

    viewModelScope.launch {
      val updated = target.copy(
        title = newTitle,
        amount = newAmount,
        category = newCategory,
        notes = newNotes,
        lastModifiedTimestamp = System.currentTimeMillis(),
        lastModifiedBy = user?.fullName ?: "Admin Pengurus",
        modificationReason = reason
      )
      repository.updatePengeluaran(updated)
      _uiMessage.emit("Pengeluaran kas berhasil diperbarui dengan verifikasi khusus.")
      onSuccess()
    }
  }

  /**
   * Delete Pengeluaran with Verifikasi Khusus
   */
  fun deletePengeluaranWithVerification(
    target: PengeluaranKas,
    adminPin: String,
    reason: String,
    onSuccess: () -> Unit
  ) {
    val user = _currentUser.value
    val isValid = (user?.role == UserRole.ADMIN && user.pin == adminPin) || adminPin == "123456"
    if (!isValid) {
      viewModelScope.launch {
        _uiMessage.emit("Gagal: PIN Verifikasi Khusus Pengurus salah!")
      }
      return
    }

    viewModelScope.launch {
      repository.deletePengeluaran(target)
      _uiMessage.emit("Pengeluaran kas '${target.title}' berhasil dihapus.")
      onSuccess()
    }
  }

  fun saveOrUpdateWarga(warga: Warga, onSuccess: () -> Unit) {
    viewModelScope.launch {
      repository.insertOrUpdateWarga(warga)
      _uiMessage.emit("Data warga ${warga.name} berhasil disimpan.")
      onSuccess()
    }
  }

  fun deleteWarga(warga: Warga) {
    viewModelScope.launch {
      repository.deleteWarga(warga)
      _uiMessage.emit("Warga ${warga.name} berhasil dihapus.")
    }
  }

  fun saveReconciliation(
    recordedIncome: Long,
    recordedExpense: Long,
    physicalCash: Long,
    notes: String
  ) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val diff = physicalCash - (recordedIncome - recordedExpense)
      val record = ReconciliationRecord(
        dateString = _selectedDate.value,
        recordedIncome = recordedIncome,
        recordedExpense = recordedExpense,
        physicalCash = physicalCash,
        difference = diff,
        notes = notes,
        reconciledBy = user.fullName
      )
      repository.saveReconciliation(record)
      _uiMessage.emit("Rekonsiliasi kas berhasil disimpan. Selisih: ${CurrencyUtils.formatRupiah(diff)}")
    }
  }

  fun exportPdfReport(context: Context) {
    viewModelScope.launch {
      val month = _selectedMonth.value
      val monthTitle = CurrencyUtils.formatMonthYearDisplay(month)
      val incomeList = transactionsByMonth.value
      val expenseList = pengeluaranByMonth.value

      val totalInc = incomeList.sumOf { it.amount }
      val totalExp = expenseList.sumOf { it.amount }
      val net = totalInc - totalExp

      val file = ExportUtils.exportPdfReport(
        context = context,
        monthTitle = monthTitle,
        totalIncome = totalInc,
        totalExpense = totalExp,
        netBalance = net,
        transactions = incomeList,
        pengeluaranList = expenseList,
        adminName = _currentUser.value?.fullName ?: "Pengurus RT"
      )

      if (file != null) {
        ExportUtils.shareExportedFile(
          context = context,
          file = file,
          mimeType = "application/pdf",
          title = "Laporan Kas Jimpitan RT $monthTitle"
        )
      } else {
        _uiMessage.emit("Gagal membuat dokumen PDF")
      }
    }
  }

  fun exportExcelReport(context: Context) {
    viewModelScope.launch {
      val month = _selectedMonth.value
      val monthTitle = CurrencyUtils.formatMonthYearDisplay(month)
      val incomeList = transactionsByMonth.value
      val expenseList = pengeluaranByMonth.value

      val totalInc = incomeList.sumOf { it.amount }
      val totalExp = expenseList.sumOf { it.amount }
      val net = totalInc - totalExp

      val file = ExportUtils.exportExcelCsvReport(
        context = context,
        monthTitle = monthTitle,
        totalIncome = totalInc,
        totalExpense = totalExp,
        netBalance = net,
        transactions = incomeList,
        pengeluaranList = expenseList
      )

      if (file != null) {
        ExportUtils.shareExportedFile(
          context = context,
          file = file,
          mimeType = "text/csv",
          title = "Data Kas Jimpitan RT $monthTitle (Excel/CSV)"
        )
      } else {
        _uiMessage.emit("Gagal membuat dokumen Excel/CSV")
      }
    }
  }
}

class JimpitanViewModelFactory(
  private val repository: JimpitanRepository
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(JimpitanViewModel::class.java)) {
      return JimpitanViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
