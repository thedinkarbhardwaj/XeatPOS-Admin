package com.xeatpos.order.orders

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.xeatpos.R
import com.xeatpos.activities.BaseActivity
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.data.ResponseReadyOrderDetails
import com.xeatpos.databinding.PreparingOrderdetailsListBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.Constants
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReadyOrderDetailsActivity : BaseActivity() {

    private lateinit var binding: PreparingOrderdetailsListBinding
    private var list: MutableList<ResponseReadyOrderDetails.ReadyOrderData>? = ArrayList()
    private val progressDialog = CustomProgressDialog()


    lateinit var orderId: String
    lateinit var orderType: String
    lateinit var phone: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.preparing_orderdetails_list)

        supportActionBar?.hide()

        binding.toolbarNewOrder.imgBackArrow.setOnClickListener {
            finish()
        }

        binding.toolbarNewOrder.txtScreenName.text =
            getString(R.string.order_id) + " " + intent.getStringExtra(Constants.ORDER_ID)

        progressDialog.show(this, getString(R.string.loading_orders))
        getPreparingOrderDetail()

        binding.btnCallPreparingDetails.visibility = View.GONE


        binding.btnReadyPreparingDetails.setOnClickListener {

            progressDialog.show(this, getString(R.string.loading_orders))
            OrderPickedUp("" + intent.getStringExtra("order_id"))

            //  val gson = Gson()
            //  val personString = gson.toJson(responseData)


        }


    }

    fun getPreparingOrderDetail() {
        val apiInterface =
            APIService.create().getReadyOrderDetails(
                "application/json", "Bearer " + prefs.token,
                intent.getStringExtra(Constants.ORDER_ID)!!
            )

        apiInterface.enqueue(object : Callback<ResponseReadyOrderDetails.ReadyOrderRoot> {
            override fun onResponse(
                call: Call<ResponseReadyOrderDetails.ReadyOrderRoot>?,
                response: Response<ResponseReadyOrderDetails.ReadyOrderRoot>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        if (response.body()!!.ready_order_data.order_type == "Pickup") {
                            binding.layoutDriverPreparing.visibility = View.GONE
                            binding.btnReadyPreparingDetails.text = "Picked by User"
                        } else {
                            if (response.body()!!.ready_order_data.driver_type == "0") {
                                binding.layoutDriverPreparing.visibility = View.GONE
                                binding.btnReadyPreparingDetails.text = "Picked by Driver"
                            }else {
                                binding.layoutDriverPreparing.visibility = View.VISIBLE
                                binding.btnReadyPreparingDetails.text = "Picked by Driver"
                            }
                        }
                        Glide.with(applicationContext)
                            .load(response.body()!!.ready_order_data.driver_pic)
                            .into(binding.imageDriverPreparingOrderDetails)

                        binding.textDriverNamePreparingOrderDetails.text =
                            response.body()!!.ready_order_data.driver_name
                        binding.textDriverPhonePreparingOrderDetails.text =
                            response.body()!!.ready_order_data.driver_phone

                        phone = response.body()!!.ready_order_data.phone

                        orderId = "" + response.body()!!.ready_order_data.order_id
                        orderType = response.body()!!.ready_order_data.order_type

                        binding.textOrderIDPreparingOrderDetails.text =
                            response.body()!!.ready_order_data.order_id.toString()
                        binding.textOrderByPreparingOrderDetails.text =
                            response.body()!!.ready_order_data.orderby
                        binding.textOrderAtPreparingOrderDetails.text =
                            response.body()!!.ready_order_data.order_at

                        binding.textOrderTotalPreparingOrderDetails.text =
                            response.body()!!.ready_order_data.total_amount
                        binding.textOrderPreparingInstructionsNewOrderDetails.text =
                            response.body()!!.ready_order_data.instruction

                        binding.textOrderPaymentModePreparingOrderDetails.text =
                            response.body()!!.ready_order_data.payment_mode
                        binding.textOrderTypePreparingOrderDetails.text =
                            response.body()!!.ready_order_data.order_type

                        binding.txtVAT.text = response.body()!!.ready_order_data.vat_charges
                        binding.txtDeliveryCharges.text =
                            response.body()!!.ready_order_data.delivery_charges
                        binding.txtServiceCharges.text =
                            response.body()!!.ready_order_data.service_charges
                        binding.txtDiscount.text = response.body()!!.ready_order_data.discount_rate
                        binding.smallCharges.text = response.body()!!.ready_order_data.small_charges


                        for (item in response.body()!!.ready_order_data.order_item_data) {

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

                            binding.llContainerPreparingOrderDetails.addView(v)


                        }


                    } else {


                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseReadyOrderDetails.ReadyOrderRoot>?,
                t: Throwable?
            ) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun OrderPickedUp(orderId: String) {
        val apiInterface =
            APIService.create().orderPickup(
                "application/json", "Bearer " + prefs.token,
                orderId
            )

        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        Toast.makeText(
                            applicationContext,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else if (response.body()!!.status == "2") {

                        Confirm(response.body()!!.message)

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

    private fun Confirm(message: String) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.conifrm_preparing)
        val textOrderHavePickedDialog =
            dialog.findViewById(R.id.textOrderHavePickedDialog) as TextView
        val btnOKDialog = dialog.findViewById(R.id.btnOKDialog) as Button

        textOrderHavePickedDialog.text = message

        btnOKDialog.setOnClickListener {
            dialog.dismiss()

            finish()
        }
        dialog.show()


    }

}