package com.xeatpos.order

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeatpos.R
import com.xeatpos.adapter.ReadyOrderAdapter
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.data.ResponseReadyOrdersList
import com.xeatpos.databinding.FragmentPreparingOrderBinding
import com.xeatpos.order.orders.ReadyOrderDetailsActivity
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReadyOrderFragment : Fragment() {
    private lateinit var binding: FragmentPreparingOrderBinding
    private var list: MutableList<ResponseReadyOrdersList.ReadyOrder>? = ArrayList()
    private val progressDialogReady = CustomProgressDialog()
    private var flagReady : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_preparing_order, container, false)
        // init()

        binding.swipeRefreshPreparingOrder.setOnRefreshListener {
            binding.progressReady.visibility = View.VISIBLE
            //context?.let { progressDialogReady.show(it, getString(R.string.loading_orders)) }
            getAllReadyOrders()

        }

        binding.textNoPreparingOrder.text = getString(R.string.no_order_ready_yet)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(flagReady == false) {
           // if (progressDialogReady != null) {
               // context?.let { progressDialogReady.show(it, getString(R.string.loading_orders)) }
                   binding.progressReady.visibility = View.VISIBLE
                getAllReadyOrders()
          //  }
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
           // if(progressDialogReady != null) {
                flagReady = true
               // context?.let { progressDialogReady.show(it, getString(R.string.loading_orders)) }
            try {
                binding.progressReady.visibility = View.VISIBLE
                getAllReadyOrders()
            }catch (e:UninitializedPropertyAccessException){
                getAllReadyOrders()
                e.printStackTrace()
            }
           // }
        }
    }

    fun getAllReadyOrders() {
        val apiInterface =
            APIService.create().getReadyOrders("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponseReadyOrdersList.Ready> {
            override fun onResponse(
                call: Call<ResponseReadyOrdersList.Ready>?,
                response: Response<ResponseReadyOrdersList.Ready>?
            ) {

                if (response?.body() != null) {
                    binding.progressReady.visibility = View.GONE
                 /*   if(progressDialogReady != null) {
                        progressDialogReady.dialog.dismiss()

                    }*/
                    if (response.body()!!.status == "1") {

                        binding.swipeRefreshPreparingOrder.isRefreshing = false

                        list!!.clear()

                        binding.textNoPreparingOrder.visibility = View.GONE
                        binding.prepairingRecylerView.visibility = View.VISIBLE

                        //   Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        preparingOrderList(response.body()!!.ready_order)


                    }  else  if (response.body()!!.status == "9") {

                        try{
                             Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }catch (e : NullPointerException){
                            e.printStackTrace()
                        }

                    }else {

                       // Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        binding.swipeRefreshPreparingOrder.isRefreshing = false

                        binding.textNoPreparingOrder.visibility = View.VISIBLE
                        binding.prepairingRecylerView.visibility = View.GONE

                        /*  Toast.makeText(
                              context,
                              response.body()!!.message,
                              Toast.LENGTH_SHORT
                          )
                              .show()*/

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseReadyOrdersList.Ready>?, t: Throwable?) {
               // progressDialogReady.dialog.dismiss()
                binding.progressReady.visibility = View.GONE
                binding.swipeRefreshPreparingOrder.isRefreshing = false
                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun preparingOrderList(courses: List<ResponseReadyOrdersList.ReadyOrder>): List<ResponseReadyOrdersList.ReadyOrder> {

        val newList = mutableListOf<ResponseReadyOrdersList.ReadyOrder>()
        courses.forEach {
            list!!.add(
                ResponseReadyOrdersList.ReadyOrder(
                    it.discount_rate,
                    it.driver_id,
                    it.driver_name,
                    it.driver_phone,
                    it.driver_pic,
                    it.order_at,
                    it.order_id,
                    it.order_type,
                    it.driver_type,
                    it.orderby,
                    it.phone,
                    it.status,
                    it.total_amount,
                    it.user_id
                )
            )
        }


        binding.prepairingRecylerView.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = ReadyOrderAdapter(context, list!!, object : ReadyOrderAdapter.PickedUp {
                override fun pickedUp(position: Int) {
                    //binding.progressReady.visibility = View.VISIBLE
                    progressDialogReady.show(context, "Loading...")

                    OrderPickedup(""+ list!![position].order_id)

                }


            }, object : ReadyOrderAdapter.ItemClick {
                override fun itemClick(position: Int) {

                    flagReady = false

                    context.startActivity(
                        Intent(context, ReadyOrderDetailsActivity::class.java)
                            .putExtra("order_id", ""+ list!![position].order_id)
                            .putExtra("driver_type", ""+ list!![position].driver_type))

                }

            })
            this.adapter = adapter
        }
        return newList
    }

    fun OrderPickedup(orderId: String) {
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

                    progressDialogReady.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                       // context?.let { progressDialogReady.show(it, getString(R.string.loading_orders)) }
                        getAllReadyOrders()

                    }
                    else if (response.body()!!.status == "2") {

                        Confirm(response.body()!!.message)

                    }
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

    private fun Confirm(message:String ) {
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
                getAllReadyOrders()
            }
            dialog.show()
        }


    }

}