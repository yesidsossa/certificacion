package com.mallacreativa.certificacion.Driver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mallacreativa.certificacion.R
import kotlinx.android.synthetic.main.activity_main.*

class DriverRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        imageView.setImageResource(R.drawable.driver)
    }
}
