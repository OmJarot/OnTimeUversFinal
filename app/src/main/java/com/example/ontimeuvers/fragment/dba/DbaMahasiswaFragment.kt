package com.example.ontimeuvers.fragment.dba

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.ontimeuvers.R
import com.example.ontimeuvers.entity.Jurusan
import com.example.ontimeuvers.entity.User
import com.example.ontimeuvers.model.AddNewUserRequest
import com.example.ontimeuvers.model.EditMatkulRequest
import com.example.ontimeuvers.model.EditUserRequest
import com.example.ontimeuvers.model.FindMahasiswaRequest
import com.example.ontimeuvers.repository.JurusanRepositoryImpl
import com.example.ontimeuvers.repository.MatkulRepositoryImpl
import com.example.ontimeuvers.repository.UserRepositoryImpl
import com.example.ontimeuvers.service.DBAServiceImpl
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty

class DbaMahasiswaFragment : Fragment(), Validator.ValidationListener {

    private val dbaService = DBAServiceImpl(UserRepositoryImpl(), MatkulRepositoryImpl(), JurusanRepositoryImpl())
    private lateinit var request : FindMahasiswaRequest
    private lateinit var spinnerJurusan : Spinner
    private lateinit var spinnerAngkatan : Spinner
    private lateinit var searchEditText : EditText
    private lateinit var tableMahasiswa: TableLayout
    private lateinit var refreshButton: ImageButton
    private lateinit var backImageButton: ImageButton
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var isSpinnerInitialized = false
    private var isLoading = false

    private lateinit var focusLinearLayout : LinearLayout

    private lateinit var formAddAccount : LinearLayout
    private lateinit var addAccountButton : Button
    @NotEmpty(message = "NIM tidak boleh kosong")
    private lateinit var nimAddAccountEditText : EditText
    @NotEmpty(message = "Nama tidak boleh kosong")
    private lateinit var nameAddAccountEditText : EditText
    private lateinit var jurusanAddAccountSpinner : Spinner
    private lateinit var angkatanAddAccountSpinner : Spinner
    @NotEmpty(message = "Password tidak boleh kosong")
    private lateinit var passwordAddAccountEditText : EditText
    private lateinit var addNewAccountButton : Button
    private lateinit var closeAddButton : ImageButton

    private lateinit var formHapusAccount : LinearLayout
    private lateinit var closeAddHapusButton : ImageButton
    private lateinit var batalHapusButton : Button
    private lateinit var yaHapusAccount : Button
    private lateinit var jurusanHapusTextView : TextView

    private lateinit var closeEditButton : ImageButton
    private lateinit var formEditAccount : LinearLayout
    private lateinit var nameEditAccountEditText : EditText
    private lateinit var jurusanEditAccountSpinner : Spinner
    private lateinit var angkatanEditAccountSpinner : Spinner
    private lateinit var passwordEditAccountEditText : EditText
    private lateinit var editAccountButton : Button
    private lateinit var editAccountTextView : TextView

    private lateinit var formMatkulAccount : LinearLayout
    private lateinit var nameMatkulTextView : TextView
    private lateinit var nimMatkulTextView : TextView
    private lateinit var jurusanMatkulTextView : TextView
    private lateinit var detailMatkulTableLayout : TableLayout
    private lateinit var matkulButton : Button
    private lateinit var closeMatkulImageButton : ImageButton

    private var jurusanInitialized = false
    private var angkatanInitialized = false

    val jurusan = mutableListOf("Pilih Jurusan")
    val angkatan = mutableListOf("Pilih Angkatan")


    private lateinit var validator: Validator

