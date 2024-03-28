package com.xeatpos.activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.xeatpos.R
import com.xeatpos.data.ResponseNewOrderDetails
import com.xeatpos.data.ResponseReportPrint
import com.xeatpos.databinding.PrintReportBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.sunmi.SunmiPrintHelper
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrintReportsActivity : BaseActivity() {

    lateinit var binding: PrintReportBinding

    lateinit var response: ResponseNewOrderDetails.Data

    private val progressDialog = CustomProgressDialog()
    lateinit var orders: ResponseReportPrint.Orders
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.print_report)

        supportActionBar?.hide()

        SunmiPrintHelper.getInstance().initSunmiPrinterService(this)

        val typeface = ResourcesCompat.getFont(this, R.font.test)

        if (intent.getStringExtra("type") == null) {
            var result = intent.getParcelableExtra<ResponseReportPrint.Orders>("print_data")!!

            setDataOnView(result)
        } else {
            progressDialog.show(this, getString(R.string.loading_orders))
            GetReport(intent.getStringExtra("type")!!)
        }

    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap? {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)
        return b
    }

    fun GetReport(type: String) {
        val apiInterface =
            APIService.create().rangeOrderList("application/json", "Bearer " + prefs.token, type)

        apiInterface.enqueue(object : Callback<ResponseReportPrint.ReportPrint> {
            override fun onResponse(
                call: Call<ResponseReportPrint.ReportPrint>?,
                response: Response<ResponseReportPrint.ReportPrint>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        setDataOnView(response.body()!!.orders)

                    } else {

                        Toast.makeText(
                            applicationContext,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT).show()

                    }
                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseReportPrint.ReportPrint>?, t: Throwable?) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })
    }


    fun setDataOnView(orders: ResponseReportPrint.Orders) {

        binding.txtResturantNameReport.text = orders.restaurant_name

        binding.txtTotalOrders.text =
            "Total Orders: " + orders.total_order
        binding.txtPickedupOrders.text =
            "Total Picked Orders: " + orders.total_pickup_order
        binding.txtDeliveredOrders.text =
            "Total Deliver Orders:" + orders.total_delivery_order
        binding.txtAdminComm.text =
            "Admin Commission:" + orders.admin_commision
        binding.txtItemPrice.text =
            "Total Items Amount:" + orders.amount
        binding.txtDeliveryFee.text =
            "Total Delivery Fee:" + orders.delivery_fees

        binding.txtTotalOrderAmount.text = orders.total_sales

        binding.llReports.postDelayed(Runnable {

            val bitmap: Bitmap =
                loadBitmapFromView(
                    binding.llReports,
                    binding.llReports.width,
                    binding.llReports.getHeight()
                )!!

            val finalBitmap =
                Bitmap.createBitmap(
                    bitmap.width,
                    bitmap.height * 2,
                    Bitmap.Config.ARGB_8888)

            val canvas = Canvas(finalBitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.drawBitmap(bitmap, 0f, bitmap.height.toFloat(), null)

            Glide.with(applicationContext).load(bitmap).into(binding.imageBitmap)
           // Glide.with(applicationContext).load(orders.website_qrcode).into(binding.imgQR)

            SunmiPrintHelper.getInstance().printBitmap(bitmap, 1)
            SunmiPrintHelper.getInstance().feedPaper()

        }, 1000)

        binding.llReports.postDelayed(Runnable {

            finish()


        }, 3000)
    }

}