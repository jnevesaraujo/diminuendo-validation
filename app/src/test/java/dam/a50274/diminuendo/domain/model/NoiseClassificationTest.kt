package dam.a50274.diminuendo.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class NoiseClassificationTest {

    @Test
    fun toNoiseClassification_below70_returnsSafe() {
        val db = 69.9
        assertEquals(NoiseClassification.SAFE, db.toNoiseClassification())
    }

    @Test
    fun toNoiseClassification_at70_returnsConcerning() {
        val db = 70.0
        assertEquals(NoiseClassification.CONCERNING, db.toNoiseClassification())
    }

    @Test
    fun toNoiseClassification_above85_returnsDangerous() {
        val db = 85.1
        assertEquals(NoiseClassification.DANGEROUS, db.toNoiseClassification())
    }
}
