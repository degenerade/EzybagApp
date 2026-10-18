package bag.ezy.ezybagapp.model.repository

import kotlinx.coroutines.delay

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(name: String, email: String, password: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
}

/** fake backend */
class FakeAuthRepository : AuthRepository {
    private val users = mutableMapOf("test@example.com" to "password123")

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        delay(800)
        return if (users[email.lowercase()] == password) Result.success(Unit)
        else Result.failure(Exception("Wrong email or password"))
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<Unit> {
        delay(800)
        if (email.lowercase() in users) return Result.failure(Exception("Account with that email already exists"))
        users[email.lowercase()] = password
        return Result.success(Unit)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        delay(800)
        return Result.success(Unit)
    }
}