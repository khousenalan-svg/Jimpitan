package com.example

import com.example.util.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCurrencyFormatting() {
    val formatted = CurrencyUtils.formatRupiah(2000L)
    assertTrue(formatted.contains("2.000") || formatted.contains("2,000"))
  }

  @Test
  fun testDateFormatting() {
    val dateDisplay = CurrencyUtils.formatDateDisplay("2026-10-04")
    assertTrue(dateDisplay.contains("Oktober") || dateDisplay.contains("10") || dateDisplay.contains("2026"))
  }
}
