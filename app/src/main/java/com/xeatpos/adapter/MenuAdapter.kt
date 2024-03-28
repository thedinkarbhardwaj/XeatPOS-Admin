package com.xeatpos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xeatpos.data.ResponseModel
import com.xeatpos.databinding.MenuListViewBinding
import com.xeatpos.prefs

class MenuAdapter(
    var context: Context,
    var list: MutableList<ResponseModel.Data>,
    var switchClick: SwitchListener,
    var itemClick: ItemClickListener
) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {


    class ViewHolder(val binding: MenuListViewBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = MenuListViewBinding.inflate(view)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textMenuName.text = list[position].name
        holder.binding.textMenuPrice.text = list[position].f_price
        holder.binding.textIngrediants.text = list[position].ingredients

        holder.binding.switchMenus.isChecked = list[position].status == "1"

        holder.binding.switchMenus.setOnClickListener {
            if (holder.binding.switchMenus.isChecked) {
                holder.binding.switchMenus.isChecked = false;
                switchClick.switchClick(position, "2")
            } else {
                holder.binding.switchMenus.isChecked = true;
                switchClick.switchClick(position, "1")
            }
        }

        /*holder.binding.switchMenus.setOnCheckedChangeListener { buttonView, isChecked ->
             if (isChecked) {
                 holder.binding.switchMenus.setChecked(false);
                 switchClick.switchClick(position, "1")
            }else{
                 holder.binding.switchMenus.setChecked(true);
                 switchClick.switchClick(position, "0")
             }

        }*/

        holder.binding.layoutTopMenuItem.setOnClickListener {

            itemClick.itemClick(position)

        }
        if (prefs.type == "1") {
            holder.binding.imgDish.visibility = View.VISIBLE
            Glide.with(context).load(list[position].image).into(holder.binding.imgDish)
        } else {
            holder.binding.imgDish.visibility = View.GONE
        }

    }

    override fun getItemCount() = list.size

    open interface SwitchListener {
        fun switchClick(position: Int, status: String)
    }

    open interface ItemClickListener {
        fun itemClick(position: Int)
    }

}