package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
  ADMIN,
  PETUGAS
}

@Entity(tableName = "users")
data class UserAccount(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val username: String,
  val pin: String,
  val fullName: String,
  val role: UserRole,
  val phone: String = "",
  val isActive: Boolean = true
)

@Entity(tableName = "warga")
data class Warga(
  @PrimaryKey
  val id: String, // e.g. "WRG-RT04-001"
  val name: String,
  val houseNumber: String, // e.g. "Blok A1 No. 04"
  val rtRw: String = "RT 04 / RW 08",
  val phone: String = "",
  val barcodeData: String = id,
  val defaultTarif: Long = 2000L,
  val notes: String = "",
  val isActive: Boolean = true
)

@Entity(tableName = "jimpitan_transactions")
data class JimpitanTransaction(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val wargaId: String,
  val wargaName: String,
  val houseNumber: String,
  val amount: Long,
  val dateString: String, // format: "yyyy-MM-dd"
  val timestamp: Long = System.currentTimeMillis(),
  val petugasId: Long,
  val petugasName: String,
  val category: String = "Jimpitan Harian",
  val notes: String = "",
  val paymentMethod: String = "Tunai / Koin"
)

@Entity(tableName = "pengeluaran_kas")
data class PengeluaranKas(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val amount: Long,
  val dateString: String, // format: "yyyy-MM-dd"
  val timestamp: Long = System.currentTimeMillis(),
  val category: String, // "Konsumsi Ronda", "Penerangan Jalan", "Sosial Warga", "Pemeliharaan Pos", "ATK RT"
  val notes: String = "",
  val authorizedByAdmin: String,
  val lastModifiedTimestamp: Long? = null,
  val lastModifiedBy: String? = null,
  val modificationReason: String? = null
)

@Entity(tableName = "reconciliation_records")
data class ReconciliationRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val dateString: String,
  val recordedIncome: Long,
  val recordedExpense: Long,
  val physicalCash: Long,
  val difference: Long,
  val notes: String = "",
  val reconciledBy: String,
  val timestamp: Long = System.currentTimeMillis()
)
