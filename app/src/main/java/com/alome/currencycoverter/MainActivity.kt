package com.alome.currencycoverter

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.Log.e
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alome.currencycoverter.Utils.ApiAdapter
import com.alome.currencycoverter.Utils.Constants
import com.google.gson.JsonObject
import com.mynameismidori.currencypicker.CurrencyPicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.country_picker.*
import kotlinx.android.synthetic.main.country_picker_2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope()  {


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // set Date Value
        date.setText("Mid Market Exchange Rate at ${DateUtils.toSimpleString(Date())} UTC")

        //Default Currencies
        var first_country:String="NGN"
        var second_country:String="USD"

        //get the value of txt from @ativity_main.xml, and change the color of "."
        val mTxt=txt.text.toString().replace(".", "<font color=#00d998>.</font>")
        txt.setText(Html.fromHtml(mTxt))
        countryPicker1.setOnClickListener{
            val picker =
                CurrencyPicker.newInstance("Select Currency") // dialog title
                 picker.setListener { name, cCode, symbol, flagDrawableResID ->
                     country_flag_1.setImageResource(flagDrawableResID)
                     picker.dismiss()
                     code_1.text=cCode
                     first_country=cCode
                     c1.hint = cCode
            }
            picker.show(supportFragmentManager, "CURRENCY_PICKER")
        }

        countryPicker2.setOnClickListener{
            val picker =
                CurrencyPicker.newInstance("Select Currency") // dialog title
            picker.setListener { name, code, symbol, flagDrawableResID ->
                country_flag_2.setImageResource(flagDrawableResID)
                picker.dismiss()
                code_2.text=code
                second_country=code
                c2.hint=code

            }
            picker.show(supportFragmentManager, "CURRENCY_PICKER")
        }


        fetch.setOnClickListener{
            if (isNetworkConnected()) {
                if (second_country.isEmpty()){
                    Toast.makeText(this, "Please Select Currencies", Toast.LENGTH_LONG).show()
                }else{
                    when {
                        c1.text.isEmpty() -> {
                            c1.error = "Required"
                        }

                        else -> {
                            val  pd:ProgressDialog= ProgressDialog(this@MainActivity)
                            pd.setMessage("Please Wait...")
                            val alertDialog: AlertDialog.Builder=AlertDialog.Builder(this)
                            pd.show()
                            launch(Dispatchers.Main) {
                                val response = ApiAdapter.apiClient.getResult(DateUtils.toSimpleString(Date()),Constants.API_KEY,  first_country+","+second_country)
                                // Check if response was successful.
                                if (response.isSuccessful){
                                    pd.dismiss()
                                    var mObject:JsonObject= response.body()!!
                                    var rate=mObject.getAsJsonObject("rates")
                                    var total:Double=rate.get(first_country).asDouble
                                    var total_2:Double=rate.get(second_country).asDouble
                                    c1.setText(total_2.toString())
                                    c2.setText(total.toString())
                                }else{
                                    alertDialog.setMessage(response.body().toString())
                                    alertDialog.show()
                                    pd.dismiss()
                                }

                        }


                        }
                    }
                }
            } else {
                AlertDialog.Builder(this).setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .show()
            }
        }
    }



    // check if the internet is available
    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    object DateUtils {
        @JvmStatic
        fun toSimpleString(date: Date) : String {
            val format = SimpleDateFormat("YYYY-MM-dd")
            Log.d("Date", format.format(Date()))
            return format.format(date)
        }
    }
}
