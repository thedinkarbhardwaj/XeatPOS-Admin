package com.xeatpos.home

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
import com.xeatpos.adapter.CategoryAdapter
import com.xeatpos.data.ResponseCategory
import com.xeatpos.databinding.FragmentCategoryBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFragment : Fragment() {
    lateinit var binding: FragmentCategoryBinding

    var list: MutableList<ResponseCategory.Data>? = ArrayList()
    private val progressDialog = CustomProgressDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        //init()

        context?.let { progressDialog.show(it, "Please Wait...") }

        getCats()

        binding.swipeToRefreshCats.setOnRefreshListener {
            // context?.let { progressDialog.show(it, "Please Wait...") }
            getCats()
        }


        return binding.root
    }

    fun getCats(){
        val apiInterface = APIService.create().getCategories("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue( object : Callback<ResponseCategory.Categories> {
            override fun onResponse(call: Call<ResponseCategory.Categories>?, response: Response<ResponseCategory.Categories>?) {

                if(response?.body() != null){

                    progressDialog.dialog.dismiss()

                    if(response.body()!!.status.equals("1")){

                        binding.swipeToRefreshCats.isRefreshing = false

                        list?.clear()

                        //   Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        categoryList(response.body()!!.data)

                    }else{

                        Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseCategory.Categories>?, t: Throwable?) {

               // Toast.makeText(context, "Exception: ", Toast.LENGTH_SHORT).show()

                Log.i("Exception: ",t.toString())

            }
        })
    }

    fun categoryList(courses: List<ResponseCategory.Data>) : List<ResponseCategory.Data> {
        val newList = mutableListOf<ResponseCategory.Data>()
        courses.forEach {

            list!!.add(ResponseCategory.Data( it.c_name,
                it.id,
            it.image,
            it.r_id,
            it.total_items))
        }

        binding.categoryRecycler.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = CategoryAdapter(context, list!!)
            this.adapter = adapter
        }

        return newList
    }

   /* fun init() {



        binding.categoryRecycler.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = CategoryAdapter(context, list)
            this.adapter = adapter
        }
    }*/
}