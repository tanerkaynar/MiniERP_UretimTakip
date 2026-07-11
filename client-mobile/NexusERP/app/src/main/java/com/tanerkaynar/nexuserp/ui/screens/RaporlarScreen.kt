package com.tanerkaynar.nexuserp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.data.model.UretimKaydi
import com.tanerkaynar.nexuserp.ui.components.GlobalErrorDialog
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.UretimViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.UretimViewModel.RaporUiState
import com.tanerkaynar.nexuserp.ui.viewmodel.UretimViewModel.GrupluUiState
import com.tanerkaynar.nexuserp.ui.viewmodel.UretimViewModel.DurusUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatMillisToDateString(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaporlarScreen(viewModel: UretimViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Üretim Raporu", "Gruplu Rapor", "Duruş Analizi")

    var baslangicTarih by remember { mutableStateOf("") }
    var bitisTarih by remember { mutableStateOf("") }
    var selectedGrupTipi by remember { mutableStateOf("Urun Bazli") }

    val raporState by viewModel.raporState.collectAsState()
    val grupluState by viewModel.grupluState.collectAsState()
    val durusState by viewModel.durusState.collectAsState()

    var showBaslangicPicker by remember { mutableStateOf(false) }
    var showBitisPicker by remember { mutableStateOf(false) }

    val baslangicDatePickerState = rememberDatePickerState()
    val bitisDatePickerState = rememberDatePickerState()

    if (showBaslangicPicker) {
        DatePickerDialog(
            onDismissRequest = { showBaslangicPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    baslangicDatePickerState.selectedDateMillis?.let { millis ->
                        baslangicTarih = formatMillisToDateString(millis)
                    }
                    showBaslangicPicker = false
                }) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { showBaslangicPicker = false }) { Text("İptal") }
            }
        ) {
            DatePicker(state = baslangicDatePickerState)
        }
    }

    if (showBitisPicker) {
        DatePickerDialog(
            onDismissRequest = { showBitisPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    bitisDatePickerState.selectedDateMillis?.let { millis ->
                        bitisTarih = formatMillisToDateString(millis)
                    }
                    showBitisPicker = false
                }) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { showBitisPicker = false }) { Text("İptal") }
            }
        ) {
            DatePicker(state = bitisDatePickerState)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Raporlar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TabRow(selectedTabIndex = selectedTab, containerColor = CardWhite) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(1.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = baslangicTarih,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Başlangıç") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { showBaslangicPicker = true })
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = bitisTarih,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Bitiş") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { showBitisPicker = true })
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { if (baslangicTarih.isNotBlank() && bitisTarih.isNotBlank()) viewModel.loadRapor(baslangicTarih, bitisTarih) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) { Text("Rapor Getir", color = CardWhite) }

                            if (raporState is RaporUiState.Success) {
                                val uretimList = (raporState as RaporUiState.Success).data
                                if (uretimList.isNotEmpty()) {
                                    val context = androidx.compose.ui.platform.LocalContext.current
                                    Button(
                                        onClick = {
                                            val csvHeader = "Urun Adi,Makine Adi,Personel Adi,Uretim Adedi,Uretim Tarihi\n"
                                            val csvRows = uretimList.joinToString("\n") { kayit ->
                                                "${kayit.urunadi ?: "-"},${kayit.makineadi ?: "-"},${kayit.personeladi ?: "-"},${kayit.uretimadedi},${kayit.uretimtarihi ?: "-"}"
                                            }
                                            val csvContent = csvHeader + csvRows
                                            val sendIntent = android.content.Intent().apply {
                                                action = android.content.Intent.ACTION_SEND
                                                putExtra(android.content.Intent.EXTRA_TEXT, csvContent)
                                                type = "text/csv"
                                            }
                                            val shareIntent = android.content.Intent.createChooser(sendIntent, "Raporu CSV Olarak Paylaş")
                                            context.startActivity(shareIntent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) { Text("CSV'ye Aktar", color = CardWhite) }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = raporState) {
                    is RaporUiState.Loading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentLight) }
                    is RaporUiState.Error -> GlobalErrorDialog(state.message) { viewModel.loadRapor(baslangicTarih, bitisTarih) }
                    is RaporUiState.Success -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.data) { kayit ->
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(8.dp)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("${kayit.urunadi ?: "-"}", fontWeight = FontWeight.SemiBold, color = TextDark)
                                        Text("Makine: ${kayit.makineadi ?: "-"} | Personel: ${kayit.personeladi ?: "-"}", color = TextMuted, fontSize = 12.sp)
                                        Text("Adet: ${kayit.uretimadedi} | Tarih: ${kayit.uretimtarihi ?: "-"}", color = TextMuted, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
            1 -> {
                
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        var expanded by remember { mutableStateOf(false) }
                        val tipOptions = listOf("Urun Bazli", "Makine Bazli", "Personel Bazli")
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            OutlinedTextField(
                                value = selectedGrupTipi,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                label = { Text("Gruplama Tipi") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                tipOptions.forEach { tip ->
                                    DropdownMenuItem(text = { Text(tip) }, onClick = { selectedGrupTipi = tip; expanded = false })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadGruplu(selectedGrupTipi) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Rapor Getir", color = CardWhite) }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = grupluState) {
                    is GrupluUiState.Loading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentLight) }
                    is GrupluUiState.Error -> GlobalErrorDialog(state.message) {}
                    is GrupluUiState.Success -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.gruplar) { row ->
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(8.dp)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        val name = row.values.firstOrNull()?.toString() ?: "-"
                                        Text(name, fontWeight = FontWeight.SemiBold, color = TextDark)
                                        val entries = row.entries.toList().drop(1)
                                        for (entry in entries) {
                                            Text("${entry.key}: ${entry.value ?: "-"}", color = TextMuted, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
            2 -> {
                
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = baslangicTarih,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Başlangıç") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { showBaslangicPicker = true })
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = bitisTarih,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Bitiş") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                                )
                                Box(modifier = Modifier.matchParentSize().clickable { showBitisPicker = true })
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { if (baslangicTarih.isNotBlank() && bitisTarih.isNotBlank()) viewModel.loadDurus(baslangicTarih, bitisTarih) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningColor),
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("Analiz Getir", color = CardWhite) }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = durusState) {
                    is DurusUiState.Loading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentLight) }
                    is DurusUiState.Error -> GlobalErrorDialog(state.message) {}
                    is DurusUiState.Success -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.duruslar) { row ->
                                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(1.dp), shape = RoundedCornerShape(8.dp)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(row["makineadi"]?.toString() ?: "-", fontWeight = FontWeight.SemiBold, color = ErrorColor)
                                        Text("Kod: ${row["makinekodu"] ?: "-"} | Durum: ${row["durum"] ?: "-"}", color = TextMuted, fontSize = 12.sp)
                                        Text("Bu tarih aralığında üretim yapılmadı", color = WarningColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}