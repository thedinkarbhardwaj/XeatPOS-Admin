package com.xeatpos.order.orders

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.xeatpos.activities.BaseActivity
import com.xeatpos.data.ResponseNewOrderDetails
import com.xeatpos.data.ResponseNewOrders
import com.xeatpos.databinding.FragmentNewOrderDetailBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import android.content.Intent

import android.widget.TextView

import android.view.LayoutInflater
import android.widget.LinearLayout
import com.xeatpos.R
import com.xeatpos.activities.PrintOrderActivity
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.utils.CustomProgressDialog


class NewOrderDetailsActivity : BaseActivity() {

    lateinit var binding: FragmentNewOrderDetailBinding
    var list: MutableList<ResponseNewOrders.Data>? = ArrayList()
    private val progressDialog = CustomProgressDialog()

    lateinit var responseData: ResponseNewOrderDetails.Data

    lateinit var orderId: String
    lateinit var orderType: String
    lateinit var driverType: String
    lateinit var orderStatus: String
    var sch_type="" //later or now

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_new_order_detail)

        binding.toolbarNewOrder.imgBackArrow.setOnClickListener {
            finish()
        }

        supportActionBar.hashCode()
        binding.toolbarNewOrder.txtScreenName.text =
            getString(R.string.order_id) + " " + intent.getStringExtra(Constants.ORDER_ID)
        orderStatus = intent.getStringExtra("order_status").toString()
        sch_type = intent.getStringExtra("sch_type").toString()
        if(sch_type.equals("later") && orderStatus.equals("17"))
        {
            binding.btnAcceptDetails.visibility = View.GONE
            binding.btnRejectDetails.visibility = View.GONE
        }
        Log.e("check status order", orderStatus)
        if(orderStatus.equals("4"))
        {
            binding.textWaitingForDriver.visibility=View.VISIBLE
            binding.btnsLl.visibility = View.GONE
        }
        progressDialog.show(this, getString(R.string.loading_orders))
        getNewOrderDetail()

        binding.btnAcceptDetails.setOnClickListener {

            val notify_manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notify_manager.cancelAll();
            try {
                if (Constants.player_ring.isPlaying) {
                    Constants.player_ring.stop()
                }
            } catch (e: java.lang.Exception) {
            }
            progressDialog.show(this, getString(R.string.loading_orders))

            if(sch_now_type.equals("later")) {
                AcceptRejectOrderAdv("accepted")
            }
            else
                AcceptRejectOrder("accepted")

            //  val gson = Gson()
            //  val personString = gson.toJson(responseData)


        }

        binding.btnRejectDetails.setOnClickListener {
            try {
                if (Constants.player_ring.isPlaying) {
                    Constants.player_ring.stop()
                }
            } catch (e: java.lang.Exception) {
            }

            val notify_manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notify_manager.cancelAll();

            progressDialog.show(this, getString(R.string.loading_orders))
            if(sch_now_type.equals("later")) {
                AcceptRejectOrderAdv("rejected")
            }
            else
                AcceptRejectOrder("rejected")

            //  val gson = Gson()
            //  val personString = gson.toJson(responseData)

        }
    }

    var sch_now_type=""
    fun getNewOrderDetail() {
        val apiInterface =
            APIService.create().getNewOrderDetails(
                "application/json", "Bearer " + prefs.token,
                intent.getStringExtra(Constants.ORDER_ID)!!
            )


        apiInterface.enqueue(object : Callback<ResponseNewOrderDetails.NewOrderDetails> {
            override fun onResponse(
                call: Call<ResponseNewOrderDetails.NewOrderDetails>?,
                response: Response<ResponseNewOrderDetails.NewOrderDetails>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    responseData = response.body()!!.data

                    if (response.body()!!.status == "1") {

                        orderId = ""+response.body()!!.data.order_id
                        orderType = response.body()!!.data.order_type
                        driverType = response.body()!!.data.driver_type

                        if(orderType == "Pickup"){
                            binding.textAddressNewOrderDetails.visibility = View.GONE
                        }else{
                            binding.textAddressNewOrderDetails.visibility = View.VISIBLE
                        }

                        binding.textOrderIDNewOrderDetails.text =
                            response.body()!!.data.order_id.toString()
                        binding.textOrderByNewOrderDetails.text = response.body()!!.data.orderby
                        binding.textOrderAtNewOrderDetails.text = response.body()!!.data.order_at
                        binding.textAddressNewOrderDetails.text = "Address: "+response.body()!!.data.address

                        binding.textOrderTotalNewOrderDetails.text =
                            response.body()!!.data.total_amount
                        binding.textOrderInstructionsNewOrderDetails.text =
                            response.body()!!.data.instruction

                        binding.textOrderPaymentModeNewOrderDetails.text =
                            response.body()!!.data.payment_mode
                        binding.textOrderTypeNewOrderDetails.text =
                            response.body()!!.data.order_type

                       /* binding.textOrderDiscountNewOrderDetails.text =
                            response.body()!!.data.discount_rate
*/

                        binding.textOrderDiscountNewOrderDetails.visibility = View.GONE

                        binding.txtVAT.text = response.body()!!.data.vat_charges
                        binding.txtDeliveryCharges.text = response.body()!!.data.delivery_charges
                        binding.txtServiceCharges.text = response.body()!!.data.service_charges
                        binding.txtDiscount.text = response.body()!!.data.discount_rate
                        binding.txtSmallFeeCharges.text =response.body()!!.data.small_charges
                            sch_now_type = response.body()!!.data.schedule_type
                        if(sch_now_type.equals("later")) {
                            binding.schDateTime.text =
                                response.body()!!.data.schedule_date + ", " + response.body()!!.data.schedule_time
                            binding.schLl.visibility = View.VISIBLE
                        }

                        for (item in response.body()!!.data.order_item_data) {

                            val vi =
                                applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val v: View = vi.inflate(R.layout.order_items_list, null)
                            val txt_quentity =
                                v.findViewById<View>(com.xeatpos.R.id.txt_quentity) as TextView
                            val txt_amount =
                                v.findViewById<View>(com.xeatpos.R.id.txt_amount) as TextView
                            val txt_AddOns =
                                v.findViewById<View>(com.xeatpos.R.id.txt_AddOns) as TextView
                            val layoutAddOnDynamic =
                                v.findViewById<View>(com.xeatpos.R.id.layoutAddOnDynamic) as LinearLayout
                            txt_quentity.text = "" + item.count + " x " + item.menu_item_name
                            txt_amount.text = item.total_price

                            if (item.add_on_items.isNotEmpty()) {
                                txt_AddOns.visibility = View.VISIBLE
                            } else {
                                txt_AddOns.visibility = View.GONE
                            }

                            for (itemAddOn in item.add_on_items) {

                                val viAdd =
                                    applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                val vAddOn: View = viAdd.inflate(R.layout.custom_ad_on, null)
                                val txt_quentity =
                                    vAddOn.findViewById<View>(R.id.txt_quentityAdd) as TextView
                                val txt_amount =
                                    vAddOn.findViewById<View>(R.id.txt_amountAdd) as TextView
                                txt_quentity.text = itemAddOn.name
                                txt_amount.text = itemAddOn.price

                                layoutAddOnDynamic.addView(vAddOn)
                            }

                            binding.llContainerNewOrderDetails.addView(v)


                        }


                    } else {


                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseNewOrderDetails.NewOrderDetails>?,
                t: Throwable?
            ) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun AcceptRejectOrder(status: String) {
        val apiInterface =
            APIService.create().acceptrejectorder(
                "application/json", "Bearer " + prefs.token,
                intent.getStringExtra(Constants.ORDER_ID)!!, status
            )
       // Toast.makeText(this@NewOrderDetailsActivity, "api/2.0/accept_reject_order_new", Toast.LENGTH_LONG).show()


        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        if (status == "accepted") {
                            if(intent.getStringExtra(Constants.DRIVER_FROM) == "0") {

                                Toast.makeText(applicationContext,
                                    response.body()!!.message,
                                    Toast.LENGTH_SHORT).show()

                                startActivity(
                                    Intent(applicationContext, PrintOrderActivity::class.java)
                                        .putExtra("response", responseData)
                                        .putExtra("order_id", orderId)
                                        .putExtra("driver_type", driverType)
                                        .putExtra("order_type", intent.getStringExtra("order_type"))
                                        .putExtra(Constants.ORDER_TYPE, intent.getStringExtra(Constants.ORDER_TYPE))
                                        .putExtra("order_type", orderType))

                                finish()

                            }
                            else{

                                Log.i("=========driver_type", driverType);
                                startActivity(
                                    Intent(applicationContext, PrintOrderActivity::class.java)
                                        .putExtra("response", responseData)
                                        .putExtra("order_id", orderId)
                                        .putExtra(Constants.ORDER_TYPE, intent.getStringExtra(Constants.ORDER_TYPE))
                                        .putExtra("driver_type", driverType)
                                        .putExtra("order_type", intent.getStringExtra("order_type")))

                                finish()

                            }
                        } else {
                            finish()
                        }

                    } else {

                        Toast.makeText(
                            applicationContext,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()


                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                t: Throwable?
            ) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun AcceptRejectOrderAdv(status: String) {
        val apiInterface =
            APIService.create().acceptrejectorderAdvance(
                "application/json", "Bearer " + prefs.token,
                intent.getStringExtra(Constants.ORDER_ID)!!, status
            )

        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        if (status == "accepted") {
                          Log.e("resp success","Order Accepted advance")
                            finish()
                        } else {
                            finish()
                        }

                    } else {

                        Toast.makeText(
                            applicationContext,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()


                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                t: Throwable?
            ) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }
}