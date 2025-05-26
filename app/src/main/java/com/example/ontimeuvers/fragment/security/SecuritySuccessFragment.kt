package com.example.ontimeuvers.fragment.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.fragment.user.UserCekDetailFragment
import com.example.ontimeuvers.model.InputDataResponse

class SecuritySuccessFragment : Fragment() {

    private lateinit var nameStatusUserTextView : TextView
    private lateinit var nimStatusUserTextView : TextView
    private lateinit var tanggalStatusUserTextView : TextView
    private lateinit var jamStatusUserTextView : TextView
    private lateinit var matkulStatusUserTextView : TextView
    private lateinit var cekDetailStatusImageButton : ImageButton
    private lateinit var backImageButton: ImageButton

    private fun initComponents(view: View){
        nameStatusUserTextView = view.findViewById(R.id.nameStatusUserTextView)
        nimStatusUserTextView = view.findViewById(R.id.nimStatusUserTextView)
        tanggalStatusUserTextView = view.findViewById(R.id.tanggalStatusUserTextView)
        jamStatusUserTextView = view.findViewById(R.id.jamStatusUserTextView)
        matkulStatusUserTextView = view.findViewById(R.id.matkulStatusUserTextView)
        cekDetailStatusImageButton = view.findViewById(R.id.cekDetailStatusImageButton)
        backImageButton = view.findViewById(R.id.backImageButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.security_success_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)
        val responseData = arguments?.getSerializable("response_data") as? InputDataResponse

        if(responseData != null){
            nameStatusUserTextView.text = responseData.user.name
            nimStatusUserTextView.text = responseData.user.nim
            tanggalStatusUserTextView.text = responseData.tanggal
            jamStatusUserTextView.text = responseData.jam
            matkulStatusUserTextView.text = responseData.matkul
        }
        back()

        cekDetailStatusImageButton.setOnClickListener{
            val newFragment = SecurityDataTerlambatFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .commit()
        }
        back()
    }

    private fun back(){
        backImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}