package Projet.Panier.controller

import Projet.Panier.domain.Panier
import Projet.Panier.repository.PanierRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/panier/admin")
@Validated
class PanierControllerAdmin(private val panierRepository: PanierRepository) {

    private val logger : Logger = LoggerFactory.getLogger(PanierControllerAdmin::class.java)

    @Operation(summary = "Create a panier", description = "Pour une bonne utilisation de l'api, il faut créer un panier avec 0 article dedans, puis les ajouter 1 par 1", tags = ["admin"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201", description = "Panier created", content = [
                    Content(
                        mediaType = "application/json", schema = Schema(implementation = Panier::class)
                    )
                ]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
            ApiResponse(responseCode = "409", description = "Panier already exists", content = [Content()])
        ]
    )
    @PostMapping("/{id}")
    fun create(@PathVariable id : String): ResponseEntity<Panier> {
        val decodedId = URLDecoder.decode(id, StandardCharsets.UTF_8)
        val result = panierRepository.create(decodedId)
        return if (result.isSuccess) {
            logger.info("Panier created")
            ResponseEntity(result.getOrNull(), HttpStatus.CREATED)
        } else {
            logger.info("Panier already exists")
            ResponseEntity(HttpStatus.CONFLICT)
        }
    }

    @Operation(summary = "Add an article to a panier", description = "", tags = ["admin"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Panier updated", content = [
                    Content(
                        mediaType = "application/json", schema = Schema(implementation = Panier::class)
                    )
                ]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Panier not found", content = [Content()])
        ]
    )
    @PutMapping("/{id}/add/{articleId}/{quantity}")
    fun addArticle(@PathVariable id: String, @PathVariable articleId: Int, @PathVariable quantity: Int): ResponseEntity<Panier> {
        val decodedId = URLDecoder.decode(id, StandardCharsets.UTF_8)
        val result = panierRepository.addArticle(decodedId, articleId, quantity)
        return if (result.isSuccess) {
            logger.info("Article added to panier")
            ResponseEntity(result.getOrNull(), HttpStatus.OK)
        } else {
            logger.info("Panier not found")
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(summary = "Delete an article from a panier", description = "", tags = ["admin"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Panier updated", content = [
                    Content(
                        mediaType = "application/json", schema = Schema(implementation = Panier::class)
                    )
                ]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Panier not found", content = [Content()])
        ]
    )
    @PutMapping("/{id}/delete/{articleId}")
    fun deleteArticle(@PathVariable id: String, @PathVariable articleId: Int): ResponseEntity<Panier> {
        val decodedId = URLDecoder.decode(id, StandardCharsets.UTF_8)
        val result = panierRepository.deleteArticle(decodedId, articleId)
        return if (result.isSuccess) {
            logger.info("Article deleted from panier")
            ResponseEntity(result.getOrNull(), HttpStatus.OK)
        } else {
            logger.info("Panier not found")
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }


}