package com.example.ontimeuvers.fragment.dba

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

class DbaHomeFragment : Fragment() {

    private lateinit var nameDbaTextView: TextView
    private lateinit var idDbaTextView : TextView

    private lateinit var akunMahasiswaCardView: CardView
    private lateinit var jurusanCardView: CardView

    private fun initComponents(view: View){
        nameDbaTextView = view.findViewById(R.id.nameDbaTextView)
        idDbaTextView = view.findViewById(R.id.idDbaTextView)

        akunMahasiswaCardView = view.findViewById(R.id.akunMahasiswaCardView)
        jurusanCardView = view.findViewById(R.id.jurusanCardView)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dba_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)

        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nama = prefs.getString("nama", null)
        val nim = prefs.getString("nim", null)

        nameDbaTextView.text = nama
        idDbaTextView.text = nim

        toJurusan()
        toMahasiswa()
    }

    private fun toJurusan(){
        jurusanCardView.setOnClickListener{
            val newFragment = DbaJurusanFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun toMahasiswa(){
        akunMahasiswaCardView.setOnClickListener{
            val newFragment = DbaMahasiswaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}