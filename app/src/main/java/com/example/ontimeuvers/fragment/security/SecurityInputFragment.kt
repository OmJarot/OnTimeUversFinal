package com.example.ontimeuvers.fragment.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.fragment.user.UserFailedFragment
import com.example.ontimeuvers.fragment.user.UserSuccessFragment
import com.example.ontimeuvers.model.ErrorResponse
import com.example.ontimeuvers.model.InputManualRequest
import com.example.ontimeuvers.repository.DataKeterlambatanRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.SecurityServiceImpl
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import java.time.LocalDateTime

class SecurityInputFragment : Fragment(), Validator.ValidationListener {

    private val securityService = SecurityServiceImpl(UserRepositoryImpl(), DataKeterlambatanRepositoryImpl())
    @NotEmpty(message = "NIM tidak boleh kosong")
    private lateinit var nimSecurityEditText : EditText
    @NotEmpty(message = "Nama tidak boleh kosong")
    private lateinit var namaSecurityEditText : EditText
    private lateinit var inputManualSecurityImageButton: ImageButton

    private lateinit var backImageButton: ImageButton

    private lateinit var validator: Validator

    private fun initComponents(view: View){
        nimSecurityEditText = view.findViewById(R.id.nimSecurityEditText)
        namaSecurityEditText = view.findViewById(R.id.namaSecurityEditText)
        inputManualSecurityImageButton = view.findViewById(R.id.inputManualSecurityImageButton)
        backImageButton = view.findViewById(R.id.backImageButton)

        validator = Validator(this)
        validator.setValidationListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.security_input_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)

        inputManualSecurityImageButton.setOnClickListener {
            validator.validate()
        }
        back()
    }

    override fun onValidationSucceeded() {
        requireActivity().runOnUiThread {
            val inputRequest = InputManualRequest().apply {
                nim = nimSecurityEditText.text.toString()
                name = namaSecurityEditText.text.toString()
                localDateTime = LocalDateTime.now()
            }

            securityService.inputDataManual(inputRequest).thenAccept { response ->
                requireActivity().runOnUiThread {
                    if (response == null) {
                        val errorResponse = ErrorResponse().apply {
                            error = "User not found"
                        }
                        val fragment = SecurityFailedFragment()

                        val bundle = Bundle().apply {
                            putSerializable("response_data", errorResponse)
                        }

                        fragment.arguments = bundle
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        val fragment = SecuritySuccessFragment()
                        val bundle = Bundle().apply {
                            putSerializable("response_data", response)
                        }
                        fragment.arguments = bundle
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit()
                    }
                }
            }.exceptionally { ex ->
                requireActivity().runOnUiThread {
                    val errorResponse = ErrorResponse().apply {
                        error = ex.message?.split(" ")?.drop(1)?.joinToString(" ") ?: "Terjadi kesalahan"
//                        error = ex.message
                    }
                    val fragment = SecurityFailedFragment()

                    val bundle = Bundle().apply {
                        putSerializable("response_data", errorResponse)
                    }

                    fragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                null
            }
        }
    }

    override fun onValidationFailed(errors: List<ValidationError>) {
        for (error in errors) {
            val view = error.view
            val message = error.getCollatedErrorMessage(requireContext())

            if (view is EditText) {
                view.error = message
            }
        }
    }
    private fun back(){
        backImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}