    private fun initComponents(view: View){
        spinnerJurusan = view.findViewById(R.id.spinnerJurusan)
        spinnerAngkatan = view.findViewById(R.id.spinnerAngkatan)
        searchEditText = view.findViewById(R.id.searchEditText)
        tableMahasiswa = view.findViewById(R.id.tableMahasiswa)
        refreshButton = view.findViewById(R.id.refreshButton)
        request = FindMahasiswaRequest()
        backImageButton = view.findViewById(R.id.backImageButton)

        focusLinearLayout = view.findViewById(R.id.focusLinearLayout)

        addAccountButton = view.findViewById(R.id.addAccountButton)
        nimAddAccountEditText = view.findViewById(R.id.nimAddAccountEditText)
        nameAddAccountEditText = view.findViewById(R.id.nameAddAccountEditText)
        jurusanAddAccountSpinner = view.findViewById(R.id.jurusanAddAccountSpinner)
        angkatanAddAccountSpinner = view.findViewById(R.id.angkatanAddAccountSpinner)
        passwordAddAccountEditText = view.findViewById(R.id.passwordAddAccountEditText)
        addNewAccountButton = view.findViewById(R.id.addNewAccountButton)
        formAddAccount = view.findViewById(R.id.formAddAccount)
        closeAddButton = view.findViewById(R.id.closeAddButton)

        formHapusAccount = view.findViewById(R.id.formHapusAccount)
        closeAddHapusButton = view.findViewById(R.id.closeAddHapusButton)
        batalHapusButton = view.findViewById(R.id.batalHapusButton)
        yaHapusAccount = view.findViewById(R.id.yaHapusAccount)
        jurusanHapusTextView = view.findViewById(R.id.jurusanHapusTextView)

        closeEditButton = view.findViewById(R.id.closeEditButton)
        nameEditAccountEditText = view.findViewById(R.id.nameEditAccountEditText)
        jurusanEditAccountSpinner = view.findViewById(R.id.jurusanEditAccountSpinner)
        angkatanEditAccountSpinner = view.findViewById(R.id.angkatanEditAccountSpinner)
        passwordEditAccountEditText = view.findViewById(R.id.passwordEditAccountEditText)
        editAccountButton = view.findViewById(R.id.editAccountButton)
        formEditAccount = view.findViewById(R.id.formEditAccount)
        editAccountTextView = view.findViewById(R.id.editAccountTextView)

        formMatkulAccount = view.findViewById(R.id.formMatkulAccount)
        nameMatkulTextView = view.findViewById(R.id.nameMatkulTextView)
        nimMatkulTextView = view.findViewById(R.id.nimMatkulTextView)
        jurusanMatkulTextView = view.findViewById(R.id.jurusanMatkulTextView)
        detailMatkulTableLayout = view.findViewById(R.id.detailMatkulTableLayout)
        matkulButton = view.findViewById(R.id.matkulButton)
        closeMatkulImageButton = view.findViewById(R.id.closeMatkulImageButton)

        validator = Validator(this)
        validator.setValidationListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dba_mahasiswa_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents(view)
        back()

        spinner()
        requestData()

        refreshButton.setOnClickListener {
            if (isLoading) return@setOnClickListener

            isLoading = true
            refreshButton.isEnabled = false
            dbaService.clearCache()
            getTable(request)
        }
        addAccount()
        closeButton()
    }

    private fun createJadwalRow(
        hari: String,
        sesi1Value: String?,
        sesi2Value: String?,
        sesi1Edit: MutableList<EditText>,
        sesi2Edit: MutableList<EditText>
    ): TableRow {
        val row = TableRow(requireContext())

        val label = TextView(requireContext()).apply {
            text = hari
            setPadding(8, 8, 8, 8)
            setBackgroundResource(R.drawable.border_table)
            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
        }

        val sesi1 = EditText(requireContext()).apply {
            setText(sesi1Value)
            setPadding(8, 8, 8, 8)
            setBackgroundResource(R.drawable.border_table)
            inputType = InputType.TYPE_CLASS_TEXT
            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
            textSize = 16f
            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
        }

        val sesi2 = EditText(requireContext()).apply {
            setText(sesi2Value)
            setPadding(8, 8, 8, 8)
            setBackgroundResource(R.drawable.border_table)
            inputType = InputType.TYPE_CLASS_TEXT
            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
            textSize = 16f
            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
        }

        sesi1Edit.add(sesi1)
        sesi2Edit.add(sesi2)

        row.addView(label)
        row.addView(sesi1)
        row.addView(sesi2)

        return row
    }

