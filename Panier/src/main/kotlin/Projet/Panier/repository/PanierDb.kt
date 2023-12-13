package Projet.Panier.repository

import Projet.Panier.domain.Panier
import Projet.Panier.repository.entity.ArticleEntity
import Projet.Panier.repository.entity.PanierEntity
import Projet.Panier.repository.entity.asEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

import java.net.HttpURLConnection
import java.net.URL

@Repository
class PanierDb(private val jpa: PanierJpaRepository) : PanierRepository {
    override fun create(id : String): Result<Panier> {
        if (jpa.findById(id).isPresent ) {
            return Result.failure(Exception("Panier already in DB"))
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
                    val saved = jpa.save(panier)
                    Result.success(saved.asPanier())
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    override fun list(): List<Panier> {
        return jpa.findAll().map { it.asPanier() }
    }

    override fun get(id: String): Panier? {
        return jpa.findById(id)
            .map { it.asPanier() }
            .getOrNull()
    }

    override fun update(panier: Panier): Result<Panier> = if (jpa.findById(panier.userEmail).isPresent ) {
        val saved = jpa.save(panier.asEntity())
        Result.success(saved.asPanier())
    } else {
        Result.failure(Exception("Panier not in DB"))
    }

    override fun delete(id: String): Panier? {
        return jpa.findById(id)
            .also { jpa.deleteById(id) }
            .map { it.asPanier() }
            .getOrNull()
    }

    override fun validate(id: String): Boolean {
        var url = "http://localhost:8081/articles/quantity/"
        var validate = true
        for (item in jpa.findById(id).get().items) {
            val quantity = item.quantite.toString()
            val connectUrl = url + item.articleId + "/" + quantity
            val connection = URL(connectUrl).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"

            try {
                println("entre")
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
            val url = "http://localhost:8081/articles/admin/quantity/"
            for (item in jpa.findById(id).get().items) {
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
                val panier = jpa.findById(id).get()
                val article = panier.items.find { it.articleId == articleId }
                if (article != null) {
                    article.quantite += quantite
                } else {
                    panier.items.add(ArticleEntity(articleId, quantite))
                }
                val saved = jpa.save(panier)
                Result.success(saved.asPanier())
            }
        } finally {
            connection.disconnect()
        }
    }

    override fun deleteArticle(id: String, articleId: Int): Result<Panier> {
        return if (jpa.findById(id).isPresent) {
            val panier = jpa.findById(id).get()
            val article = panier.items.find { it.articleId == articleId }
            if (article != null) {
                panier.items.remove(article)
            }
            val saved = jpa.save(panier)
            Result.success(saved.asPanier())
        } else {
            Result.failure(Exception("Panier not in DB"))
        }
    }
}

interface PanierJpaRepository : JpaRepository<PanierEntity, String>