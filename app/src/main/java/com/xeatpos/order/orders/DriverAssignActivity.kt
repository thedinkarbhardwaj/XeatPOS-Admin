package com.xeatpos.order.orders

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeatpos.R
import com.xeatpos.activities.BaseActivity
import com.xeatpos.adapter.DriversListAdapter
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.data.ResponseDriversList
import com.xeatpos.databinding.DriverListScreenBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverAssignActivity : BaseActivity() {

    lateinit var binding: DriverListScreenBinding
    var list: MutableList<ResponseDriversList.Data>? = ArrayList()
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.driver_list_screen)

        supportActionBar?.hide()

        binding.layoutTopDrivers.imgBackArrow.setOnClickListener { finish() }

        binding.layoutTopDrivers.txtScreenName.text = getString(R.string.drivers)

        binding.swipeRefreshDrivers.setOnRefreshListener {

            progressDialog.show(this, getString(R.string.loading_drivers))
            getAllDrivers()

        }

        progressDialog.show(this, getString(R.string.loading_drivers))
        getAllDrivers()

    }

    fun getAllDrivers() {
        val apiInterface =
            APIService.create().getDrivers("application/json", "Bearer " + prefs.token,
                intent.getStringExtra("order_id")!!, intent.getStringExtra("order_type")!!)

        apiInterface.enqueue(object : Callback<ResponseDriversList.Drivers> {
            override fun onResponse(
                call: Call<ResponseDriversList.Drivers>?,
                response: Response<ResponseDriversList.Drivers>?
            ) {

                if (response?.body() != null) {

                    if (progressDialog != null) {
                        progressDialog.dialog.dismiss()
                    }

                    if (response.body()!!.status == "1") {

                        binding.swipeRefreshDrivers.isRefreshing = false

                        list!!.clear()

                        binding.textNoDrivers.visibility = View.GONE
                        binding.driversRecylerView.visibility = View.VISIBLE

                        //Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        driversList(response.body()!!.data)

                    } else {

                        binding.swipeRefreshDrivers.isRefreshing = false

                        binding.textNoDrivers.visibility = View.VISIBLE
                        binding.driversRecylerView.visibility = View.GONE

                        Toast.makeText(applicationContext,
                           response.body()!!.message,
                           Toast.LENGTH_SHORT).show()

                    }

                }else{

                    binding.textNoDrivers.visibility = View.VISIBLE
                    binding.driversRecylerView.visibility = View.GONE

                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseDriversList.Drivers>?, t: Throwable?) {
                progressDialog.dialog.dismiss()
                binding.swipeRefreshDrivers.isRefreshing = false
                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun driversList(courses: List<ResponseDriversList.Data>): List<ResponseDriversList.Data> {
        val newList = mutableListOf<ResponseDriversList.Data>()
        courses.forEach {
            list!!.add(
                ResponseDriversList.Data(
                    it.contact_num,
                    it.device_token,
                    it.device_type,
                    it.driver_name,
                    it.driver_pic,
                    it.id,
                    it.password,
                    it.res_id,
                    it.vehicle_name,
                    it.vehicle_num,
                    it.vehicle_pic
                )
            )
        }

        binding.driversRecylerView.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = DriversListAdapter(context, list!!, object : DriversListAdapter.AssignDriver {

                override fun assignDriver(position: Int) {

                    progressDialog.show(context, getString(R.string.assigning_driver))

                    AssignDriverOrder(intent.getStringExtra("order_id")!!,""+ list!![position].id)

                }

            })
            this.adapter = adapter
        }
        return newList
    }

    fun AssignDriverOrder(orderId: String, driverId:String) {
        val apiInterface =
            APIService.create().assignDriver(
                "application/json", "Bearer " + prefs.token,
                orderId, driverId
            )

        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        finish()

                    } else {

                        Toast.makeText(applicationContext,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT).show()

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