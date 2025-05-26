package com.example.ontimeuvers.fragment.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.ontimeuvers.R
import com.example.ontimeuvers.model.ErrorResponse
import com.example.ontimeuvers.model.InputDataRequest
import com.example.ontimeuvers.repository.DataKeterlambatanRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.UserServiceImpl
import java.time.LocalDateTime


class UserScanQrFragment : Fragment() {

    private val userService = UserServiceImpl(UserRepositoryImpl(), DataKeterlambatanRepositoryImpl())
    private lateinit var codeScanner: CodeScanner
    private lateinit var backImageButton: ImageButton

    private fun initComponents(view: View){
        backImageButton = view.findViewById(R.id.backImageButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)
        back()

        val scanView = view.findViewById<CodeScannerView>(R.id.codeScannerView)
        codeScanner = CodeScanner(requireContext(), scanView)

        codeScanner.decodeCallback = DecodeCallback { result ->
            requireActivity().runOnUiThread {
                val text = result.text
                if (text == "piterpangaribuan") {
                    val inputDataRequest = InputDataRequest()
                    val token = activity?.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        ?.getString("token", null)
                    inputDataRequest.localDateTime = LocalDateTime.now()

                    userService.inputDataKeterlambatan(inputDataRequest, token).thenAccept { response ->
                        activity?.runOnUiThread {
                            if (response == null) {
                                val errorResponse = ErrorResponse().apply {
                                    error = "User not found"
                                }
                                val fragment = UserFailedFragment()

                                val bundle = Bundle().apply {
                                    putSerializable("response_data", errorResponse)
                                }

                                fragment.arguments = bundle
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit()

                            } else {
                                val fragment = UserSuccessFragment()
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
                            }
                            val fragment = UserFailedFragment()

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
                } else {
                    val errorResponse = ErrorResponse().apply {
                        error = "QR Code tidak valid"
                    }
                    val fragment = UserFailedFragment()

                    val bundle = Bundle().apply {
                        putSerializable("response_data", errorResponse)
                    }

                    fragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        scanView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun back(){
        backImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

}