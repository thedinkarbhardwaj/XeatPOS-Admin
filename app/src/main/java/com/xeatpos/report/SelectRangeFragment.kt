package com.xeatpos.report

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.xeatpos.R
import com.xeatpos.activities.PrintReportsActivity
import com.xeatpos.comman.ToastClass.toast
import com.xeatpos.data.ResponseReportPrint
import com.xeatpos.databinding.FragmentSelectRangeBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SelectRangeFragment : Fragment() {
    lateinit var binding: FragmentSelectRangeBinding
    var mdateStatus = 0
    var mDateListener: DatePickerDialog.OnDateSetListener? = null
    lateinit var orders: ResponseReportPrint.Orders

    private val progressDialog = CustomProgressDialog()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_range, container, false)

        init()

        binding.layoutPrintDaily.visibility = View.GONE


        return binding.root
    }

    fun init() {
        binding.txtFromDate.setOnClickListener {
            mdateStatus = 1
            datepickerDialog()
        }
        binding.txtToDate.setOnClickListener {
            mdateStatus = 2
            datepickerDialog()
        }
        binding.btnSubmitDates.setOnClickListener {
            submitDates()
        }
        binding.layoutPrintDaily.setOnClickListener {
            var intent = Intent(context, PrintReportsActivity::class.java)
            intent.putExtra("print_data", orders)
            startActivity(intent)

        }
        mDateListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->

                var month = month

                month = month + 1



                if (day < 10) {
                    if (mdateStatus == 1) {
                        if(month < 10){
                            binding.txtFromDate.setText(
                                StringBuilder().append(year).append("-")
                                    .append("0"+month).append("-").append("0" + day)
                            )
                        }else{
                            binding.txtFromDate.setText(
                                StringBuilder().append(year).append("-")
                                    .append(month).append("-").append("0" + day)
                            )
                        }

                    } else {
                        if(month < 10) {
                            binding.txtToDate.setText(
                                StringBuilder().append(year).append("-")
                                    .append("0"+month).append("-").append("0" + day)
                            )
                        }else{
                            binding.txtToDate.setText(
                                StringBuilder().append(year).append("-")
                                    .append(month).append("-").append("0" + day)
                            )
                        }

                    }

                } else {

                    if (mdateStatus == 1) {
                        if(month < 10) {
                            binding.txtFromDate.setText(
                                StringBuilder().append(year).append("-")
                                    .append("0"+month).append("-").append(day)
                            )
                        }else{
                            binding.txtFromDate.setText(
                                StringBuilder().append(year).append("-")
                                    .append(month).append("-").append(day)
                            )
                        }

                    } else {
                        if(month < 10) {
                            binding.txtToDate.setText(
                                StringBuilder().append(year).append("-")
                                    .append("0"+month).append("-").append(day)
                            )
                        }else{
                            binding.txtToDate.setText(
                                StringBuilder().append(year).append("-")
                                    .append(month).append("-").append(day)
                            )
                        }

                    }

                }

            }
    }

    fun datepickerDialog() {

        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]

        val dialog = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_DeviceDefault_Light_Dialog_Alert,
            mDateListener,
            year, month, day
        )
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }


    fun submitDates() {
        var startDate = binding.txtFromDate.text.toString().trim()
        var endDate = binding.txtToDate.text.toString().trim()
        if (startDate.equals("")) {
            context?.toast("Please select start date")
        } else if (endDate.equals("")) {
            context?.toast("Please select end date")
        } else {
            context?.let { it1 -> progressDialog.show(it1, getString(R.string.loading_orders)) }

            callRangeReposts(startDate, endDate)
//            context?.toast(startDate+"   "+endDate)
        }


    }


    fun callRangeReposts(startDate: String, endDate: String) {
        var apiService = APIService.create().rangeOrderList(
            "application/json",
            "Bearer " + prefs.token, startDate, endDate
        )
        apiService.enqueue(object : Callback<ResponseReportPrint.ReportPrint> {
            override fun onResponse(
                call: Call<ResponseReportPrint.ReportPrint>,
                response: Response<ResponseReportPrint.ReportPrint>
            ) {
                if (response.isSuccessful) {
                    var result = response.body()
                    if (progressDialog != null) {
                        progressDialog.dialog.dismiss()

                    }
                    if (result?.status.equals("1")) {

                        binding.layoutPrintDaily.visibility = View.VISIBLE

                        orders =
                            ResponseReportPrint.Orders(
                                result!!.orders.amount,
                                result!!.orders.delivery_fees,
                                result!!.orders.total_delivery_order,
                                result!!.orders.total_order,
                                result!!.orders.total_pickup_order,
                                result!!.orders.total_sales,
                                result!!.orders.restaurant_name,
                                result!!.orders.admin_commision,
                                result!!.orders.website_qrcode
                            )

                        binding.txtTotalOrders.text =
                            "Total Orders: " + orders.total_order
                        binding.txtPickedupOrders.text =
                            "Pickup Orders : " + orders.total_pickup_order
                        binding.txtDeliveredOrders.text =
                            "Delivered Orders: " + orders.total_delivery_order
                        binding.txtItemPrice.text =
                            "Total Items Amount: " + orders.amount
                        binding.txtDeliveryFee.text =
                            "Total Delivery Fee: " + orders.delivery_fees
                        binding.txtTotalOrderAmount.text = orders.total_sales
                        binding.llPrintView.visibility = View.VISIBLE
//

                    } else {
                        context?.toast(response.body()!!.message)
                    }
                } else {
                    context?.toast("Check your internet connection")
                }
            }

            override fun onFailure(call: Call<ResponseReportPrint.ReportPrint>, t: Throwable) {
                context?.toast("Check your internet connection")
            }

        })


    }


}