package com.xeatpos.report

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.xeatpos.R
import com.xeatpos.activities.LoginActivity
import com.xeatpos.activities.PrintReportsActivity
import com.xeatpos.comman.ToastClass.toast
import com.xeatpos.databinding.FragmentDailyReportBinding
import com.xeatpos.prefs
import com.xeatpos.response.daily.DailyReport
import com.xeatpos.retrofit.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NullPointerException
import java.text.DecimalFormat
import java.util.ArrayList


class DailyReportFragment : Fragment() {
    lateinit var binding: FragmentDailyReportBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_daily_report, container, false)
        init()

        binding.imagePrintDaily.setOnClickListener {

            startActivity(Intent(context, PrintReportsActivity::class.java).putExtra("type","0"))

        }

        return binding.root
    }

    fun init() {

        callDailyReposts()


    }

    fun callDailyReposts() {

         var apiService = APIService.create().dailyOrderCall("application/json", "Bearer " + prefs.token,"daily")


        apiService.enqueue(object : Callback<DailyReport> {
            override fun onResponse(
                call: Call<DailyReport>,
                response: Response<DailyReport>
            ) {
                if (response.isSuccessful) {
                    var result = response.body()
                    if (result?.status.equals("1")) {
//                        context?.toast(result?.yearly_orders?.January.toString())


                        binding.chart.setBackgroundColor(Color.WHITE)
                        binding.chart.setDrawBarShadow(false)
                        binding.chart.setDrawValueAboveBar(true)
                        binding.chart.getDescription().setEnabled(false)
                        binding.chart.setPinchZoom(false)
                        binding.chart.setDrawGridBackground(false)

                        val xAxis: XAxis = binding.chart.getXAxis()
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.setDrawGridLines(true)
                        xAxis.setDrawAxisLine(true)
                        xAxis.textColor = Color.LTGRAY
                        xAxis.textSize = 10f
                        xAxis.labelCount = 5
                        xAxis.setCenterAxisLabels(true)
                        xAxis.granularity = 1f

                        val left: YAxis = binding.chart.getAxisLeft()
                        left.setDrawLabels(false)
                        left.spaceTop = 25f
//                      left.spaceBottom = 25f
                        left.setDrawAxisLine(true)
                        left.setDrawGridLines(true)
                        left.setDrawZeroLine(true) // draw a zero line

                        left.zeroLineColor = Color.BLACK
//                        left.zeroLineWidth = 0.7f
                        binding.chart.getAxisRight().setEnabled(true)
                        binding.chart.getLegend().setEnabled(true)

                        val data: MutableList<Data> = ArrayList<Data>()
                        for (i in 0 until result!!.daily_orders.size) {
                            data.add(
                                Data(
                                    i.toFloat(),
                                    result.daily_orders[i].count.toFloat(),
                                    result.daily_orders[i].hour
                                )
                            )
                        }


                        xAxis.valueFormatter =
                            object : com.github.mikephil.charting.formatter.ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    var rom = data[Math.min(
                                        Math.max(value.toInt(), 0),
                                        data.size - 1
                                    )].xAxisValue
                                    return rom
                                }
                            }

                        setData(data)


                    } else {
                        context?.toast(result?.message.toString())
                    }

                }


            }

            override fun onFailure(call: Call<DailyReport>, t: Throwable) {
                try {
                    Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT).show()
                }catch (e : NullPointerException){
                    e.printStackTrace()
                }
            }

        })


    }

    private fun setData(dataList: List<Data>) {
        val values = ArrayList<BarEntry>()
        for (i in dataList.indices) {
            val d = dataList[i]
            val entry = BarEntry(d.xValue, d.yValue)
            values.add(entry)
        }
        val set: BarDataSet
        if (binding.chart.getData() != null &&
            binding.chart.getData().getDataSetCount() > 0
        ) {
            set = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
            set.values = values
            binding.chart.getData().notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            set = BarDataSet(values, "Daily")
            set.setColors(ColorTemplate.COLORFUL_COLORS, 12222)
            set.valueTextColor = Color.BLACK
            set.valueTextSize = 8f
            val data = BarData(set)
            data.setValueTextSize(10f)
            data.setValueFormatter(ValueFormatter() as com.github.mikephil.charting.formatter.ValueFormatter)
            data.barWidth = 0.7f
            binding.chart.setData(data)
            binding.chart.invalidate()
            binding.chart.setVisibleXRangeMaximum(23f) // allow 5 values to be displayed
            binding.chart.moveViewToX(1f)
            binding.chart.animateY(2000)
        }
    }


    private class Data internal constructor(
        val xValue: Float,
        val yValue: Float,
        val xAxisValue: String
    )

    private class ValueFormatter internal constructor() :
        com.github.mikephil.charting.formatter.ValueFormatter() {
        private val mFormat: DecimalFormat
        override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
        ): String {
            return mFormat.format(value.toDouble())
        }

        init {
            mFormat = DecimalFormat("######.0")
        }
    }

}