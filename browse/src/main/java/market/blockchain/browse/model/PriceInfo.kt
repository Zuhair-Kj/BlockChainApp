package market.blockchain.browse.model

data class PriceInfo(
    val name: String? = null,
    val values: List<Point>? = null
)

data class Point(val x: Float, val y: Float)