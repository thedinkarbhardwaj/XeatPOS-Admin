package com.xeatpos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xeatpos.R
import com.xeatpos.data.ResponseCategory
import com.xeatpos.databinding.CategoryListViewBinding
import com.xeatpos.model.CategoryModel

class CategoryAdapter(
    var context: Context,
    var list: MutableList<ResponseCategory.Data>?
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
     class ViewHolder(var binding: CategoryListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
      
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context)
        var binding = CategoryListViewBinding.inflate(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
      //  holder.bind(list!![position])
        holder.binding.txtCategoryName.text = list!![position].c_name
        holder.binding.txtTotalItems.text = context.getString(R.string.total_items) +" "+ list!![position].total_items
    }

    override fun getItemCount() = list!!.size
}