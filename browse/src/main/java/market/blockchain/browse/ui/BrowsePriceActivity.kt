package market.blockchain.browse.ui

import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import market.blockchain.browse.R
import market.blockchain.browse.databinding.ActivityBrowsePriceBinding
import market.blockchain.browse.model.PriceInfo
import market.blockchain.browse.viewmodel.BrowsePriceViewModel
import market.blockchain.core.util.Resource
import market.blockchain.core.util.Resource.Status.ERROR
import market.blockchain.core.util.Resource.Status.LOADING
import market.blockchain.core.util.Resource.Status.NETWORK_DISCONNECTED
import market.blockchain.core.util.Resource.Status.SUCCESS
import market.blockchain.core.util.hide
import market.blockchain.core.util.show
import market.blockchain.core.util.toStringTrimmed
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BrowsePriceActivity : AppCompatActivity() {
    private val browsePriceViewModel: BrowsePriceViewModel by viewModel()
    lateinit var binding: ActivityBrowsePriceBinding
    private val chartFillColor: Int by lazy { ContextCompat.getColor(this, R.color.purple_200) }
    private val chartLineColor: Int by lazy { ContextCompat.getColor(this, R.color.purple_700) }
    private val axisYTextSize: Float by lazy { resources.getDimensionPixelOffset(R.dimen.font_size_medium).toFloat() }
    private val axisXTextSize: Float by lazy { resources.getDimensionPixelOffset(R.dimen.font_size_tiny).toFloat() }
    private val colorBlack: Int by lazy { ContextCompat.getColor(this, R.color.black) }
    private val chartAnimation: Int by lazy { resources.getInteger(R.integer.chart_animation_duration) }
    private val marginTrivial: Float by lazy { resources.getDimensionPixelOffset(R.dimen.margin_trivial).toFloat() }
    private val marginTiny: Float by lazy { resources.getDimensionPixelOffset(R.dimen.margin_tiny).toFloat() }
    private val marginSmaller: Float by lazy { resources.getDimensionPixelOffset(R.dimen.margin_smaller).toFloat() }
    private val marginSmall: Float by lazy { resources.getDimensionPixelOffset(R.dimen.margin_small).toFloat() }
    private val marginMedium: Float by lazy { resources.getDimensionPixelOffset(R.dimen.margin_medium).toFloat() }
    private val marginLarge: Float by lazy { resources.getDimensionPixelOffset(R.dimen.margin_large).toFloat() }
    private val dateFormat: String by lazy { resources.getString(R.string.chart_date_format) }
    private val markerView: CustomMarkerView by lazy {
        CustomMarkerView(
            this,
            R.layout.marker_view,
            SimpleDateFormat(dateFormat, Locale.ENGLISH)
        )
    }

    private val browsePriceObserver = Observer<Resource<PriceInfo>> { resource ->
        when(resource.status) {
            SUCCESS -> {
                if (offlineSnackBar.isShownOrQueued)
                    offlineSnackBar.dismiss()
                resource.data?.let {
                    binding.chart.show()
                    binding.groupComponents.show()
                    binding.lottieLoader.hide()
                    setChartData(it)
                }
            }
            LOADING -> {
                binding.chart.hide()
                binding.layoutRetry.root.hide()
                binding.lottieLoader.show()
            }
            ERROR -> {
                binding.lottieLoader.hide()
                binding.layoutRetry.root.show()
                binding.chart.hide()
            }
            NETWORK_DISCONNECTED -> {
                binding.lottieLoader.hide()
                resource.data?.let {
                    binding.chart.show()
                } ?: kotlin.run {
                    binding.layoutRetry.root.show()
                    binding.chart.hide()
                }
                offlineSnackBar.show()
            }
        }
    }

    private val seekBarChangeListener = object: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (seekBar.id == R.id.seek_bar_time_span) {
                binding.textViewTimeSpan.text = resources.getQuantityString(
                    R.plurals.time_span_weeks_format,
                    progress,
                    progress
                )
            } else {
                binding.textViewAverage.text = resources.getQuantityString(
                    R.plurals.average_weeks_format,
                    progress,
                    progress
                )
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) { }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            val progress = seekBar.progress
            if (seekBar.id == R.id.seek_bar_time_span) {
                browsePriceViewModel.requestParams.timeSpanInWeeks = progress
            } else {
                browsePriceViewModel.requestParams.rollingAverageInWeeks = progress
            }
            browsePriceViewModel.getPricesInfo()
        }
    }

    private val onRetryClickListener = View.OnClickListener {
        browsePriceViewModel.getPricesInfo()
    }

    private val offlineSnackBar by lazy {
        Snackbar.make(binding.coordinatorLayout, R.string.offline, Snackbar.LENGTH_INDEFINITE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_browse_price)

        browsePriceViewModel.requestParams.let {
            initProgressBars(it.timeSpanInWeeks, it.rollingAverageInWeeks)
        }

        binding.layoutRetry.buttonRetry.setOnClickListener(onRetryClickListener)
        initialiseChart()
        browsePriceViewModel.stateLiveData.observe(this, browsePriceObserver)
        if (browsePriceViewModel.stateLiveData.value?.data == null)
            browsePriceViewModel.getPricesInfo()
    }

    private fun initialiseChart() {
        binding.chart.let { chart ->
            markerView.chartView = chart
            chart.marker = markerView

            chart.setTouchEnabled(true)
            chart.setDrawGridBackground(false)
            chart.isHighlightPerDragEnabled = true
            chart.setViewPortOffsets(marginLarge, marginLarge, marginLarge, marginLarge)

            chart.legend.isEnabled = false

            val xAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
            xAxis.typeface = Typeface.DEFAULT
            xAxis.textSize = axisXTextSize
            xAxis.labelRotationAngle = 270f
            xAxis.textColor = colorBlack
            xAxis.axisLineColor = colorBlack
            xAxis.setDrawAxisLine(true)
            xAxis.setDrawGridLines(true)
            xAxis.setCenterAxisLabels(true)

            xAxis.valueFormatter = object : ValueFormatter() {
                private val mFormat: SimpleDateFormat =
                    SimpleDateFormat(dateFormat, Locale.ENGLISH)

                override fun getFormattedValue(value: Float): String {
                    return mFormat.format(
                        Date(value.toLong() * 1000)
                    )
                }
            }

            val leftAxis = chart.axisLeft
            leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            leftAxis.typeface = Typeface.DEFAULT
            leftAxis.textColor = colorBlack
            leftAxis.axisLineColor = colorBlack
            leftAxis.textSize = axisYTextSize
            leftAxis.axisMinimum = 0f
            leftAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toStringTrimmed()
                }
            }

            chart.axisRight.isEnabled = false
        }
    }

    private fun initProgressBars(timeSpanWeeks: Int, averageWeeks: Int) {
        binding.seekBarTimeSpan.setOnSeekBarChangeListener(seekBarChangeListener)
        binding.seekBarAverage.setOnSeekBarChangeListener(seekBarChangeListener)

        binding.seekBarTimeSpan.progress = timeSpanWeeks
        binding.seekBarAverage.progress = averageWeeks
//        binding.textViewTimeSpan.text = resources.getQuantityString(
//            R.plurals.time_span_weeks_format,
//            timeSpanWeeks,
//            timeSpanWeeks
//        )
//
//        binding.textViewAverage.text = resources.getQuantityString(
//            R.plurals.average_weeks_format,
//            averageWeeks,
//            averageWeeks
//        )
    }

    private fun setChartData(priceInfo: PriceInfo) {
        val values: List<Entry> = priceInfo.values?.map { Entry(it.x, it.y) }
            ?: emptyList()

        var xMin = 0f
        var xMax = 0f
        var yMax = 0f

        values.forEach { point ->
            xMin = minOf(xMin, point.x)
            xMax = maxOf(xMax, point.x)
            yMax = maxOf(yMax, point.y)
        }

        binding.chart.let { chart ->
            priceInfo.name?.let {
                chart.description.text = it
                chart.description.textColor = colorBlack

            } ?: kotlin.run {
                chart.description.isEnabled = false
            }

            chart.xAxis.mAxisMaximum = xMin
            chart.xAxis.mAxisMaximum = xMax
            chart.axisLeft.axisMaximum = yMax
        }
        // create a dataset and give it a type
        val set1 = LineDataSet(values, "")
        set1.setDrawIcons(false)
        set1.enableDashedLine(marginSmall, marginSmaller, 0f)
        set1.color = chartLineColor
        set1.setCircleColor(chartLineColor)
        set1.lineWidth = marginTrivial
        set1.circleRadius = marginTiny
        set1.setDrawCircleHole(false)
        set1.formLineWidth = marginTiny
        set1.formLineDashEffect = DashPathEffect(floatArrayOf(marginSmall, marginSmaller), 0f)
        set1.formSize = marginMedium
        set1.valueTextSize = 0f
        set1.enableDashedHighlightLine(marginSmall, marginSmaller, 0f)
        set1.setDrawFilled(true)
        set1.fillFormatter = IFillFormatter { _, _ ->
            binding.chart.axisLeft.axisMinimum
        }

        set1.fillColor = chartFillColor

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1)

        binding.chart.data = LineData(dataSets)
        binding.chart.animateXY(chartAnimation, chartAnimation)
    }
}