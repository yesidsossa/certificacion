package com.mallacreativa.certificacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.R.attr.password





class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_login -> { login(et_email.text.toString(),et_pass.text.toString()) }

            R.id.tv_register -> { openRegister() }
        }
    }
    fun openRegister()
    {
        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)

    }

    fun login(user: String,pass: String)
    {

        auth.signInWithEmailAndPassword(user, pass)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()


                } else {
                    Toast.makeText(this, "No inicio", Toast.LENGTH_SHORT).show()


                }

            })

    }

}
