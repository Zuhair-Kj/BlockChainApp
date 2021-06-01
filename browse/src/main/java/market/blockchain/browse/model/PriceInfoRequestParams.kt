package market.blockchain.browse.model

import market.blockchain.browse.api.rollingAverage
import market.blockchain.browse.api.timeSpan

class PriceInfoRequestParams(var timeSpanInWeeks: Int, var rollingAverageInWeeks: Int = 0) {
    fun convertToMap(): Map<String, String> {
        return mutableMapOf(Pair(timeSpan, "${timeSpanInWeeks}weeks"))
            .also {
                if (rollingAverageInWeeks > 0)
                    it[rollingAverage] = "${rollingAverageInWeeks}weeks"
            }
    }
}