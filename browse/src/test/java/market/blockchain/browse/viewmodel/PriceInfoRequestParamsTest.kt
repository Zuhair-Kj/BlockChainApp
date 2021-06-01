package market.blockchain.browse.viewmodel

import market.blockchain.browse.api.rollingAverage
import market.blockchain.browse.api.timeSpan
import market.blockchain.browse.model.PriceInfoRequestParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PriceInfoRequestParamsTest {
    @Test
    fun `should convert to map correctly`() {
        val params = PriceInfoRequestParams(4, 1)
        val map = params.convertToMap()
        assertEquals(2, map.entries.size)
        assertTrue(map.containsKey(timeSpan))
        assertTrue(map.containsKey(rollingAverage))
        assertEquals("1weeks", map[rollingAverage])
        assertEquals("4weeks", map[timeSpan])
    }
}