package com.example.ontimeuvers.fragment.admin

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.entity.User
import com.example.ontimeuvers.repository.DataKeterlambatanRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.AdminServiceImpl

class AdminCekDetailMahasiswaFragment : Fragment() {

    private val adminService = AdminServiceImpl(UserRepositoryImpl(), DataKeterlambatanRepositoryImpl())
    private lateinit var prefs: SharedPreferences
    private lateinit var nameDetailUserTextView: TextView
    private lateinit var nimDetailUserTextView: TextView
    private lateinit var jurusanDetailUserTextView: TextView
    private lateinit var userDetailTableLayout: TableLayout
    private lateinit var refreshButton: ImageButton
    private lateinit var backImageButton: ImageButton
    private var responseData: User? = null
    private var isLoading = false

    private fun initComponents(view: View){
        nameDetailUserTextView = view.findViewById(R.id.nameDetailUserTextView)
        nimDetailUserTextView = view.findViewById(R.id.nimDetailUserTextView)
        jurusanDetailUserTextView = view.findViewById(R.id.jurusanDetailUserTextView)
        userDetailTableLayout = view.findViewById(R.id.userDetailTableLayout)
        prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        refreshButton = view.findViewById(R.id.refreshButton)
        backImageButton = view.findViewById(R.id.backImageButton)
        responseData = arguments?.getSerializable("response_data") as? User
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.admin_detail_mahasiswa_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
        back()
        nameDetailUserTextView.text = responseData?.name
        nimDetailUserTextView.text = responseData?.nim
        jurusanDetailUserTextView.text = responseData?.jurusan?.nama

        getTable()

        refreshButton.setOnClickListener {
            if (isLoading) return@setOnClickListener

            isLoading = true
            refreshButton.isEnabled = false
            adminService.clearCacheDetail()
            getTable()
        }

    }

    private fun getTable(){
        userDetailTableLayout.removeAllViews()
        adminService.getDetailUser(responseData).thenAccept { datas ->
            requireActivity().runOnUiThread {
                for (data in datas) {
                    val tableRow = TableRow(requireContext())

                    val textTanggal = TextView(requireContext()).apply {
                        text = data.tanggal
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
                        Log.i("DetailActivity", "Add data tanggal: $text")
                        setBackgroundResource(R.drawable.border_table)
                    }

                    val textJam = TextView(requireContext()).apply {
                        text = data.jam
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
                        Log.i("DetailActivity", "Add data jam: $text")
                        setBackgroundResource(R.drawable.border_table)
                    }

                    val textMatkul = TextView(requireContext()).apply {
                        text = data.matkul
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT)
                        Log.i("DetailActivity", "Add data matkul: $text")
                        setBackgroundResource(R.drawable.border_table)
                    }

                    tableRow.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            tableRow.viewTreeObserver.removeOnGlobalLayoutListener(this)

                            val maxHeight = listOf(textTanggal, textJam, textMatkul).maxOf { it.height }

                            textTanggal.height = maxHeight
                            textJam.height = maxHeight
                            textMatkul.height = maxHeight

                        }
                    })

                    tableRow.addView(textTanggal)
                    tableRow.addView(textJam)
                    tableRow.addView(textMatkul)

                    userDetailTableLayout.addView(tableRow)
                }
                isLoading = false
                refreshButton.isEnabled = true
            }
        }.exceptionally {
            requireActivity().runOnUiThread {
                isLoading = false
                refreshButton.isEnabled = true
            }
            null
        }
    }
    private fun back(){
        backImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}