package com.example.ontimeuvers.fragment.security

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
import com.example.ontimeuvers.repository.DataKeterlambatanRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.SecurityServiceImpl
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SecurityDataTerlambatFragment : Fragment() {

    private val securityService = SecurityServiceImpl(UserRepositoryImpl(), DataKeterlambatanRepositoryImpl())
    private lateinit var nameDetailUserTextView : TextView
    private lateinit var securityDataTableLayout : TableLayout
    private lateinit var backImageButton: ImageButton
    private lateinit var refreshButton: ImageButton
    private var isLoading = false

    private fun initComponents(view: View){
        nameDetailUserTextView = view.findViewById(R.id.nameDetailUserTextView)
        securityDataTableLayout = view.findViewById(R.id.securityDataTableLayout)
        backImageButton = view.findViewById(R.id.backImageButton)
        refreshButton = view.findViewById(R.id.refreshButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.security_terlambat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)
        nameDetailUserTextView.text = LocalDate.now().toString()
        back()

        getTable()

        refreshButton.setOnClickListener {
            if (isLoading) return@setOnClickListener

            isLoading = true
            refreshButton.isEnabled = false
            securityService.clearCache()
            getTable()
        }
    }

    private fun getTable(){
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        securityDataTableLayout.removeAllViews()
        securityService.getAllDataToday(LocalDate.now()).thenAccept { datas ->
            requireActivity().runOnUiThread {

                for (data in datas) {
                    val tableRow = TableRow(requireContext())

                    val textNama = TextView(requireContext()).apply {
                        text = data.user.name
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(150), TableRow.LayoutParams.WRAP_CONTENT)
                        Log.i("DetailActivity", "Add data tanggal: $text")
                        setBackgroundResource(R.drawable.border_table)
                    }
                    val textNim = TextView(requireContext()).apply {
                        text = data.user.nim
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(120), TableRow.LayoutParams.WRAP_CONTENT)
                        Log.i("DetailActivity", "Add data tanggal: $text")
                        setBackgroundResource(R.drawable.border_table)
                    }
                    val textJurusan = TextView(requireContext()).apply {
                        text = data.user.jurusan.nama
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(160), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }
                    val textJam = TextView(requireContext()).apply {
                        text = formatter.format(data.jam)
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.MATCH_PARENT)
                        Log.i("DetailActivity", "Add data tanggal: $text")
                        setBackgroundResource(R.drawable.border_table)
                    }
                    val textMatkul = TextView(requireContext()).apply {
                        text = data.matkul
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(120), TableRow.LayoutParams.WRAP_CONTENT)
                        Log.i("DetailActivity", "Add data tanggal: $text")
                        setBackgroundResource(R.drawable.border_table)
                    }
                    tableRow.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            tableRow.viewTreeObserver.removeOnGlobalLayoutListener(this)

                            val maxHeight = listOf(textNim, textNama, textJurusan, textJam, textMatkul).maxOf { it.height }

                            textNim.height = maxHeight
                            textNama.height = maxHeight
                            textJurusan.height = maxHeight
                            textJam.height = maxHeight
                            textMatkul.height = maxHeight

                        }
                    })
                    tableRow.addView(textNama)
                    tableRow.addView(textNim)
                    tableRow.addView(textJurusan)
                    tableRow.addView(textJam)
                    tableRow.addView(textMatkul)

                    securityDataTableLayout.addView(tableRow)
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
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun back(){
        backImageButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}