package com.xeatpos.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.xeatpos.R
import com.xeatpos.activities.BaseActivity
import com.xeatpos.data.ResponseMenuDetailsModel
import com.xeatpos.databinding.FragmentFoodItemDetailBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.view.View.GONE
import android.view.View.VISIBLE

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.*
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.utils.CustomProgressDialog


class MenuDetailActivity : BaseActivity() {

    lateinit var binding : FragmentFoodItemDetailBinding

    private val progressDialog = CustomProgressDialog()

    var itemPrice : String = ""
    var itemDiscount : String = ""
    var itemDiscountType : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, com.xeatpos.R.layout.fragment_food_item_detail)

        supportActionBar?.hide()

        binding.toolbar.imgBackArrow.setOnClickListener {
            finish()
        }

        binding.toolbar.txtScreenName.text = intent.getStringExtra(Constants.MENU_NAME)

        if(prefs.type == "1"){
            binding.imageEditGroceryPrice.visibility = View.VISIBLE
        }else{
            binding.imageEditGroceryPrice.visibility = View.GONE
        }

        progressDialog.show(this, "Please Wait...")

        val apiInterface = APIService.create().menuDetails("application/json",
            "Bearer " + prefs.token, intent.getStringExtra(Constants.MENU_ID).toString()
        )

        apiInterface.enqueue(object : Callback<MenuSizesModel.MenuDetails> {
            override fun onResponse(call: Call<MenuSizesModel.MenuDetails>?, response: Response<MenuSizesModel.MenuDetails>?) {

                if(response?.body() != null){

                 //   progressDialog.dialog.dismiss()

                    if(response.body()!!.status == "1"){

                        if(response.body()!!.data.image == ""){
                            binding.imageMenuDetails.visibility = GONE
                        }else{
                            Glide.with(applicationContext).load(response.body()!!.data.image).into(binding.imageMenuDetails)
                            binding.imageMenuDetails.visibility = VISIBLE
                        }

                        itemPrice = response.body()!!.data.f_price
                        itemDiscount = response.body()!!.data.discount
                        itemDiscountType = response.body()!!.data.discount_type
                        binding.textMenuNameDetails.text = response.body()!!.data.name
                        binding.textMenuPriceDetails.text = response.body()!!.data.f_price
                        binding.textMenuIngrediantsDetails.text = response.body()!!.data.ingredients

                        binding.textMenuDiscountPriceDetails.text = "Discount: "+response.body()!!.data.discount

                        if(prefs.type == "1"){
                            binding.textMenuDiscountPriceDetails.visibility = VISIBLE
                        }else{
                            binding.textMenuDiscountPriceDetails.visibility = GONE
                        }

                        binding.switchMenusDetails.isChecked = response.body()!!.data.status == "1"

                        addSizes(response.body()!!.data.menu_sizes)

                    }else{

                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT).show()

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<MenuSizesModel.MenuDetails>?, t: Throwable?) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT).show()

                Log.i("Exception: ",t.toString())

            }
        })


        binding.imageEditGroceryPrice.setOnClickListener {

            EditGroceryPrice(itemPrice, itemDiscount)

        }

    }

  /*  fun menuList(courses: List<ResponseMenuDetailsModel.Sizes>) : List<ResponseMenuDetailsModel.Sizes> {
        val newList = mutableListOf<ResponseMenuDetailsModel.Sizes>()

        binding.layoutDynamicAddOns.apply {

            courses?.forEach { n ->
                val view: View = layoutInflater.inflate(R.layout.custom_addons, null)
                var textMenuAddOnName = view.findViewById<TextView>(R.id.textMenuAddOnName)
                var textMenuAddOnsPrice = view.findViewById<TextView>(R.id.textMenuAddOnsPrice)
                textMenuAddOnName.text = n.size_fulltext
                textMenuAddOnsPrice.text = n.item_price

                this.addView(view)

            }
        }

        return newList
    }

    fun adonOuterListRoot(courses: List<ResponseMenuDetailsModel.AddOnTypes>) : List<ResponseMenuDetailsModel.AddOnTypes> {
        val newList = mutableListOf<ResponseMenuDetailsModel.AddOnTypes>()


        binding.layoutDynamicAddOnsDynmc.apply {

            courses?.forEach { n ->



                adonOuterList(n.name, n.add_on);

            }
        }

        return newList
    }


    fun adonOuterList(courses1: String, courses: List<ResponseMenuDetailsModel.AddOn>)
    : List<ResponseMenuDetailsModel.AddOn> {
        val newList = mutableListOf<ResponseMenuDetailsModel.AddOn>()

        binding.textAllAdons.text = courses1;

        binding.layoutDynamicAddOnsInside.apply {

            courses?.forEach { n ->

                adonInnerList(n.name, n.add_on_size);

            }
        }

        return newList
    }

    fun adonInnerList(name: String,courses: List<ResponseMenuDetailsModel.AddOnSize>)
    : List<ResponseMenuDetailsModel.AddOnSize> {
        val newList = mutableListOf<ResponseMenuDetailsModel.AddOnSize>()


        binding.layoutDynamicAddOnsInside.apply {

            courses?.forEach { n ->
                val view: View = layoutInflater.inflate(R.layout.custom_adoninside, null)
                var textMenuAddOnName = view.findViewById<TextView>(R.id.textMenuAddOnName)
                var textMenuAddOnsPrice = view.findViewById<TextView>(R.id.textMenuAddOnsPrice)
                textMenuAddOnName.text = name+"("+n.item_size+")"
                textMenuAddOnsPrice.text = n.item_price


                this.addView(view)

            }
        }

        return newList
    }*/

    fun addSizes(courses: List<MenuSizesModel.MenuSizes>)
            : List<MenuSizesModel.MenuSizes> {
        val newList = mutableListOf<MenuSizesModel.MenuSizes>()
        binding.layoutSizes.apply {

            courses.forEachIndexed  { index, n ->
                val view: View = layoutInflater.inflate(R.layout.custom_sizes, null)
                var layoutRootSizes = view.findViewById<LinearLayout>(R.id.layoutRootSizes)
                var textSizeName = view.findViewById<TextView>(R.id.textSizeName)
                var textSizePrice = view.findViewById<TextView>(R.id.textSizePrice)
                textSizeName.text = n.menu_full_size
                textSizePrice.text = n.menu_size_price
                view.tag = ""+ index+" "+n.menu_size

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10,0,0,0)
                view.layoutParams = params

               // val v = binding.layoutRootSizes.getChildAt(0)
                if(index == 0) {
                    layoutRootSizes.setBackgroundResource(R.drawable.tab_filled)
                    getVarients(intent.getStringExtra(Constants.MENU_ID).toString(), n.menu_size)

                }else{
                    layoutRootSizes.setBackgroundResource(R.drawable.tab_bg)
                }

                view.setOnClickListener(View.OnClickListener {

                    val data = ""+view.tag;
                    val position = data.subSequence(0, data.indexOf(" "))
                    val size = data.subSequence( data.indexOf(" "),data.length)
                    val finalPosition = position.toString()
                    val newPosition = Integer.parseInt(finalPosition)

                    val count = this.childCount

                    for (i in 0 until count) {

                        val v = binding.layoutSizes.getChildAt(i)
                        v.setBackgroundResource(R.drawable.tab_bg)
                        //textSizeName.setTextColor(Color.BLACK)

                    }

                    layoutRootSizes.setBackgroundResource(R.drawable.tab_filled)

                    progressDialog.show(context, "Please Wait...")

                    getVarients(intent.getStringExtra(Constants.MENU_ID).toString(), size.toString())

                    // textSizeName.setTextColor(Color.WHITE)

                })
                this.addView(view)
            }
        }

        return newList
    }

    fun getVarients(itemId: String, size: String) {
        val apiInterface = APIService.create().menuSizesVaients(
            "application/json",
            "Bearer " + prefs.token, itemId, size
        )

        apiInterface.enqueue(object : Callback<SizeDetailsModel.SizeDetails> {
            override fun onResponse(
                call: Call<SizeDetailsModel.SizeDetails>?,
                response: Response<SizeDetailsModel.SizeDetails>?
            ) {
                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {


                        addInnerSizes(response.body()!!.data)



                    } else {
                      //  Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT)
                          //  .show()

                    }

                } else {

                   Toast.makeText(applicationContext, response?.body()!!.message, Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(
                call: Call<SizeDetailsModel.SizeDetails>?,
                t: Throwable?
            ) {

            }
        })
    }

    fun addInnerSizes(courses: List<SizeDetailsModel.Data>)
            : List<ResponseMenuDetailsModel.AddOnSize> {
        val newList = mutableListOf<ResponseMenuDetailsModel.AddOnSize>()

        binding.layoutDynamicAddOnsDynmc.removeAllViews()

        binding.layoutDynamicAddOnsDynmc.apply {

            courses?.forEach { n ->
                val view: View = layoutInflater.inflate(R.layout.custom_varients, null)
                var textAllAdons = view.findViewById<TextView>(R.id.textAllAdons)
                var layoutDynamicAddOnsInside = view.findViewById<LinearLayout>(R.id.layoutDynamicAddOnsInside)
                textAllAdons.text = n.title
                layoutDynamicAddOnsInside.removeAllViews()
                addSizes(n.selection,layoutDynamicAddOnsInside)


                this.addView(view)

            }
        }

        return newList
    }

    fun addSizes(courses: List<SizeDetailsModel.Selection>, layoutDynamicAddOnsInside : LinearLayout)
            : List<SizeDetailsModel.Selection> {
        val newList = mutableListOf<SizeDetailsModel.Selection>()

        layoutDynamicAddOnsInside.removeAllViews()

        layoutDynamicAddOnsInside.apply {

            courses?.forEach { n ->
                val view: View = layoutInflater.inflate(R.layout.custom_adoninside, null)
                var textMenuAddOnName = view.findViewById<TextView>(R.id.textMenuAddOnName)
                var textMenuAddOnsPrice = view.findViewById<TextView>(R.id.textMenuAddOnsPrice)
                textMenuAddOnName.text = n.name+"("+n.variant_size+")"
                textMenuAddOnsPrice.text = n.variant_price


                this.addView(view)

            }
        }

        return newList
    }

    private fun EditGroceryPrice(price : String, discount : String) {

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.edit_grocery_price_dialog)
            val editPriceDialog = dialog.findViewById(R.id.editPriceDialog) as EditText
            val editDiscountDialog = dialog.findViewById(R.id.editDiscountDialog) as EditText
            val btnCancelDialogEditPrice = dialog.findViewById(R.id.btnCancelDialogEditPrice) as Button
            val btnUpdateDialogEditPrice = dialog.findViewById(R.id.btnUpdateDialogEditPrice) as Button
            val textDiscountTitle = dialog.findViewById(R.id.textDiscountTitle) as TextView

             editPriceDialog.setText(price.replace("£",""))
             editDiscountDialog.setText(discount.replace("%",""))
             editDiscountDialog.setText(discount.replace("£",""))
             textDiscountTitle.setText("Discount("+itemDiscountType+")")

            btnCancelDialogEditPrice.setOnClickListener {
                dialog.dismiss()
            }
            btnUpdateDialogEditPrice.setOnClickListener {

                if(editPriceDialog.text.trim().toString() == ""){
                    Toast.makeText(this@MenuDetailActivity,"Enter price for update.", Toast.LENGTH_SHORT)
                }else if(editDiscountDialog.text.trim().toString() == ""){
                    Toast.makeText(this@MenuDetailActivity,"Enter discount for update.", Toast.LENGTH_SHORT)

                }
                else {
                    dialog.dismiss()
                    progressDialog.show(this, "Please Wait...")
                    EditPriceAPI(
                        intent.getStringExtra(Constants.MENU_ID)!!,
                        editPriceDialog.text.toString(), editDiscountDialog.text.toString()
                    )
                }

            }
            dialog.show()



    }


    fun EditPriceAPI(grocery_id:String, price : String, discount : String) {
        val apiInterface =
            APIService.create().updateGroceryPrice(
                "application/json", "Bearer " + prefs.token, grocery_id, price, discount
            )

        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        startActivity(Intent(this@MenuDetailActivity, MenuDetailActivity::class.java)
                            .putExtra(Constants.MENU_ID, intent.getStringExtra(Constants.MENU_ID)))
                        finish()

                        // Toast.makeText(context, response?.body()!!.message, Toast.LENGTH_SHORT).show()
                    } else {

                        Toast.makeText(this@MenuDetailActivity, response?.body()!!.message, Toast.LENGTH_SHORT).show()

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                t: Throwable?
            ) {
                // Toast.makeText(context, "Exception: " + t.toString(), Toast.LENGTH_SHORT)
                //  .show()

                // Log.i("Exception: ", t.toString())

            }
        })

    }
}