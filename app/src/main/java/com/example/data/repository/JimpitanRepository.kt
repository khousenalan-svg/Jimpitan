package com.example.data.repository

import com.example.data.dao.JimpitanTransactionDao
import com.example.data.dao.PengeluaranKasDao
import com.example.data.dao.ReconciliationDao
import com.example.data.dao.UserDao
import com.example.data.dao.WargaDao
import com.example.data.model.JimpitanTransaction
import com.example.data.model.PengeluaranKas
import com.example.data.model.ReconciliationRecord
import com.example.data.model.UserAccount
import com.example.data.model.Warga
import com.example.util.CurrencyUtils
import com.example.util.SampleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class JimpitanRepository(
  private val userDao: UserDao,
  private val wargaDao: WargaDao,
  private val transactionDao: JimpitanTransactionDao,
  private val pengeluaranDao: PengeluaranKasDao,
  private val reconciliationDao: ReconciliationDao
) {

  // Users
  val activeUsers: Flow<List<UserAccount>> = userDao.getAllActiveUsers()

  suspend fun getUserByUsername(username: String): UserAccount? = withContext(Dispatchers.IO) {
    userDao.getUserByUsername(username)
  }

  suspend fun insertUser(user: UserAccount): Long = withContext(Dispatchers.IO) {
    userDao.insertUser(user)
  }

  // Warga
  val allWarga: Flow<List<Warga>> = wargaDao.getAllWarga()

  suspend fun getWargaByBarcode(barcode: String): Warga? = withContext(Dispatchers.IO) {
    wargaDao.getWargaByBarcode(barcode)
  }

  suspend fun getWargaById(id: String): Warga? = withContext(Dispatchers.IO) {
    wargaDao.getWargaById(id)
  }

  suspend fun insertOrUpdateWarga(warga: Warga) = withContext(Dispatchers.IO) {
    wargaDao.insertWarga(warga)
  }

  suspend fun deleteWarga(warga: Warga) = withContext(Dispatchers.IO) {
    wargaDao.deleteWarga(warga)
  }

  // Transactions (Uang Masuk)
  val allTransactions: Flow<List<JimpitanTransaction>> = transactionDao.getAllTransactions()

  fun getTransactionsByDate(dateString: String): Flow<List<JimpitanTransaction>> {
    return transactionDao.getTransactionsByDate(dateString)
  }

  fun getTransactionsByMonth(monthPrefix: String): Flow<List<JimpitanTransaction>> {
    return transactionDao.getTransactionsByMonth(monthPrefix)
  }

  fun getTotalIncomeByDate(dateString: String): Flow<Long?> {
    return transactionDao.getTotalIncomeByDate(dateString)
  }

  fun getTotalIncomeByMonth(monthPrefix: String): Flow<Long?> {
    return transactionDao.getTotalIncomeByMonth(monthPrefix)
  }

  val totalIncomeAllTime: Flow<Long?> = transactionDao.getTotalIncomeAllTime()

  suspend fun recordJimpitan(transaction: JimpitanTransaction): Long = withContext(Dispatchers.IO) {
    transactionDao.insertTransaction(transaction)
  }

  suspend fun deleteTransaction(transaction: JimpitanTransaction) = withContext(Dispatchers.IO) {
    transactionDao.deleteTransaction(transaction)
  }

  // Pengeluaran Kas (Uang Keluar)
  val allPengeluaran: Flow<List<PengeluaranKas>> = pengeluaranDao.getAllPengeluaran()

  fun getPengeluaranByMonth(monthPrefix: String): Flow<List<PengeluaranKas>> {
    return pengeluaranDao.getPengeluaranByMonth(monthPrefix)
  }

  fun getTotalPengeluaranByMonth(monthPrefix: String): Flow<Long?> {
    return pengeluaranDao.getTotalPengeluaranByMonth(monthPrefix)
  }

  val totalPengeluaranAllTime: Flow<Long?> = pengeluaranDao.getTotalPengeluaranAllTime()

  suspend fun addPengeluaran(pengeluaran: PengeluaranKas): Long = withContext(Dispatchers.IO) {
    pengeluaranDao.insertPengeluaran(pengeluaran)
  }

  suspend fun updatePengeluaran(pengeluaran: PengeluaranKas) = withContext(Dispatchers.IO) {
    pengeluaranDao.updatePengeluaran(pengeluaran)
  }

  suspend fun deletePengeluaran(pengeluaran: PengeluaranKas) = withContext(Dispatchers.IO) {
    pengeluaranDao.deletePengeluaran(pengeluaran)
  }

  // Reconciliation
  val allReconciliations: Flow<List<ReconciliationRecord>> = reconciliationDao.getAllRecords()

  suspend fun saveReconciliation(record: ReconciliationRecord): Long = withContext(Dispatchers.IO) {
    reconciliationDao.insertRecord(record)
  }

  // Seeding initial data if empty
  suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
    val existingUsers = userDao.getAllActiveUsers().first()
    if (existingUsers.isEmpty()) {
      userDao.insertUsers(SampleData.defaultUsers)
    }

    val existingWarga = wargaDao.getAllWarga().first()
    if (existingWarga.isEmpty()) {
      wargaDao.insertAllWarga(SampleData.defaultWarga)

      val today = CurrencyUtils.getCurrentDateString()
      val yesterday = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).apply {
        timeZone = java.util.TimeZone.getDefault()
      }.format(java.util.Date(System.currentTimeMillis() - 86400000L))

      transactionDao.insertAllTransactions(SampleData.getInitialTransactions(today, yesterday))
      pengeluaranDao.insertAllPengeluaran(SampleData.getInitialPengeluaran(today))
    }
  }
}
