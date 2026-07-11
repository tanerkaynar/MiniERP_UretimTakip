package com.tanerkaynar.nexuserp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.ui.navigation.Screen
import com.tanerkaynar.nexuserp.ui.navigation.getScreensForRole
import com.tanerkaynar.nexuserp.ui.theme.*
import com.tanerkaynar.nexuserp.ui.viewmodel.*
import kotlinx.coroutines.launch

data class MenuCardEntry(
    val title: String,
    val description: String,
    val screenTarget: Screen,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    username: String,
    role: String,
    personelId: Int? = null,
    personelName: String? = null,
    urunViewModel: UrunViewModel,
    tanimlamaViewModel: TanimlamaViewModel,
    siparisViewModel: SiparisViewModel,
    uretimViewModel: UretimViewModel,
    logViewModel: LogViewModel,
    themeMode: Int,
    onThemeChange: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val allowedScreens = remember(role) { getScreensForRole(role) }
    var currentScreen by remember(allowedScreens) {
        mutableStateOf(if (allowedScreens.contains(Screen.Dashboard)) Screen.Dashboard else allowedScreens.firstOrNull() ?: Screen.Dashboard)
    }

    var showSettingsDialog by remember { mutableStateOf(false) }

    val hasDashboard = allowedScreens.contains(Screen.Dashboard)
    BackHandler(enabled = hasDashboard && currentScreen != Screen.Dashboard) {
        currentScreen = Screen.Dashboard
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Tamam", fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("Ayarlar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DesktopBlue,
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Column {
                                Text(
                                    text = "NexusERP",
                                    color = CardWhite,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mobil Yönetim Paneli",
                                    color = TextLight.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        HorizontalDivider(color = CardWhite.copy(alpha = 0.1f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        allowedScreens.forEach { screen ->
                            val isSelected = currentScreen == screen
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AccentLight else Color.Transparent)
                                    .clickable {
                                        currentScreen = screen
                                        scope.launch { drawerState.close() }
                                    }
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) CardWhite else TextLight.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = screen.title,
                                    color = if (isSelected) CardWhite else TextLight.copy(alpha = 0.8f),
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                    }

                    Column {
                        HorizontalDivider(color = CardWhite.copy(alpha = 0.1f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onLogout() }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Çıkış Yap",
                                tint = ErrorColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Oturumu Kapat",
                                color = ErrorColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentScreen.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val displayName = if (!personelName.isNullOrBlank()) "$personelName ($username)" else username
                            Text(
                                text = "$displayName | ${role.uppercase()}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menü",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ayarlar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (currentScreen == Screen.Dashboard) {
                    DashboardHomeContent(
                        role = role,
                        onNavigate = { currentScreen = it }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        when (currentScreen) {
                            Screen.UrunYonetimi -> UrunYonetimiScreen(viewModel = urunViewModel)
                            Screen.Tanimlamalar -> TanimlamalarScreen(viewModel = tanimlamaViewModel)
                            Screen.Uretim -> UretimGirisScreen(
                                viewModel = uretimViewModel,
                                userRole = role,
                                userPersonelId = personelId,
                                userPersonelName = personelName
                            )
                            Screen.Siparis -> {
                                if (role.lowercase() == "sevkiyatci") {
                                    SevkiyatScreen(viewModel = siparisViewModel)
                                } else {
                                    SiparisGirisScreen(viewModel = siparisViewModel, tanimlamaViewModel = tanimlamaViewModel, urunViewModel = urunViewModel)
                                }
                            }
                            Screen.Stok -> StokTakipScreen(viewModel = urunViewModel)
                            Screen.Raporlar -> RaporlarScreen(viewModel = uretimViewModel)
                            Screen.Loglar -> LoglarScreen(viewModel = logViewModel)
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardHomeContent(
    role: String,
    onNavigate: (Screen) -> Unit
) {
    var stats by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    var isLoadingStats by remember { mutableStateOf(true) }

    LaunchedEffect(role) {
        isLoadingStats = true
        try {
            val response = com.tanerkaynar.nexuserp.data.api.RetrofitClient.getApiService().getDashboardStats(role)
            if (response.isSuccessful && response.body() != null) {
                stats = response.body()!!
            }
        } catch (e: Exception) {
            
        } finally {
            isLoadingStats = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        item {
            Text(
                text = "Sistem Özet Bilgileri",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            if (isLoadingStats) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentLight)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val r = role.lowercase()
                    when (r) {
                        "admin", "planlamaci" -> {
                            val urunSayisi = stats["urunSayisi"]?.toString() ?: "-"
                            val toplamUretim = stats["toplamUretim"]?.toString() ?: "-"
                            val aktifMakine = stats["aktifMakine"]?.toString() ?: "-"
                            val kritikStok = stats["kritikStok"]?.toString() ?: "-"

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DashboardCard("Toplam Ürün", urunSayisi, Color(0xFF3B82F6))
                                DashboardCard("Aktif Makineler", aktifMakine, Color(0xFFA855F7))
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DashboardCard("Toplam Üretim", toplamUretim, Color(0xFF22C55E))
                                DashboardCard("Kritik Stok", kritikStok, Color(0xFFEF4444))
                            }
                        }
                        "operator" -> {
                            val bugunkuUretim = stats["bugunkuUretim"]?.toString() ?: "-"
                            val calisanMakine = stats["calisanMakine"]?.toString() ?: "-"
                            val sonUretimStr = stats["sonUretimStr"]?.toString() ?: "-"

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DashboardCard("Bugün Üretim", bugunkuUretim, Color(0xFF22C55E))
                                DashboardCard("Son Kayıt Saati", sonUretimStr, Color(0xFFF59E0B))
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DashboardCard("Aktif Makineler", calisanMakine, Color(0xFF3B82F6))
                                DashboardCard("Sistem Durumu", "Aktif", Color(0xFF22C55E))
                            }
                        }
                        "sevkiyatci" -> {
                            val bekleyenSiparis = stats["bekleyenSiparis"]?.toString() ?: "-"
                            val kritikStok = stats["kritikStok"]?.toString() ?: "-"
                            val sevkEdilen = stats["sevkEdilen"]?.toString() ?: "-"

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DashboardCard("Bekleyen Sevkiyat", bekleyenSiparis, Color(0xFFF59E0B))
                                DashboardCard("Tamamlanan Sevk", sevkEdilen, Color(0xFF22C55E))
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                DashboardCard("Kritik Stok Ürün", kritikStok, Color(0xFFEF4444))
                                DashboardCard("Sevkiyat Alanı", "Hazır", Color(0xFF3B82F6))
                            }
                        }
                    }
                }
            }
        }

        val r = role.lowercase()
        if (r == "admin") {
            item { DashboardCategorySection("Ürün ve Sistem Yönetimi", "Ürün kayıtları, müşteri, makine ve personel tanımlamaları.",
                listOf(
                    MenuCardEntry("Ürün Yönetimi", "Ürün kaydet, güncelle, sil ve listele", Screen.UrunYonetimi, Color(0xFF0EA5E9)),
                    MenuCardEntry("Sistem Tanımlamaları", "Müşteri, Makine ve Personel kartları", Screen.Tanimlamalar, Color(0xFF06B6D4))
                ), onNavigate)
            }
            item { DashboardCategorySection("Üretim Operasyonları", "Üretim kaydı ve ilişkili işlem ekranları.",
                listOf(
                    MenuCardEntry("Üretim Girişi", "Personel, makine ve ürünle kayıt aç", Screen.Uretim, Color(0xFF22C55E))
                ), onNavigate)
            }
            item { DashboardCategorySection("Stok, Sipariş ve Sevkiyat", "Stok kontrolü, sipariş girişi ve sevk işlemleri.",
                listOf(
                    MenuCardEntry("Stok Takip & Kontrol", "Stok ve yeterlilik takibi", Screen.Stok, Color(0xFFF59E0B)),
                    MenuCardEntry("Sipariş Girişi", "Müşteri siparişi ve detay kaydı", Screen.Siparis, Color(0xFFFB923C)),
                    MenuCardEntry("Sevkiyat Yönetimi", "Siparişi sevk et ve stoktan düş", Screen.Siparis, Color(0xFFEA580C))
                ), onNavigate)
            }
            item { DashboardCategorySection("Raporlar ve Analiz", "Üretim raporları, duruş analizi ve filtrelemeler.",
                listOf(
                    MenuCardEntry("Raporlar & Analizler", "Üretim Raporu, Gruplu Rapor ve Duruş Analizi", Screen.Raporlar, Color(0xFF8B5CF6))
                ), onNavigate)
            }
            item { DashboardCategorySection("Sistem Yardımcıları", "Sistem işlem logları.",
                listOf(
                    MenuCardEntry("Sistem Günlükleri", "Son sistem işlemlerini gör", Screen.Loglar, Color(0xFF475569))
                ), onNavigate)
            }
        } else if (r == "planlamaci") {
            item { DashboardCategorySection("Ürün ve Sistem Yönetimi", "Ürün kayıtları, müşteri, makine ve personel tanımlamaları.",
                listOf(
                    MenuCardEntry("Ürün Yönetimi", "Ürün kaydet, güncelle, sil ve listele", Screen.UrunYonetimi, Color(0xFF0EA5E9)),
                    MenuCardEntry("Sistem Tanımlamaları", "Müşteri, Makine ve Personel kartları", Screen.Tanimlamalar, Color(0xFF06B6D4))
                ), onNavigate)
            }
            item { DashboardCategorySection("Stok ve Sipariş", "Stok kontrolü ve sipariş giriş işlemleri.",
                listOf(
                    MenuCardEntry("Stok Takip & Kontrol", "Stok ve yeterlilik takibi", Screen.Stok, Color(0xFFF59E0B)),
                    MenuCardEntry("Sipariş Girişi", "Müşteri siparişi ve detay kaydı", Screen.Siparis, Color(0xFFFB923C))
                ), onNavigate)
            }
            item { DashboardCategorySection("Raporlar ve Analiz", "Üretim raporları, duruş analizi.",
                listOf(
                    MenuCardEntry("Raporlar & Analizler", "Üretim Raporu, Gruplu Rapor ve Duruş Analizi", Screen.Raporlar, Color(0xFF8B5CF6))
                ), onNavigate)
            }
        } else if (r == "operator") {
            item { DashboardCategorySection("Üretim Operasyonları", "Üretim kaydı ve ilişkili işlem ekranları.",
                listOf(
                    MenuCardEntry("Üretim Girişi", "Personel, makine ve ürünle kayıt aç", Screen.Uretim, Color(0xFF22C55E))
                ), onNavigate)
            }
        } else if (r == "sevkiyatci") {
            item { DashboardCategorySection("Stok ve Sevkiyat", "Stok kontrolü ve sevk işlemleri.",
                listOf(
                    MenuCardEntry("Stok Takip & Kontrol", "Stok ve yeterlilik takibi", Screen.Stok, Color(0xFFF59E0B)),
                    MenuCardEntry("Sevkiyat Yönetimi", "Siparişi sevk et ve stoktan düş", Screen.Siparis, Color(0xFFEA580C))
                ), onNavigate)
            }
        }
    }
}

@Composable
fun DashboardCategorySection(
    title: String,
    description: String,
    items: List<MenuCardEntry>,
    onNavigate: (Screen) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardWhite)
                        .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                        .clickable { onNavigate(entry.screenTarget) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(5.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(entry.color)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = entry.description,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "›",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = entry.color,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, accentColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
        }
    }
}