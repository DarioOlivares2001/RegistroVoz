package com.example.registrovoz

import UserListScreen
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.registrovoz.Model.User
import com.google.firebase.database.DatabaseReference
import org.junit.Rule
import org.junit.Test


class UserListScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testUserListScreenDisplaysTitle() {
        // Configurar la pantalla
        composeTestRule.setContent {
            UserListScreen(currentUserEmail = "admin@gmail.com") // Usar un email de admin para mostrar el título
        }

        // Verificar que el título se muestra correctamente
        composeTestRule.onNodeWithText("Usuarios Registrados").assertIsDisplayed()
    }

    @Test
    fun testDeleteUserButtonNotVisibleForRegularUser() {
        // Configurar la pantalla con un email de usuario normal
        composeTestRule.setContent {
            UserListScreen(currentUserEmail = "user@gmail.com")
        }

        // Verificar que el botón de eliminar no se muestra
        composeTestRule.onNodeWithContentDescription("Eliminar usuario").assertDoesNotExist()
    }
}
