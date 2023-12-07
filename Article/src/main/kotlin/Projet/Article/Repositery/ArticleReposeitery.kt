package Projet.Article.Repositery

import Projet.Article.Domain.Article
interface ArticleReposeitery {
    fun create(article : Article): Result<Article>
    fun list(price: Float? = null): List<Article>
    fun get(id : Int): Article?
    fun update(article: Article): Result<Article>
    fun delete(id : Int): Article?
}
