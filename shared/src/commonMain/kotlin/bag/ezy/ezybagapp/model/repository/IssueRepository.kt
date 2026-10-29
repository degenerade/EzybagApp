package bag.ezy.ezybagapp.model.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

interface IssueRepository {
    suspend fun countUnresolvedIssues(): Result<Int>
    suspend fun deleteIssue(issueId: String = ""): Result<Unit>
    suspend fun changeIssueAnswered(issueId: String = "", answered: Boolean = true): Result<Unit>
}

class FirebaseIssueRepository : IssueRepository {

    private val firestore = Firebase.firestore

    override suspend fun countUnresolvedIssues(): Result<Int> = runCatching {
        // TODO: Query unresolved issues from Firestore
        0
    }

    override suspend fun deleteIssue(issueId: String): Result<Unit> = runCatching {
        // TODO: Delete issue document from Firestore
    }

    override suspend fun changeIssueAnswered(issueId: String, answered: Boolean): Result<Unit> = runCatching {
        // TODO: Update issue 'answered' field in Firestore
    }
}
