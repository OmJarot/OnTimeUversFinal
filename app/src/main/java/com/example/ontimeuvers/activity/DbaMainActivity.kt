package com.example.ontimeuvers.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ontimeuvers.R
import com.example.ontimeuvers.filter.DbaBaseActivity
import com.example.ontimeuvers.fragment.dba.DbaAccountFragment
import com.example.ontimeuvers.fragment.dba.DbaHomeFragment


class DbaMainActivity : DbaBaseActivity() {

    private lateinit var accountImageButton : ImageButton
    private lateinit var homeImageButton : ImageButton
    private lateinit var bottomBar : LinearLayout
    private lateinit var mainLayout : LinearLayout

    private var activeTab = "home"

    private fun initComponents(){
        accountImageButton = findViewById(R.id.accountImageButton)
        homeImageButton = findViewById(R.id.homeImageButton)
        bottomBar = findViewById(R.id.bottomBar)
        mainLayout = findViewById(R.id.mainLayout)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.dba_activity_main)
        initComponents()

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0,  systemInsets.top, 0,0)
            insets
        }

        homeImageButton.setImageResource(R.drawable.home_focus)
        accountImageButton.setImageResource(R.drawable.account)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DbaHomeFragment())
            .commit()


        homeImageButton.setOnClickListener {
            if (activeTab != "home") {
                activeTab = "home"
                homeImageButton.setImageResource(R.drawable.home_focus)
                accountImageButton.setImageResource(R.drawable.account)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DbaHomeFragment())
                    .commit()
            }
        }

        accountImageButton.setOnClickListener {
            if (activeTab != "account") {
                activeTab = "account"
                accountImageButton.setImageResource(R.drawable.account_focus)
                homeImageButton.setImageResource(R.drawable.home)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DbaAccountFragment())
                    .commit()
            }
        }
    }
}