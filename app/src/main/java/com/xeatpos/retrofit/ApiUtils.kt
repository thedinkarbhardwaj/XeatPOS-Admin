package com.xeatpos.retrofit

class ApiUtils {

    private fun ApiUtils() {}

   // val BASE_URL = "https://phpstack-102119-2218830.cloudwaysapps.com/"
   // val BASE_URL = "https://phpstack-715663-2374578.cloudwaysapps.com/backend/"
    val BASE_URL = "https://xeat.co.uk/backend/"
   // val BASE_URL = "https://phpstack-102119-2292222.cloudwaysapps.com/"
    fun getAPIService(token: String?): APIService? {

        return RetrofitClient.getClient(BASE_URL, token)?.create(APIService::class.java)

    }

}