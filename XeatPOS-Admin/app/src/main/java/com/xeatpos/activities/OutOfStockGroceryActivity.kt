package com.xeatpos.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.xeatpos.R
import com.xeatpos.adapter.MenuAdapter
import com.xeatpos.data.ResponseEnableDisableMenus
import com.xeatpos.data.ResponseModel
import com.xeatpos.databinding.OutofstockScreenBinding
import com.xeatpos.home.MenuDetailActivity
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.Constants
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OutOfStockGroceryActivity   : BaseActivity() {

    private lateinit var binding: OutofstockScreenBinding

    var list: MutableList<ResponseModel.Data>? = ArrayList()
    private val progressDialog = CustomProgressDialog()
    val temp: MutableList<ResponseModel.Data> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.outofstock_screen)

        supportActionBar?.hide()

        binding.layoutTopMenus.imgBackArrow.setOnClickListener {
            finish()
        }

        binding.layoutTopMenus.txtScreenName.text =
            "Out of stock grocery"

        // progressDialog.show(this, getString(R.string.loading_orders))
        binding.progressOutOfStock.visibility = View.GONE

         progressDialog.show(this, "Please Wait...")
        getMenus()

        binding.swipeToRefreshMenus.setOnRefreshListener {
            // context?.let { progressDialog.show(it, "Please Wait...") }
            getMenus()
        }
        init()

    }

    fun getMenus() {
        val apiInterface = APIService.create().getGroceryOutOfStock("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponseModel.Menus> {
            override fun onResponse(
                call: Call<ResponseModel.Menus>?,
                response: Response<ResponseModel.Menus>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        binding.etSearch.setText("")
                      /*  if (applicationContext != null){
                            val inputMethodManager =
                                applicationContext?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                        }*/
                        binding.swipeToRefreshMenus.isRefreshing = false

                        list!!.clear()

                        binding.textNoMenuFound.visibility = View.GONE
                        binding.menuRecycler.visibility = View.VISIBLE

                        //   Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        menuList(response.body()!!.data)

                    } else {

                        binding.textNoMenuFound.visibility = View.VISIBLE
                        binding.menuRecycler.visibility = View.GONE

                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()

                    }
                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseModel.Menus>?, t: Throwable?) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT).show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun menuList(courses: List<ResponseModel.Data>): List<ResponseModel.Data> {
        val newList = mutableListOf<ResponseModel.Data>()
        courses.forEach {
            Log.i("======", it.name);
            list!!.add(
                ResponseModel.Data(
                    it.id, it.r_id,
                    it.name,
                    it.franch_name,
                    it.type,
                    it.image,
                    it.f_price,
                    it.category,
                    it.status,
                    it.ingredients
                )
            )
        }

        binding.menuRecycler.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = MenuAdapter(context, list!!, object : MenuAdapter.SwitchListener {

                override fun switchClick(position: Int, status: String) {
                    confirmEnableDisableMenu(
                        position,
                        list!![position].image,
                        list!![position].name,
                        list!![position].f_price,
                        list!![position].ingredients
                    )
                    /* Toast.makeText(
                         context,
                         "Position: " + position + " Status: " + status,
                         Toast.LENGTH_SHORT
                     ).show()*/
                }
            }, object : MenuAdapter.ItemClickListener {
                override fun itemClick(position: Int) {

                    startActivity(
                        Intent(context, MenuDetailActivity::class.java)
                            .putExtra(Constants.MENU_NAME, list!![position].name)
                            .putExtra(Constants.MENU_ID, "" + list!![position].id)
                    )

                }

            })
            this.adapter = adapter
        }
        return newList
    }

    private fun confirmEnableDisableMenu(
        position: Int,
        image: String, name: String, price: String, ingrediants: String
    ) {
        this?.let {
            val dialog = Dialog(it)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.confirm_switch_dialog)
            val img_dishMenuDialog = dialog.findViewById(R.id.img_dishMenuDialog) as ImageView
            val switchMenusDialog = dialog.findViewById(R.id.switchMenusDialog) as SwitchCompat
            val textMenuNameDialog = dialog.findViewById(R.id.textMenuNameDialog) as TextView
            val textMenuPriceDialog = dialog.findViewById(R.id.textMenuPriceDialog) as TextView
            val textIngrediantsDialog = dialog.findViewById(R.id.textIngrediantsDialog) as TextView
            val btnCancelMenuDialog = dialog.findViewById(R.id.btnCancelMenuDialog) as Button
            val btnSaveMenuDialog = dialog.findViewById(R.id.btnSaveMenuDialog) as Button

            Glide.with(this).load(image).into(img_dishMenuDialog);

            switchMenusDialog.isChecked = list!![position].status == "1"
            textMenuNameDialog.text = name
            textMenuPriceDialog.text = price
            textIngrediantsDialog.text = ingrediants

            btnCancelMenuDialog.setOnClickListener {
                dialog.dismiss()
            }
            btnSaveMenuDialog.setOnClickListener {
                dialog.dismiss()

                this?.let { progressDialog.show(this, "Please Wait...") }
                val status: String
                if (switchMenusDialog.isChecked) {
                    status = "1"
                } else {
                    status = "2"
                }

                changeStatusMenu(position, list!![position].id.toString(), status)
            }
            dialog.show()
        }


    }

    private fun confirmEnableDisableMenuFilter(
        position: Int,
        image: String, name: String, price: String, ingrediants: String
    ) {
        this?.let {
            val dialog = Dialog(it)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.confirm_switch_dialog)
            val img_dishMenuDialog = dialog.findViewById(R.id.img_dishMenuDialog) as ImageView
            val switchMenusDialog = dialog.findViewById(R.id.switchMenusDialog) as SwitchCompat
            val textMenuNameDialog = dialog.findViewById(R.id.textMenuNameDialog) as TextView
            val textMenuPriceDialog = dialog.findViewById(R.id.textMenuPriceDialog) as TextView
            val textIngrediantsDialog = dialog.findViewById(R.id.textIngrediantsDialog) as TextView
            val btnCancelMenuDialog = dialog.findViewById(R.id.btnCancelMenuDialog) as Button
            val btnSaveMenuDialog = dialog.findViewById(R.id.btnSaveMenuDialog) as Button

            Glide.with(this).load(image).into(img_dishMenuDialog);

            switchMenusDialog.isChecked = list!![position].status == "1"
            textMenuNameDialog.text = name
            textMenuPriceDialog.text = price
            textIngrediantsDialog.text = ingrediants

            btnCancelMenuDialog.setOnClickListener {
                dialog.dismiss()
            }

            btnSaveMenuDialog.setOnClickListener {
                dialog.dismiss()

                this?.let { progressDialog.show(this, "Please Wait...") }
                val status: String
                if (switchMenusDialog.isChecked) {
                    status = "1"
                } else {
                    status = "2"
                }

                changeStatusMenuFilter(position, temp!![position].id.toString(), status)
            }
            dialog.show()
        }


    }

    fun changeStatusMenu(position: Int, itemId: String, status: String) {
        val apiInterface = APIService.create().changeStatusMenu(
            "application/json",
            "Bearer " + prefs.token, itemId, status
        )

        apiInterface.enqueue(object : Callback<ResponseEnableDisableMenus.EnableDisable> {
            override fun onResponse(
                call: Call<ResponseEnableDisableMenus.EnableDisable>?,
                response: Response<ResponseEnableDisableMenus.EnableDisable>?
            ) {
                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()

                        list!![position].status = status

                        binding.menuRecycler.adapter?.notifyDataSetChanged()

                        startActivity(Intent(applicationContext, OutOfStockGroceryActivity::class.java))
                        finish()



                    } else {
                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()

                    }

                } else {

                    Toast.makeText(applicationContext, response?.body()!!.message, Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(
                call: Call<ResponseEnableDisableMenus.EnableDisable>?,
                t: Throwable?
            ) {

            }
        })
    }

    fun changeStatusMenuFilter(position: Int, itemId: String, status: String) {
        val apiInterface = APIService.create().changeStatusMenu(
            "application/json",
            "Bearer " + prefs.token, itemId, status
        )

        apiInterface.enqueue(object : Callback<ResponseEnableDisableMenus.EnableDisable> {
            override fun onResponse(
                call: Call<ResponseEnableDisableMenus.EnableDisable>?,
                response: Response<ResponseEnableDisableMenus.EnableDisable>?
            ) {
                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()


                        temp!![position].status = status

                        binding.menuRecycler.adapter?.notifyDataSetChanged()


                        startActivity(Intent(applicationContext, OutOfStockGroceryActivity::class.java))
                        finish()



                    } else {

                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()

                    }

                } else {

                    Toast.makeText(applicationContext, response?.body()!!.message, Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(
                call: Call<ResponseEnableDisableMenus.EnableDisable>?,
                t: Throwable?
            ) {

            }
        })
    }

    fun init() {

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                // temp: MutableList<ResponseModel.Data> = ArrayList()
                temp.clear()


                var text = p0.toString().trim()
                if (text!!.length > 0) {

                    for (d in list!!) {
                        var strName = d.name
                        if (strName!!.contains(text, ignoreCase = true)
                        ) {
                            temp.add(d)
                        }
                    }

                    if (temp.size > 0) {
                        binding.menuRecycler.visibility = View.VISIBLE
                        binding.textNoMenuFound.visibility = View.GONE
                        val adapter = MenuAdapter(
                            applicationContext,
                            temp,
                            object : MenuAdapter.SwitchListener {

                                override fun switchClick(position: Int, status: String) {
                                    confirmEnableDisableMenuFilter(
                                        position,
                                        temp!![position].image,
                                        temp!![position].name,
                                        temp!![position].f_price,
                                        temp!![position].ingredients
                                    )
                                }
                            },
                            object : MenuAdapter.ItemClickListener {
                                override fun itemClick(position: Int) {

                                    startActivity(
                                        Intent(applicationContext, MenuDetailActivity::class.java)
                                            .putExtra(Constants.MENU_NAME, temp!![position].name)
                                            .putExtra(Constants.MENU_ID, "" + temp!![position].id)
                                    )

                                }

                            })
                        binding.menuRecycler.adapter = adapter

                    } else {
//                        context?.toast("No record found!!!")
                        binding.menuRecycler.visibility = View.GONE
                        binding.textNoMenuFound.visibility = View.VISIBLE
                    }

                } else {
                    binding.menuRecycler.visibility = View.VISIBLE
                    binding.textNoMenuFound.visibility = View.GONE
                    try {
                        val adapter = MenuAdapter(
                            applicationContext,
                            list!!,
                            object : MenuAdapter.SwitchListener {

                                override fun switchClick(position: Int, status: String) {
                                    confirmEnableDisableMenu(
                                        position,
                                        list!![position].image,
                                        list!![position].name,
                                        list!![position].f_price,
                                        list!![position].ingredients
                                    )
                                }
                            },
                            object : MenuAdapter.ItemClickListener {
                                override fun itemClick(position: Int) {

                                    startActivity(
                                        Intent(applicationContext, MenuDetailActivity::class.java)
                                            .putExtra(Constants.MENU_NAME, list!![position].name)
                                            .putExtra(Constants.MENU_ID, "" + list!![position].id)
                                    )

                                }

                            })
                        binding.menuRecycler.adapter = adapter

                    }catch (e : IllegalStateException){
                        e.printStackTrace()
                    }

                }
            }
        })

    }
}