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