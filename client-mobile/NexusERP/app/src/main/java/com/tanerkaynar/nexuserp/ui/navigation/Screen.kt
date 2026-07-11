package com.tanerkaynar.nexuserp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Ana Ekran", Icons.Default.Home)
    object UrunYonetimi : Screen("urun_yonetimi", "Ürün Yönetimi", Icons.Default.List)
    object Tanimlamalar : Screen("tanimlamalar", "Sistem Tanımları", Icons.Default.Settings)
    object Uretim : Screen("uretim", "Üretim Girişi", Icons.Default.Build)
    object Siparis : Screen("siparis", "Sipariş & Sevkiyat", Icons.Default.ShoppingCart)
    object Stok : Screen("stok", "Stok Durumu", Icons.Default.List)
    object Raporlar : Screen("raporlar", "Analiz & Raporlar", Icons.Default.Info)
    object Loglar : Screen("loglar", "Sistem Logları", Icons.Default.Lock)
}

fun getScreensForRole(rol: String): List<Screen> {
    return when (rol.lowercase()) {
        "admin" -> listOf(
            Screen.Dashboard,
            Screen.UrunYonetimi,
            Screen.Tanimlamalar,
            Screen.Uretim,
            Screen.Siparis,
            Screen.Stok,
            Screen.Raporlar,
            Screen.Loglar
        )
        "planlamaci" -> listOf(
            Screen.Dashboard,
            Screen.UrunYonetimi,
            Screen.Tanimlamalar,
            Screen.Uretim,
            Screen.Siparis,
            Screen.Stok,
            Screen.Raporlar
        )
        "sevkiyatci" -> listOf(
            Screen.Dashboard,
            Screen.Siparis,
            Screen.Stok
        )
        "operator" -> listOf(
            Screen.Dashboard,
            Screen.Uretim
        )
        else -> emptyList()
    }
}