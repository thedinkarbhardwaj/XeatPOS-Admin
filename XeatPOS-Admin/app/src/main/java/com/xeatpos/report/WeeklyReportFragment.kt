package com.xeatpos.report

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.xeatpos.databinding.FragmentWeeklyReportBinding
import com.github.mikephil.charting.utils.ColorTemplate

import com.xeatpos.R.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.xeatpos.activities.PrintReportsActivity
import com.xeatpos.comman.ToastClass.toast
import com.xeatpos.prefs
import com.xeatpos.response.weekly.WeeklyReport
import com.xeatpos.retrofit.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeeklyReportFragment : Fragment() {

    lateinit var binding: FragmentWeeklyReportBinding

    var list = arrayListOf<BarEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, layout.fragment_weekly_report, container, false)

        init()

        binding.imagePrintWeekly.setOnClickListener {

            startActivity(Intent(context, PrintReportsActivity::class.java).putExtra("type","1"))

        }

        return binding.root
    }

    fun init() {


        callDailyReposts()

    }

    fun callDailyReposts() {
        var apiService = APIService.create().weeklyOrderCall("application/json", "Bearer " + prefs.token,"weekly")
        apiService.enqueue(object : Callback<WeeklyReport> {
            override fun onResponse(
                call: Call<WeeklyReport>,
                response: Response<WeeklyReport>
            ) {
                if (response.isSuccessful) {
                    var result = response.body()
                    if (result?.status.equals("1")) {
//                        context?.toast(result?.weekly_orders?.Monday.toString())
                        val labels = arrayListOf(
                            result?.weekly_orders!![0].day,  result?.weekly_orders!![1].day,  result?.weekly_orders!![2].day,
                            result?.weekly_orders!![3].day,  result?.weekly_orders!![4].day,  result?.weekly_orders!![5].day,
                            result?.weekly_orders!![6].day)

                        binding.chart2.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                        binding.chart2.xAxis.position = XAxis.XAxisPosition.BOTTOM

                        binding.chart2.setDrawGridBackground(false)
                        binding.chart2.axisLeft.isEnabled = true
                        binding.chart2.axisRight.isEnabled = true
                        binding.chart2.description.isEnabled = false

                        list = arrayListOf(
                            BarEntry(0f, result!!.weekly_orders!![0].count.toFloat()),
                            BarEntry(1f, result!!.weekly_orders!![1].count.toFloat()),
                            BarEntry(2f, result!!.weekly_orders!![2].count.toFloat()),
                            BarEntry(3f, result!!.weekly_orders!![3].count.toFloat()),
                            BarEntry(4f, result!!.weekly_orders!![4].count.toFloat()),
                            BarEntry(5f, result!!.weekly_orders!![5].count.toFloat()),
                            BarEntry(6f, result!!.weekly_orders!![6].count.toFloat()),

                            )
                        val set = BarDataSet(list, "This week")
                        set.setColors(ColorTemplate.COLORFUL_COLORS, 12222);
                        set.setValueTextColor(Color.BLACK)
                        set.setValueTextSize(8f)

                        binding.chart2.data = BarData(set)
                        binding.chart2.animateY(2000)

                        binding.chart2.invalidate()
                    } else {
                        context?.toast(result?.message.toString())
                    }

                }


            }

            override fun onFailure(call: Call<WeeklyReport>, t: Throwable) {

            }

        })


    }


}