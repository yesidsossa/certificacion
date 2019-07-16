package com.mallacreativa.certificacion.User

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mallacreativa.certificacion.Driver.MapDriverActivity
import com.mallacreativa.certificacion.MapsActivity
import com.mallacreativa.certificacion.R


class UserLoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView.setImageResource(R.drawable.user)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().reference


        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.btn_login -> { login(et_email.text.toString(),et_pass.text.toString()) }

            R.id.tv_register -> {openRegister() }
        }
    }
    fun createchild()
    {

        database.child("addresess").child("address_uYWOMmmkhXe1Fo6mjn6RLxmwGy93").child("origin_lat").setValue("40.712784 ")
        database.child("addresess").child("address_uYWOMmmkhXe1Fo6mjn6RLxmwGy93").child("origin_long").setValue("-74.005941 ")
        database.child("addresess").child("address_uYWOMmmkhXe1Fo6mjn6RLxmwGy93").child("destination_lat").setValue("40.711668")
        database.child("addresess").child("address_uYWOMmmkhXe1Fo6mjn6RLxmwGy93").child("destination_long").setValue("-74.015018")
    }
    fun openRegister()
    {
        val intent = Intent(this, UserRegisterActivity::class.java)
        startActivity(intent)

    }
    fun openMap()
    {
        startActivity(Intent(this, MapUserActivity::class.java))
    }

    fun login(user: String,pass: String)
    {

        auth.signInWithEmailAndPassword(user, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                    openMap()

                } else {
                    Toast.makeText(this, "No inicio", Toast.LENGTH_SHORT).show()


                }

            }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

}
