package com.xeatpos.retrofit

import com.xeatpos.data.*
import com.xeatpos.home.MenuSizesModel
import com.xeatpos.home.SizeDetailsModel
import com.xeatpos.response.daily.DailyReport
import com.xeatpos.response.rangereport.RangeReports
import com.xeatpos.response.weekly.WeeklyReport
import com.xeatpos.response.yearly.YearlyReport
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface APIService {

    /*  @POST("api/login")
      @FormUrlEncoded
      fun login(
          @Field("dial_code") dial_code: String?,
          @Field("phone") phone: String?,
          @Field("password") password: String?,
          @Field("lang") lang: String?
      ): Call<LoginModel?>?*/


    companion object {

     //   var BASE_URL = "https://phpstack-102119-2292222.cloudwaysapps.com/"
    //    var BASE_URL = "https://phpstack-715663-2374578.cloudwaysapps.com/backend/"
          var BASE_URL = "https://xeat.co.uk/backend/"
       //   var BASE_URL = "https://phpstack-102119-2292222.cloudwaysapps.com/"

        fun create(): APIService {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()
            return retrofit.create(APIService::class.java)

        }
    }


    @POST("api/login")
    @FormUrlEncoded
    fun loginPost(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("type") type: String,
        @Field("fcm_token") fcm_token: String
    ): Call<ResponseModel.Login>


    @GET("api/restaurantmenulist")
    fun getMenus(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseModel.Menus>

    @GET("api/out_of_stock_grocery_items")
    fun getGroceryOutOfStock(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseModel.Menus>

    @GET("api/category_list")
    fun getCategories(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseCategory.Categories>


   /* @POST("api/restaurantmenulist_details")
    @FormUrlEncoded
    fun menuDetails(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("item_id") item_id: String
    ): Call<ResponseMenuDetailsModel.MenuDetails>*/

    @POST("api/rest_subitems_sizes_list")
    @FormUrlEncoded
    fun menuDetails(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("subitem_id") subitem_id: String
    ): Call<MenuSizesModel.MenuDetails>

    @POST("api/search_sizes_menuitems_categroy")
    @FormUrlEncoded
    fun menuSizesVaients(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("menu_id") menu_id: String,
        @Field("size") size: String
    ): Call<SizeDetailsModel.SizeDetails>

    @POST("api/enable_disable_menu")
    @FormUrlEncoded
    fun changeStatusMenu(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("item_id") item_id: String,
        @Field("status") status: String
    ): Call<ResponseEnableDisableMenus.EnableDisable>

    @GET("api/get_profile")
    fun getProfile(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseProfileModel.Profile>


    @GET("api/wallet")
    fun getWalletBalance(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseWalletBalance.WalletBalance>

    @POST("api/wallet_transactions")
    @FormUrlEncoded
    fun getWalletTransaction(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("limit") limit: String,
        @Field("page") page: String
    ): Call<ResponseWalletTransaction.Wallet>

    /******************* Orders ************************/

    //@GET("api/new_order")
    @GET("api/2.0/new_order_new")
    fun getNewOrders(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseNewOrders.NewOrders>

    // Advance order listing
    @GET("api/2.0/pos_advanced_orders")
    fun getAllAdvanceOrders(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseScheduledOrders.NewOrders>

    @POST("api/order_details")
    @FormUrlEncoded
    fun getNewOrderDetails(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponseNewOrderDetails.NewOrderDetails>

    @POST("api/prepearing_orderlist_details")
    @FormUrlEncoded
    fun getPreparingOrderDetails(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponsePreparingOrderDetails.PreparingOrders>

   // @POST("api/accept_reject_order")
    @POST("api/2.0/accept_reject_order_new")
    @FormUrlEncoded
    fun acceptrejectorder(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String,
        @Field("status") status: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>

    //accept/reject for advance order
    @POST("api/2.0/restaurant_accept_reject_advanced_order")
    @FormUrlEncoded
    fun acceptrejectorderAdvance(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String,
        @Field("status") status: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>

    //for schedule order proceed click, for driver request or preparing
    @POST("api/2.0/accept_reject_advanced_order")
    @FormUrlEncoded
    fun proceedOrderAdvance(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String,
        @Field("status") status: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>



    @POST("api/checkNewOrder")
    @FormUrlEncoded
    fun checkNewOrder(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>

    @GET("api/prepearing_orderlist")
    fun getPreparingOrders(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponsePreparingOrder.Preparing>

    @POST("api/get_restordrivers")
    @FormUrlEncoded
    fun getDrivers(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String,
        @Field("driver_from") driver_from: String,
    ): Call<ResponseDriversList.Drivers>

    @POST("api/order_assignedtodriver")
    @FormUrlEncoded
    fun assignDriver(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String,
        @Field("driver_id") driver_id: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>

    @POST("api/ready_order")
    @FormUrlEncoded
    fun orderReady(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>


    @GET("api/ready_orderlist")
    fun getReadyOrders(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseReadyOrdersList.Ready>

    @POST("api/pickup_order")
    @FormUrlEncoded
    fun orderPickup(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>

    @POST("api/complete_order_pos")
    @FormUrlEncoded
    fun completeOrder(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>

    @POST("api/ready_orderlist_details")
    @FormUrlEncoded
    fun getReadyOrderDetails(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponseReadyOrderDetails.ReadyOrderRoot>


    @GET("api/completepickup_orderlist")
    fun getPickedOrders(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponsePickedOrders.PickedOrdersRoot>

    @POST("api/completepickup_orderlist_details")
    @FormUrlEncoded
    fun getPickedOrderDetails(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponsePickedOrdersDetails.PickedOrdersDetails>

    @GET("api/order_history")
    fun getHistoryOrders(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseHistoryOrdersList.OrdersHistory>

    @POST("api/order_history_details")
    @FormUrlEncoded
    fun getHistoryOrderDetails(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("order_id") order_id: String
    ): Call<ResponseOrderHistoryDetails.OrderHistoryDetails>


    /***********************Reports***************************/

    @POST("api/reportson_timebasis")
    @FormUrlEncoded
    fun dailyOrderCall(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("status") status: String
    ): Call<DailyReport>


   @POST("api/reportson_timebasis")
    @FormUrlEncoded
    fun weeklyOrderCall(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("status") status: String
    ): Call<WeeklyReport>

    @POST("api/reportson_timebasis")
   @FormUrlEncoded
   fun yearlyOrderCall(
       @Header("Accept") accept: String?,
       @Header("Authorization") authorization: String?,
       @Field("status") status: String
   ): Call<YearlyReport>

     @POST("api/custom_report")
     @FormUrlEncoded
     fun rangeOrderList(
         @Header("Accept") accept: String?,
         @Header("Authorization") authorization: String?,
         @Field("select_range") select_range: String,
         @Field("end_range") end_range: String,
     ): Call<ResponseReportPrint.ReportPrint>

     /*@POST("api/report_orderdetails")
     @FormUrlEncoded
     fun report_orderdetails(
         @Header("Accept") accept: String?,
         @Header("Authorization") authorization: String?,
         @Field("date") date: String,
         @Field("limit") limit: String,
         @Field("page") page: String
     ): Call<RangeReportDetail>*/

    @POST("api/daily_weekly_monthly_orderreport")
    @FormUrlEncoded
    fun rangeOrderList(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("type") type: String
    ): Call<ResponseReportPrint.ReportPrint>


    @POST("api/online_offline_rest")
    @FormUrlEncoded
    fun onlineoffline(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("status") rest_id: String
    ): Call<OnlineOfflineModel.OnlineOffline>

    @POST("api/edit_grocery_discount_price")
    @FormUrlEncoded
    fun updateGroceryPrice(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?,
        @Field("groc_item_id") groc_item_id: String,
        @Field("price") price: String,
        @Field("discount") discount: String
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>

    @GET("api/logout")
    fun logout(
        @Header("Accept") accept: String?,
        @Header("Authorization") authorization: String?
    ): Call<ResponseAcceptRejectOrder.AcceptRejectOrder>


}