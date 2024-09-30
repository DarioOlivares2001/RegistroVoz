package com.example.registrovoz

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class MenuScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMenuScreenDisplaysCorrectTitle() {
        // Configurar la pantalla
        composeTestRule.setContent {
            MenuScreen(navController = rememberNavController())
        }

        // Verificar que el título se muestra correctamente
        composeTestRule.onNodeWithText("Menú del Restaurante").assertIsDisplayed()
    }

    @Test
    fun testMenuItemsDisplayed() {
        // Configurar la pantalla
        composeTestRule.setContent {
            MenuScreen(navController = rememberNavController())
        }

        // Verificar que los elementos del menú se muestran
        val menuItems = listOf(
            "Ensalada César - $8.50",
            "Sopa de Tomate - $6.00",
            "Pizza Margarita - $12.00",
            "Hamburguesa con Queso - $10.50",
            "Tiramisú - $7.00"
        )

        menuItems.forEach { item ->
            composeTestRule.onNodeWithText(item).assertIsDisplayed()
        }
    }

    @Test
    fun testClickOnMenuItemTriggersSpeech() {
        // Configurar la pantalla
        composeTestRule.setContent {
            MenuScreen(navController = rememberNavController())
        }

        // Simular el clic en un elemento del menú
        composeTestRule.onNodeWithText("Ensalada César - $8.50").performClick()

        // Aquí deberías verificar que el TextToSpeech se activa.
        // Como esto es más complicado de verificar, puedes agregar
        // una función en tu ViewModel o usar un mock para verificar que se llama.
    }

}
