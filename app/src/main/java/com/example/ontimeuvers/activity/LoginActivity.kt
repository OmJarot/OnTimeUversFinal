package com.example.ontimeuvers.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ontimeuvers.R
import com.example.ontimeuvers.model.LoginRequest
import com.example.ontimeuvers.repository.AdminRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.AuthServiceImpl
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty


class LoginActivity : AppCompatActivity(), Validator.ValidationListener{

    private val authServiceImpl = AuthServiceImpl(UserRepositoryImpl(), AdminRepositoryImpl())
    private lateinit var loginRequest: LoginRequest
    @NotEmpty(message = "NIM tidak boleh kosong")
    private lateinit var nimEditText: EditText
    @NotEmpty(message = "Password tidak boleh kosong")
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: ImageButton
    private lateinit var validator: Validator
    private lateinit var editTextWrong: TextView

    private fun initComponents(){
        nimEditText = findViewById(R.id.nimEditTextLogin)
        passwordEditText = findViewById(R.id.passwordEditTextLogin)
        loginButton = findViewById(R.id.loginImageButton)

        validator = Validator(this)
        validator.setValidationListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)

        initComponents()

        loginButton.setOnClickListener{
            validator.validate()
        }
    }

    override fun onValidationSucceeded() {
        val nim = nimEditText.text.toString()
        val password = passwordEditText.text.toString()
        loginRequest = LoginRequest(nim, password)

        authServiceImpl.login(loginRequest).thenAccept{ response ->
            val prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            if (response.level.equals("admin") || response.level.equals("security") || response.level.equals("dba")){
                prefs.edit()
                    .putString("nama", response.name)
                    .putString("nim", response.nim)
                    .putString("expiredAt", response.expiredAt.toString())
                    .putString("token", response.token)
                    .putString("level", response.level)
                    .apply()
            }else{
                prefs.edit()
                    .putString("nama", response.name)
                    .putString("nim", response.nim)
                    .putString("jurusan", response.jurusan)
                    .putString("expiredAt", response.expiredAt.toString())
                    .putString("token", response.token)
                    .putString("level", response.level)
                    .apply()
            }
            Log.i("Login", "Login Success")
            runOnUiThread {
                if (response.level.equals("security")){
                    val intent = Intent(this, SecurityMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else if (response.level.equals("admin")){
                    val intent = Intent(this, AdminMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if (response.level.equals("dba")){
                    val intent = Intent(this, DbaMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    val intent = Intent(this, UserMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.exceptionally { ex ->
            runOnUiThread {
                editTextWrong = findViewById(R.id.editTextWrong)
                editTextWrong.visibility = View.VISIBLE
            }
            null
        }
    }

    override fun onValidationFailed(errors: List<ValidationError>) {
        for (error in errors) {
            val view = error.view
            val message = error.getCollatedErrorMessage(this)

            if (view is EditText) {
                view.error = message
            }
        }
    }

}
