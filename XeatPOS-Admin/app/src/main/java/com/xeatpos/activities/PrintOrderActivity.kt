package com.xeatpos.activities

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.xeatpos.R
import com.xeatpos.data.ResponseNewOrderDetails
import com.xeatpos.databinding.ActivityPosprintBinding
import com.xeatpos.order.orders.DriverAssignActivity
import com.xeatpos.sunmi.SunmiPrintHelper
import com.xeatpos.utils.Constants
import com.xeatpos.utils.CustomProgressDialog


class PrintOrderActivity : BaseActivity() {

    lateinit var binding: ActivityPosprintBinding

    lateinit var response: ResponseNewOrderDetails.Data

    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_posprint)

        SunmiPrintHelper.getInstance().initSunmiPrinterService(this)

        val typeface = ResourcesCompat.getFont(this, R.font.test)

        if (intent != null) {

            val responseBefore = intent.getSerializableExtra("response")
            response = responseBefore as ResponseNewOrderDetails.Data
            // val gson = Gson()
            //response = gson.fromJson(responseBefore, ResponseNewOrderDetails.Data::class.java)
            //val jsonObj = JSONObject(json)

            // Log.i("===========", responseBefore.toString())

            binding.txtResturantName.text = response.restaurant_name
            binding.txtOrderBy.text = getString(R.string.order_by) + "" + response.orderby
            binding.txtOrderId.text = getString(R.string.order_id) + "" + response.order_id
            binding.txtOrderDate.text = response.order_at
            binding.txtTotalAmount.text = response.total_amount
            binding.txtOrderType.text = response.order_type
            binding.txtPaymentMode.text = response.payment_mode
            binding.txtVAT.text = response.vat_charges
            binding.txtDeliveryCharges.text = response.delivery_charges
            binding.txtServiceCharges.text = response.service_charges
            binding.txtDiscount.text = response.discount_rate
            binding.txtInstructionSlip.text = response.instruction
            binding.txtUserAddress.text = "Address: " + response.address
            binding.txtUserPhone.text = "Phone: " + response.phone

            //  Glide.with(this).load(response.website_qrcode).into(binding.imgQR)


            for (item in response.order_item_data) {

                val vi =
                    applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val v: View = vi.inflate(R.layout.print_order_item_list, null)
                val txt_quentity =
                    v.findViewById<View>(com.xeatpos.R.id.txt_quentityPrint) as TextView
                val txt_amount = v.findViewById<View>(com.xeatpos.R.id.txt_amountPrint) as TextView
                val txt_AddOns = v.findViewById<View>(com.xeatpos.R.id.txt_AddOnsPrint) as TextView
                val layoutAddOnDynamic =
                    v.findViewById<View>(com.xeatpos.R.id.layoutAddOnDynamicPrint) as LinearLayout
                txt_quentity.text = "" + item.count + "x" + item.menu_item_name
                txt_amount.text = item.total_price
                //  txt_amount.visibility = View.GONE

                txt_quentity.setTypeface(txt_AddOns.typeface, Typeface.BOLD);
                txt_amount.setTypeface(txt_amount.typeface, Typeface.BOLD);
                txt_AddOns.setTypeface(txt_AddOns.typeface, Typeface.BOLD);

                if (item.add_on_items.isNotEmpty()) {
                    txt_AddOns.visibility = View.VISIBLE
                } else {
                    txt_AddOns.visibility = View.GONE
                }

                for (itemAddOn in item.add_on_items) {

                    val viAdd =
                        applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val vAddOn: View = viAdd.inflate(R.layout.print_adon_item, null)
                    val txt_quentity =
                        vAddOn.findViewById<View>(R.id.txt_quentityAddPrint) as TextView
                    val txt_amount = vAddOn.findViewById<View>(R.id.txt_amountAddPrint) as TextView
                    txt_quentity.text = itemAddOn.name
                    txt_amount.text = itemAddOn.price
                    // txt_amount.visibility = View.GONE

                    txt_quentity.typeface = typeface
                    txt_amount.typeface = typeface
                    txt_quentity.setTypeface(txt_quentity.typeface, Typeface.BOLD);
                    txt_amount.setTypeface(txt_amount.typeface, Typeface.BOLD);

                    layoutAddOnDynamic.addView(vAddOn)
                }

                binding.llContainer.addView(v)


            }

        }

        // binding.ll.visibility = View.VISIBLE
        //  binding.imageBitmap.visibility = View.GONE


        binding.llContainer.postDelayed(Runnable {
/*
            var bitmap = getBitmapFromView(binding.ll)

            binding.ll.visibility = View.GONE
            binding.imageBitmap.visibility = View.VISIBLE

            Glide.with(this).load(bitmap).into(binding.imageBitmap)*/


            // var bitmap = getBitmapFromView(binding.ll)

            val bitmap: Bitmap =
                loadBitmapFromView(binding.ll, binding.ll.width, binding.ll.getHeight())!!

            val finalBitmap =
                Bitmap.createBitmap(bitmap.width, bitmap.height * 2, Bitmap.Config.ARGB_8888)


            val canvas = Canvas(finalBitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.drawBitmap(bitmap, 0f, bitmap.height.toFloat(), null)

            Glide.with(this).load(bitmap).into(binding.imageBitmap)

            SunmiPrintHelper.getInstance().printBitmap(bitmap, 1)
            SunmiPrintHelper.getInstance().feedPaper()
        },
            2000)
            //1000)

        binding.llContainer.postDelayed(Runnable {

            if (intent.getStringExtra("order_type") == "Pickup") {
                Log.i("=======", "Back To New Orders List")
                finish()
            } /*else if (intent.getStringExtra("order_from") == "0") {
                Log.i("=======", "Back To New Orders List")
                finish()
            }*/ else {
                Log.i("=======", "Go To Driver List")
                Log.i("=======Order Id", "" + intent.getStringExtra("order_id"))
                Log.i("=======Order Type", "" + "" + intent.getStringExtra("order_type"))
                /* startActivity(Intent(applicationContext, DriverAssignActivity::class.java)
                          .putExtra("order_type", intent.getStringExtra("order_type"))
                         .putExtra("order_id", intent.getStringExtra("order_id"))*/
             //   if (intent.getStringExtra("order_from") == "1") {
                if(intent.getStringExtra(Constants.ORDER_TYPE) == "Pickup"){
                    finish()
                }else {
                    if(intent.getStringExtra("order_type") == "0"){
                        finish()
                    }
                    else {
                        startActivity(
                            Intent(applicationContext, DriverAssignActivity::class.java)
                                .putExtra("order_id", "" + intent.getStringExtra("order_id"))
                                .putExtra(
                                    Constants.ORDER_TYPE,
                                    "" + intent.getStringExtra(Constants.ORDER_TYPE)
                                )
                                .putExtra("order_type", intent.getStringExtra("order_type"))
                        )
                        finish()
                    }
                }
              /*  } else {
                    Log.i("=======", "Request from others")
                    finish()
                }*/
            }

        }, 2000)
      //  }, 2000)

    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap? {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)
        return b
    }


    private fun getBitmapFromView(view: View): Bitmap? {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background

        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        // draw the view on the canvas
        view.draw(canvas)


        //return the bitmap
        return returnedBitmap
    }


}