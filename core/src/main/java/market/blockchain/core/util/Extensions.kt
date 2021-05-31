package market.blockchain.core.util

import java.math.BigDecimal
import java.math.RoundingMode

fun Float.toStringTrimmed(): String {
    return when {
        this > 1000000 -> String.format("%sm", (this / 1000000f).round(2).toString())
        this > 1000 -> String.format("%sk", (this / 1000).round(1).toString())
        else -> this.toString()
    }
}

fun Float.round(numOfDigits: Int): Float {
    return BigDecimal(this.toDouble()).setScale(numOfDigits, RoundingMode.HALF_EVEN).toFloat()
}