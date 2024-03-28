package com.xeatpos.adapter

import android.Manifest
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xeatpos.data.ResponsePreparingOrder
import com.xeatpos.databinding.ListPrepairOrderBinding
import com.xeatpos.model.CategoryModel
import com.karumi.dexter.PermissionToken

import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener

import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.Glide


class PrepairingOrderAdapter(var context: Context, var list: MutableList<ResponsePreparingOrder.PreparingOrder>,
    var orderReady: OrderReady, var itemClick : ItemClick) :
    RecyclerView.Adapter<PrepairingOrderAdapter.ViewHolder>() {
    class ViewHolder(val binding: ListPrepairOrderBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ListPrepairOrderBinding.inflate(view)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context).load(list[position].driver_pic).into(holder.binding.imageDriverPreparingList)

        holder.binding.textOrderIdPreparing.text = ""+list[position].order_id
        holder.binding.textOrderByPreparing.text = list[position].orderby
        holder.binding.textOrderAtPreparing.text = list[position].order_at
        holder.binding.textOrderTypePreparing.text = list[position].order_type
        holder.binding.textOrderTotalPreparing.text = list[position].total_amount
        holder.binding.textDriverNamePreparing.text = list[position].driver_name
        holder.binding.textDriverPhonePreparing.text = list[position].driver_phone

        holder.binding.btnCall.setOnClickListener {

            callUser(list[position].phone)

        }

        if(list[position].order_type == "Pickup"){
            holder.binding.layoutDriverPreparing.visibility = View.GONE
        }else{
            if(list[position].driver_type == "0"){
                holder.binding.layoutDriverPreparing.visibility = View.GONE
            }else {
                holder.binding.layoutDriverPreparing.visibility = View.VISIBLE
            }
        }


        holder.binding.btnReady.setOnClickListener {
            orderReady.readyOrder(position)
        }

        holder.binding.layoutTopPreparing.setOnClickListener {

            itemClick.itemClick(position)

         /*   context.startActivity(Intent(context, PreparingOrderDetailsActivity::class.java).putExtra(
                "order_id", ""+list[position].order_id))*/


        }

    }

    override fun getItemCount() = list.size

    private fun callUser(phone:String){
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CALL_PHONE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:" + phone)
                    context.startActivity(intent)
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

    open interface OrderReady {
        fun readyOrder(position: Int)
    }

    open interface ItemClick {
        fun itemClick(position: Int)
    }
}