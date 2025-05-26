package com.example.ontimeuvers.fragment.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.activity.LoginActivity
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.AuthServiceImpl

class UserAccountFragment : Fragment() {

    private val authService = AuthServiceImpl(UserRepositoryImpl())
    private lateinit var nameAccountUserTextView : TextView
    private lateinit var nimAccountUserTextView : TextView
    private lateinit var jurusanAccountUserTextView : TextView

    private lateinit var editAccountImageButton: ImageButton
    private lateinit var logoutAccountImageButton: ImageButton


    private fun initComponents(view: View){
        nameAccountUserTextView = view.findViewById(R.id.nameAccountUserTextView)
        nimAccountUserTextView = view.findViewById(R.id.nimAccountUserTextView)
        jurusanAccountUserTextView = view.findViewById(R.id.jurusanAccountUserTextView)
        editAccountImageButton = view.findViewById(R.id.editAccountImageButton)
        logoutAccountImageButton = view.findViewById(R.id.logoutAccountImageButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)

        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nama = prefs.getString("nama", null)
        val nim = prefs.getString("nim", null)
        val jurusan = prefs.getString("jurusan", null)

        nameAccountUserTextView.text = nama
        nimAccountUserTextView.text = nim
        jurusanAccountUserTextView.text = jurusan

        editAccount()
        logout()
    }

    private fun editAccount(){
        editAccountImageButton.setOnClickListener{
            val newFragment = UserEditAccountFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun logout(){
        logoutAccountImageButton.setOnClickListener{
            val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val token = prefs.getString("token", null)
            authService.logout(token).thenAccept {
                requireActivity().runOnUiThread {
                    prefs.edit().clear().apply()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }.exceptionally {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Gagal Logout, Silakan coba lagi", Toast.LENGTH_SHORT).show();
                }
                null
            }
        }
    }
}