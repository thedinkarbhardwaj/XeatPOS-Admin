package com.xeatpos.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.xeatpos.R
import com.xeatpos.comman.CloseKeyboard.closeKey
import com.xeatpos.comman.ToastClass.toast
import com.xeatpos.data.ResponseModel
import com.xeatpos.databinding.ActivityLoginBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnFocusChangeListener, View.OnClickListener {

    lateinit var binding: ActivityLoginBinding

    private val progressDialog = CustomProgressDialog()
    private var token = ""
    private var loginType = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val prefsToken = getSharedPreferences(
            "pos", Context.MODE_PRIVATE
        )

        token = prefsToken.getString("token","").toString()

        Log.i("FCMM=========",token)
        loginType = "0"

        binding.radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
            })
        init()
    }

    fun init() {
        binding.edtUsername.setOnFocusChangeListener(this)
        binding.edtPassword.setOnFocusChangeListener(this)
        binding.llLogin.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.txtForgotPass.setOnClickListener(this)

        runtimePermissionsNoti()
    }

    fun radio_button_click(view: View){
        // Get the clicked radio button instance
        val radio: RadioButton = findViewById(binding.radioGroup.checkedRadioButtonId)

        Log.i("===========", "On click : ${radio.text}")
        if(radio.text == "Restaurant")
            loginType = "0"
        else if(radio.text == "Grocery")
            loginType = "1"
        else
            loginType = "2"
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        when (p0?.id) {
            R.id.edt_username -> {
                if (p1) {
                    binding.edtUsername.setBackgroundResource(R.drawable.focus_border_style)
                } else {
                    binding.edtUsername.setBackgroundResource(R.drawable.lost_focus_border_style)
                }
            }
            R.id.edt_password -> {
                if (p1) {
                    binding.edtPassword.setBackgroundResource(R.drawable.focus_border_style)
                } else {
                    binding.edtPassword.setBackgroundResource(R.drawable.lost_focus_border_style)
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ll_login -> {
                closeKey(binding.llLogin)
            }
            R.id.btn_login -> {
               //startActivity(Intent(this, TestActivity::class.java))
                if(binding.edtUsername.text.trim() == ""){
                    Toast.makeText(applicationContext, getString(R.string.enter_username), Toast.LENGTH_SHORT).show()

                }else if(binding.edtPassword.text.trim() == ""){
                    Toast.makeText(applicationContext, getString(R.string.enter_password), Toast.LENGTH_SHORT).show()

                }else {
                    progressDialog.show(this, "Please Wait...")

                    callLogin(binding.edtUsername.text.toString(), binding.edtPassword.text.toString())
                }
            }
            R.id.txt_forgot_pass -> {
                toast("comming soon")
            }
        }
    }

    fun callLogin(username: String, password : String){
        val apiInterface = APIService.create().loginPost(username,password, loginType, token)

        apiInterface.enqueue( object : Callback<ResponseModel.Login> {
            override fun onResponse(call: Call<ResponseModel.Login>?, response: Response<ResponseModel.Login>?) {
                if(response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if(response.body()!!.status == "1"){
                        Log.i("Status=====", response.body()!!.status)
                        Log.i("Message=====", response.body()!!.message)
                        Log.i("Token=====", response.body()!!.token)
                        prefs.token =  response.body()!!.token
                        prefs.restaurantImage =  response.body()!!.image
                        prefs.restaurantName = response.body()!!.rest_name
                        prefs.type = response.body()!!.type
                        startActivity(Intent(applicationContext, HomeActivity::class.java))
                        finish()
                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(applicationContext, response?.body()!!.message, Toast.LENGTH_SHORT).show()

                    }

                }else{

                    Toast.makeText(applicationContext, response?.body()!!.message, Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<ResponseModel.Login>?, t: Throwable?) {

            }
        })
    }

    private fun runtimePermissionsNoti() {
        if (Build.VERSION.SDK_INT >= 33) {
            // Toast.makeText(this@LoginScreen, "greater or equal 33 sdk", Toast.LENGTH_SHORT).show()
            Dexter.withActivity(this@LoginActivity)
                .withPermissions(
                    Manifest.permission.POST_NOTIFICATIONS
                    )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) { // do you work now
                        }
                        for (i in report.deniedPermissionResponses.indices) { //
                            //                        Log.d("dennial permision res", report.getDeniedPermissionResponses().get(i).getPermissionName());

//                            Toast.makeText(
//                                this@LoginScreen,
//                                report.getDeniedPermissionResponses().get(i).getPermissionName(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            var intent = Intent(this@LoginScreen, CheckPermis::class.java)
//                            startActivity(intent)
//                            overridePendingTransition(
//                                R.anim.slide_in_right,
//                                R.anim.slide_out
//                            )
                            break;
                            //popupPermission()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
    }

}