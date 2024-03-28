package com.xeatpos.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeatpos.R
import com.xeatpos.adapter.PrepairingOrderAdapter
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.data.ResponsePreparingOrder
import com.xeatpos.databinding.FragmentPreparingOrderBinding
import com.xeatpos.order.orders.PreparingOrderDetailsActivity
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PreparingOrderFragment : Fragment() {
    lateinit var binding: FragmentPreparingOrderBinding
    var list: MutableList<ResponsePreparingOrder.PreparingOrder>? = ArrayList()
    private val progressDialog = CustomProgressDialog()
    private var flag : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_preparing_order, container, false)
       // init()

        binding.swipeRefreshPreparingOrder.setOnRefreshListener {

           // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
            binding.progressReady.visibility = View.VISIBLE
            getAllPreparingOrders()

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(flag == false) {
           // if (progressDialog != null) {
               // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
                binding.progressReady.visibility = View.VISIBLE
                getAllPreparingOrders()
           // }
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
           // if(progressDialog != null) {
            try {
                flag = true
                binding.progressReady.visibility = View.VISIBLE
                // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
                getAllPreparingOrders()
            }catch (e: Exception){
                e.printStackTrace()
            }
          //  }
        }
    }

    fun getAllPreparingOrders() {
        val apiInterface =
            APIService.create().getPreparingOrders("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponsePreparingOrder.Preparing> {
            override fun onResponse(
                call: Call<ResponsePreparingOrder.Preparing>?,
                response: Response<ResponsePreparingOrder.Preparing>?
            ) {

                if (response?.body() != null) {

                    binding.progressReady.visibility = View.GONE
                  /*  if(progressDialog != null) {
                        progressDialog.dialog.dismiss()

                    }*/
                    if (response.body()!!.status == "1") {

                        binding.swipeRefreshPreparingOrder.isRefreshing = false

                        list!!.clear()

                        binding.textNoPreparingOrder.visibility = View.GONE
                        binding.prepairingRecylerView.visibility = View.VISIBLE

                        // Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        preparingOrderList(response.body()!!.preparing_order)


                    }  else  if (response.body()!!.status == "9") {

                        try {
                            Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT)
                                .show()
                        }catch (e : NullPointerException){
                            e.printStackTrace()
                        }

                    }else {

                        //Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        binding.swipeRefreshPreparingOrder.isRefreshing = false

                        binding.textNoPreparingOrder.visibility = View.VISIBLE
                        binding.prepairingRecylerView.visibility = View.GONE

                        /*  Toast.makeText(
                              context,
                              response.body()!!.message,
                              Toast.LENGTH_SHORT
                          ).show()*/

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponsePreparingOrder.Preparing>?, t: Throwable?) {
               // progressDialog.dialog.dismiss()
                binding.progressReady.visibility = View.GONE
                binding.swipeRefreshPreparingOrder.isRefreshing = false
                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun preparingOrderList(courses: List<ResponsePreparingOrder.PreparingOrder>): List<ResponsePreparingOrder.PreparingOrder> {

        val newList = mutableListOf<ResponsePreparingOrder.PreparingOrder>()
        courses.forEach {
            list!!.add(
                ResponsePreparingOrder.PreparingOrder(
                    it.order_at,
                    it.order_id,
                    it.order_type,
                    it.driver_type,
                    it.orderby,
                    it.phone,
                    it.status,
                    it.driver_name,
                    it.driver_pic,
                    it.driver_phone,
                    it.total_amount,
                    it.user_id
                )
            )
        }


        binding.prepairingRecylerView.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = PrepairingOrderAdapter(context, list!!, object : PrepairingOrderAdapter.OrderReady {
                override fun readyOrder(position: Int) {

                    progressDialog.show(context, "Collecting...")

                    OrderReady(""+ list!![position].order_id)
                }
            }, object : PrepairingOrderAdapter.ItemClick {
                override fun itemClick(position: Int) {
                    flag = false
                    context.startActivity(Intent(context, PreparingOrderDetailsActivity::class.java)
                        .putExtra("order_id", ""+ list!![position].order_id)
                        .putExtra("driver_type", ""+ list!![position].driver_type))
                }
            })
            this.adapter = adapter
        }
        return newList
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

                      //  context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
                        getAllPreparingOrders()

                    }
                   /* else if (response.body()!!.status == "2") {
                        Confirm(response.body()!!.message)
                    }*/
                    else {

                        Toast.makeText(
                            context,
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

                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    /* private fun Confirm(message:String ) {
        context?.let {
            val dialog = Dialog(it)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.conifrm_preparing)
            val textOrderHavePickedDialog = dialog.findViewById(R.id.textOrderHavePickedDialog) as TextView
            val btnOKDialog = dialog.findViewById(R.id.btnOKDialog) as Button

            textOrderHavePickedDialog.text = message

            btnOKDialog.setOnClickListener {
                dialog.dismiss()
                binding.progressReady.visibility = View.VISIBLE
                getAllPreparingOrders()
            }
            dialog.show()
        }


    }*/

}