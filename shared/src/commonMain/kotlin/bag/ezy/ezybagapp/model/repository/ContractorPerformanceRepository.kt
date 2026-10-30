package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Booking
import bag.ezy.ezybagapp.model.data.Contractor

data class ContractorPerformanceReport(
    val totalContractors: Int,
    val assignedJobs: Int,
    val completedJobs: Int,
    val completionRate: Double,
    val contractorStats: List<ContractorStats>
)

data class ContractorStats(
    val contractorName: String,
    val assignedJobs: Int,
    val completedJobs: Int
)

class ContractorPerformanceRepository {

    fun generateContractorPerformanceReport(
        contractors: List<Contractor>,
        bookings: List<Booking>
    ): ContractorPerformanceReport {

        // Only count bookings that have been assigned to a contractor
        val assignedBookings = bookings.filter {
            !it.contractorId.isNullOrBlank()
        }

        // Count completed assigned bookings
        val completedBookings = assignedBookings.count {
            it.status.equals("Completed", ignoreCase = true)
        }

        // Calculate completion rate
        val completionRate = if (assignedBookings.isNotEmpty()) {
            (completedBookings.toDouble() / assignedBookings.size) * 100
        } else {
            0.0
        }

        // Calculate performance for each contractor
        val contractorStats = contractors.map { contractor ->

            val contractorBookings = assignedBookings.filter {
                it.contractorId == contractor.uid
            }

            val completedJobs = contractorBookings.count {
                it.status.equals("Completed", ignoreCase = true)
            }

            ContractorStats(
                contractorName = contractor.companyName.ifBlank {
                    "${contractor.firstName} ${contractor.lastName}".trim()
                },
                assignedJobs = contractorBookings.size,
                completedJobs = completedJobs
            )
        }

        return ContractorPerformanceReport(
            totalContractors = contractors.size,
            assignedJobs = assignedBookings.size,
            completedJobs = completedBookings,
            completionRate = completionRate,
            contractorStats = contractorStats
        )
    }
}