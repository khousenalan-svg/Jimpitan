package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyUtils {
  private val localeID = Locale("id", "ID")
  private val rupiahFormat = NumberFormat.getCurrencyInstance(localeID).apply {
    maximumFractionDigits = 0
  }

  fun formatRupiah(amount: Long): String {
    return try {
      val formatted = rupiahFormat.format(amount)
      // Clean up spacing if needed: "Rp 2.000"
      formatted.replace("Rp", "Rp ")
    } catch (e: Exception) {
      "Rp $amount"
    }
  }

  fun getCurrentDateString(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
  }

  fun getCurrentMonthPrefix(): String {
    return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
  }

  fun formatDateDisplay(dateString: String): String {
    return try {
      val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val date = parser.parse(dateString) ?: return dateString
      SimpleDateFormat("dd MMMM yyyy", localeID).format(date)
    } catch (e: Exception) {
      dateString
    }
  }

  fun formatDateTimeDisplay(timestamp: Long): String {
    return try {
      SimpleDateFormat("dd MMM yyyy, HH:mm", localeID).format(Date(timestamp))
    } catch (e: Exception) {
      ""
    }
  }

  fun formatTimeOnly(timestamp: Long): String {
    return try {
      SimpleDateFormat("HH:mm", localeID).format(Date(timestamp))
    } catch (e: Exception) {
      ""
    }
  }

  fun formatMonthYearDisplay(monthPrefix: String): String {
    return try {
      val parser = SimpleDateFormat("yyyy-MM", Locale.getDefault())
      val date = parser.parse(monthPrefix) ?: return monthPrefix
      SimpleDateFormat("MMMM yyyy", localeID).format(date)
    } catch (e: Exception) {
      monthPrefix
    }
  }
}
