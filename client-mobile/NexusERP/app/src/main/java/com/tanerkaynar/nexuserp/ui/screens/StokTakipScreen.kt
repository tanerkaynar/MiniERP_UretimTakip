package com.tanerkaynar.nexuserp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.data.model.Urun
import com.tanerkaynar.nexuserp.ui.components.GlobalErrorDialog
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.UrunViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StokTakipScreen(viewModel: UrunViewModel) {
    val stokList by viewModel.stokTakipList.collectAsState()
    val urunListesi by viewModel.urunListesi.collectAsState()
    val isLoading by viewModel.stokLoading.collectAsState()
    val errorMessage by viewModel.stokError.collectAsState()

    var selectedUrunId by remember { mutableStateOf<Int?>(null) }
    var selectedUrunAdi by remember { mutableStateOf("Ürün Seçiniz") }
    var urunExpanded by remember { mutableStateOf(false) }
    var testMiktar by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var snackbarIsSuccess by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadStokTakip()
        viewModel.loadUrunler()
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        containerColor = DesktopWhite,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (snackbarIsSuccess) SuccessColor else ErrorColor,
                    contentColor = CardWhite,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            errorMessage?.let { msg ->
                GlobalErrorDialog(
                    message = msg,
                    onDismiss = { viewModel.clearStokError() }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = AccentLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stok Yeterlilik Testi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = urunExpanded,
                        onExpandedChange = { urunExpanded = !urunExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedUrunAdi,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ürün") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = urunExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                focusedBorderColor = AccentLight,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = urunExpanded,
                            onDismissRequest = { urunExpanded = false }
                        ) {
                            urunListesi.forEach { urun ->
                                DropdownMenuItem(
                                    text = { Text(urun.urunadi) },
                                    onClick = {
                                        selectedUrunId = urun.urunid
                                        selectedUrunAdi = urun.urunadi
                                        urunExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedUrunId != null) {
                        val seciliUrun = stokList.find { (it["urunid"] as? Number)?.toInt() == selectedUrunId }
                        val mevcutStok = if (seciliUrun != null) {
                            (seciliUrun["stokmiktari"] as? Number)?.toInt() ?: 0
                        } else {
                            urunListesi.find { it.urunid == selectedUrunId }?.stokmiktari ?: 0
                        }
                        Text(
                            text = "Seçili Ürünün Mevcut Stoğu: $mevcutStok adet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mevcutStok < 10) ErrorColor else SuccessColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    OutlinedTextField(
                        value = testMiktar,
                        onValueChange = { testMiktar = it },
                        label = { Text("İhtiyaç Miktarı") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedBorderColor = AccentLight,
                            unfocusedBorderColor = BorderColor
                        )
                    )

                    Button(
                        onClick = {
                            val ihtiyac = testMiktar.toIntOrNull() ?: 0
                            if (selectedUrunId != null && ihtiyac > 0) {
                                val seciliUrun = stokList.find { (it["urunid"] as? Number)?.toInt() == selectedUrunId }
                                val mevcutStok = if (seciliUrun != null) {
                                    (seciliUrun["stokmiktari"] as? Number)?.toInt() ?: 0
                                } else {
                                    urunListesi.find { it.urunid == selectedUrunId }?.stokmiktari ?: 0
                                }

                                if (mevcutStok >= ihtiyac) {
                                    snackbarIsSuccess = true
                                    snackbarMessage = "✓ Stok yeterli! Mevcut: $mevcutStok, İhtiyaç: $ihtiyac"
                                } else {
                                    snackbarIsSuccess = false
                                    snackbarMessage = "✗ Stok yetersiz! Mevcut: $mevcutStok, İhtiyaç: $ihtiyac, Eksik: ${ihtiyac - mevcutStok}"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CardWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kontrol Et", fontWeight = FontWeight.Bold, color = CardWhite)
                    }
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.List,
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stok Durumu",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentLight)
                    }
                }

                stokList.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Stok verisi bulunamadı", color = TextMuted, fontSize = 14.sp)
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(stokList) { stok ->
                            val status = stok["stokdurumu"]?.toString() ?: "Yeterli"
                            val name = stok["urunadi"]?.toString() ?: "-"
                            val amount = (stok["stokmiktari"] as? Number)?.toInt() ?: 0

                            val (bgColor, textColor) = when (status.lowercase()) {
                                "kritik" -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
                                "azalıyor", "azaliyor" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
                                else -> Color(0xFFF0FDF4) to Color(0xFF166534)
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Stok: $amount",
                                            fontSize = 13.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(bgColor)
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (status.lowercase() == "kritik") {
                                                Icon(
                                                    Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = textColor,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = status,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}