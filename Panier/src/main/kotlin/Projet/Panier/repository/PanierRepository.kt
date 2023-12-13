package Projet.Panier.repository

import Projet.Panier.domain.Panier

interface PanierRepository {
    fun create(panier: Panier): Result<Panier>
    fun list(): List<Panier>
    fun get(id: String): Panier?
    fun update(panier: Panier): Result<Panier>
    fun delete(id: String): Panier?
    fun validate(id: String): Boolean
}