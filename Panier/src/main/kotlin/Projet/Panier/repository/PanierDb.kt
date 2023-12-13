package Projet.Panier.repository

import Projet.Panier.domain.Panier
import Projet.Panier.repository.entity.PanierEntity
import Projet.Panier.repository.entity.asEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Repository
class PanierDb(private val jpa: PanierJpaRepository) : PanierRepository {
    override fun create(panier: Panier): Result<Panier> = if (jpa.findById(panier.userEmail).isPresent ) {
        Result.failure(Exception("Panier already in DB"))
    } else {
        val saved = jpa.save(panier.asEntity())
        Result.success(saved.asPanier())
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
        println(panier.items.size)
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
        val url = "http://localhost:8081/articles/"
        var validate = true
        for (item in jpa.findById(id).get().items) {
            val connectUrl = url + item.articleId
            val connection = URL(connectUrl).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"

            try {
                
            } catch (e : Exception) {
                validate = false
            }

        }
        return validate
    }
}

interface PanierJpaRepository : JpaRepository<PanierEntity, String>