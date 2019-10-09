package com.example.tahfidzdoaquplay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnAbasa.setOnClickListener {
            val intent = Intent(this@MainActivity, AbasaActivity::class.java)
            startActivity(intent)
        }

        btnAnnaba.setOnClickListener {
            val intent = Intent(this@MainActivity, AnnabaActivity::class.java)
            startActivity(intent)
        }
    }
}
