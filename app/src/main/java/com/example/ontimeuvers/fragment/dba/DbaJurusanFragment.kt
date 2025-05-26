package com.example.ontimeuvers.fragment.dba

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.entity.Jurusan
import com.example.ontimeuvers.model.FindJurusanRequest
import com.example.ontimeuvers.repository.JurusanRepositoryImpl
import com.example.ontimeuvers.repository.MatkulRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl

import com.example.ontimeuvers.service.DBAServiceImpl
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty

class DbaJurusanFragment : Fragment(), Validator.ValidationListener {

    private val dbaService = DBAServiceImpl(UserRepositoryImpl(), MatkulRepositoryImpl(), JurusanRepositoryImpl())
    private lateinit var request : FindJurusanRequest

    private lateinit var spinnerJurusan : Spinner
    private lateinit var spinnerAngkatan : Spinner
    private lateinit var tableMahasiswa: TableLayout
    private lateinit var refreshButton: ImageButton
    private lateinit var backImageButton: ImageButton
    private val handler = Handler(Looper.getMainLooper())
    private var isSpinnerInitialized = false
    private var isLoading = false

    private lateinit var focusLinearLayout : LinearLayout

    private lateinit var formHapusJurusan : LinearLayout
    private lateinit var jurusanHapusTextView : TextView
    private lateinit var closeHapusButton : ImageButton
    private lateinit var batalHapusButton : Button
    private lateinit var yaHapusJurusan : Button

    private lateinit var formAddJurusan : LinearLayout
    private lateinit var closeAddJurusanButton : ImageButton
    @NotEmpty(message = "Tidak boleh kosong")
    private lateinit var nameAddJurusanEditText : EditText
    @NotEmpty(message = "Tidak boleh kosong")
    private lateinit var angkatanAddJurusanEditText : EditText
    private lateinit var addJurusanButton : Button
    private lateinit var addNewJurusanButton : Button
    private lateinit var validator: Validator

    private fun initComponents(view: View){
        spinnerJurusan = view.findViewById(R.id.spinnerJurusan)
        spinnerAngkatan = view.findViewById(R.id.spinnerAngkatan)
        tableMahasiswa = view.findViewById(R.id.tableMahasiswa)
        refreshButton = view.findViewById(R.id.refreshButton)
        request = FindJurusanRequest()
        backImageButton = view.findViewById(R.id.backImageButton)
        focusLinearLayout = view.findViewById(R.id.focusLinearLayout)
        formHapusJurusan = view.findViewById(R.id.formHapusJurusan)
        jurusanHapusTextView = view.findViewById(R.id.jurusanHapusTextView)
        closeHapusButton = view.findViewById(R.id.closeHapusButton)
        batalHapusButton = view.findViewById(R.id.batalHapusButton)
        yaHapusJurusan = view.findViewById(R.id.yaHapusJurusan)
        addJurusanButton = view.findViewById(R.id.addJurusanButton)
        formAddJurusan = view.findViewById(R.id.formAddJurusan)
        closeAddJurusanButton = view.findViewById(R.id.closeAddJurusanButton)

        nameAddJurusanEditText = view.findViewById(R.id.nameAddJurusanEditText)
        angkatanAddJurusanEditText = view.findViewById(R.id.angkatanAddJurusanEditText)

        addNewJurusanButton = view.findViewById(R.id.addNewJurusanButton)
        validator = Validator(this)
        validator.setValidationListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dba_jurusan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)
        back()

        requestData()

        refresh()

