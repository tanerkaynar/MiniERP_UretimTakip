package com.tanerkaynar.nexuserp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.data.model.Makine
import com.tanerkaynar.nexuserp.data.model.Musteri
import com.tanerkaynar.nexuserp.data.model.Personel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.tanerkaynar.nexuserp.ui.components.GlobalErrorDialog
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.TanimlamaViewModel
import com.tanerkaynar.nexuserp.ui.viewmodel.TanimlamaViewModel.ListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TanimlamalarScreen(viewModel: TanimlamaViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Müşteriler", "Makineler", "Personeller")

    val musteriState by viewModel.musteriState.collectAsState()
    val makineState by viewModel.makineState.collectAsState()
    val personelState by viewModel.personelState.collectAsState()

    var showMusteriDialog by remember { mutableStateOf(false) }
    var showMakineDialog by remember { mutableStateOf(false) }
    var showPersonelDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var editingId by remember { mutableStateOf<Int?>(null) }
    var deleteId by remember { mutableStateOf<Int?>(null) }
    var deleteType by remember { mutableStateOf("") }

    var musteriAdi by remember { mutableStateOf("") }

    var makineAdi by remember { mutableStateOf("") }
    var makineKodu by remember { mutableStateOf("") }
    var makineDurum by remember { mutableStateOf("Aktif") }
    var durumExpanded by remember { mutableStateOf(false) }

    var adSoyad by remember { mutableStateOf("") }
    var departman by remember { mutableStateOf("") }
    var personelAktif by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadMusteriler()
        viewModel.loadMakineler()
        viewModel.loadPersoneller()
    }

    Scaffold(
        containerColor = DesktopWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingId = null
                    when (selectedTabIndex) {
                        0 -> {
                            musteriAdi = ""
                            showMusteriDialog = true
                        }
                        1 -> {
                            makineAdi = ""
                            makineKodu = ""
                            makineDurum = "Aktif"
                            showMakineDialog = true
                        }
                        2 -> {
                            adSoyad = ""
                            departman = ""
                            personelAktif = true
                            showPersonelDialog = true
                        }
                    }
                },
                containerColor = AccentLight,
                contentColor = CardWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CardWhite,
                contentColor = AccentLight,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AccentLight
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) AccentLight else TextMuted
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> MusteriTab(
                    state = musteriState,
                    onEdit = { id, adi ->
                        editingId = id
                        musteriAdi = adi
                        showMusteriDialog = true
                    },
                    onDelete = { id ->
                        deleteId = id
                        deleteType = "musteri"
                        showDeleteDialog = true
                    },
                    onClearError = { viewModel.loadMusteriler() }
                )
                1 -> MakineTab(
                    state = makineState,
                    onEdit = { id, adi, kodu, durum ->
                        editingId = id
                        makineAdi = adi
                        makineKodu = kodu
                        makineDurum = durum
                        showMakineDialog = true
                    },
                    onDelete = { id ->
                        deleteId = id
                        deleteType = "makine"
                        showDeleteDialog = true
                    },
                    onClearError = { viewModel.loadMakineler() }
                )
                2 -> PersonelTab(
                    state = personelState,
                    onEdit = { id, ad, dept, aktif ->
                        editingId = id
                        adSoyad = ad
                        departman = dept
                        personelAktif = aktif
                        showPersonelDialog = true
                    },
                    onDelete = { id ->
                        deleteId = id
                        deleteType = "personel"
                        showDeleteDialog = true
                    },
                    onClearError = { viewModel.loadPersoneller() }
                )
            }
        }
    }

    if (showMusteriDialog) {
        AlertDialog(
            onDismissRequest = { showMusteriDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    if (editingId == null) "Yeni Müşteri" else "Müşteriyi Düzenle",
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                OutlinedTextField(
                    value = musteriAdi,
                    onValueChange = { musteriAdi = it },
                    label = { Text("Müşteri Adı") },
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingId != null) {
                            viewModel.updateMusteri(editingId!!, musteriAdi)
                        } else {
                            viewModel.addMusteri(musteriAdi)
                        }
                        showMusteriDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showMusteriDialog = false }) {
                    Text("İptal", color = TextMuted)
                }
            }
        )
    }

    if (showMakineDialog) {
        AlertDialog(
            onDismissRequest = { showMakineDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    if (editingId == null) "Yeni Makine" else "Makineyi Düzenle",
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = makineAdi,
                        onValueChange = { makineAdi = it },
                        label = { Text("Makine Adı") },
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
                        value = makineKodu,
                        onValueChange = { makineKodu = it },
                        label = { Text("Makine Kodu") },
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
                    ExposedDropdownMenuBox(
                        expanded = durumExpanded,
                        onExpandedChange = { durumExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = makineDurum,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Durum") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = durumExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                focusedBorderColor = AccentLight,
                                unfocusedBorderColor = BorderColor
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = durumExpanded,
                            onDismissRequest = { durumExpanded = false }
                        ) {
                            listOf("Aktif", "Pasif", "Arizali").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        makineDurum = option
                                        durumExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingId != null) {
                            viewModel.updateMakine(editingId!!, makineAdi, makineKodu, makineDurum)
                        } else {
                            viewModel.addMakine(makineAdi, makineKodu, makineDurum)
                        }
                        showMakineDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showMakineDialog = false }) {
                    Text("İptal", color = TextMuted)
                }
            }
        )
    }

    if (showPersonelDialog) {
        AlertDialog(
            onDismissRequest = { showPersonelDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    if (editingId == null) "Yeni Personel" else "Personeli Düzenle",
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = adSoyad,
                        onValueChange = { adSoyad = it },
                        label = { Text("Ad Soyad") },
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
                        value = departman,
                        onValueChange = { departman = it },
                        label = { Text("Departman") },
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
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Personel Aktif mi?", color = TextDark, fontSize = 14.sp)
                        Switch(
                            checked = personelAktif,
                            onCheckedChange = { personelAktif = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CardWhite,
                                checkedTrackColor = AccentLight,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = BorderColor
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingId != null) {
                            viewModel.updatePersonel(editingId!!, adSoyad, departman, personelAktif)
                        } else {
                            viewModel.addPersonel(adSoyad, departman, personelAktif)
                        }
                        showPersonelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentLight),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showPersonelDialog = false }) {
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
                Text("Kaydı Sil", fontWeight = FontWeight.Bold, color = TextDark)
            },
            text = {
                Text(
                    "Bu kaydı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        deleteId?.let { id ->
                            when (deleteType) {
                                "musteri" -> viewModel.deleteMusteri(id)
                                "makine" -> viewModel.deleteMakine(id)
                                "personel" -> viewModel.deletePersonel(id)
                            }
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Sil") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun MusteriTab(
    state: ListUiState<Musteri>,
    onEdit: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onClearError: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is ListUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentLight
                )
            }
            is ListUiState.Error -> {
                GlobalErrorDialog(
                    message = state.message,
                    onDismiss = onClearError
                )
            }
            is ListUiState.Success -> {
                if (state.items.isEmpty()) {
                    EmptyListMessage("Henüz müşteri eklenmemiş", Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.items) { musteri ->
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
                                            text = musteri.musteriadi,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = { onEdit(musteri.musteriid ?: 0, musteri.musteriadi) }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = AccentLight)
                                        }
                                        IconButton(onClick = { onDelete(musteri.musteriid ?: 0) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Sil", tint = ErrorColor)
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

@Composable
private fun MakineTab(
    state: ListUiState<Makine>,
    onEdit: (Int, String, String, String) -> Unit,
    onDelete: (Int) -> Unit,
    onClearError: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is ListUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentLight
                )
            }
            is ListUiState.Error -> {
                GlobalErrorDialog(
                    message = state.message,
                    onDismiss = onClearError
                )
            }
            is ListUiState.Success -> {
                if (state.items.isEmpty()) {
                    EmptyListMessage("Henüz makine eklenmemiş", Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.items) { makine ->
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
                                            text = makine.makineadi,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text(
                                                text = "Kod: ${makine.makinekodu}",
                                                fontSize = 13.sp,
                                                color = TextMuted
                                            )
                                            Text(
                                                text = "Durum: ${makine.durum}",
                                                fontSize = 13.sp,
                                                color = when (makine.durum.lowercase()) {
                                                    "aktif" -> SuccessColor
                                                    "arızalı" -> ErrorColor
                                                    else -> WarningColor
                                                }
                                            )
                                        }
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            onEdit(makine.makineid ?: 0, makine.makineadi, makine.makinekodu, makine.durum)
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = AccentLight)
                                        }
                                        IconButton(onClick = { onDelete(makine.makineid ?: 0) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Sil", tint = ErrorColor)
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

@Composable
private fun PersonelTab(
    state: ListUiState<Personel>,
    onEdit: (Int, String, String, Boolean) -> Unit,
    onDelete: (Int) -> Unit,
    onClearError: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is ListUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentLight
                )
            }
            is ListUiState.Error -> {
                GlobalErrorDialog(
                    message = state.message,
                    onDismiss = onClearError
                )
            }
            is ListUiState.Success -> {
                if (state.items.isEmpty()) {
                    EmptyListMessage("Henüz personel eklenmemiş", Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.items) { personel ->
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
                                            text = personel.adsoyad,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text(
                                                text = "Departman: ${personel.departman}",
                                                fontSize = 13.sp,
                                                color = TextMuted
                                            )
                                            Text(
                                                text = "Durum: ${if (personel.aktifmi) "Aktif" else "Pasif"}",
                                                fontSize = 13.sp,
                                                color = if (personel.aktifmi) SuccessColor else ErrorColor
                                            )
                                        }
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            onEdit(personel.personelid ?: 0, personel.adsoyad, personel.departman, personel.aktifmi)
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = AccentLight)
                                        }
                                        IconButton(onClick = { onDelete(personel.personelid ?: 0) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Sil", tint = ErrorColor)
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

@Composable
private fun EmptyListMessage(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.List,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = TextMuted
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, color = TextMuted, fontSize = 15.sp)
    }
}