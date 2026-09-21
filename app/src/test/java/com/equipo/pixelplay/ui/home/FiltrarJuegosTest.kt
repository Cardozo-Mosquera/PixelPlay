package com.equipo.pixelplay.ui.home

import com.equipo.pixelplay.domain.model.Game
import org.junit.Assert.assertEquals
import org.junit.Test

/** Test JVM puro de la función de filtrado en memoria. */
class FiltrarJuegosTest {

    private fun game(id: Int, name: String) =
        Game(id = id, name = name, genre = "", platform = "", thumbnail = "", shortDescription = "", description = null)

    private val juegos = listOf(
        game(1, "Fortnite"),
        game(2, "Warframe"),
        game(3, "Free Fire"),
        game(4, "Ragnarök")
    )

    @Test
    fun `query vacio devuelve la lista completa`() {
        assertEquals(juegos, filtrarJuegos(juegos, ""))
        assertEquals(juegos, filtrarJuegos(juegos, "   "))
    }

    @Test
    fun `filtra por nombre ignorando mayusculas`() {
        val res = filtrarJuegos(juegos, "FIRE")
        assertEquals(listOf("Free Fire"), res.map { it.name })
    }

    @Test
    fun `coincidencia parcial en cualquier parte del nombre`() {
        val res = filtrarJuegos(juegos, "fra")
        assertEquals(listOf("Warframe"), res.map { it.name })
    }

    @Test
    fun `ignora acentos basicos`() {
        val res = filtrarJuegos(juegos, "ragnarok")
        assertEquals(listOf("Ragnarök"), res.map { it.name })
    }

    @Test
    fun `sin coincidencias devuelve lista vacia`() {
        assertEquals(emptyList<Game>(), filtrarJuegos(juegos, "zelda"))
    }
}