        addJurusan()
    }

    private fun refresh(){
        refreshButton.setOnClickListener {
            if (isLoading) return@setOnClickListener

            isLoading = true
            refreshButton.isEnabled = false
            dbaService.refreshAllJurusans()
            requestData()
            getTable(request)
        }
    }

    private fun addJurusan(){
        addJurusanButton.setOnClickListener {
            focusLinearLayout.visibility = View.VISIBLE
            formAddJurusan.visibility = View.VISIBLE
            nameAddJurusanEditText.text = null
            angkatanAddJurusanEditText.text = null
        }
        closeAddJurusanButton.setOnClickListener {
            focusLinearLayout.visibility = View.GONE
            formAddJurusan.visibility = View.GONE
            nameAddJurusanEditText.text = null
            angkatanAddJurusanEditText.text = null
        }
        addNewJurusanButton.setOnClickListener {
            validator.validate()
        }
    }
    override fun onValidationSucceeded() {
        val jurusan = Jurusan()
        jurusan.nama = nameAddJurusanEditText.text.toString()
        jurusan.angkatan = angkatanAddJurusanEditText.text.toString().toIntOrNull()
        dbaService.addNewJurusan(jurusan).thenAccept { jurusanResponse ->
            focusLinearLayout.visibility = View.GONE
            formAddJurusan.visibility = View.GONE
            nameAddJurusanEditText.text = null
            angkatanAddJurusanEditText.text = null
            refresh()
            Toast.makeText(requireContext(), "Success Add new jurusan: "+ jurusanResponse.nama, Toast.LENGTH_SHORT).show();
        }.exceptionally { ex ->
            Toast.makeText(requireContext(), "Jurusan sudah ada", Toast.LENGTH_SHORT).show();
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

    private fun requestData(){
        val jurusan = mutableListOf("Pilih Jurusan")

        val angkatan = mutableListOf("Pilih Angkatan")

        dbaService.allJurusan().thenAccept { allJurusan ->
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
            }
        }

        spinnerJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                request.jurusan = if (position == 0) null else parent.getItemAtPosition(position).toString()
                if (isSpinnerInitialized) getTable(request)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                request.jurusan = null
            }
        }

        spinnerAngkatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                request.angkatan = if (position == 0) null else (parent.getItemAtPosition(position).toString().toIntOrNull())
                if (isSpinnerInitialized) getTable(request)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                request.angkatan = null
            }
        }

        handler.postDelayed({
            isSpinnerInitialized = true
            getTable(request)
        }, 300)
    }


    private fun getTable(request: FindJurusanRequest){
        tableMahasiswa.removeAllViews()
        dbaService.filterJurusan(request).thenAccept { datas ->
            requireActivity().runOnUiThread {
                for (data in datas) {
                    val tableRow = TableRow(requireContext())

                    val textNama = TextView(requireContext()).apply {
                        text = data.nama
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(150), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }

                    val textAngkatan = TextView(requireContext()).apply {
                        text = data.angkatan.toString()
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(120), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }

                    val textHapus = TextView(requireContext()).apply {
                        text = "Hapus"
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                        setTextColor(Color.RED)
                        setOnClickListener {
                            focusLinearLayout.visibility = View.VISIBLE
                            formHapusJurusan.visibility = View.VISIBLE
                            jurusanHapusTextView.text = data.nama+ " " +data.angkatan

                            closeHapus(data.nama, data.angkatan)
                        }
                    }

                    tableRow.addView(textNama)
                    tableRow.addView(textAngkatan)
                    tableRow.addView(textHapus)

                    tableRow.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            tableRow.viewTreeObserver.removeOnGlobalLayoutListener(this)

                            val maxHeight = listOf(textNama, textAngkatan, textHapus).maxOf { it.height }

                            textNama.height = maxHeight
                            textAngkatan.height = maxHeight
                            textHapus.height = maxHeight
                        }
                    })

                    tableMahasiswa.addView(tableRow)
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
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun closeHapus(jurusan : String, angkatan : Int){
        closeHapusButton.setOnClickListener {
            focusLinearLayout.visibility = View.GONE
            formHapusJurusan.visibility = View.GONE
            jurusanHapusTextView.text = null
        }
        batalHapusButton.setOnClickListener {
            focusLinearLayout.visibility = View.GONE
            formHapusJurusan.visibility = View.GONE
            jurusanHapusTextView.text = null
        }

        yaHapusJurusan.setOnClickListener {
            dbaService.removeJurusan(jurusan, angkatan).thenAccept {
                requireActivity().runOnUiThread{
                    focusLinearLayout.visibility = View.GONE
                    formHapusJurusan.visibility = View.GONE
                    jurusanHapusTextView.text = null

                    if (isLoading) return@runOnUiThread

                    isLoading = true
                    refreshButton.isEnabled = false
                    dbaService.refreshAllJurusans()
                    getTable(request)

                    Toast.makeText(requireContext(), "Success Hapus jurusan", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}