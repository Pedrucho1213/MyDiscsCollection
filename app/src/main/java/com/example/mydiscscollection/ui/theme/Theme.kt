package com.example.mydiscscollection.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val Woodsmoke     = Color(0xFF111318)
val EbonyClay     = Color(0xFF222731)
val EbonyClay2    = Color(0xFF2A3040)
val Mischka       = Color(0xFFE2E2E9)
val Ghost         = Color(0xFFC3C6CF)
val AzureBlue     = Color(0xFFA8C7FA)
val AzureBlueDim  = Color(0xFF004A77)
val ErrorRed      = Color(0xFFF2B8B8)
val ErrorRedDim   = Color(0xFF8C1D18)
val DividerColor  = Color(0xFF2E3447)

private val DarkColorScheme = darkColorScheme(
    background          = Woodsmoke,
    surface             = Woodsmoke,
    surfaceVariant      = EbonyClay,
    surfaceContainer    = EbonyClay,
    surfaceContainerHigh= EbonyClay2,

    primary             = AzureBlue,
    onPrimary           = Color(0xFF00315C),
    primaryContainer    = AzureBlueDim,
    onPrimaryContainer  = Color(0xFFD1E4FF),

    onBackground        = Mischka,
    onSurface           = Mischka,
    onSurfaceVariant    = Ghost,

    error               = ErrorRed,
    onError             = Color(0xFF601410),
    errorContainer      = ErrorRedDim,
    onErrorContainer    = Color(0xFFF9DEDC),

    outline             = DividerColor,
    outlineVariant      = Color(0xFF44474E),
)


@Composable
fun DiscogsAppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content,
    )
}