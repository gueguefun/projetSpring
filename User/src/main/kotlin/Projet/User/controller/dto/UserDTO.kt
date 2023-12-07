package Projet.User.controller.dto

import Projet.User.domain.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.util.Date

data class UserDTO(
        @field:Email val email: String,
        val name: String,
        val livraisonAdresse: String,
        val isSub : Boolean,
        val lastBuy : String
) {

    fun asUser() = User(email, name, livraisonAdresse, isSub, lastBuy)
}

fun User.asUserDTO() = UserDTO(this.email, this.name, this.livraisonAdresse, this.isSub, this.lastBuy)