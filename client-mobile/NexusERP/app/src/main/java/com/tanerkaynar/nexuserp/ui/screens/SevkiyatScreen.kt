package com.tanerkaynar.nexuserp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.ui.components.GlobalErrorDialog
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.SiparisViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.SiparisViewModel.SiparisUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevkiyatScreen(viewModel: SiparisViewModel) {
    val sevkState by viewModel.sevkState.collectAsState()

    var showSevkDialog by remember { mutableStateOf(false) }
    var sevkSiparisId by remember { mutableStateOf<Int?>(null) }
    var sevkSiparisInfo by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadBekleyenler()
    }

    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar("Sevkiyat başarıyla tamamlandı!")
            showSuccessSnackbar = false
        }
    }

    Scaffold(
        containerColor = DesktopWhite,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = SuccessColor,
                    contentColor = CardWhite,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            
            if (sevkState is SiparisUiState.Error) {
                GlobalErrorDialog(
                    message = (sevkState as SiparisUiState.Error).message,
                    onDismiss = { viewModel.loadBekleyenler() }
                )
            }

            when (val state = sevkState) {
                is SiparisUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentLight
                    )
                }

                is SiparisUiState.Success -> {
                    if (state.siparisler.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = TextMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Bekleyen sipariş bulunmuyor",
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
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = TextDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Bekleyen Siparişler (${state.siparisler.size})",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                }
                            }

                            items(state.siparisler) { siparis ->
                                val stokYeterli = siparis.mevcutstok >= siparis.miktar
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
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
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = siparis.musteriadi ?: "—",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextDark
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Ürün: ${siparis.urunadi ?: "—"}",
                                                    fontSize = 13.sp,
                                                    color = TextMuted
                                                )
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                    Text(
                                                        text = "Miktar: ${siparis.miktar}",
                                                        fontSize = 13.sp,
                                                        color = TextMuted
                                                    )
                                                    Text(
                                                        text = "Tarih: ${siparis.siparistarihi ?: "—"}",
                                                        fontSize = 13.sp,
                                                        color = TextMuted
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (stokYeterli) Color(0xFFF0FDF4) else Color(0xFFFEE2E2))
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (stokYeterli) Icons.Default.Info else Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = if (stokYeterli) SuccessColor else ErrorColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (stokYeterli) {
                                                    "Stok Yeterli (Mevcut: ${siparis.mevcutstok} adet)"
                                                } else {
                                                    "Stok Yetersiz! (Mevcut: ${siparis.mevcutstok} adet)"
                                                },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (stokYeterli) SuccessColor else ErrorColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = {
                                                sevkSiparisId = siparis.siparisid
                                                sevkSiparisInfo =
                                                    "${siparis.musteriadi ?: "—"} - ${siparis.urunadi ?: "—"} (${siparis.miktar} adet)"
                                                showSevkDialog = true
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (stokYeterli) AccentLight else Color(0xFF94A3B8)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ShoppingCart,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = CardWhite
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Sevk Et",
                                                fontWeight = FontWeight.Bold,
                                                color = CardWhite,
                                                fontSize = 14.sp
                                            )
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

    if (showSevkDialog) {
        AlertDialog(
            onDismissRequest = { showSevkDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = AccentLight,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    "Sevkiyat Onayı",
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                Column {
                    Text(
                        "Aşağıdaki siparişi sevk etmek istediğinizden emin misiniz?",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = sevkSiparisInfo,
                        fontWeight = FontWeight.Medium,
                        color = TextDark,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sevkSiparisId?.let { id ->
                            viewModel.sevkEt(id) {
                                showSuccessSnackbar = true
                            }
                        }
                        showSevkDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sevk Et")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSevkDialog = false }) {
                    Text("İptal", color = TextMuted)
                }
            }
        )
    }
}