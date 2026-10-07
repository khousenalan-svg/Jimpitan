package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
  entities = [
    UserAccount::class,
    Warga::class,
    JimpitanTransaction::class,
    PengeluaranKas::class,
    ReconciliationRecord::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun wargaDao(): WargaDao
  abstract fun jimpitanTransactionDao(): JimpitanTransactionDao
  abstract fun pengeluaranKasDao(): PengeluaranKasDao
  abstract fun reconciliationDao(): ReconciliationDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "jimpitan_rt_db"
        ).fallbackToDestructiveMigration()
         .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
