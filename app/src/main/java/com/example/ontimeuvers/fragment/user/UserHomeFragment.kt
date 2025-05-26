package com.example.ontimeuvers.fragment.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.ontimeuvers.R

class UserHomeFragment : Fragment() {

    private lateinit var nameHomeUserTextView: TextView
    private lateinit var nimHomeUserTextView: TextView
    private lateinit var jurusanHomeUserTextView: TextView

    private lateinit var scanQRCardView : CardView
    private lateinit var cekDetailCardView : CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
        loadUserData()
        setupListeners()
    }


    private fun initComponents(view: View) {
        nameHomeUserTextView = view.findViewById(R.id.nameHomeUserTextView)
        nimHomeUserTextView = view.findViewById(R.id.nimHomeUserTextView)
        jurusanHomeUserTextView = view.findViewById(R.id.jurusanHomeUserTextView)

        scanQRCardView = view.findViewById(R.id.scanQRCardView)
        cekDetailCardView = view.findViewById(R.id.cekDetailCardView)
    }

    private fun loadUserData() {
        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        nameHomeUserTextView.text = prefs.getString("nama", "Nama")
        nimHomeUserTextView.text = prefs.getString("nim", "NIM")
        jurusanHomeUserTextView.text = prefs.getString("jurusan", "Jurusan")
    }

    private fun setupListeners() {
        scanQRCardView.setOnClickListener {
            val newFragment = UserScanQrFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }

        cekDetailCardView.setOnClickListener {
            val newFragment = UserCekDetailFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}