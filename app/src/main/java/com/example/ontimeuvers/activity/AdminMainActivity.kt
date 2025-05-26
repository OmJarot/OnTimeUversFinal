package com.example.ontimeuvers.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ontimeuvers.R
import com.example.ontimeuvers.filter.AdminBaseActivity
import com.example.ontimeuvers.fragment.admin.AdminAccountFragment
import com.example.ontimeuvers.fragment.admin.AdminHomeFragment
import com.example.ontimeuvers.fragment.admin.AdminQrFragment


class AdminMainActivity : AdminBaseActivity() {

    private lateinit var accountImageButton : ImageButton
    private lateinit var homeImageButton : ImageButton
    private lateinit var qrImageButton : ImageButton
    private lateinit var bottomBar : LinearLayout
    private lateinit var mainLayout : LinearLayout

    private var activeTab = "home"

    private fun initComponents(){
        accountImageButton = findViewById(R.id.accountImageButton)
        homeImageButton = findViewById(R.id.homeImageButton)
        qrImageButton = findViewById(R.id.qrImageButton)
        bottomBar = findViewById(R.id.bottomBar)
        mainLayout = findViewById(R.id.mainLayout)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.admin_activity_main)

        initComponents()

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0,  systemInsets.top, 0,0)
            insets
        }

        homeImageButton.setImageResource(R.drawable.home_focus)
        accountImageButton.setImageResource(R.drawable.account)
        qrImageButton.setImageResource(R.drawable.qr)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AdminHomeFragment())
            .commit()

        homeImageButton.setOnClickListener {
            if (activeTab != "home") {
                activeTab = "home"
                homeImageButton.setImageResource(R.drawable.home_focus)
                accountImageButton.setImageResource(R.drawable.account)
                qrImageButton.setImageResource(R.drawable.qr)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AdminHomeFragment())
                    .commit()
            }
        }

        accountImageButton.setOnClickListener {
            if (activeTab != "account") {
                activeTab = "account"
                accountImageButton.setImageResource(R.drawable.account_focus)
                homeImageButton.setImageResource(R.drawable.home)
                qrImageButton.setImageResource(R.drawable.qr)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AdminAccountFragment())
                    .commit()
            }
        }
        qrImageButton.setOnClickListener {
            if (activeTab != "qr") {
                activeTab = "qr"
                accountImageButton.setImageResource(R.drawable.account)
                homeImageButton.setImageResource(R.drawable.home)
                qrImageButton.setImageResource(R.drawable.qr_focus)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AdminQrFragment())
                    .commit()
            }
        }
    }
}