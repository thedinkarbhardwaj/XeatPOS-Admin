package com.xeatpos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xeatpos.R
import com.xeatpos.data.ResponseCategory
import com.xeatpos.data.ResponseDriversList
import com.xeatpos.databinding.CategoryListViewBinding
import com.xeatpos.databinding.CustomDriverItemBinding

class DriversListAdapter(
    var context: Context,
    var list: MutableList<ResponseDriversList.Data>?, var assignDriver : AssignDriver
) :
    RecyclerView.Adapter<DriversListAdapter.ViewHolder>() {
    class ViewHolder(var binding: CustomDriverItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriversListAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context)
        var binding = CustomDriverItemBinding.inflate(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DriversListAdapter.ViewHolder, position: Int) {

        Glide.with(context).load(list?.get(position)?.driver_pic).into(holder.binding.imageDriver)

        holder.binding.textDriverName.text = list!![position].driver_name
        holder.binding.textDriverPhone.text = list!![position].contact_num
        holder.binding.textVehicleName.text = context.getString(R.string.vehicle_name)+" "+list!![position].vehicle_name
        holder.binding.textVehicleNumber.text = "Vehicle Number:"+" "+list!![position].vehicle_num

        holder.binding.btnAssignDriver.setOnClickListener {
            assignDriver.assignDriver(position)
        }

        //  holder.bind(list!![position])
       // holder.binding.txtCategoryName.text = list!![position].c_name
      //  holder.binding.txtTotalItems.text = context.getString(R.string.total_items) +" "+ list!![position].total_items
    }

    override fun getItemCount() = list!!.size

    open interface AssignDriver {
        fun assignDriver(position: Int)
    }
}