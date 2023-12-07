package Projet.User.repository

import Projet.User.domain.User

interface UserRepository {
    fun create(user: User): Result<User>
    fun list(name: String? = null): List<User>
    fun get(email: String): User?
    fun update(user: User): Result<User>
    fun delete(email: String): User?
}