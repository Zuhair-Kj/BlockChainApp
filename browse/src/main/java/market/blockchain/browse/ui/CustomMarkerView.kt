package market.blockchain.browse.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import market.blockchain.browse.R
import java.text.DateFormat
import java.util.Date

// Nothing can be done here since the library class does not accept attributeSet
@SuppressLint("ViewConstructor")
class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val dateFormat: DateFormat
) : MarkerView(context, layoutResource) {
    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        val yValueText = Utils.formatNumber(entry.y, 0, false)
        val xValueText = dateFormat.format(
            Date(entry.x.toLong() * 1000)
        )

        tvContent.text = String.format("%s \n on \n %s", yValueText, xValueText)
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

}
