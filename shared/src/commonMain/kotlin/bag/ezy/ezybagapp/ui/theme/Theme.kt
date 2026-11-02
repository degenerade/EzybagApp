package bag.ezy.ezybagapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF1E6F5C), onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF0E8), onPrimaryContainer = Color(0xFF1E6F5C),

    secondary = Color(0xFFE8A33D), onSecondary = Color(0xFF14232A),
    secondaryContainer = Color(0xFFFBEBCB), onSecondaryContainer = Color(0xFF7A5210),
    tertiary = Color(0xFFE8A33D), onTertiary = Color(0xFF14232A),

    error = Color(0xFFC4472F), onError = Color.White,
    errorContainer = Color(0xFFF8DDD7), onErrorContainer = Color(0xFF8C2A18),

    background = Color(0xFFEEF1EE), onBackground = Color(0xFF14232A),
    surface = Color.White, onSurface = Color(0xFF14232A),
    surfaceVariant = Color(0xFFE3E9E5), onSurfaceVariant = Color(0xFF5F7078),

    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F9F7),
    surfaceContainer = Color(0xFFF1F4F2),
    surfaceContainerHigh = Color(0xFFEBEFEC),
    surfaceContainerHighest = Color(0xFFE3E9E5),

    outline = Color(0xFFD9E0DC),
    outlineVariant = Color(0xFFE6EBE8),

    inverseSurface = Color(0xFF14232A), inverseOnSurface = Color(0xFFE8EFEC),
    inversePrimary = Color(0xFF3FB592),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF3FB592), onPrimary = Color.White,
    primaryContainer = Color(0xFF173A32), onPrimaryContainer = Color(0xFF3FB592),

    secondary = Color(0xFFF0B355), onSecondary = Color(0xFF14232A),
    secondaryContainer = Color(0xFF4A3712), onSecondaryContainer = Color(0xFFF7D9A0),
    tertiary = Color(0xFFF0B355), onTertiary = Color(0xFF14232A),

    error = Color(0xFFE0705A), onError = Color(0xFF14232A),
    errorContainer = Color(0xFF4A1F17), onErrorContainer = Color(0xFFF4B5A8),

    background = Color(0xFF0E1719), onBackground = Color(0xFFE8EFEC),
    surface = Color(0xFF17252A), onSurface = Color(0xFFE8EFEC),
    surfaceVariant = Color(0xFF22343A), onSurfaceVariant = Color(0xFF95A7AD),

    surfaceContainerLowest = Color(0xFF0A1214),
    surfaceContainerLow = Color(0xFF131F23),
    surfaceContainer = Color(0xFF17252A),
    surfaceContainerHigh = Color(0xFF1D2D32),
    surfaceContainerHighest = Color(0xFF22343A),

    outline = Color(0xFF2B3C42),
    outlineVariant = Color(0xFF22343A),

    inverseSurface = Color(0xFFE8EFEC), inverseOnSurface = Color(0xFF14232A),
    inversePrimary = Color(0xFF1E6F5C),
)

@Composable
fun EzybagTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        shapes = Shapes(small = RoundedCornerShape(10.dp), medium = RoundedCornerShape(12.dp)),
        content = content,
    )
}