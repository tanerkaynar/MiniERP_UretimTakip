package com.tanerkaynar.nexuserp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.data.model.LoginResponse
import com.tanerkaynar.nexuserp.data.model.Personel
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.AuthViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.LoginUiState
import com.tanerkaynar.nexuserp.ui.viewmodel.RegisterUiState
import com.tanerkaynar.nexuserp.ui.viewmodel.TanimlamaViewModel.ListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    tanimlamaViewModel: com.tanerkaynar.nexuserp.ui.viewmodel.TanimlamaViewModel,
    themeMode: Int,
    onThemeChange: (Int) -> Unit,
    onLoginSuccess: (LoginResponse) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }

    var showRegister by remember { mutableStateOf(false) }
    var regUsername by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("Operator") }
    var roleExpanded by remember { mutableStateOf(false) }
    
    var selectedPersonelIndex by remember { mutableIntStateOf(-1) }
    var personelExpanded by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val registerState by viewModel.registerState.collectAsState()
    val personelState: ListUiState<Personel> by tanimlamaViewModel.kullaniciAtanmamisPersonelState.collectAsState()

    val personeller: List<Personel> = remember(personelState) {
        if (personelState is ListUiState.Success) {
            (personelState as ListUiState.Success<Personel>).items
        } else {
            emptyList()
        }
    }

    LaunchedEffect(showRegister) {
        if (showRegister) {
            tanimlamaViewModel.loadKullaniciAtanmamisPersoneller()
            selectedPersonelIndex = -1
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess((uiState as LoginUiState.Success).data)
            viewModel.resetState()
        }
    }

    LaunchedEffect(registerState) {
        if (registerState is RegisterUiState.Success) {
            showRegister = false
            regUsername = ""
            regPassword = ""
            viewModel.resetRegisterState()
        }
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("Tamam", fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("Uygulama Teması", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Görünüm tercihinizi seçin:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeChange(0) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = themeMode == 0, onClick = { onThemeChange(0) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sistem Varsayılanı", color = MaterialTheme.colorScheme.onSurface)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeChange(1) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = themeMode == 1, onClick = { onThemeChange(1) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Açık Tema", color = MaterialTheme.colorScheme.onSurface)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeChange(2) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = themeMode == 2, onClick = { onThemeChange(2) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Koyu Tema", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        )
    }

    if (showRegister) {
        AlertDialog(
            onDismissRequest = { showRegister = false },
            confirmButton = {
                val registerEnabled = selectedPersonelIndex >= 0
                TextButton(
                    onClick = {
                        val personelId = personeller.getOrNull(selectedPersonelIndex)?.personelid
                        viewModel.register(regUsername, regPassword, regRole, personelId)
                    },
                    enabled = registerEnabled
                ) {
                    Text("Kayıt Ol", fontWeight = FontWeight.Bold, color = if (registerEnabled) AccentLight else TextMuted)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegister = false }) {
                    Text("İptal", color = TextMuted)
                }
            },
            title = { Text("Yeni Hesap Oluştur (Kayıt Ol)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = regUsername,
                        onValueChange = { regUsername = it },
                        label = { Text("Kullanıcı Adı") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = AccentLight,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("Şifre") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = AccentLight,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Column {
                        Text("Rol Seçin:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = roleExpanded,
                            onExpandedChange = { roleExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = regRole.uppercase(),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedBorderColor = AccentLight,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = roleExpanded,
                                onDismissRequest = { roleExpanded = false }
                            ) {
                                listOf("Operator", "Sevkiyatci", "Planlamaci").forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role) },
                                        onClick = {
                                            regRole = role
                                            roleExpanded = false
                                            selectedPersonelIndex = -1
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text("Personel Seçin:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = personelExpanded,
                            onExpandedChange = { personelExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = if (selectedPersonelIndex >= 0) personeller.getOrNull(selectedPersonelIndex)?.adsoyad ?: "" else "Bir personel seçin...",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = personelExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedBorderColor = AccentLight,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = personelExpanded,
                                 onDismissRequest = { personelExpanded = false }
                            ) {
                                personeller.forEachIndexed { index, personel ->
                                    DropdownMenuItem(
                                        text = { Text(personel.adsoyad) },
                                        onClick = {
                                            selectedPersonelIndex = index
                                            personelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (registerState is RegisterUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = AccentLight)
                    }
                    if (registerState is RegisterUiState.Error) {
                        Text(
                            text = (registerState as RegisterUiState.Error).message,
                            color = ErrorColor,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        
        IconButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ayarlar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                text = "NexusERP",
                color = AccentLight,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Mobil Üretim & Takip Sistemi",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Kullanıcı Adı") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = AccentLight,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = AccentLight,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Şifre") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = AccentLight,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = AccentLight,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(color = AccentLight)
            } else {
                Button(
                    onClick = { viewModel.login(username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Giriş Yap",
                        color = CardWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (uiState is LoginUiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (uiState as LoginUiState.Error).message,
                    color = ErrorColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Yeni Hesap Oluştur (Kayıt Ol)",
                color = AccentLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        viewModel.resetRegisterState()
                        showRegister = true
                    }
                    .padding(8.dp)
            )
        }
    }
}