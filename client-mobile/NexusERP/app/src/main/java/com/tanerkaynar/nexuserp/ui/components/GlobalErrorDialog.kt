package com.tanerkaynar.nexuserp.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tanerkaynar.nexuserp.ui.theme.ErrorColor

@Composable
fun GlobalErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Text(
                text = "İşlem / Bağlantı Hatası",
                color = ErrorColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }
    )
}