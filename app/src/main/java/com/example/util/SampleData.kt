package com.example.util

import com.example.data.model.JimpitanTransaction
import com.example.data.model.PengeluaranKas
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.model.Warga
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SampleData {
  val defaultUsers = listOf(
    UserAccount(
      id = 1,
      username = "admin",
      pin = "123456",
      fullName = "H. Bambang Sudiro (Ketua RT / Bendahara)",
      role = UserRole.ADMIN,
      phone = "0812-3456-7890"
    ),
    UserAccount(
      id = 2,
      username = "petugas1",
      pin = "1234",
      fullName = "Agus Santoso (Ronda Regu A)",
      role = UserRole.PETUGAS,
      phone = "0813-9876-5432"
    ),
    UserAccount(
      id = 3,
      username = "petugas2",
      pin = "1234",
      fullName = "Joko Purwanto (Ronda Regu B)",
      role = UserRole.PETUGAS,
      phone = "0815-5555-1234"
    )
  )

  val defaultWarga = listOf(
    Warga(
      id = "WRG-RT04-001",
      name = "Bpk. Hendra Wijaya",
      houseNumber = "Blok A1 No. 01",
      rtRw = "RT 04 / RW 08",
      phone = "0812-1111-2222",
      barcodeData = "WRG-RT04-001",
      defaultTarif = 2000L,
      notes = "Kaleng jimpitan di tiang teras"
    ),
    Warga(
      id = "WRG-RT04-002",
      name = "Ibu Siti Rahmawati",
      houseNumber = "Blok A1 No. 02",
      rtRw = "RT 04 / RW 08",
      phone = "0812-2222-3333",
      barcodeData = "WRG-RT04-002",
      defaultTarif = 2000L,
      notes = "Kotak kayu depan pintu"
    ),
    Warga(
      id = "WRG-RT04-003",
      name = "Bpk. Slamet Riyadi",
      houseNumber = "Blok A2 No. 05",
      rtRw = "RT 04 / RW 08",
      phone = "0813-3333-4444",
      barcodeData = "WRG-RT04-003",
      defaultTarif = 2000L,
      notes = "Botol jimpitan di pagar besi"
    ),
    Warga(
      id = "WRG-RT04-004",
      name = "Bpk. Dedi Supriyadi",
      houseNumber = "Blok A2 No. 06",
      rtRw = "RT 04 / RW 08",
      phone = "0813-4444-5555",
      barcodeData = "WRG-RT04-004",
      defaultTarif = 5000L,
      notes = "Jimpitan mingguan / sukarela"
    ),
    Warga(
      id = "WRG-RT04-005",
      name = "Ibu Sri Wahyuni",
      houseNumber = "Blok B1 No. 09",
      rtRw = "RT 04 / RW 08",
      phone = "0815-5555-6666",
      barcodeData = "WRG-RT04-005",
      defaultTarif = 2000L,
      notes = "Gantung di dekat bel rumah"
    ),
    Warga(
      id = "WRG-RT04-006",
      name = "Bpk. Wahyu Pratama",
      houseNumber = "Blok B1 No. 10",
      rtRw = "RT 04 / RW 08",
      phone = "0816-6666-7777",
      barcodeData = "WRG-RT04-006",
      defaultTarif = 2000L,
      notes = "Kotak pos depan pagar"
    ),
    Warga(
      id = "WRG-RT04-007",
      name = "Bpk. Eko Prasetyo",
      houseNumber = "Blok B2 No. 14",
      rtRw = "RT 04 / RW 08",
      phone = "0817-7777-8888",
      barcodeData = "WRG-RT04-007",
      defaultTarif = 2000L,
      notes = "Samping meteran air"
    ),
    Warga(
      id = "WRG-RT04-008",
      name = "Ibu Nurul Aini",
      houseNumber = "Blok B2 No. 15",
      rtRw = "RT 04 / RW 08",
      phone = "0818-8888-9999",
      barcodeData = "WRG-RT04-008",
      defaultTarif = 2000L,
      notes = "Depan jendela ruang tamu"
    ),
    Warga(
      id = "WRG-RT04-009",
      name = "Bpk. Rudi Hartono",
      houseNumber = "Blok C1 No. 19",
      rtRw = "RT 04 / RW 08",
      phone = "0819-9999-0000",
      barcodeData = "WRG-RT04-009",
      defaultTarif = 2000L,
      notes = "Dekat lampu teras"
    ),
    Warga(
      id = "WRG-RT04-010",
      name = "Bpk. Suwandi",
      houseNumber = "Blok C1 No. 20",
      rtRw = "RT 04 / RW 08",
      phone = "0821-1234-5678",
      barcodeData = "WRG-RT04-010",
      defaultTarif = 2000L,
      notes = "Pohon mangga dekat gerbang"
    )
  )

  fun getInitialTransactions(todayDateString: String, yesterdayDateString: String): List<JimpitanTransaction> {
    val cal = Calendar.getInstance()
    val now = cal.timeInMillis
    val yesterday = now - 24 * 60 * 60 * 1000L

    return listOf(
      JimpitanTransaction(
        wargaId = "WRG-RT04-001",
        wargaName = "Bpk. Hendra Wijaya",
        houseNumber = "Blok A1 No. 01",
        amount = 2000L,
        dateString = yesterdayDateString,
        timestamp = yesterday + 3600000L,
        petugasId = 2,
        petugasName = "Agus Santoso",
        category = "Jimpitan Harian",
        notes = "Koin pas 2 keping seribu"
      ),
      JimpitanTransaction(
        wargaId = "WRG-RT04-002",
        wargaName = "Ibu Siti Rahmawati",
        houseNumber = "Blok A1 No. 02",
        amount = 2000L,
        dateString = yesterdayDateString,
        timestamp = yesterday + 4000000L,
        petugasId = 2,
        petugasName = "Agus Santoso",
        category = "Jimpitan Harian",
        notes = ""
      ),
      JimpitanTransaction(
        wargaId = "WRG-RT04-003",
        wargaName = "Bpk. Slamet Riyadi",
        houseNumber = "Blok A2 No. 05",
        amount = 5000L,
        dateString = yesterdayDateString,
        timestamp = yesterday + 4500000L,
        petugasId = 2,
        petugasName = "Agus Santoso",
        category = "Sukarela",
        notes = "Uang kertas Rp 5.000 titip kas"
      ),
      JimpitanTransaction(
        wargaId = "WRG-RT04-001",
        wargaName = "Bpk. Hendra Wijaya",
        houseNumber = "Blok A1 No. 01",
        amount = 2000L,
        dateString = todayDateString,
        timestamp = now - 7200000L,
        petugasId = 2,
        petugasName = "Agus Santoso",
        category = "Jimpitan Harian",
        notes = "Koin kaleng depan pagar"
      ),
      JimpitanTransaction(
        wargaId = "WRG-RT04-002",
        wargaName = "Ibu Siti Rahmawati",
        houseNumber = "Blok A1 No. 02",
        amount = 2000L,
        dateString = todayDateString,
        timestamp = now - 5400000L,
        petugasId = 2,
        petugasName = "Agus Santoso",
        category = "Jimpitan Harian",
        notes = ""
      )
    )
  }

  fun getInitialPengeluaran(currentDateString: String): List<PengeluaranKas> {
    return listOf(
      PengeluaranKas(
        title = "Konsumsi Kopi & Camilan Ronda Malam",
        amount = 35000L,
        dateString = currentDateString,
        timestamp = System.currentTimeMillis() - 86400000L,
        category = "Konsumsi Ronda",
        notes = "Beli kopi sachet, gula, dan gorengan ronda regu A",
        authorizedByAdmin = "H. Bambang Sudiro"
      ),
      PengeluaranKas(
        title = "Ganti Bohlam Lampu Penerangan Gang Blok B",
        amount = 45000L,
        dateString = currentDateString,
        timestamp = System.currentTimeMillis() - 172800000L,
        category = "Penerangan Jalan",
        notes = "Lampu LED Phillips 18 Watt untuk tiang gang 2",
        authorizedByAdmin = "H. Bambang Sudiro"
      ),
      PengeluaranKas(
        title = "Beli Baterai Senter & Peluit Pos Ronda",
        amount = 25000L,
        dateString = currentDateString,
        timestamp = System.currentTimeMillis() - 259200000L,
        category = "Operasional Ronda",
        notes = "Baterai ABC besar 4 buah untuk senter patroli",
        authorizedByAdmin = "H. Bambang Sudiro"
      )
    )
  }
}
