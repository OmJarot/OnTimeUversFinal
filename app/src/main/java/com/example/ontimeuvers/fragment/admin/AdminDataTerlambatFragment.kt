package com.example.ontimeuvers.fragment.admin

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.model.FindDataRequest
import com.example.ontimeuvers.repository.DataKeterlambatanRepositoryImpl
import com.example.ontimeuvers.repository.JurusanRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.AdminServiceImpl
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdminDataTerlambatFragment : Fragment() {

    private val adminService = AdminServiceImpl(UserRepositoryImpl(), DataKeterlambatanRepositoryImpl(), JurusanRepositoryImpl())

    private lateinit var request : FindDataRequest
    private lateinit var tanggalEditText : EditText

    private lateinit var searchEditText : EditText
    private lateinit var spinnerJurusan : Spinner
    private lateinit var spinnerAngkatan : Spinner

    private lateinit var dataTableLayout : TableLayout
    private lateinit var backImageButton: ImageButton
    private lateinit var refreshButton: ImageButton

    private var isSpinnerInitialized = false

    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isLoading = false

    private var jurusanInitialized = false
    private var angkatanInitialized = false

    private fun initComponents(view: View){
        tanggalEditText = view.findViewById(R.id.tanggalEditText)
        searchEditText = view.findViewById(R.id.searchEditText)
        spinnerJurusan = view.findViewById(R.id.spinnerJurusan)
        spinnerAngkatan = view.findViewById(R.id.spinnerAngkatan)
        refreshButton = view.findViewById(R.id.refreshButton)
        dataTableLayout = view.findViewById(R.id.dataTableLayout)
        backImageButton = view.findViewById(R.id.backImageButton)
        refreshButton = view.findViewById(R.id.refreshButton)
        request = FindDataRequest()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.admin_data_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents(view)
        back()
        getRequest()

        refreshButton.setOnClickListener {
            if (isLoading) return@setOnClickListener

            isLoading = true
            refreshButton.isEnabled = false
            adminService.clearCaches()
            request.tanggal = null
            tanggalEditText.setText("Pilih tanggal")
            getTable(request)
        }
    }

    private fun getRequest(){
        tanggalEditText.setOnClickListener {
            val today = LocalDate.now()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    tanggalEditText.setText(formattedDate)
                    request.tanggal = selectedDate
                    getTable(request)
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            )
            datePicker.setOnCancelListener {
                request.tanggal = null
                tanggalEditText.setText("Pilih tanggal")
                getTable(request)
            }

            datePicker.show()
        }

        val jurusan = mutableListOf("Pilih Jurusan")

        val angkatan = mutableListOf("Pilih Angkatan")

        adminService.allJurusan().thenAccept { allJurusan ->
            requireActivity().runOnUiThread {
                val jurusanList = allJurusan.map { it.nama }.distinct().sorted()
                jurusan.addAll(jurusanList)

                val adapterJurusan = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    jurusan
                ).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerJurusan.adapter = it
                }

                val angkatanList = allJurusan.mapNotNull { it.angkatan }.distinct().sorted()
                angkatan.addAll(angkatanList.map { it.toString() })

                val adapterAngkatan = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    angkatan
                ).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAngkatan.adapter = it
                }
                spinnerJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                        request.jurusan = if (position == 0) null else parent.getItemAtPosition(position).toString()
//                        if (isSpinnerInitialized) getTable(request)
                        if (jurusanInitialized) {
                            request.jurusan = if (position == 0) null else parent.getItemAtPosition(position).toString()
                            getTable(request)
                        } else {
                            jurusanInitialized = true
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        request.jurusan = null
                    }
                }

                spinnerAngkatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                        request.angkatan = if (position == 0) null else (parent.getItemAtPosition(position).toString().toIntOrNull())
//                        if (isSpinnerInitialized) getTable(request)
                        if (angkatanInitialized) {
                            request.jurusan = if (position == 0) null else parent.getItemAtPosition(position).toString()
                            getTable(request)
                        } else {
                            jurusanInitialized = true
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        request.angkatan = null
                    }
                }
            }
        }



        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { handler.removeCallbacks(it) }

                searchRunnable = Runnable {
                    request.name = s.toString()
                    getTable(request)
                }

                handler.postDelayed(searchRunnable!!, 800)
            }
        })
        getTable(request)
//        handler.postDelayed({
//            isSpinnerInitialized = true
//            getTable(request)
//        }, 300)
    }

    private fun getTable(findRequest: FindDataRequest){
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        dataTableLayout.removeAllViews()
        adminService.searchData(findRequest).thenAccept { datas ->
            requireActivity().runOnUiThread {
                for (data in datas) {
                    val tableRow = TableRow(requireContext())

                    val textNim = TextView(requireContext()).apply {
                        text = data.user.nim
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(110), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                        setTextColor(Color.BLUE)
                        setOnClickListener {
                            val fragment = AdminCekDetailMahasiswaFragment()

                            val bundle = Bundle().apply {
                                putSerializable("response_data", data.user)
                            }

                            fragment.arguments = bundle
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }

                    val textNama = TextView(requireContext()).apply {
                        text = data.user.name
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(190), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }
                    val textJurusan = TextView(requireContext()).apply {
                        text = data.user.jurusan.nama
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(160), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }

                    val textTanggal = TextView(requireContext()).apply {
                        text = data.waktu.toLocalDate().toString()
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(90), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }
                    val textJam = TextView(requireContext()).apply {
                        text = formatter.format(data.waktu.toLocalTime())
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(90), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }
                    val textMatkul = TextView(requireContext()).apply {
                        text = data.matkul
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }

                    tableRow.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            tableRow.viewTreeObserver.removeOnGlobalLayoutListener(this)

                            val maxHeight = listOf(textNim, textNama, textJurusan, textTanggal, textJam, textMatkul).maxOf { it.height }

                            textNim.height = maxHeight
                            textNama.height = maxHeight
                            textJurusan.height = maxHeight
                            textTanggal.height = maxHeight
                            textJam.height = maxHeight
                            textMatkul.height = maxHeight

                        }
                    })

                    tableRow.addView(textNim)
                    tableRow.addView(textNama)
                    tableRow.addView(textJurusan)
                    tableRow.addView(textTanggal)
                    tableRow.addView(textJam)
                    tableRow.addView(textMatkul)

                    dataTableLayout.addView(tableRow)
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