package com.xeatpos.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.xeatpos.R
import com.xeatpos.data.ResponseOrderHistoryDetails
import com.xeatpos.data.ResponseReadyOrderDetails
import com.xeatpos.databinding.OrderHistoryDetailsBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.Constants
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryItemDetailsActivity : BaseActivity(){

    private lateinit var binding: OrderHistoryDetailsBinding
    private var list: MutableList<ResponseReadyOrderDetails.ReadyOrderData>? = ArrayList()

    private val progressDialog = CustomProgressDialog()

    lateinit var orderId: String
    lateinit var orderType: String
    lateinit var phone: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.order_history_details)

        supportActionBar?.hide()

        binding.toolbarHistory.imgBackArrow.setOnClickListener {
            finish()
        }

        binding.toolbarHistory.txtScreenName.text =
            getString(R.string.order_id) + " " + intent.getStringExtra(Constants.ORDER_ID)

        progressDialog.show(this, getString(R.string.loading_orders))
        getHistoryOrderDetail()



    }

    fun getHistoryOrderDetail() {
        val apiInterface =
            APIService.create().getHistoryOrderDetails(
                "application/json", "Bearer " + prefs.token,
                intent.getStringExtra(Constants.ORDER_ID)!!
            )

        apiInterface.enqueue(object : Callback<ResponseOrderHistoryDetails.OrderHistoryDetails> {
            override fun onResponse(
                call: Call<ResponseOrderHistoryDetails.OrderHistoryDetails>?,
                response: Response<ResponseOrderHistoryDetails.OrderHistoryDetails>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()


                    if (response.body()!!.status == "1") {

                        if(response.body()!!.data.order_type == "Pickup"){
                            binding.layoutDriverHistory.visibility = View.GONE
                        }else{
                            if(response.body()!!.data.driver_type == "0"){
                                binding.layoutDriverHistory.visibility = View.GONE
                            }else {
                                binding.layoutDriverHistory.visibility = View.VISIBLE
                            }
                        }
                        Glide.with(applicationContext).load(response.body()!!.data.driver_pic)
                            .into(binding.imageDriverHistoryOrderDetails)

                        binding.textDriverNameHistoryOrderDetails.text = response.body()!!.data.driver_name
                        binding.textDriverPhoneHistoryOrderDetails.text = response.body()!!.data.driver_phone

                        binding.textRating.text = response.body()!!.data.rating


                        orderId = ""+response.body()!!.data.order_id
                        orderType = response.body()!!.data.order_type

                        binding.textOrderIDHistoryOrderDetails.text =
                            response.body()!!.data.order_id.toString()
                        binding.textOrderByHistoryOrderDetails.text = response.body()!!.data.orderby
                        binding.textOrderAtHistoryOrderDetails.text = response.body()!!.data.order_at

                        binding.textOrderTotalHistoryOrderDetails.text =
                            response.body()!!.data.total_amount
                        binding.textOrderHistoryInstructionsNewOrderDetails.text =
                            response.body()!!.data.instruction

                        binding.textOrderPaymentModeHistoryOrderDetails.text =
                            response.body()!!.data.payment_mode
                        binding.textOrderTypeHistoryOrderDetails.text =
                            response.body()!!.data.order_type

                        binding.txtVAT.text = response.body()!!.data.vat_charges
                        binding.txtDeliveryCharges.text = response.body()!!.data.delivery_charges
                        binding.txtServiceCharges.text = response.body()!!.data.service_charges
                        binding.txtDiscount.text = response.body()!!.data.discount_rate


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

                            binding.llContainerHistoryOrderDetails.addView(v)
                        }


                    } else {


                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseOrderHistoryDetails.OrderHistoryDetails>?,
                t: Throwable?
            ) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }



}