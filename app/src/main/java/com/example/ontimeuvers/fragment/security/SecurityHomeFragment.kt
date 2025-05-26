package com.example.ontimeuvers.fragment.security

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.fragment.user.UserCekDetailFragment

class SecurityHomeFragment : Fragment() {

    private lateinit var nameSecurityTextView: TextView
    private lateinit var idSecurityTextView: TextView

    private lateinit var inputManualSecurityCardView: CardView
    private lateinit var cekTerlambatSecurityCardView: CardView


    private fun initComponents(view: View){
        nameSecurityTextView = view.findViewById(R.id.nameSecurityTextView)
        idSecurityTextView = view.findViewById(R.id.idSecurityTextView)
        inputManualSecurityCardView = view.findViewById(R.id.inputManualSecurityCardView)
        cekTerlambatSecurityCardView = view.findViewById(R.id.cekTerlambatSecurityCardView)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.security_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)

        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nama = prefs.getString("nama", null)
        val nim = prefs.getString("nim", null)

        nameSecurityTextView.text = nama
        idSecurityTextView.text = nim

        toInput()
        toCekTerlambat()

    }

    private fun toInput(){
        inputManualSecurityCardView.setOnClickListener{
            val newFragment = SecurityInputFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun toCekTerlambat(){
        cekTerlambatSecurityCardView.setOnClickListener{
            val newFragment = SecurityDataTerlambatFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}