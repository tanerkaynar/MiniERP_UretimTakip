package com.tanerkaynar.nexuserp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.tanerkaynar.nexuserp.data.SettingsManager
import com.tanerkaynar.nexuserp.data.api.RetrofitClient
import com.tanerkaynar.nexuserp.data.model.LoginResponse
import com.tanerkaynar.nexuserp.data.repository.AuthRepository
import com.tanerkaynar.nexuserp.ui.screens.DashboardScreen
import com.tanerkaynar.nexuserp.ui.screens.LoginScreen
import com.tanerkaynar.nexuserp.ui.theme.NexusERPTheme
import com.tanerkaynar.nexuserp.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {

        ViewModelFactory()
    }

    private val urunViewModel: UrunViewModel by viewModels()
    private val tanimlamaViewModel: TanimlamaViewModel by viewModels()
    private val siparisViewModel: SiparisViewModel by viewModels()
    private val uretimViewModel: UretimViewModel by viewModels()
    private val logViewModel: LogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.initialize(this)

        val settingsManager = SettingsManager(this)

        setContent {
            var themeModeState by remember { mutableIntStateOf(settingsManager.getThemeMode()) }
            val onThemeChange: (Int) -> Unit = { mode ->
                settingsManager.setThemeMode(mode)
                themeModeState = mode
            }

            NexusERPTheme(themeMode = themeModeState) {
                var loggedInUser by remember { mutableStateOf<LoginResponse?>(null) }

                if (loggedInUser == null) {
                    LoginScreen(
                        viewModel = authViewModel,
                        tanimlamaViewModel = tanimlamaViewModel,
                        themeMode = themeModeState,
                        onThemeChange = onThemeChange,
                        onLoginSuccess = { userResponse ->
                            loggedInUser = userResponse
                        }
                    )
                } else {
                    DashboardScreen(
                        username = loggedInUser!!.kullaniciadi,
                        role = loggedInUser!!.rol,
                        personelId = loggedInUser!!.personel?.personelid,
                        personelName = loggedInUser!!.personel?.adsoyad,
                        urunViewModel = urunViewModel,
                        tanimlamaViewModel = tanimlamaViewModel,
                        siparisViewModel = siparisViewModel,
                        uretimViewModel = uretimViewModel,
                        logViewModel = logViewModel,
                        themeMode = themeModeState,
                        onThemeChange = onThemeChange,
                        onLogout = {
                            loggedInUser = null
                        }
                    )
                }
            }
        }
    }
}

class ViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(AuthRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}