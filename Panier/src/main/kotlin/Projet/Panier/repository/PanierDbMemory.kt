package Projet.Panier.repository

import Projet.Panier.domain.Panier

class PanierDbMemory : PanierRepository {

    private val map = mutableMapOf<String, Panier>()

    override fun create(panier: Panier): Result<Panier> {

        val previous = map.putIfAbsent(panier.userEmail, panier)
        return if (previous == null) {
            Result.success(panier)
        } else {
            Result.failure(Exception("Panier already exists"))
        }
    }

    override fun list(): List<Panier> = map.values.toList()

    override fun get(id: String): Panier? = map[id]

    override fun update(panier: Panier): Result<Panier> {
        val updated = map.replace(panier.userEmail, panier)
        return if (updated == null) {
            Result.failure(Exception("Panier doesn't exist"))
        } else {
            Result.success(panier)
        }
    }

    override fun delete(id: String): Panier? = map.remove(id)
}