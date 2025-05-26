package com.example.ontimeuvers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ontimeuvers.activity.AdminMainActivity
import com.example.ontimeuvers.activity.DbaMainActivity
import com.example.ontimeuvers.activity.LoginActivity
import com.example.ontimeuvers.activity.SecurityMainActivity
import com.example.ontimeuvers.activity.UserMainActivity

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val nim = prefs.getString("nim", null)

        if (token == null || nim == null){
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()}, 1500)
        }else if (nim.equals("security")){
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, SecurityMainActivity::class.java)
                startActivity(intent)
                finish()}, 1500)
        }
        else if (nim.equals("admin")){
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, AdminMainActivity::class.java)
                startActivity(intent)
                finish()}, 1500)
        }
        else if (nim.equals("dba")){
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, DbaMainActivity::class.java)
                startActivity(intent)
                finish()}, 1500)
        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, UserMainActivity::class.java)
                startActivity(intent)
                finish()}, 1500)
        }
    }
}