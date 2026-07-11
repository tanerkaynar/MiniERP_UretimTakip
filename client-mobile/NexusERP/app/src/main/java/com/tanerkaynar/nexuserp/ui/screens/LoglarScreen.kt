package com.tanerkaynar.nexuserp.ui.screens

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
import com.tanerkaynar.nexuserp.ui.components.GlobalErrorDialog
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.LogViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.LogViewModel.LogUiState

@Composable
fun LoglarScreen(viewModel: LogViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadLoglar() }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Sistem Günlükleri",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = uiState) {
            is LogUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentLight)
                }
            }
            is LogUiState.Error -> {
                GlobalErrorDialog(message = state.message, onDismiss = { viewModel.loadLoglar() })
            }
            is LogUiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.loglar) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.islemturu,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AccentLight,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = log.islemtarihi ?: "",
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = log.aciklama, color = TextDark, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Kullanıcı: ${log.kullaniciadi}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}