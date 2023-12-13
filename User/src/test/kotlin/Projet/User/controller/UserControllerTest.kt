package Projet.User.controller

import Projet.User.controller.dto.UserDTO
import Projet.User.domain.User
import Projet.User.repository.UserRepository
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity

@SpringBootTest
class UserControllerTest {
    @MockkBean
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userController: UserController

    @Nested
    inner class UpdateTests {
        @Test
        fun testUpdate() {
            every { userRepository.update(any()) } returns Result.success(User("truc@bidule.com", "Test", "3 rue Marechal", true, "07/07/2021"))
            val updateUser = UserDTO("truc@bidule.com", "Test", "3 rue Marechal", true, "07/07/2021")
            val result = userController.update("truc@bidule.com", updateUser)
            assertThat(result).isEqualTo(ResponseEntity.ok(updateUser))
        }
        @Test
        fun testUpdateNonExistingUser() {
            every { userRepository.update(any()) } returns Result.failure(Exception("Nope"))
            val update = UserDTO("truc@bidule.com", "Test", "3 rue Marechal", true, "07/07/2021")
            val result = userController.update("truc@bidule.com", update)
            assertThat(result).isEqualTo(ResponseEntity.badRequest().body("User not updated"))
        }

        @Test
        fun testUpdateWithTwoEmail() {
            val update = UserDTO("truc@bidule.com", "Test", "3 rue Marechal", true, "07/07/2021")
            val result = userController.update("machin@bidule.com", update)
            assertThat(result).isEqualTo(ResponseEntity.badRequest().body("Email doesn't exist"))
        }
    }
}