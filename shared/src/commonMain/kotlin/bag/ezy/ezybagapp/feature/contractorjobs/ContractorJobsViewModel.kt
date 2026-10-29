package bag.ezy.ezybagapp.feature.contractorjobs

import androidx.lifecycle.ViewModel

data class ContractorJobUiModel(
    val reference: String,
    val time: String,
    val wasteType: String,
    val distanceMiles: Double,
    val price: String,
)

data class ContractorJobsUiState(
    val radiusMiles: Int = 18,
    val nearbyCount: Int = 5,
    val area: String = "South London",
    val jobs: List<ContractorJobUiModel> = emptyList(),
) {
    val nearestJob: ContractorJobUiModel?
        get() = jobs.minByOrNull { it.distanceMiles }
}

class ContractorJobsViewModel : ViewModel() {

    val state = ContractorJobsUiState(
        jobs = listOf(
            ContractorJobUiModel(
                reference = "CC-10518",
                time = "Today 16:00",
                wasteType = "Furniture",
                distanceMiles = 6.1,
                price = "£88.40"
            ),
            ContractorJobUiModel(
                reference = "CC-10522",
                time = "Tomorrow 09:00",
                wasteType = "Construction",
                distanceMiles = 10.4,
                price = "£144.70"
            )
        )
    )
}
