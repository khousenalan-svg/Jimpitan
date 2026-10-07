package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.JimpitanTransaction
import com.example.data.model.PengeluaranKas
import com.example.data.model.ReconciliationRecord
import com.example.data.model.UserAccount
import com.example.data.model.Warga
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE isActive = 1")
  fun getAllActiveUsers(): Flow<List<UserAccount>>

  @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
  suspend fun getUserByUsername(username: String): UserAccount?

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  suspend fun getUserById(id: Long): UserAccount?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserAccount): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUsers(users: List<UserAccount>)

  @Update
  suspend fun updateUser(user: UserAccount)
}

@Dao
interface WargaDao {
  @Query("SELECT * FROM warga WHERE isActive = 1 ORDER BY houseNumber ASC")
  fun getAllWarga(): Flow<List<Warga>>

  @Query("SELECT * FROM warga WHERE barcodeData = :barcode OR id = :barcode LIMIT 1")
  suspend fun getWargaByBarcode(barcode: String): Warga?

  @Query("SELECT * FROM warga WHERE id = :id LIMIT 1")
  suspend fun getWargaById(id: String): Warga?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWarga(warga: Warga)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllWarga(wargaList: List<Warga>)

  @Update
  suspend fun updateWarga(warga: Warga)

  @Delete
  suspend fun deleteWarga(warga: Warga)
}

@Dao
interface JimpitanTransactionDao {
  @Query("SELECT * FROM jimpitan_transactions ORDER BY timestamp DESC")
  fun getAllTransactions(): Flow<List<JimpitanTransaction>>

  @Query("SELECT * FROM jimpitan_transactions WHERE dateString = :dateString ORDER BY timestamp DESC")
  fun getTransactionsByDate(dateString: String): Flow<List<JimpitanTransaction>>

  @Query("SELECT * FROM jimpitan_transactions WHERE dateString LIKE :monthPrefix || '%' ORDER BY timestamp DESC")
  fun getTransactionsByMonth(monthPrefix: String): Flow<List<JimpitanTransaction>>

  @Query("SELECT SUM(amount) FROM jimpitan_transactions WHERE dateString = :dateString")
  fun getTotalIncomeByDate(dateString: String): Flow<Long?>

  @Query("SELECT SUM(amount) FROM jimpitan_transactions WHERE dateString LIKE :monthPrefix || '%'")
  fun getTotalIncomeByMonth(monthPrefix: String): Flow<Long?>

  @Query("SELECT SUM(amount) FROM jimpitan_transactions")
  fun getTotalIncomeAllTime(): Flow<Long?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: JimpitanTransaction): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllTransactions(transactions: List<JimpitanTransaction>)

  @Delete
  suspend fun deleteTransaction(transaction: JimpitanTransaction)
}

@Dao
interface PengeluaranKasDao {
  @Query("SELECT * FROM pengeluaran_kas ORDER BY timestamp DESC")
  fun getAllPengeluaran(): Flow<List<PengeluaranKas>>

  @Query("SELECT * FROM pengeluaran_kas WHERE dateString LIKE :monthPrefix || '%' ORDER BY timestamp DESC")
  fun getPengeluaranByMonth(monthPrefix: String): Flow<List<PengeluaranKas>>

  @Query("SELECT SUM(amount) FROM pengeluaran_kas WHERE dateString LIKE :monthPrefix || '%'")
  fun getTotalPengeluaranByMonth(monthPrefix: String): Flow<Long?>

  @Query("SELECT SUM(amount) FROM pengeluaran_kas")
  fun getTotalPengeluaranAllTime(): Flow<Long?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPengeluaran(pengeluaran: PengeluaranKas): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllPengeluaran(list: List<PengeluaranKas>)

  @Update
  suspend fun updatePengeluaran(pengeluaran: PengeluaranKas)

  @Delete
  suspend fun deletePengeluaran(pengeluaran: PengeluaranKas)
}

@Dao
interface ReconciliationDao {
  @Query("SELECT * FROM reconciliation_records ORDER BY timestamp DESC")
  fun getAllRecords(): Flow<List<ReconciliationRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: ReconciliationRecord): Long
}
