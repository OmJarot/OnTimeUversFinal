package com.example.ontimeuvers.filter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.ontimeuvers.activity.LoginActivity
import com.example.ontimeuvers.repository.AdminRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.AuthServiceImpl

open class DbaBaseActivity : AppCompatActivity() {

    private var authService = AuthServiceImpl(UserRepositoryImpl(), AdminRepositoryImpl())
    private lateinit var prefs: SharedPreferences

    override fun onStart() {
        super.onStart()

        prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val expiredAt = prefs.getString("expiredAt", null)
        val level = prefs.getString("level", null)

        val expired: Long? = try {
            expiredAt?.toLong()
        } catch (e: NumberFormatException) {
            null
        }

        if (token == null){
            goToLogin()
            return
        }else if (expired == null || expired < System.currentTimeMillis()){
            authService.logout(token).whenComplete { _, _ ->
                runOnUiThread {
                    goToLogin()
                }
            }
        }else if(level == null || level != "dba"){
            authService.logout(token).whenComplete { _, _ ->
                runOnUiThread {
                    goToLogin()
                }
            }
        }
        else{
            authService.getAdminCurrent(token).thenAccept { admin ->
                runOnUiThread {
                    if (!admin.level.equals("dba")){
                        goToLogin()
                    }
                }
            }
                .exceptionally { ex ->
                runOnUiThread {
                    goToLogin()
                }
                null
            }
        }
    }

    fun back(id: Int){
        findViewById<ImageButton>(id)?.setOnClickListener{
            Log.i("Back", "Back activity")
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun goToLogin() {
        prefs.edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}