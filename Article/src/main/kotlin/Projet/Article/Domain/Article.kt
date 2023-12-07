package Projet.Article.Domain

import java.util.*

data class Article(
    val id: Int,
    val name: String,
    val price: Float,
    val quantity: Int,
    val majDate: Date
)