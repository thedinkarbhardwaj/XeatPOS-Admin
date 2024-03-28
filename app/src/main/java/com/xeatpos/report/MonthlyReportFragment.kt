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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.xeatpos.R
import com.xeatpos.activities.PrintReportsActivity
import com.xeatpos.comman.ToastClass.toast
import com.xeatpos.databinding.FragmentMonthlyReportBinding
import com.xeatpos.databinding.FragmentWeeklyReportBinding
import com.xeatpos.prefs
import com.xeatpos.response.yearly.YearlyReport
import com.xeatpos.retrofit.APIService
import kotlinx.android.synthetic.main.fragment_weekly_report.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MonthlyReportFragment : Fragment() {
    lateinit var binding: FragmentMonthlyReportBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_monthly_report, container, false)

        callDailyReposts()

        binding.imagePrintMonthly.setOnClickListener {

            startActivity(Intent(context, PrintReportsActivity::class.java).putExtra("type","2"))

        }



        return binding.root
    }

    fun callDailyReposts() {
        var apiService = APIService.create().yearlyOrderCall("application/json", "Bearer " + prefs.token,"yearly")
        apiService.enqueue(object : Callback<YearlyReport> {
            override fun onResponse(
                call: Call<YearlyReport>,
                response: Response<YearlyReport>
            ) {
                if (response.isSuccessful) {
                    var result = response.body()
                    if (result?.status.equals("1")) {
//                        context?.toast(result?.yearly_orders?.January.toString())


                        val labels = arrayListOf(
                            result!!.yearly_orders[0].months,  result!!.yearly_orders[1].months,  result!!.yearly_orders[2].months,
                            result!!.yearly_orders[3].months,  result!!.yearly_orders[4].months,  result!!.yearly_orders[5].months,
                            result!!.yearly_orders[6].months,  result!!.yearly_orders[7].months,  result!!.yearly_orders[8].months,
                            result!!.yearly_orders[9].months,  result!!.yearly_orders[10].months,  result!!.yearly_orders[11].months

                        )

                        binding.chart1.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                        binding.chart1.xAxis.position = XAxis.XAxisPosition.BOTTOM

                        binding.chart1.setDrawGridBackground(false)
                        binding.chart1.axisLeft.isEnabled = true
                        binding.chart1.axisRight.isEnabled = true
                        binding.chart1.description.isEnabled = false

                        val entries = arrayListOf(

                            BarEntry(0f, result!!.yearly_orders[0].count.toFloat()),
                            BarEntry(1f, result!!.yearly_orders[1].count.toFloat()),
                            BarEntry(2f, result!!.yearly_orders[2].count.toFloat()),
                            BarEntry(3f, result!!.yearly_orders[3].count.toFloat()),
                            BarEntry(4f, result!!.yearly_orders[4].count.toFloat()),
                            BarEntry(5f, result!!.yearly_orders[5].count.toFloat()),
                            BarEntry(6f, result!!.yearly_orders[6].count.toFloat()),
                            BarEntry(7f, result!!.yearly_orders[7].count.toFloat()),
                            BarEntry(8f, result!!.yearly_orders[8].count.toFloat()),
                            BarEntry(9f, result!!.yearly_orders[9].count.toFloat()),
                            BarEntry(10f, result!!.yearly_orders[10].count.toFloat()),
                            BarEntry(11f, result!!.yearly_orders[11].count.toFloat()),
                        )
                        val set = BarDataSet(entries, "This Year")
                        set.setColors(ColorTemplate.COLORFUL_COLORS, 12222);
                        set.setValueTextColor(Color.BLACK)
                        set.setValueTextSize(8f)

                        binding.chart1.data = BarData(set)
                        binding.chart1.animateY(2000)

                        binding.chart1.invalidate()
                    } else {
                        context?.toast(result?.message.toString())
                    }

                }


            }

            override fun onFailure(call: Call<YearlyReport>, t: Throwable) {

            }

        })


    }

}