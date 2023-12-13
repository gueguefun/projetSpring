package Projet.Article.Repositery

import Projet.Article.Domain.Article
import org.springframework.stereotype.Repository

@Repository
class ArticleBD : ArticleRepository {

    private val map = mutableMapOf<String, Article>()

    override fun create(article: Article): Result<Article> {
        val previous = map.putIfAbsent(article.id.toString(), article)
        return if (previous == null) {
            Result.success(article)
        } else {
            Result.failure(Exception("Article already exist"))
        }
    }

    override fun list(price: Float?) = if (price == null) {
        map.values.toList()
    } else {
        map.values.filter { it.price == price }
    }

    override fun get(id: Int) = map[id.toString()]

    override fun update(article: Article): Result<Article> {
        val updated = map.replace(article.id.toString(), article)
        return if (updated == null) {
            Result.failure(Exception("Article doesn't exit"))
        } else {
            Result.success(article)
        }
    }

    override fun delete(id : Int) = map.remove(id.toString())

    override fun delQuantity(id: Int, quantity: Int): Result<Article> {
        val article = map[id.toString()]
        return if (article == null) {
            Result.failure(Exception("Article doesn't exit"))
        } else {
            val updated = article.copy(quantity = article.quantity - quantity)
            map.replace(id.toString(), updated)
            Result.success(updated)
        }
    }
}