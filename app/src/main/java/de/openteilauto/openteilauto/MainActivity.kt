package de.openteilauto.openteilauto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.reflect.TypeToken
import de.openteilauto.openteilauto.api.LoginResponse
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidNetworking.initialize(applicationContext)
    }

    fun login(view: View) {
        val editMembershipNo = findViewById<EditText>(R.id.editMembershipNo)
        val editPassword = findViewById<EditText>(R.id.editPassword)

        val membershipNo = editMembershipNo.text.toString()
        val password = editPassword.text.toString()

        AndroidNetworking.post("https://sal2.teilauto.net/api/login")
            .addBodyParameter("password", password)
            .addBodyParameter("membershipNo", membershipNo)
            .addBodyParameter("rememberMe", "false")
            .addBodyParameter("requestTimestamp", "1639351101910")
            .addBodyParameter("driveMode", "tA")
            .addBodyParameter("platform", "ios")
            .addBodyParameter("pg", "pg")
            .addBodyParameter("version", "22748")
            .addBodyParameter("tracking", "off")
            .setPriority(Priority.HIGH)
            .build()
            .getAsParsed(object : TypeToken<LoginResponse>() {}, object : ParsedRequestListener<LoginResponse> {
                override fun onResponse(response: LoginResponse?) {
                    val loginData = response?.login?.data
                    val errorData = response?.error

                    when {
                        loginData?.membershipNo != null -> {
                            val firstname = loginData.salutation.firstname
                            val lastname = loginData.salutation.lastname
                            Toast.makeText(this@MainActivity, "Welcome $firstname $lastname",
                                Toast.LENGTH_SHORT).show()
                        }
                        errorData?.message != null -> {
                            Toast.makeText(this@MainActivity, errorData.message, Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(this@MainActivity, "Unknown error!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(this@MainActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            })
    }
}