package bag.ezy.ezybagapp.di

import bag.ezy.ezybagapp.model.queries.AdminRepository
import bag.ezy.ezybagapp.model.repository.ContractorRepository
import bag.ezy.ezybagapp.model.repository.FirebaseAuthRepository
import bag.ezy.ezybagapp.model.repository.FirebaseBookingRepository
import bag.ezy.ezybagapp.model.repository.FirebaseContractorRepository
import bag.ezy.ezybagapp.model.repository.FirebaseCustomerRepository
import bag.ezy.ezybagapp.model.repository.FirebaseReviewRepository
import bag.ezy.ezybagapp.model.repository.IssueRepository
import bag.ezy.ezybagapp.model.repository.FirebaseIssueRepository
import bag.ezy.ezybagapp.model.repository.ReportRepository
import bag.ezy.ezybagapp.model.repository.ContractorPerformanceRepository

object AppContainer {
    val authRepository = FirebaseAuthRepository()
    val bookingRepository = FirebaseBookingRepository()
    val reportRepository = ReportRepository()
    val reviewRepository = FirebaseReviewRepository()
    val adminRepository = AdminRepository()
    val contractorRepository = FirebaseContractorRepository()
    val customerRepository = FirebaseCustomerRepository()
    val issueRepository = FirebaseIssueRepository()
    val contractorPerformanceRepository = ContractorPerformanceRepository()


}