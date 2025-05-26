package com.example.ontimeuvers.filter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.ontimeuvers.activity.LoginActivity
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.AuthServiceImpl
import com.example.ontimeuvers.service.UserServiceImpl

open class BaseActivity : AppCompatActivity() {

    private var userService = UserServiceImpl(UserRepositoryImpl())
    private var authService = AuthServiceImpl(UserRepositoryImpl())
    private lateinit var prefs: SharedPreferences

    override fun onStart() {
        super.onStart()

        prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val expiredAt = prefs.getString("expiredAt", null)

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
        }
        else{
            userService.getCurrent(token).thenAccept { user ->
                runOnUiThread {
                    if (user.nim.equals("test2")){
                        goToLogin()
                    }else if (user.nim.equals("admin")) {
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