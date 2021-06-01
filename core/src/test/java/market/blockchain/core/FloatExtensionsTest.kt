package market.blockchain.core

import market.blockchain.core.util.round
import market.blockchain.core.util.toStringTrimmed
import org.junit.Test

import org.junit.Assert.assertEquals

class FloatExtensionsTest {
    @Test
    fun `test rounding`() {
        val float = 3.14444f
        assertEquals(3.14f, float.round(2))
        assertEquals(3.1f, float.round(1))
        assertEquals(3.14444f, float.round(0))
        assertEquals(3.14444f, float.round(-1))
    }

    @Test
    fun `test trimming float to string`() {
        assertEquals("4.0m", 4000000f.toStringTrimmed())
        assertEquals("4.1m", 4100000f.toStringTrimmed())
        assertEquals("4.25m", 4250000f.toStringTrimmed())
        assertEquals("4.25m", 4254000f.toStringTrimmed())
        assertEquals("4.26m", 4255000f.toStringTrimmed())

        assertEquals("4.0k", 4000f.toStringTrimmed())
        assertEquals("4.1k", 4100f.toStringTrimmed())
        assertEquals("4.2k", 4200f.toStringTrimmed())
        assertEquals("4.2k", 4240f.toStringTrimmed())
        assertEquals("4.3k", 4260f.toStringTrimmed())
    }
}