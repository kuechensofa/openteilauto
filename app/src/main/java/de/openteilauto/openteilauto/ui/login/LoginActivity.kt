package de.openteilauto.openteilauto.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.ui.bookings.BookingsActivity

class LoginActivity : AppCompatActivity() {
    private val model: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        model.loggedInUser.observe(this, { user ->
                if (user != null) {
                    Toast.makeText(this, "Hello ${user.firstname} ${user.lastname}!",
                                Toast.LENGTH_SHORT).show()
                    val bookingsIntent = Intent(this, BookingsActivity::class.java)
                    startActivity(bookingsIntent)
                }
            })

        model.loginError.observe(this, { error ->
                    if (error != null) {
                        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                    }
            })
    }

    fun login(view: View) {
        val editMembershipNo = findViewById<EditText>(R.id.editMembershipNo)
        val editPassword = findViewById<EditText>(R.id.editPassword)

        val membershipNo = editMembershipNo.text.toString()
        val password = editPassword.text.toString()

        model.login(membershipNo, password)
    }
}