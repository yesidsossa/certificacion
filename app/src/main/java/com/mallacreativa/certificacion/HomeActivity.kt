package com.mallacreativa.certificacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mallacreativa.certificacion.Driver.DriverLoginActivity
import com.mallacreativa.certificacion.Driver.DriverRegisterActivity
import com.mallacreativa.certificacion.User.UserLoginActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        btnUser.setOnClickListener(this)
        btnDriver.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnUser -> {
                val intent = Intent(this, UserLoginActivity::class.java)
                startActivity(intent)
            }

            R.id.btnDriver -> {
                val intent = Intent(this, DriverLoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

}
