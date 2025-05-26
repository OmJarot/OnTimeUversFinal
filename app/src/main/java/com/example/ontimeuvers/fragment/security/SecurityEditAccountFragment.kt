package com.example.ontimeuvers.fragment.security

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.model.UpdatePasswordRequest
import com.example.ontimeuvers.repository.AdminRepositoryImpl
import com.example.ontimeuvers.service.AdminServiceImpl
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty

class SecurityEditAccountFragment : Fragment(), Validator.ValidationListener {

    private val adminService = AdminServiceImpl(AdminRepositoryImpl())

    @NotEmpty(message = "Tidak boleh kosong")
    private lateinit var oldPasswordEditText : EditText
    @NotEmpty(message = "Tidak boleh kosong")
    private lateinit var newPasswordEditText : EditText
    @NotEmpty(message = "Tidak boleh kosong")
    private lateinit var retypePasswordEditText : EditText
    private lateinit var backImageButton: ImageButton

    private lateinit var gantiPasswordImageButton: ImageButton
    private lateinit var validator: Validator

    private fun initComponents(view: View){
        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText)
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText)
        retypePasswordEditText = view.findViewById(R.id.retypePasswordEditText)
        gantiPasswordImageButton = view.findViewById(R.id.gantiPasswordImageButton)
        backImageButton = view.findViewById(R.id.backImageButton)
        validator = Validator(this)
        validator.setValidationListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.security_edit_account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)

        gantiPasswordImageButton.setOnClickListener {
            validator.validate()
        }
        back()
    }

    override fun onValidationSucceeded() {
        requireActivity().runOnUiThread{
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val retypePassword = retypePasswordEditText.text.toString()

            val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val token = prefs.getString("token", null)

            val updatePasswordRequest = UpdatePasswordRequest()
            updatePasswordRequest.token = token
            updatePasswordRequest.oldPassword = oldPassword
            updatePasswordRequest.newPassword = newPassword
            updatePasswordRequest.retypePassword = retypePassword
            adminService.updatePassword(updatePasswordRequest).thenAccept { response ->
                requireActivity().runOnUiThread{
                    if ("Success".equals(response)){
                        Toast.makeText(requireContext(), "Success Edit Account", Toast.LENGTH_SHORT).show();
                        val newFragment = SecurityAccountFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, newFragment)
                            .commit()
                    }else{
                        Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                        null
                    }
                }
            }.exceptionally {
                requireActivity().runOnUiThread{
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
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