package de.openteilauto.openteilauto.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.ui.bookings.BookingsActivity

class LoginActivity : AppCompatActivity() {
    private var model: LoginViewModel? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setTitle(R.string.login_title)

        model = ViewModelProvider(this)[LoginViewModel::class.java]
        progressBar = findViewById(R.id.login_progress_bar)

        model?.loggedInUser?.observe(this, { user ->
            progressBar?.visibility = View.INVISIBLE
            if (user != null) {
                Toast.makeText(this,
                    resources.getString(R.string.login_welcome, user.firstname, user.lastname),
                    Toast.LENGTH_SHORT).show()
                val bookingsIntent = Intent(this, BookingsActivity::class.java)
                startActivity(bookingsIntent)
            }
        })

        model?.loginError?.observe(this, { error ->
            if (error != null) {
                progressBar?.visibility = View.INVISIBLE
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun login(view: View) {
        val editMembershipNo = findViewById<EditText>(R.id.editMembershipNo)
        val editPassword = findViewById<EditText>(R.id.editPassword)

        val membershipNo = editMembershipNo.text.toString()
        val password = editPassword.text.toString()

        model?.login(membershipNo, password)
        progressBar?.visibility = View.VISIBLE
    }
}