package com.tanerkaynar.nexuserp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.ui.components.GlobalErrorDialog
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.UrunViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.UrunViewModel.UrunUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrunYonetimiScreen(viewModel: UrunViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingUrunId by remember { mutableStateOf<Int?>(null) }
    var deleteUrunId by remember { mutableStateOf<Int?>(null) }

    var urunAdi by remember { mutableStateOf("") }
    var stokMiktari by remember { mutableStateOf("") }
    var birimFiyat by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadUrunler()
    }

    Scaffold(
        containerColor = DesktopWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingUrunId = null
                    urunAdi = ""
                    stokMiktari = ""
                    birimFiyat = ""
                    showAddEditDialog = true
                },
                containerColor = AccentLight,
                contentColor = CardWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yeni Ürün")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UrunUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentLight
                    )
                }

                is UrunUiState.Error -> {
                    GlobalErrorDialog(
                        message = state.message,
                        onDismiss = { viewModel.loadUrunler() }
                    )
                }

                is UrunUiState.Success -> {
                    if (state.urunler.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = TextMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Henüz ürün eklenmemiş",
                                color = TextMuted,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.urunler) { urun ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
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
                                                text = urun.urunadi,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextDark
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                Text(
                                                    text = "Stok: ${urun.stokmiktari}",
                                                    fontSize = 13.sp,
                                                    color = TextMuted
                                                )
                                                Text(
                                                    text = "Fiyat: ₺${urun.birimfiyat}",
                                                    fontSize = 13.sp,
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                        Row {
                                            IconButton(onClick = {
                                                editingUrunId = urun.urunid
                                                urunAdi = urun.urunadi
                                                stokMiktari = urun.stokmiktari.toString()
                                                birimFiyat = urun.birimfiyat.toString()
                                                showAddEditDialog = true
                                            }) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Düzenle",
                                                    tint = AccentLight
                                                )
                                            }
                                            IconButton(onClick = {
                                                deleteUrunId = urun.urunid
                                                showDeleteDialog = true
                                            }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Sil",
                                                    tint = ErrorColor
                                                )
                                            }
                                        }
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

    if (showAddEditDialog) {
        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = if (editingUrunId == null) "Yeni Ürün Ekle" else "Ürünü Düzenle",
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = urunAdi,
                        onValueChange = { urunAdi = it },
                        label = { Text("Ürün Adı") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedBorderColor = AccentLight,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                    OutlinedTextField(
                        value = stokMiktari,
                        onValueChange = { stokMiktari = it },
                        label = { Text("Stok Miktarı") },
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
                    OutlinedTextField(
                        value = birimFiyat,
                        onValueChange = { birimFiyat = it },
                        label = { Text("Birim Fiyat (₺)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedBorderColor = AccentLight,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val stok = stokMiktari.toIntOrNull() ?: 0
                        val fiyat = birimFiyat.toDoubleOrNull() ?: 0.0
                        if (editingUrunId != null) {
                            viewModel.updateUrun(editingUrunId!!, urunAdi, stok, fiyat)
                        } else {
                            viewModel.addUrun(urunAdi, stok, fiyat)
                        }
                        showAddEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = false }) {
                    Text("İptal", color = TextMuted)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Ürünü Sil", fontWeight = FontWeight.Bold, color = TextDark)
            },
            text = {
                Text(
                    "Bu ürünü silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deleteUrunId?.let { viewModel.deleteUrun(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal", color = TextMuted)
                }
            }
        )
    }
}