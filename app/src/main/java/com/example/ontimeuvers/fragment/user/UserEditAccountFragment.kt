package com.example.ontimeuvers.fragment.user

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
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.UserServiceImpl
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty

class UserEditAccountFragment : Fragment(), Validator.ValidationListener {

    private val userService = UserServiceImpl(UserRepositoryImpl())

    @NotEmpty(message = "Tidak boleh kosong")
    private lateinit var oldPasswordEditText : EditText
    @NotEmpty(message = "Tidak boleh kosong")
    private lateinit var newPasswordEditText : EditText
    @NotEmpty(message = "Tidak boleh kosong")
    private lateinit var retypePasswordEditText : EditText

    private lateinit var gantiPasswordImageButton: ImageButton
    private lateinit var validator: Validator

    private lateinit var backImageButton: ImageButton

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_edit_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)


        gantiPasswordImageButton.setOnClickListener {
            validator.validate()
        }

        back()
    }

    private fun back(){
        backImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onValidationSucceeded() {
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
        userService.updatePassword(updatePasswordRequest).thenAccept { response ->
            requireActivity().runOnUiThread {
                if ("Success".equals(response)){
                    Toast.makeText(requireContext(), "Success Edit Account", Toast.LENGTH_SHORT).show();
                    val newFragment = UserAccountFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, newFragment)
                        .commit()
                }else{
                    Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show();
                    null
                }
            }
        }.exceptionally {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
            }
            null
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
}