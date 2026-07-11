package com.tanerkaynar.nexuserp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.tanerkaynar.nexuserp.data.model.UretimRequest
import com.tanerkaynar.nexuserp.ui.components.GlobalErrorDialog
import com.tanerkaynar.nexuserp.ui.components.QrCodeScannerView
import com.tanerkaynar.nexuserp.ui.theme.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.tanerkaynar.nexuserp.ui.viewmodel.UretimViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.UretimViewModel.UretimUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UretimGirisScreen(
    viewModel: UretimViewModel,
    userRole: String,
    userPersonelId: Int? = null,
    userPersonelName: String? = null
) {
    val context = LocalContext.current
    val aktifUrunler by viewModel.aktifUrunler.collectAsState()
    val aktifMakineler by viewModel.aktifMakineler.collectAsState()
    val aktifPersoneller by viewModel.aktifPersoneller.collectAsState()
    val uretimState by viewModel.uretimState.collectAsState()

    var selectedUrunIndex by remember { mutableIntStateOf(-1) }
    var selectedMakineIndex by remember { mutableIntStateOf(-1) }
    var selectedPersonelIndex by remember { mutableIntStateOf(-1) }
    var uretimAdedi by remember { mutableStateOf("") }
    var aciklama by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    var isScanning by remember { mutableStateOf(false) }
    var showQrResultDialog by remember { mutableStateOf(false) }
    var scannedUrunId by remember { mutableIntStateOf(0) }
    var scannedMakineId by remember { mutableIntStateOf(0) }
    var scannedPersonelId by remember { mutableIntStateOf(0) }
    var scannedUrunAdi by remember { mutableStateOf("") }
    var scannedMakineAdi by remember { mutableStateOf("") }
    var scannedPersonelAdi by remember { mutableStateOf("") }
    var scannedAdet by remember { mutableStateOf("1") }

    var hasCameraPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (granted) {
                isScanning = true
            } else {
                errorMsg = "Kamera izni reddedildi. QR tarayıcıyı başlatmak için kamera izni vermelisiniz."
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.loadDropdowns()
        viewModel.loadAll()
    }

    LaunchedEffect(aktifPersoneller) {
        if (userPersonelId != null) {
            val idx = aktifPersoneller.indexOfFirst { it.personelid == userPersonelId }
            if (idx >= 0) {
                selectedPersonelIndex = idx
            }
        }
    }

    if (errorMsg != null) {
        GlobalErrorDialog(message = errorMsg!!, onDismiss = { errorMsg = null })
    }

    if (showQrResultDialog) {
        AlertDialog(
            onDismissRequest = { showQrResultDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = "QR Hızlı Üretim Girişi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Aşağıdaki bilgilerle hızlı üretim kaydı oluşturulacaktır:", color = TextMuted)
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BorderColor.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Ürün: $scannedUrunAdi", fontWeight = FontWeight.SemiBold, color = TextDark)
                            Text(text = "Makine: $scannedMakineAdi", fontWeight = FontWeight.SemiBold, color = TextDark)
                            Text(text = "Personel: $scannedPersonelAdi", fontWeight = FontWeight.SemiBold, color = TextDark)
                        }
                    }

                    OutlinedTextField(
                        value = scannedAdet,
                        onValueChange = { scannedAdet = it },
                        label = { Text("Üretim Adedi") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val adet = scannedAdet.toIntOrNull()
                        if (adet == null || adet <= 0) {
                            errorMsg = "Lütfen geçerli bir üretim adedi girin."
                            return@Button
                        }
                        viewModel.addUretim(
                            scannedUrunId,
                            scannedMakineId,
                            scannedPersonelId,
                            adet,
                            "QR Okuyucu ile hızlı kayıt",
                            onSuccess = {
                                showQrResultDialog = false
                                showSuccess = true
                                errorMsg = null
                            },
                            onError = { msg ->
                                showQrResultDialog = false
                                showSuccess = false
                                errorMsg = msg
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessColor)
                ) {
                    Text("Kaydet", color = CardWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQrResultDialog = false }) {
                    Text("İptal", color = TextMuted)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f)
            ) {
                QrCodeScannerView(onBarcodeScanned = { rawText ->
                    val parts = rawText.split(";")
                    var makineKod: String? = null
                    var urunPart: String? = null
                    var miktarVal = 1

                    if (parts.size >= 2) {
                        
                        makineKod = parts[0].trim()
                        urunPart = parts[1].trim()
                        if (parts.size > 2) {
                            val parsedMiktar = parts[2].trim().toIntOrNull()
                            if (parsedMiktar != null) {
                                miktarVal = parsedMiktar
                            }
                        }
                    } else if (parts.isNotEmpty()) {
                        
                        urunPart = parts[0].trim()
                    }

                    val urunId = urunPart?.toIntOrNull()

                    val makine = if (makineKod != null) {
                        aktifMakineler.find { it.makinekodu.equals(makineKod, ignoreCase = true) || it.makineadi.contains(makineKod, ignoreCase = true) }
                    } else {
                        
                        if (selectedMakineIndex >= 0) aktifMakineler.getOrNull(selectedMakineIndex) else null
                    }

                    val urun = aktifUrunler.find { it.urunid == urunId }

                    val personel = if (userRole.equals("operator", ignoreCase = true) && userPersonelId != null) {
                        aktifPersoneller.find { it.personelid == userPersonelId }
                    } else {
                        if (selectedPersonelIndex >= 0) aktifPersoneller.getOrNull(selectedPersonelIndex) else null
                    }

                    if (urun != null && makine != null && personel != null) {
                        scannedUrunId = urun.urunid ?: 0
                        scannedMakineId = makine.makineid ?: 0
                        scannedPersonelId = personel.personelid ?: 0
                        scannedUrunAdi = urun.urunadi
                        scannedMakineAdi = makine.makineadi
                        scannedPersonelAdi = personel.adsoyad
                        scannedAdet = miktarVal.toString()

                        isScanning = false
                        showQrResultDialog = true
                    } else {
                        isScanning = false
                        errorMsg = "Uyuşmayan veya eksik veriler bulundu:\n" +
                                "Ürün: ${if (urun != null) "✓" else "✗ (ID: $urunId)"}\n" +
                                "Makine: ${if (makine != null) "✓" else "✗ (Kod: $makineKod - Lütfen formdan seçin)"}\n" +
                                "Personel: ${if (personel != null) "✓" else "✗ (Lütfen formdan seçin)"}"
                    }
                })

                Button(
                    onClick = { isScanning = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Text("Tarama İptal", color = CardWhite)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = "Üretim Kaydı Girişi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        
                        Text("Ürün Seçin", fontWeight = FontWeight.SemiBold, color = TextDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        var urunExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = urunExpanded, onExpandedChange = { urunExpanded = it }) {
                            OutlinedTextField(
                                value = if (selectedUrunIndex >= 0) aktifUrunler[selectedUrunIndex].urunadi else "",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = urunExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                            )
                            ExposedDropdownMenu(expanded = urunExpanded, onDismissRequest = { urunExpanded = false }) {
                                aktifUrunler.forEachIndexed { index, urun ->
                                    DropdownMenuItem(text = { Text(urun.urunadi) }, onClick = { selectedUrunIndex = index; urunExpanded = false })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Makine Seçin", fontWeight = FontWeight.SemiBold, color = TextDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        var makineExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = makineExpanded, onExpandedChange = { makineExpanded = it }) {
                            OutlinedTextField(
                                value = if (selectedMakineIndex >= 0) aktifMakineler[selectedMakineIndex].makineadi else "",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = makineExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                            )
                            ExposedDropdownMenu(expanded = makineExpanded, onDismissRequest = { makineExpanded = false }) {
                                aktifMakineler.forEachIndexed { index, makine ->
                                    DropdownMenuItem(text = { Text(makine.makineadi) }, onClick = { selectedMakineIndex = index; makineExpanded = false })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val isOperator = userRole.equals("operator", ignoreCase = true)
                        val personelEnabled = !isOperator || userPersonelId == null

                        Text("Personel Seçin", fontWeight = FontWeight.SemiBold, color = TextDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        var personelExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = if (personelEnabled) personelExpanded else false,
                            onExpandedChange = { if (personelEnabled) personelExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = if (selectedPersonelIndex >= 0) aktifPersoneller[selectedPersonelIndex].adsoyad else "",
                                onValueChange = {},
                                readOnly = true,
                                enabled = personelEnabled,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                trailingIcon = {
                                    if (personelEnabled) {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = personelExpanded)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextDark,
                                    unfocusedTextColor = TextDark,
                                    disabledTextColor = TextDark.copy(alpha = 0.6f),
                                    focusedBorderColor = AccentLight,
                                    unfocusedBorderColor = BorderColor,
                                    disabledBorderColor = BorderColor.copy(alpha = 0.5f)
                                )
                            )
                            if (personelEnabled) {
                                ExposedDropdownMenu(expanded = personelExpanded, onDismissRequest = { personelExpanded = false }) {
                                    aktifPersoneller.forEachIndexed { index, personel ->
                                        DropdownMenuItem(text = { Text(personel.adsoyad) }, onClick = { selectedPersonelIndex = index; personelExpanded = false })
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uretimAdedi,
                            onValueChange = { uretimAdedi = it },
                            label = { Text("Üretim Adedi") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = aciklama,
                            onValueChange = { aciklama = it },
                            label = { Text("Açıklama") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextDark, unfocusedTextColor = TextDark, focusedBorderColor = AccentLight, unfocusedBorderColor = BorderColor)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val adet = uretimAdedi.toIntOrNull()
                                if (selectedUrunIndex < 0 || selectedMakineIndex < 0 || selectedPersonelIndex < 0 || adet == null || adet <= 0) {
                                    errorMsg = "Lütfen tüm alanları eksiksiz doldurun."
                                    return@Button
                                }
                                viewModel.addUretim(
                                    aktifUrunler[selectedUrunIndex].urunid ?: 0,
                                    aktifMakineler[selectedMakineIndex].makineid ?: 0,
                                    aktifPersoneller[selectedPersonelIndex].personelid ?: 0,
                                    adet,
                                    aciklama,
                                    onSuccess = {
                                        showSuccess = true
                                        errorMsg = null
                                        uretimAdedi = ""
                                        aciklama = ""
                                    },
                                    onError = { msg ->
                                        showSuccess = false
                                        errorMsg = msg
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Üretim Kaydet", color = CardWhite, fontWeight = FontWeight.Bold)
                        }

                        if (showSuccess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Üretim kaydı başarıyla oluşturuldu!", color = SuccessColor, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Son Üretim Kayıtları",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            when (val state = uretimState) {
                is UretimUiState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AccentLight)
                        }
                    }
                }
                is UretimUiState.Error -> {
                    item {
                        Text("Hata: ${state.message}", color = ErrorColor)
                    }
                }
                is UretimUiState.Success -> {
                    if (state.kayitlar.isEmpty()) {
                        item {
                            Text("Henüz üretim kaydı bulunmuyor.", color = TextMuted)
                        }
                    } else {
                        items(state.kayitlar) { kayit ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = kayit.urunadi ?: "—",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Makine: ${kayit.makineadi ?: "—"} | Personel: ${kayit.personeladi ?: "—"}",
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                        Text(
                                            text = "Miktar: ${kayit.uretimadedi} adet | Tarih: ${kayit.uretimtarihi ?: "—"}",
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                        if (!kayit.aciklama.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Açıklama: ${kayit.aciklama}",
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    var showDeleteConfirm by remember { mutableStateOf(false) }
                                    if (showDeleteConfirm) {
                                        AlertDialog(
                                            onDismissRequest = { showDeleteConfirm = false },
                                            containerColor = CardWhite,
                                            title = { Text("Kayıt Silme Onayı", fontWeight = FontWeight.Bold) },
                                            text = { Text("Bu üretim kaydını silmek istediğinizden emin misiniz? (Ürün stoğu bu miktar kadar geri düşürülecektir.)") },
                                            confirmButton = {
                                                Button(
                                                    onClick = {
                                                        viewModel.deleteUretim(
                                                            kayit.uretimid,
                                                            onSuccess = {
                                                                showDeleteConfirm = false
                                                                errorMsg = null
                                                            },
                                                            onError = { msg ->
                                                                showDeleteConfirm = false
                                                                errorMsg = msg
                                                            }
                                                        )
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

                                    IconButton(onClick = { showDeleteConfirm = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Kaydı Sil",
                                            tint = ErrorColor
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

        ExtendedFloatingActionButton(
            onClick = {
                if (hasCameraPermission) {
                    isScanning = true
                } else {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            },
            containerColor = AccentLight,
            contentColor = CardWhite,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("QR Tara", fontWeight = FontWeight.Bold)
        }
    }
}