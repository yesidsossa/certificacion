package com.mallacreativa.certificacion.Driver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mallacreativa.certificacion.R
import kotlinx.android.synthetic.main.activity_main.*

class DriverLoginActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)
    }

    fun login(user: String, pass: String) {

        auth.signInWithEmailAndPassword(user, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "No inicio", Toast.LENGTH_SHORT).show()


                }

            }
    }

    fun openRegister()
    {
        val intent = Intent(this, DriverRegisterActivity::class.java)
        startActivity(intent)

    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.btn_login -> {
                login(et_email.text.toString(), et_pass.text.toString())
            }

            R.id.tv_register -> {
                openRegister()
            }
        }
    }
}
