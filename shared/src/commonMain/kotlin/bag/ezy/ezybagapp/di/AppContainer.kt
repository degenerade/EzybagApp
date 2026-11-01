package bag.ezy.ezybagapp.di

import bag.ezy.ezybagapp.model.queries.AdminRepository
import bag.ezy.ezybagapp.model.repository.ContractorPerformanceRepository
import bag.ezy.ezybagapp.model.repository.CustomerActivityRepository
import bag.ezy.ezybagapp.model.repository.FirebaseAuthRepository
import bag.ezy.ezybagapp.model.repository.FirebaseBookingRepository
import bag.ezy.ezybagapp.model.repository.FirebaseContractorRepository
<<<<<<< Updated upstream
import bag.ezy.ezybagapp.model.repository.FirebaseCustomerRepository
import bag.ezy.ezybagapp.model.repository.FirebaseIssueRepository
import bag.ezy.ezybagapp.model.repository.FirebaseRevenueRepository
=======
>>>>>>> Stashed changes
import bag.ezy.ezybagapp.model.repository.FirebaseReviewRepository
import bag.ezy.ezybagapp.model.repository.ReportRepository

object AppContainer {
    val authRepository = FirebaseAuthRepository()
    val bookingRepository = FirebaseBookingRepository()
<<<<<<< Updated upstream
    val reportRepository = ReportRepository()
=======
    val contractorRepository = FirebaseContractorRepository()
>>>>>>> Stashed changes
    val reviewRepository = FirebaseReviewRepository()
    val adminRepository = AdminRepository()
    val contractorRepository = FirebaseContractorRepository()
    val customerRepository = FirebaseCustomerRepository()
    val issueRepository = FirebaseIssueRepository()
    val contractorPerformanceRepository = ContractorPerformanceRepository()
    val customerActivityRepository = CustomerActivityRepository()
    val revenueRepository = FirebaseRevenueRepository()
}
