package com.xeatpos.order.orders

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.xeatpos.R
import com.xeatpos.activities.BaseActivity
import com.xeatpos.activities.PrintPreparingOrderActivity
import com.xeatpos.data.*
import com.xeatpos.databinding.PreparingOrderdetailsListBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.Constants
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreparingOrderDetailsActivity : BaseActivity() {

    lateinit var binding: PreparingOrderdetailsListBinding
    var list: MutableList<ResponsePreparingOrderDetails.PreparingOrderDetails>? = ArrayList()
    private val progressDialog = CustomProgressDialog()


    lateinit var orderId: String
    lateinit var orderType: String
    lateinit var phone: String
    lateinit var responseData: ResponsePreparingOrderDetails.PreparingOrderDetails


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

        binding.layoutPrintPreparing.visibility = View.VISIBLE

        binding.btnReadyPreparingDetails.setOnClickListener {

            progressDialog.show(this, getString(R.string.loading_orders))
            OrderReady("" + intent.getStringExtra("order_id"))

            //  val gson = Gson()
            //  val personString = gson.toJson(responseData)


        }

        binding.btnCallPreparingDetails.setOnClickListener {

            Dexter.withContext(applicationContext)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse("tel:" + phone)
                        applicationContext.startActivity(intent)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied) {
                            // navigate user to app settings
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()


        }

        binding.imagePrintPreparing.setOnClickListener {

            startActivity(
                Intent(applicationContext, PrintPreparingOrderActivity::class.java)
                    .putExtra("response", responseData)
                    .putExtra("order_id", orderId)
                    .putExtra("order_type", orderType)

            )

        }
    }

    fun getPreparingOrderDetail() {
        val apiInterface =
            APIService.create().getPreparingOrderDetails(
                "application/json", "Bearer " + prefs.token,
                intent.getStringExtra(Constants.ORDER_ID)!!
            )

        apiInterface.enqueue(object : Callback<ResponsePreparingOrderDetails.PreparingOrders> {
            override fun onResponse(
                call: Call<ResponsePreparingOrderDetails.PreparingOrders>?,
                response: Response<ResponsePreparingOrderDetails.PreparingOrders>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()


                    if (response.body()!!.status == "1") {

                        responseData = response.body()!!.preparing_order_details

                        if (response.body()!!.preparing_order_details.order_type == "Pickup") {
                            binding.layoutDriverPreparing.visibility = View.GONE
                        } else {
                            if (response.body()!!.preparing_order_details.driver_type == "0") {
                                binding.layoutDriverPreparing.visibility = View.GONE
                            }else {
                                binding.layoutDriverPreparing.visibility = View.VISIBLE
                            }
                        }
                        Glide.with(applicationContext)
                            .load(response.body()!!.preparing_order_details.driver_pic)
                            .into(binding.imageDriverPreparingOrderDetails)

                        binding.textDriverNamePreparingOrderDetails.text =
                            response.body()!!.preparing_order_details.driver_name
                        binding.textDriverPhonePreparingOrderDetails.text =
                            response.body()!!.preparing_order_details.driver_phone

                        phone = response.body()!!.preparing_order_details.phone

                        orderId = "" + response.body()!!.preparing_order_details.order_id
                        orderType = response.body()!!.preparing_order_details.order_type

                        binding.textOrderIDPreparingOrderDetails.text =
                            response.body()!!.preparing_order_details.order_id.toString()
                        binding.textOrderByPreparingOrderDetails.text =
                            response.body()!!.preparing_order_details.orderby
                        binding.textOrderAtPreparingOrderDetails.text =
                            response.body()!!.preparing_order_details.order_at

                        binding.textOrderTotalPreparingOrderDetails.text =
                            response.body()!!.preparing_order_details.total_amount
                        binding.textOrderPreparingInstructionsNewOrderDetails.text =
                            response.body()!!.preparing_order_details.instruction

                        binding.textOrderPaymentModePreparingOrderDetails.text =
                            response.body()!!.preparing_order_details.payment_mode
                        binding.textOrderTypePreparingOrderDetails.text =
                            response.body()!!.preparing_order_details.order_type

                        binding.txtVAT.text = response.body()!!.preparing_order_details.vat_charges
                        binding.txtDeliveryCharges.text =
                            response.body()!!.preparing_order_details.delivery_charges
                        binding.txtServiceCharges.text =
                            response.body()!!.preparing_order_details.service_charges
                        binding.txtDiscount.text =
                            response.body()!!.preparing_order_details.discount_rate
                        binding.smallCharges.text =
                            response.body()!!.preparing_order_details.small_charges
                        for (item in response.body()!!.preparing_order_details.order_item_data) {

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
                call: Call<ResponsePreparingOrderDetails.PreparingOrders>?,
                t: Throwable?
            ) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun OrderReady(orderId: String) {
        val apiInterface =
            APIService.create().orderReady(
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

                    } /*else if (response.body()!!.status == "2") {
                        Confirm(response.body()!!.message)
                    }*/ else {

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

    /*  private fun Confirm(message:String ) {

              val dialog = Dialog(this)
              dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
              dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

              dialog.setCancelable(false)
              dialog.setContentView(R.layout.conifrm_preparing)
              val textOrderHavePickedDialog = dialog.findViewById(R.id.textOrderHavePickedDialog) as TextView
              val btnOKDialog = dialog.findViewById(R.id.btnOKDialog) as Button

              textOrderHavePickedDialog.text = message

              btnOKDialog.setOnClickListener {
                  dialog.dismiss()

                  finish()
              }
              dialog.show()



      }*/


}