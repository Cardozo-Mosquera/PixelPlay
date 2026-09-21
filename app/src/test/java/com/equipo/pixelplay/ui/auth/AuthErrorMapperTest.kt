package com.equipo.pixelplay.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test JVM puro del mapeo de errores.
 * Solo se cubre la rama genérica: las excepciones específicas de Firebase Auth
 * no son instanciables en JVM puro (requerirían Android/Robolectric).
 */
class AuthErrorMapperTest {

    @Test
    fun `una excepcion generica mapea al mensaje por defecto en espanol`() {
        val message = mapAuthErrorToSpanish(RuntimeException("boom"))
        assertEquals("Ocurrió un error. Inténtalo de nuevo", message)
    }
}
