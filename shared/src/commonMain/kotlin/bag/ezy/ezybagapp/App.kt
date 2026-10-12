package bag.ezy.ezybagapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import bag.ezy.ezybagapp.ui.theme.ClearawayTheme

@Composable
fun App() {
    var showScheduleScreen by remember {
        mutableStateOf(false)
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    ClearawayTheme {
        Box {
            if (showScheduleScreen) {
                saveableStateHolder.SaveableStateProvider("schedule-screen") {
                    ScheduleBagQuoteScreen(
                        onBack = {
                            showScheduleScreen = false
                        }
                    )
                }
            } else {
                saveableStateHolder.SaveableStateProvider("create-job-screen") {
                    CreateJobScreen(
                        onContinue = {
                            showScheduleScreen = true
                        }
                    )
                }
            }
        }
    }
}