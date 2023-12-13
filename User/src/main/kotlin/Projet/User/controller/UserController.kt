package Projet.User.controller

import Projet.User.config.errors.UserNotFoundError
import Projet.User.controller.dto.UserDTO
import Projet.User.controller.dto.asUserDTO
import Projet.User.repository.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/users")
class UserController(val userRepository: UserRepository) {

    @Operation(summary = "Create user", tags=["admin"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "User created",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = UserDTO::class)
                )]),
        ApiResponse(responseCode = "409", description = "User already exist",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])])

    @PostMapping("/admin")
    fun create(@RequestBody @Valid user: UserDTO): ResponseEntity<UserDTO> {
        val result = userRepository.create(user.asUser())
        return if (result.isSuccess) {
            ResponseEntity(user, HttpStatus.CREATED)
        } else {
            ResponseEntity(HttpStatus.CONFLICT)
        }
    }

    @Operation(summary = "List users", tags=["user"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List users",
                content = [Content(mediaType = "application/json",
                        array = ArraySchema(
                                schema = Schema(implementation = UserDTO::class))
                )])])
    @GetMapping
    fun list(@RequestParam(required = false) @Min(15) name: String?) =
            userRepository.list(name)
                    .map { it.asUserDTO() }
                    .let {
                        ResponseEntity.ok(it)
                    }

    @Operation(summary = "Get user by email", tags=["user"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "The user requested",
                content = [
                    Content(mediaType = "application/json",
                            schema = Schema(implementation = UserDTO::class))]),
        ApiResponse(responseCode = "404", description = "User requested doesn't exist")
    ])
    @GetMapping("/{email}")
    fun findOne(@PathVariable @Email email: String): ResponseEntity<UserDTO> {
        val user = userRepository.get(email)
        return if (user != null) {
            ResponseEntity.ok(user.asUserDTO())
        } else {
            throw UserNotFoundError(email)
        }
    }

    @Operation(summary = "Update a user by email", tags=["admin"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User requested updated",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = UserDTO::class))]),
        ApiResponse(responseCode = "400", description = "Invalid request",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])])
    @PutMapping("/admin/{email}")
    fun update(@PathVariable @Email email: String, @RequestBody @Valid user: UserDTO): ResponseEntity<Any> {
        if (email != user.email) {
            return ResponseEntity.badRequest().body("Email doesn't exist")
        } else {
            val result = userRepository.update(user.asUser())
            return if (result.isSuccess) {
                ResponseEntity.ok(user)
            } else {
                ResponseEntity.badRequest().body("User not updated")
            }
        }
    }

    @Operation(summary = "Delete user by email", tags=["admin"])
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "User deleted"),
        ApiResponse(responseCode = "400", description = "User not found",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])
    ])
    @DeleteMapping("/admin/{email}")
    fun delete(@PathVariable @Email email: String): ResponseEntity<Any> {
        val deleted = userRepository.delete(email)
        return if (deleted == null) {
            ResponseEntity.badRequest().body("User not found")
        } else {
            ResponseEntity.noContent().build()
        }
    }
}