package market.blockchain.browse.api

import market.blockchain.browse.model.PriceInfo
import market.blockchain.browse.model.PriceInfoRequestParams
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.QueryMap

const val timeSpan = "timespan"
const val rollingAverage = "rollingAverage"
interface GetPriceInfoApi {
    @GET("/charts/market-price")
    suspend fun getBitCoinPrice(@QueryMap map: Map<String, String>): PriceInfo
}