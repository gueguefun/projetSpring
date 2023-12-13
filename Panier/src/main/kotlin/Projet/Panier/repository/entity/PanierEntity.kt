package Projet.Panier.repository.entity

import Projet.Panier.domain.Panier
import Projet.Panier.domain.Quantite
import jakarta.persistence.*

@Entity
@Table(name = "paniers")
class PanierEntity(

    @Id val userEmail: String,
    @ElementCollection
    @CollectionTable(name = "panier_items", joinColumns = [JoinColumn(name = "panier_id")])
    var items: MutableList<QuantiteEntity> = mutableListOf(),
) {
    fun asPanier(): Panier {
        return Panier(userEmail, items.map { it.toDomain() }.toMutableList())
    }
}
fun Panier.asEntity(): PanierEntity {
    return PanierEntity(this.userEmail, this.items.map { it.toEntity() }.toMutableList())
}

fun Quantite.toEntity(): QuantiteEntity {
    return QuantiteEntity(this.articleId, this.quantite)
}

@Embeddable
data class QuantiteEntity(
    val articleId: Int,
    val quantite: Int
){
    fun toDomain():Projet.Panier.domain.Quantite {
        return Projet.Panier.domain.Quantite(
            articleId = this.articleId,
            quantite = this.quantite
        )
    }
}