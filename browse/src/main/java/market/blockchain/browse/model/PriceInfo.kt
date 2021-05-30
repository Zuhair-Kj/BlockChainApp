package market.blockchain.browse.model

data class PriceInfo(
    private val name: String? = null,
    private val values: List<Point>? = null
)

data class Point(val x: Double, val y: Double)