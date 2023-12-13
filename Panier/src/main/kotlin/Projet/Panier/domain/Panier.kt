package Projet.Panier.domain

data class Panier(val userEmail: String, val items: MutableList<Quantite>)