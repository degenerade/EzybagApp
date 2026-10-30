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
    background = Color(0xFFEEF1EE), onBackground = Color(0xFF14232A),
    surface = Color.White, onSurface = Color(0xFF14232A),
    onSurfaceVariant = Color(0xFF5F7078), outline = Color(0xFFD9E0DC),
    error = Color(0xFFC4472F),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF3FB592), onPrimary = Color.White,
    primaryContainer = Color(0xFF173A32), onPrimaryContainer = Color(0xFF3FB592),
    secondary = Color(0xFFF0B355), onSecondary = Color(0xFF14232A),
    background = Color(0xFF0E1719), onBackground = Color(0xFFE8EFEC),
    surface = Color(0xFF17252A), onSurface = Color(0xFFE8EFEC),
    onSurfaceVariant = Color(0xFF95A7AD), outline = Color(0xFF2B3C42),
    error = Color(0xFFC4472F),
)

@Composable
fun ClearawayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        shapes = Shapes(small = RoundedCornerShape(10.dp), medium = RoundedCornerShape(12.dp)),
        content = content,
    )
}