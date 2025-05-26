package com.example.ontimeuvers.fragment.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R

class AdminHomeFragment : Fragment() {

    private lateinit var nameAdminTextView: TextView
    private lateinit var idAdminTextView : TextView

    private lateinit var dataTerlambatCardView: CardView
    private lateinit var mahasiswaCardView: CardView

    private fun initComponents(view: View){
        nameAdminTextView = view.findViewById(R.id.nameAdminTextView)
        idAdminTextView = view.findViewById(R.id.idAdminTextView)

        dataTerlambatCardView = view.findViewById(R.id.dataTerlambatCardView)
        mahasiswaCardView = view.findViewById(R.id.mahasiswaCardView)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.admin_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)

        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nama = prefs.getString("nama", null)
        val nim = prefs.getString("nim", null)

        nameAdminTextView.text = nama
        idAdminTextView.text = nim

        toDataTerlambat()
        toMahasiswa()
    }

    private fun toDataTerlambat(){
        dataTerlambatCardView.setOnClickListener{
            val newFragment = AdminDataTerlambatFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun toMahasiswa(){
        mahasiswaCardView.setOnClickListener{
            val newFragment = AdminMahasiswaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}