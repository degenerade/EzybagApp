
package bag.ezy.ezybagapp.feature.startPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import bag.ezy.ezybagapp.model.data.StartOption
import bag.ezy.ezybagapp.model.data.UserRole

data class StartUiState(
    val options: List<StartOption> = listOf(
        StartOption(
            role = UserRole.CUSTOMER,
            title = "Continue as Customer"
        ),
        StartOption(
            role = UserRole.CONTRACTOR,
            title = "Continue as Contractor"
        )
    )
)

class StartViewModel : ViewModel() {

    var state by mutableStateOf(StartUiState())
        private set
}
