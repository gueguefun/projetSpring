package Projet.Panier.repository

import Projet.Panier.domain.Panier

interface PanierRepository {
    fun create(id : String): Result<Panier>
    fun list(): List<Panier>
    fun get(id: String): Panier?
    fun update(panier: Panier): Result<Panier>
    fun delete(id: String): Panier?
    fun validate(id: String): Boolean
    fun addArticle(id: String, articleId: Int, quantite: Int): Result<Panier>
    fun deleteArticle(id: String, articleId: Int): Result<Panier>
}