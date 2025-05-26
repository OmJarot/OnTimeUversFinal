package com.example.ontimeuvers.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.model.ErrorResponse

class UserFailedFragment : Fragment() {

    private lateinit var failedTextView : TextView
    private lateinit var cobaLagiFailedUserImageButton: ImageButton
    private lateinit var backImageButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_failed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)

        val responseError = arguments?.getSerializable("response_data") as? ErrorResponse

        failedTextView.text = responseError?.error

        cobaLagi()
        back()
    }


    private fun initComponents(view: View) {
        failedTextView = view.findViewById(R.id.failedTextView)
        cobaLagiFailedUserImageButton = view.findViewById(R.id.cobaLagiFailedUserImageButton)
        backImageButton = view.findViewById(R.id.backImageButton)
    }

    private fun cobaLagi(){
        cobaLagiFailedUserImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun back(){
        backImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}