package Projet.User.repository

import Projet.User.domain.User
import org.springframework.stereotype.Repository

@Repository
class UserInMemoryRepository : UserRepository {

    private val map = mutableMapOf<String, User>()

    override fun create(user: User): Result<User> {
        val previous = map.putIfAbsent(user.email, user)
        return if (previous == null) {
            Result.success(user)
        } else {
            Result.failure(Exception("User already exit"))
        }
    }

    override fun list(name: String?) = if (name == null) {
        map.values.toList()
    } else {
        map.values.filter { it.name == name }
    }

    override fun get(email: String) = map[email]

    override fun update(user: User): Result<User> {
        val updated = map.replace(user.email, user)
        return if (updated == null) {
            Result.failure(Exception("User doesn't exit"))
        } else {
            Result.success(user)
        }
    }

    override fun delete(email: String) = map.remove(email)
}