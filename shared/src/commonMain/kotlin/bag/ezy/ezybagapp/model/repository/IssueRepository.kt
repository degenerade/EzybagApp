package bag.ezy.ezybagapp.model.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable

@Serializable
data class IssueReport(
    val customerId: String = "",
    val category: String = "",
    val description: String = "",
    val status: String = "Open",
    val createdAt: Long = 0L
)

data class AdminIssue(
    val id: String,
    val report: IssueReport
)

interface IssueRepository {
    suspend fun getAllIssues(): Result<List<AdminIssue>>
    suspend fun countUnresolvedIssues(): Result<Int>
    suspend fun deleteIssue(issueId: String): Result<Unit>
    suspend fun changeIssueStatus(
        issueId: String,
        status: String
    ): Result<Unit>
}

class FirebaseIssueRepository : IssueRepository {

    private val firestore = Firebase.firestore
    private val issuesCollection = firestore.collection("issueReports")

    override suspend fun getAllIssues(): Result<List<AdminIssue>> =
        runCatching {
            val snapshot = issuesCollection.get()

            snapshot.documents.mapNotNull { document ->
                runCatching {
                    AdminIssue(
                        id = document.id,
                        report = document.data<IssueReport>()
                    )
                }.getOrNull()
            }.sortedByDescending { it.report.createdAt }
        }

    override suspend fun countUnresolvedIssues(): Result<Int> =
        runCatching {
            val snapshot = issuesCollection.get()

            snapshot.documents.count { document ->
                val report = document.data<IssueReport>()
                report.status.lowercase() != "resolved"
            }
        }

    override suspend fun deleteIssue(issueId: String): Result<Unit> =
        runCatching {
            issuesCollection.document(issueId).delete()
        }

    override suspend fun changeIssueStatus(
        issueId: String,
        status: String
    ): Result<Unit> =
        runCatching {
            issuesCollection.document(issueId).update(
                "status" to status
            )
        }
}