package Projet.Panier.repository

import Projet.Panier.domain.Article
import Projet.Panier.domain.Panier
import Projet.Panier.repository.entity.ArticleEntity
import Projet.Panier.repository.entity.PanierEntity
import java.net.HttpURLConnection
import java.net.URL


class PanierDbMemory : PanierRepository {

    private val map = mutableMapOf<String, Panier>()

    override fun create(id : String): Result<Panier> {

        if(map.containsKey(id)){
            return Result.failure(Exception("Panier already exists"))
        } else {
            val url = "http://localhost:8080/users/"
            val connectUrl = url + id
            val connection = URL(connectUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            return try {
                //Récuparation de la réponse
                val response = connection.responseCode

                if (response != 200) {
                    if (response == 404) throw Exception("User not found")
                    Result.failure(Exception("User not found"))
                } else {
                    val panier = PanierEntity(id, mutableListOf())
                    val saved = map.putIfAbsent(id, panier.asPanier())
                    Result.success(saved!!)
                }
            } finally {
                connection.disconnect()
            }
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
        var url = "http://localhost:8081/articles/quantity/"
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
            url = "http://localhost:8081/articles/admin/quantity/"
            for (item in map[id]!!.items) {
                val quantity = item.quantite.toString()
                val connectUrl = url + item.articleId + "/" + quantity
                val connection = URL(connectUrl).openConnection() as HttpURLConnection

                connection.requestMethod = "PUT"

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
        }
        return validate
    }

    override fun addArticle(id: String, articleId: Int, quantite: Int): Result<Panier> {
        val url = "http://localhost:8081/articles/"
        val connectUrl = url + articleId
        val connection = URL(connectUrl).openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        return try {
            //Récuparation de la réponse
            val response = connection.responseCode

            if (response != 200) {

                Result.failure(Exception("Article not found"))
            } else {
                val panier = map[id]!!
                val article = panier.items.find { it.articleId == articleId }
                if (article != null) {
                    article.quantite += quantite
                } else {
                    panier.items.add(Article(articleId, quantite))
                }
                Result.success(panier)
            }
        } finally {
            connection.disconnect()
        }
    }

    override fun deleteArticle(id: String, articleId: Int): Result<Panier> {
        return if (map.containsKey(id)) {
            val panier = map[id]!!
            val article = panier.items.find { it.articleId == articleId }
            if (article != null) {
                panier.items.remove(article)
            }
            Result.success(panier)
        } else {
            Result.failure(Exception("Panier not in DB"))
        }
    }
}


