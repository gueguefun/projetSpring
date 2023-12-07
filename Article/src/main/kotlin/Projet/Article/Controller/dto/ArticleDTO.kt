package Projet.Article.Controller.dto

import Projet.Article.Domain.Article
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.util.*

data class ArticleDTO(
    @field:Size(min=5, max=30) val id: Int,
    @field:Size(min=5, max=30) val name: String,
    @field:Min(0) val price: Float,
    @field:Min(0) @field:Max(200) val quantity: Int,
    @field:Size(min=5, max=30) val majDate: Date
) {
    fun asArticle() = Article(id, name, price, quantity, majDate)
}

fun Article.asArticleDTO() = ArticleDTO(id, name, price, quantity, majDate)