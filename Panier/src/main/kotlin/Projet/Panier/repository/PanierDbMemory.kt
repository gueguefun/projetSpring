package Projet.Panier.repository

import Projet.Panier.domain.Panier
import java.net.HttpURLConnection
import java.net.URL


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

    override fun validate(id: String): Boolean {
        val url = "http://localhost:8081/articles/quantity/"
        var validate = true
        for (item in map[id]!!.items) {
            val quantity = item.quantite.toString()
            val connectUrl = url + item.articleId + "/" + quantity
            val connection = URL(connectUrl).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"

            try {
                //Récuparation de la réponse
                val response = connection.responseCode

                if (response != 200) {
                    validate = false
                    if (response == 404) throw Exception("Article not found")
                    if (response == 409) throw Exception("Quantity not available")
                }

            } finally {
                connection.disconnect()
            }
        }
        if (validate) {
            //update la quantité sur la base de donnée article
        }
        return validate
    }
}