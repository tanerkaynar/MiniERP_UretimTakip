package com.tanerkaynar.nexuserp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.ui.components.GlobalErrorDialog
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.SiparisViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.SiparisViewModel.SiparisUiState
import com.tanerkaynar.nexuserp.ui.viewmodel.TanimlamaViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.UrunViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiparisGirisScreen(
    viewModel: SiparisViewModel,
    tanimlamaViewModel: TanimlamaViewModel,
    urunViewModel: UrunViewModel
) {
    val siparisState by viewModel.uiState.collectAsState()
    val musteriler by tanimlamaViewModel.musteriListesi.collectAsState()
    val urunler by urunViewModel.urunListesi.collectAsState()

    var selectedMusteriId by remember { mutableStateOf<Int?>(null) }
    var selectedMusteriAdi by remember { mutableStateOf("Müşteri Seçiniz") }
    var musteriExpanded by remember { mutableStateOf(false) }

    var selectedUrunId by remember { mutableStateOf<Int?>(null) }
    var selectedUrunAdi by remember { mutableStateOf("Ürün Seçiniz") }
    var urunExpanded by remember { mutableStateOf(false) }

    var miktar by remember { mutableStateOf("") }
    var birimFiyat by remember { mutableStateOf("") }

    var editingSiparisId by remember { mutableStateOf<Int?>(null) }
    var deletingSiparisId by remember { mutableStateOf<Int?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        tanimlamaViewModel.loadMusteriler()
        urunViewModel.loadUrunler()
        viewModel.loadSiparisler()
    }

    Scaffold(
        containerColor = DesktopWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            if (siparisState is SiparisUiState.Error) {
                GlobalErrorDialog(
                    message = (siparisState as SiparisUiState.Error).message,
                    onDismiss = { viewModel.loadSiparisler() }
                )
            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    containerColor = CardWhite,
                    title = { Text("Siparişi Sil", fontWeight = FontWeight.Bold) },
                    text = { Text("Bu siparişi silmek istediğinizden emin misiniz?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                deletingSiparisId?.let { viewModel.deleteSiparis(it) }
                                showDeleteConfirm = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                        ) {
                            Text("Sil", color = CardWhite)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("İptal", color = TextMuted)
                        }
                    }
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (editingSiparisId != null) "Sipariş Düzenle" else "Yeni Sipariş",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        if (editingSiparisId != null) {
                            TextButton(onClick = {
                                editingSiparisId = null
                                selectedMusteriId = null
                                selectedMusteriAdi = "Müşteri Seçiniz"
                                selectedUrunId = null
                                selectedUrunAdi = "Ürün Seçiniz"
                                miktar = ""
                                birimFiyat = ""
                            }) {
                                Text("Vazgeç", color = ErrorColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = musteriExpanded,
                        onExpandedChange = { musteriExpanded = !musteriExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedMusteriAdi,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Müşteri") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = musteriExpanded)
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
                            expanded = musteriExpanded,
                            onDismissRequest = { musteriExpanded = false }
                        ) {
                            musteriler.forEach { musteri ->
                                DropdownMenuItem(
                                    text = { Text(musteri.musteriadi) },
                                    onClick = {
                                        selectedMusteriId = musteri.musteriid
                                        selectedMusteriAdi = musteri.musteriadi
                                        musteriExpanded = false
                                    }
                                )
                            }
                        }
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
                            urunler.forEach { urun ->
                                DropdownMenuItem(
                                    text = { Text(urun.urunadi) },
                                    onClick = {
                                        selectedUrunId = urun.urunid
                                        selectedUrunAdi = urun.urunadi
                                        birimFiyat = urun.birimfiyat.toString()
                                        urunExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = miktar,
                            onValueChange = { miktar = it },
                            label = { Text("Miktar") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                focusedBorderColor = AccentLight,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                        OutlinedTextField(
                            value = birimFiyat,
                            onValueChange = { birimFiyat = it },
                            label = { Text("Birim Fiyat (₺)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                focusedBorderColor = AccentLight,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                    }

                    Button(
                        onClick = {
                            val mkt = miktar.toIntOrNull() ?: 0
                            val fyt = birimFiyat.toDoubleOrNull() ?: 0.0
                            if (selectedMusteriId != null && selectedUrunId != null && mkt > 0) {
                                if (editingSiparisId != null) {
                                    viewModel.updateSiparis(
                                        editingSiparisId!!,
                                        selectedMusteriId!!,
                                        selectedUrunId!!,
                                        mkt,
                                        fyt
                                    )
                                } else {
                                    viewModel.addSiparis(
                                        selectedMusteriId!!,
                                        selectedUrunId!!,
                                        mkt,
                                        fyt
                                    )
                                }
                                
                                editingSiparisId = null
                                selectedMusteriId = null
                                selectedMusteriAdi = "Müşteri Seçiniz"
                                selectedUrunId = null
                                selectedUrunAdi = "Ürün Seçiniz"
                                miktar = ""
                                birimFiyat = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = CardWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (editingSiparisId != null) "Siparişi Güncelle" else "Sipariş Kaydet",
                            fontWeight = FontWeight.Bold,
                            color = CardWhite,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Text(
                text = "Mevcut Siparişler",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when (val state = siparisState) {
                is SiparisUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentLight)
                    }
                }

                is SiparisUiState.Success -> {
                    if (state.siparisler.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Henüz sipariş bulunmuyor", color = TextMuted, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.siparisler) { siparis ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = siparis.musteriadi ?: "—",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextDark
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = siparis.durum ?: "Bekliyor",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = when (siparis.durum?.lowercase()) {
                                                        "tamamlandı", "sevk edildi" -> SuccessColor
                                                        else -> WarningColor
                                                    }
                                                )
                                                if (siparis.durum?.lowercase() != "sevk edildi" && siparis.durum?.lowercase() != "tamamlandı") {
                                                    IconButton(
                                                        onClick = {
                                                            editingSiparisId = siparis.siparisid
                                                            selectedMusteriId = siparis.musteriid
                                                            selectedMusteriAdi = siparis.musteriadi ?: "Müşteri Seçiniz"
                                                            selectedUrunId = siparis.urunid
                                                            selectedUrunAdi = siparis.urunadi ?: "Ürün Seçiniz"
                                                            miktar = siparis.miktar.toString()
                                                            birimFiyat = siparis.birimfiyat.toString()
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = AccentLight, modifier = Modifier.size(16.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            deletingSiparisId = siparis.siparisid
                                                            showDeleteConfirm = true
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = ErrorColor, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Text(
                                                text = "Ürün: ${siparis.urunadi ?: "—"}",
                                                fontSize = 13.sp,
                                                color = TextMuted
                                            )
                                            Text(
                                                text = "Miktar: ${siparis.miktar}",
                                                fontSize = 13.sp,
                                                color = TextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Fiyat: ₺${siparis.birimfiyat} | Tarih: ${siparis.siparistarihi ?: "—"}",
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                    }
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