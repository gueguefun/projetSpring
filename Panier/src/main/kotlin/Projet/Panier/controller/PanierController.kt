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
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@RestController
@RequestMapping("/panier")
@Validated
class PanierController(private val panierRepository : PanierRepository) {

    private val logger: Logger = LoggerFactory.getLogger(PanierController::class.java)

    @Operation(summary = "Get all paniers", description = "", tags = ["Panier"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Paniers found", content = [
                    Content(
                        mediaType = "application/json", array = (
                                ArraySchema(schema = Schema(implementation = Panier::class))
                                )
                    )
                ]
            ),
            ApiResponse(responseCode = "404", description = "No panier found", content = [Content()])
        ]
    )
    @GetMapping
    fun list(): ResponseEntity<List<Panier>> {
        val paniers = panierRepository.list()
        return if (paniers.isEmpty()) {
            logger.info("No panier found")
            ResponseEntity.notFound().build()
        } else {
            logger.info("Paniers found")
            ResponseEntity.ok(paniers)
        }
    }

    @Operation(summary = "Get the panier of a user", description = "", tags = ["Panier"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Panier found", content = [
                    Content(
                        mediaType = "application/json", schema = Schema(implementation = Panier::class)
                    )
                ]
            ),
            ApiResponse(responseCode = "404", description = "No panier found", content = [Content()])
        ]
    )
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<Panier> {
        val decodedId = URLDecoder.decode(id, StandardCharsets.UTF_8)
        val panier = panierRepository.get(decodedId)
        return if (panier == null) {
            logger.info("No panier found")
            ResponseEntity.notFound().build()
        } else {
            logger.info("Panier found")
            ResponseEntity.ok(panier)
        }
    }

    @Operation(summary="validate panier for a user", description="", tags=["Panier"])
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Panier validated", content = [Content(mediaType = "application/json", schema = Schema(implementation = Panier::class))]),
            ApiResponse(responseCode = "404", description = "No panier found", content = [Content()]),
            ApiResponse(responseCode = "400", description = "Panier already validated", content = [Content()])
        ]
    )
    @PutMapping("/validate/{id}")
    fun validate(@PathVariable id: String): ResponseEntity<Panier> {
        val decodedId = URLDecoder.decode(id, StandardCharsets.UTF_8)
        return if (panierRepository.validate(decodedId)) {
            val panier = panierRepository.get(decodedId)
            if (panier == null) {
                logger.info("No panier found")
                ResponseEntity.notFound().build()
            } else {
                logger.info("Panier validated")
                ResponseEntity.ok(panier)
            }
        } else {
            logger.info("Cest bizarre")
            ResponseEntity.badRequest().build()
        }
    }


}