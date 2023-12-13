package Projet.Panier.repository.entity

import Projet.Panier.domain.Panier
import Projet.Panier.domain.Article
import jakarta.persistence.*

@Entity
@Table(name = "paniers")
class PanierEntity(

    @Id val userEmail: String,
    @ElementCollection
    @CollectionTable(name = "panier_items", joinColumns = [JoinColumn(name = "panier_id")])
    var items: MutableList<ArticleEntity> = mutableListOf(),
) {
    fun asPanier(): Panier {
        return Panier(userEmail, items.map { it.toDomain() }.toMutableList())
    }
}
fun Panier.asEntity(): PanierEntity {
    return PanierEntity(this.userEmail, this.items.map { it.toEntity() }.toMutableList())
}

fun Article.toEntity(): ArticleEntity {
    return ArticleEntity(this.articleId, this.quantite)
}

@Embeddable
data class ArticleEntity(
    val articleId: Int,
    var quantite: Int
){
    fun toDomain():Projet.Panier.domain.Article {
        return Projet.Panier.domain.Article(
            articleId = this.articleId,
            quantite = this.quantite
        )
    }
}