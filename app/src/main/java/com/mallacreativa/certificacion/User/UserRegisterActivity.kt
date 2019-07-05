package com.mallacreativa.certificacion.User

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import com.mallacreativa.certificacion.R


class UserRegisterActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener(this)

        }

    fun register(user: String,pass: String)
    {
        auth.createUserWithEmailAndPassword(user, pass)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registrado", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "No Registrado", Toast.LENGTH_SHORT).show()

                }

            })


    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_register -> { register(et_email.text.toString(),et_pass.text.toString()) }
        }
    }
}