    private fun matkul(userMatkul : User){
        focusLinearLayout.visibility = View.VISIBLE
        formMatkulAccount.visibility = View.VISIBLE

        nameMatkulTextView.text = userMatkul.name
        nimMatkulTextView.text = userMatkul.nim
        jurusanMatkulTextView.text = userMatkul.jurusan.nama

        detailMatkulTableLayout.removeAllViews()

        val jadwal = userMatkul.matkul?.jadwal ?: emptyMap()

        val sesi1List = mutableListOf<EditText>()
        val sesi2List = mutableListOf<EditText>()

        val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
        for (day in days) {
            val row = createJadwalRow(
                day,
                jadwal[day]?.get("sesi1"),
                jadwal[day]?.get("sesi2"),
                sesi1List,
                sesi2List
            )
            detailMatkulTableLayout.addView(row)
        }

        matkulButton.setOnClickListener {
            val request = EditMatkulRequest()
            request.matkul = HashMap()

            val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
            for (i in days.indices) {
                val sesi1Text = sesi1List.getOrNull(i)?.text?.toString()?.trim().orEmpty()
                val sesi2Text = sesi2List.getOrNull(i)?.text?.toString()?.trim().orEmpty()

                request.matkul[days[i]] = mapOf(
                    "sesi1" to sesi1Text,
                    "sesi2" to sesi2Text
                )
            }
            request.nim = userMatkul.nim

            dbaService.editUserMatkul(request).thenAccept {
                focusLinearLayout.visibility = View.GONE
                formMatkulAccount.visibility = View.GONE
                Toast.makeText(context, "Berhasil update matkul user: " + userMatkul.nim, Toast.LENGTH_SHORT).show()
            }.exceptionally { ex ->
                Toast.makeText(context, "Gagal update matkul user: ${ex.cause?.message ?: ex.message}", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

//    private fun matkul(userMatkul: User){
//        focusLinearLayout.visibility = View.VISIBLE
//        formMatkulAccount.visibility = View.VISIBLE
//
//        nameMatkulTextView.text = userMatkul.name
//        nimMatkulTextView.text = userMatkul.nim
//        jurusanMatkulTextView.text = userMatkul.jurusan.nama
//
//        detailMatkulTableLayout.removeAllViews()
//
//        val jadwal = userMatkul.matkul?.jadwal ?: emptyMap()
//
//        val rowSenin = TableRow(requireContext())
//
//        val textSenin = TextView(requireContext()).apply {
//            text = "Senin"
//            setPadding(8, 8, 8, 8)
//            setTypeface(null, Typeface.NORMAL)
//            setBackgroundResource(R.drawable.border_table)
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val seninS1 = EditText(requireContext()).apply {
//            setText(jadwal["Senin"]?.get("sesi1"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val seninS2 = EditText(requireContext()).apply {
//            setText(jadwal["Senin"]?.get("sesi2"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//
//        rowSenin.addView(textSenin)
//        rowSenin.addView(seninS1)
//        rowSenin.addView(seninS2)
//
//        val rowSelasa = TableRow(requireContext())
//
//        val textSelasa = TextView(requireContext()).apply {
//            text = "Selasa"
//            setPadding(8, 8, 8, 8)
//            setTypeface(null, Typeface.NORMAL)
//            setBackgroundResource(R.drawable.border_table)
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val selasaS1 = EditText(requireContext()).apply {
//            setText(jadwal["Selasa"]?.get("sesi1"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val selasaS2 = EditText(requireContext()).apply {
//            setText(jadwal["Selasa"]?.get("sesi2"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//
//        rowSelasa.addView(textSelasa)
//        rowSelasa.addView(selasaS1)
//        rowSelasa.addView(selasaS2)
//
//        val rowRabu = TableRow(requireContext())
//
//        val textRabu = TextView(requireContext()).apply {
//            text = "Rabu"
//            setPadding(8, 8, 8, 8)
//            setTypeface(null, Typeface.NORMAL)
//            setBackgroundResource(R.drawable.border_table)
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val rabuS1 = EditText(requireContext()).apply {
//            setText(jadwal["Rabu"]?.get("sesi1"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val rabuS2 = EditText(requireContext()).apply {
//            setText(jadwal["Rabu"]?.get("sesi2"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//
//        rowRabu.addView(textRabu)
//        rowRabu.addView(rabuS1)
//        rowRabu.addView(rabuS2)
//
//        val rowKamis = TableRow(requireContext())
//
//        val textKamis = TextView(requireContext()).apply {
//            text = "Kamis"
//            setPadding(8, 8, 8, 8)
//            setTypeface(null, Typeface.NORMAL)
//            setBackgroundResource(R.drawable.border_table)
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val kamisS1 = EditText(requireContext()).apply {
//            setText(jadwal["Kamis"]?.get("sesi1"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val kamisS2 = EditText(requireContext()).apply {
//            setText(jadwal["Kamis"]?.get("sesi2"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//
//        rowKamis.addView(textKamis)
//        rowKamis.addView(kamisS1)
//        rowKamis.addView(kamisS2)
//
//        val rowJumat = TableRow(requireContext())
//
//        val textJumat = TextView(requireContext()).apply {
//            text = "Jumat"
//            setPadding(8, 8, 8, 8)
//            setTypeface(null, Typeface.NORMAL)
//            setBackgroundResource(R.drawable.border_table)
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val jumatS1 = EditText(requireContext()).apply {
//            setText(jadwal["Jumat"]?.get("sesi1"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//        val jumatS2 = EditText(requireContext()).apply {
//            setText(jadwal["Jumat"]?.get("sesi2"))
//            setPadding(8, 8, 8, 8)
//            setBackgroundResource(R.drawable.border_table)
//            inputType = InputType.TYPE_CLASS_TEXT
//            typeface = ResourcesCompat.getFont(context, R.font.creatoregular)
//            textSize = 16f
//            layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.WRAP_CONTENT)
//        }
//
//        rowJumat.addView(textJumat)
//        rowJumat.addView(jumatS1)
//        rowJumat.addView(jumatS2)
//
//        detailMatkulTableLayout.addView(rowSenin)
//        detailMatkulTableLayout.addView(rowSelasa)
//        detailMatkulTableLayout.addView(rowRabu)
//        detailMatkulTableLayout.addView(rowKamis)
//        detailMatkulTableLayout.addView(rowJumat)
//
//
//        matkulButton.setOnClickListener {
//            val request = EditMatkulRequest()
//            request.matkul = HashMap()
//
//            request.matkul["Senin"] = mapOf(
//                "sesi1" to seninS1.text.toString(),
//                "sesi2" to seninS2.text.toString()
//            )
//
//            request.matkul["Selasa"] = mapOf(
//                "sesi1" to selasaS1.text.toString(),
//                "sesi2" to selasaS2.text.toString()
//            )
//
//            request.matkul["Rabu"] = mapOf(
//                "sesi1" to rabuS1.text.toString(),
//                "sesi2" to rabuS2.text.toString()
//            )
//
//            request.matkul["Kamis"] = mapOf(
//                "sesi1" to kamisS1.text.toString(),
//                "sesi2" to kamisS2.text.toString()
//            )
//
//            request.matkul["Jumat"] = mapOf(
//                "sesi1" to jumatS1.text.toString(),
//                "sesi2" to jumatS2.text.toString()
//            )
//            request.nim = userMatkul.nim
//            dbaService.editUserMatkul(request).thenAccept {
//                focusLinearLayout.visibility = View.GONE
//                formMatkulAccount.visibility = View.GONE
//                Toast.makeText(context, "Berhasil update matkul user: "+userMatkul.nim, Toast.LENGTH_SHORT).show()
//            }.exceptionally { ex ->
//                Toast.makeText(context, "Gagal update matkul user: ${ex.cause?.message ?: ex.message}", Toast.LENGTH_SHORT).show()
//                null
//            }
//        }

//    }

    private fun closeButton(){
        closeEditButton.setOnClickListener {
            focusLinearLayout.visibility = View.GONE
            formEditAccount.visibility = View.GONE
            editAccountTextView.text = null
            nameEditAccountEditText.text = null
            passwordEditAccountEditText.text = null
            jurusanEditAccountSpinner.setSelection(0)
            angkatanEditAccountSpinner.setSelection(0)
        }
        closeMatkulImageButton.setOnClickListener{
            focusLinearLayout.visibility = View.GONE
            formMatkulAccount.visibility = View.GONE
        }

    }

    private fun editAccount(nim: String){
        editAccountButton.setOnClickListener{
            val editUserRequest = EditUserRequest()
            editUserRequest.name = nameEditAccountEditText.text.toString()
            val jurusanUser = jurusanEditAccountSpinner.selectedItem.toString()
            val angkatanUser = angkatanEditAccountSpinner.selectedItem.toString().toIntOrNull()
            editUserRequest.jurusan = Jurusan(jurusanUser, angkatanUser)
            editUserRequest.nim = nim
            editUserRequest.password = passwordEditAccountEditText.text.toString()

            dbaService.editMahasiswa(editUserRequest).thenAccept {
                requireActivity().runOnUiThread {
                    focusLinearLayout.visibility = View.GONE
                    formEditAccount.visibility = View.GONE
                    editAccountTextView.text = null
                    nameEditAccountEditText.text = null
                    passwordEditAccountEditText.text = null
                    jurusanEditAccountSpinner.setSelection(0)
                    angkatanEditAccountSpinner.setSelection(0)

                    Toast.makeText(requireContext(), "Success Edit account: $nim ", Toast.LENGTH_SHORT).show()
                }
            }.exceptionally { ex ->
                Toast.makeText(requireContext(), "Gagal Edit account: ${ex.cause?.message ?: ex.message}", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    private fun newForm(){
        nimAddAccountEditText.text = null
        passwordAddAccountEditText.text = null
        jurusanAddAccountSpinner.setSelection(0)
        angkatanAddAccountSpinner.setSelection(0)
    }

    private fun addAccount(){
        addAccountButton.setOnClickListener {
            focusLinearLayout.visibility = View.VISIBLE
            formAddAccount.visibility = View.VISIBLE
            newForm()
        }

        closeAddButton.setOnClickListener {
            focusLinearLayout.visibility = View.GONE
            formAddAccount.visibility = View.GONE
            newForm()
        }

        addNewAccountButton.setOnClickListener {
            validator.validate()
        }
    }

    private fun hapus(nim : String){
        closeAddHapusButton.setOnClickListener {
            focusLinearLayout.visibility = View.GONE
            formHapusAccount.visibility = View.GONE
        }
        batalHapusButton.setOnClickListener {
            focusLinearLayout.visibility = View.GONE
            formHapusAccount.visibility = View.GONE
        }
        yaHapusAccount.setOnClickListener {
            dbaService.removeMahasiswa(nim).thenAccept {
                Toast.makeText(context, "Berhasil hapus user: $nim", Toast.LENGTH_SHORT).show()
                focusLinearLayout.visibility = View.GONE
                formHapusAccount.visibility = View.GONE
                jurusanHapusTextView.text = null
            }.exceptionally { ex ->
                Toast.makeText(context, "Gagal menghapus user: ${ex.cause?.message ?: ex.message}", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }

    private fun spinner(){

        dbaService.allJurusan().thenAccept { allJurusan ->
            requireActivity().runOnUiThread {
                val jurusanList = allJurusan.map { it.nama }.distinct().sorted()
                jurusan.addAll(jurusanList)

                val angkatanList = allJurusan.mapNotNull { it.angkatan }.distinct().sorted()
                angkatan.addAll(angkatanList.map { it.toString() })

                // Buat adapter yang bisa dipakai untuk banyak spinner
                val adapterJurusan = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    jurusan
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                val adapterAngkatan = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    angkatan
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                jurusanAddAccountSpinner.adapter = adapterJurusan
                jurusanEditAccountSpinner.adapter = adapterJurusan
                spinnerJurusan.adapter = adapterJurusan

                angkatanAddAccountSpinner.adapter = adapterAngkatan
                angkatanEditAccountSpinner.adapter = adapterAngkatan
                spinnerAngkatan.adapter = adapterAngkatan


            }
        }
    }

    private fun requestData(){

        spinnerJurusan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                request.jurusan = if (position == 0) null else parent.getItemAtPosition(position).toString()
//                if (isSpinnerInitialized) getTable(request)
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
//                request.angkatan = if (position == 0) null else (parent.getItemAtPosition(position).toString().toIntOrNull())
//                if (isSpinnerInitialized) getTable(request)
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

    private fun getTable(request: FindMahasiswaRequest){
        tableMahasiswa.removeAllViews()
        dbaService.searchMahasiswa(request).thenAccept { datas ->
            requireActivity().runOnUiThread {
                for (data in datas) {
                    val tableRow = TableRow(requireContext())

                    val textNim = TextView(requireContext()).apply {
                        text = data.nim
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(120), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }

                    val textNama = TextView(requireContext()).apply {
                        text = data.name
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(190), TableRow.LayoutParams.WRAP_CONTENT)
                        setBackgroundResource(R.drawable.border_table)
                    }

                    val textJurusan = TextView(requireContext()).apply {
                        text = data.jurusan.nama
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(160), TableRow.LayoutParams.MATCH_PARENT)
                        setBackgroundResource(R.drawable.border_table)
                    }

                    val textMatkul = TextView(requireContext()).apply {
                        text = "Cek matkul"
                        setPadding(8, 8, 8, 8)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = TableRow.LayoutParams(dpToPx(100), TableRow.LayoutParams.MATCH_PARENT)
                        setBackgroundResource(R.drawable.border_table)
                        setTextColor(Color.BLUE)
                        setOnClickListener {
                            matkul(data)
                        }
                    }

                    val aksi = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(8, 8, 8, 8)
                        layoutParams = TableRow.LayoutParams(dpToPx(150), TableRow.LayoutParams.MATCH_PARENT)
                        setBackgroundResource(R.drawable.border_table)

                    }

                    val textEdit = TextView(requireContext()).apply {
                        text = "Edit"
                        setTextColor(ContextCompat.getColor(context, R.color.yellow))
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginEnd = dpToPx(15)
                        }
                        setOnClickListener {
                            focusLinearLayout.visibility = View.VISIBLE
                            formEditAccount.visibility = View.VISIBLE
                            editAccountTextView.text = data.nim

                            nameEditAccountEditText.setText(data.name)
                            passwordEditAccountEditText.setText(data.password)

                            val indexJurusan = jurusan.indexOf(data.jurusan.nama)
                            if (indexJurusan != -1) {
                                jurusanEditAccountSpinner.setSelection(indexJurusan)
                            }

                            val indexAngkatan = angkatan.indexOf(data.jurusan.angkatan.toString())
                            if (indexAngkatan != -1) {
                                angkatanEditAccountSpinner.setSelection(indexAngkatan)
                            }

                            editAccount(data.nim)
                        }
                    }

                    val textHapus = TextView(requireContext()).apply {
                        text = "Hapus"
                        setTextColor(Color.RED)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        setOnClickListener {
                            focusLinearLayout.visibility = View.VISIBLE
                            formHapusAccount.visibility = View.VISIBLE
                            jurusanHapusTextView.text = data.nim

                            hapus(data.nim)
                        }
                    }

                    aksi.addView(textEdit)
                    aksi.addView(textHapus)

                    tableRow.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            tableRow.viewTreeObserver.removeOnGlobalLayoutListener(this)

                            val maxHeight = listOf(textNim, textNama, textJurusan, aksi).maxOf { it.height }

                            textNim.height = maxHeight
                            textNama.height = maxHeight
                            textJurusan.height = maxHeight
                            aksi.layoutParams.height = maxHeight
                        }
                    })

                    tableRow.addView(textNim)
                    tableRow.addView(textNama)
                    tableRow.addView(textJurusan)
                    tableRow.addView(textMatkul)
                    tableRow.addView(aksi)

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

    override fun onValidationSucceeded() {
        if (jurusanAddAccountSpinner.selectedItemPosition == 0) {
            Toast.makeText(context, "Pilih Jurusan dulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (angkatanAddAccountSpinner.selectedItemPosition == 0) {
            Toast.makeText(context, "Pilih Angkatan dulu", Toast.LENGTH_SHORT).show()
            return
        }

        val newUserRequest = AddNewUserRequest().apply {
            nim = nimAddAccountEditText.text.toString()
            name = nameAddAccountEditText.text.toString()
            password = passwordAddAccountEditText.text.toString()
            jurusan = Jurusan(jurusanAddAccountSpinner.selectedItem.toString(),angkatanAddAccountSpinner.selectedItem.toString().toIntOrNull())
        }

        dbaService.addNewMahasiswa(newUserRequest).thenAccept { response ->
            requireActivity().runOnUiThread {
                focusLinearLayout.visibility = View.GONE
                formAddAccount.visibility = View.GONE
                newForm()
                Toast.makeText(context, "Berhasil menambahkan user: "+response.nim, Toast.LENGTH_SHORT).show()
            }
        }.exceptionally { ex ->
            Toast.makeText(context, "Gagal menambahkan user: ${ex.cause?.message ?: ex.message}", Toast.LENGTH_SHORT).show()
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