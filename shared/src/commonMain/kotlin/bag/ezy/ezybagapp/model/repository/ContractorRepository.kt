package bag.ezy.ezybagapp.model.repository

import bag.ezy.ezybagapp.model.data.Contractor
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

interface ContractorRepository {

    suspend fun registerContractor(
        contractor: Contractor,
        password: String
    ): Result<Unit>

    suspend fun getCurrentContractor(): Result<Contractor?>

    suspend fun getAllContractors(): Result<List<Contractor>>
    suspend fun getVerifiedContractors(): Result<List<Contractor>>
    suspend fun countAllContractors(): Result<Int>
    suspend fun getUnverifiedContractors(): Result<List<Contractor>>
    suspend fun countUnverifiedContractors(): Result<Int>
    suspend fun changeContractorStatus(contractorId: String = "", isApproved: Boolean = true): Result<Unit>
}

class FirebaseContractorRepository : ContractorRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val contractors = firestore.collection("contractors")

    override suspend fun registerContractor(
        contractor: Contractor,
        password: String
    ): Result<Unit> = runCatching {

        val normalizedEmail = contractor.email.trim()

        val authResult = auth.createUserWithEmailAndPassword(
            normalizedEmail,
            password
        )

        val user = authResult.user
            ?: error("Unable to retrieve the new contractor account.")

        val contractorProfile = contractor.copy(
            uid = user.uid,
            email = normalizedEmail,
            isApproved = false
        )

        try {
            contractors.document(user.uid).set(
                Contractor.serializer(),
                contractorProfile
            )

            auth.signOut()
        } catch (e: Exception) {
            try {
                user.delete()
            } catch (_: Exception) {
                // Keep the original error.
            }

            throw e
        }
    }

    // Show each contractor on page where viewing all contractors
    override suspend fun getAllContractors(): Result<List<Contractor>> = runCatching {
        contractors.get().documents.map { document ->
            document.data<Contractor>()
        }
    }

    // Show verified contractors
    override suspend fun getVerifiedContractors(): Result<List<Contractor>> = runCatching {
        // TODO: Query verified contractors from Firestore
        emptyList()
    }

    // Count total contractors
    override suspend fun countAllContractors(): Result<Int> = runCatching {
        // TODO: Count total number of contractors from Firestore
        0
    }

    // Show unverified contractors
    override suspend fun getUnverifiedContractors(): Result<List<Contractor>> = runCatching {
        // TODO: Query unverified contractors from Firestore
        emptyList()
    }

    // Count unverified contractors
    override suspend fun countUnverifiedContractors(): Result<Int> = runCatching {
        // TODO: Count total unverified contractors from Firestore
        0
    }

    // Change contractor approval status
    override suspend fun changeContractorStatus(contractorId: String, isApproved: Boolean): Result<Unit> = runCatching {
        // TODO: Update contractor approval status in Firestore
    }

    override suspend fun getCurrentContractor(): Result<Contractor?> =
        runCatching {
            val user = auth.currentUser
                ?: return@runCatching null

            val document = contractors
                .document(user.uid)
                .get()

            if (!document.exists) {
                null
            } else {
                document.data<Contractor>()
            }
        }
}