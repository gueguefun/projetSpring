package Projet.Article.Controller

import Projet.Article.Domain.Article
import Projet.Article.Repositery.ArticleRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController


@RestController
@Validated
@RequestMapping("/articles")
class ArticleController(private val articleRepository : ArticleRepository) {

    @Operation(summary = "Create a new article", description = "", tags = ["admin"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Article created",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Article::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "409", description = "Article already exists")
        ]
    )
    @PostMapping("/admin")
    fun createArticle(@RequestBody @Valid article: Article): ResponseEntity<Article> {
        val result = articleRepository.create(article)
        return if (result.isSuccess) {
            ResponseEntity(article, HttpStatus.CREATED)
        } else {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "List articles", description = "List all articles", tags = ["articles"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "List of articles",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = Article::class))
                )]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input")
        ]
    )
    @GetMapping
    fun listArticles(@RequestParam(required = false) price: Float?): List<Article> {
        return articleRepository.list(price)
    }

    @Operation(summary = "Get an article", description = "", tags = ["articles"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Article found",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Article::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "404", description = "Article not found")
        ]
    )
    @GetMapping("/{id}")
    fun getArticle(@PathVariable id: Int): ResponseEntity<Article> {
        val article = articleRepository.get(id)
        return if (article != null) {
            ResponseEntity(article, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(summary = "Update an article", description = "", tags = ["admin"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Article updated",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Article::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "404", description = "Article not found")
        ]
    )
    @PutMapping("/admin/{id}")
    fun updateArticle(@PathVariable id: Int, @RequestBody updatedArticle: Article): ResponseEntity<Article> {
        if (updatedArticle.id != id) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val result = articleRepository.update(updatedArticle)
        return if (result.isSuccess) {
            ResponseEntity(updatedArticle, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Delete an article", description = "", tags = ["admin"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Article deleted",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Article::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "404", description = "Article not found")
        ]
    )
    @DeleteMapping("/admin/{id}")
    fun deleteArticle(@PathVariable id: Int): ResponseEntity<Article> {
        val deletedArticle = articleRepository.delete(id)
        return if (deletedArticle != null) {
            ResponseEntity(deletedArticle, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(summary="Check if quantity is available", description="", tags=["articles"])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Quantity available",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Article::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "404", description = "Article not found"),
            ApiResponse(responseCode = "409", description = "Quantity not available")
        ]
    )
    @GetMapping("/quantity/{id}/{quantity}")
    fun checkQuantity(@PathVariable id: Int, @PathVariable quantity: Int): ResponseEntity<Article> {
        val article = articleRepository.get(id)
        return if (article != null) {
            if (article.quantity >= quantity) {
                ResponseEntity(article, HttpStatus.OK)
            } else {
                ResponseEntity(HttpStatus.CONFLICT)
            }
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}