package com.example.posesionista

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import android.widget.*
import androidx.fragment.app.Fragment
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

private const val TAG ="CosaFragment"
class CosaFragment : Fragment() {

    private lateinit var cosa:Cosa
    private lateinit var campoNombre : EditText
    private lateinit var campoPrecio : EditText
    private lateinit var campoSerie : EditText
    private lateinit var campoFecha : TextView
    private lateinit var vistaFoto : ImageView
    private lateinit var botonCamara : ImageButton
    private lateinit var archivoFoto : File
    private lateinit var botonEliminarFoto : ImageButton

    @SuppressLint("UseCompatLoadingForDrawables")
    private var respuestaCamara = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //Se manda el activity result para poder ecibir el resultado
        resultado ->
        if (resultado.resultCode == Activity.RESULT_OK){
            vistaFoto.setImageBitmap(BitmapFactory.decodeFile(archivoFoto.absolutePath))
            vistaFoto.background = resources.getDrawable(R.color.black)
            botonEliminarFoto.isEnabled = true
            botonEliminarFoto.isClickable = true
        }
    }
    private lateinit var botonCalendario : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cosa = Cosa()
        cosa = arguments?.getParcelable("COSA_RECIBIDA")!!
    }

    override fun onStart() {
        super.onStart()
        val observador = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.hashCode() == campoNombre.text.hashCode()){
                    cosa.nombreDeCosa = s.toString()
                    if (s!=null){
                        if (s.isEmpty()){
                            Toast.makeText(context, "Dejaste el nombre en blanco", Toast.LENGTH_SHORT).show()
                            cosa.nombreDeCosa = "Artículo sin nombrar"
                        }
                    }
                }
                if(s.hashCode() == campoPrecio.text.hashCode()){
                    if (s!=null){
                        if (s.isEmpty()){
                            cosa.valorPesos = 0
                        }else{
                            try {
                                cosa.valorPesos = s.toString().toInt()
                            }catch (e:Exception){
                                Toast.makeText(context, "número muy grande", Toast.LENGTH_SHORT).show()
                                cosa.valorPesos = 0
                            }
                        }
                    }
                }
                if(s.hashCode() == campoSerie.text.hashCode()){
                    cosa.numSerie = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
        campoPrecio.addTextChangedListener(observador)
        campoNombre.addTextChangedListener(observador)
        campoSerie.addTextChangedListener(observador)
        val barraActividad = activity as AppCompatActivity
        barraActividad.supportActionBar?.setTitle(R.string.detalleCosa)
        botonCamara.apply {
            setOnClickListener {
                val intentTomarFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //Obtenemos la foto en caso de que ya exista una de la cosa
                archivoFoto = obtenerArchivoFoto("${cosa.idCosa}.jpg")
                val filePovider = FileProvider.getUriForFile(context,"com.example.posesionista.fileprovider", archivoFoto)
                intentTomarFoto.putExtra(MediaStore.EXTRA_OUTPUT,filePovider)
                try {
                    respuestaCamara.launch(intentTomarFoto)
                }catch (e : ActivityNotFoundException){
                    Log.d(TAG,"No se encontro la cámara")
                }
            }
        }
    }

    private fun obtenerArchivoFoto(file: String): File {
        val pathFoto = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(pathFoto,file)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Para infar la vista
        val vista = inflater.inflate(R.layout.cosa_fragment,container,false)
        campoNombre = vista.findViewById(R.id.campoNombreCosa) as EditText
        campoPrecio = vista.findViewById(R.id.campoPrecio) as EditText
        campoSerie = vista.findViewById(R.id.campoSerie) as EditText
        campoFecha = vista.findViewById(R.id.campoFecha) as TextView
        campoNombre.setText(cosa.nombreDeCosa)
        campoPrecio.setText(cosa.valorPesos.toString())
        campoSerie.setText(cosa.numSerie)
        vistaFoto = vista.findViewById(R.id.fotoCosa)
        botonEliminarFoto = vista.findViewById(R.id.deleteImage)
        botonEliminarFoto.setOnClickListener { eliminarFotoCosa() }
        botonCamara = vista.findViewById(R.id.botonCamara)
        archivoFoto = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"${cosa.idCosa}.jpg")
        if(archivoFoto.exists()) {
            vistaFoto.background = resources.getDrawable(R.color.black)
            botonEliminarFoto.isEnabled = true
            botonEliminarFoto.isClickable = true
            vistaFoto.setImageBitmap(BitmapFactory.decodeFile(archivoFoto.absolutePath))
        }else{
            botonEliminarFoto.isEnabled = false
            botonEliminarFoto.isClickable = false
        }
        campoSerie.setText(cosa.numSerie)
        campoFecha.text = ajusteFecha(cosa.fechaCreacion)
        botonCalendario = vista.findViewById(R.id.botonCalendario) as ImageButton
        botonCalendario.setOnClickListener { showDataPicker() }

        return vista
    }

    private fun showDataPicker() {
        val datePicker = DatePickerFragment { dia, mes, anio -> onDateSlected(dia, mes, anio) }
        datePicker.show(childFragmentManager,"datePicker")
    }

    @SuppressLint("SimpleDateFormat")
    fun onDateSlected(dia:Int, mes:Int, anio: Int){
        val mesDelAnio = mes+1
        val fecha : String= anio.toString().plus("-").plus(mesDelAnio).plus("-").plus(dia)
        val fechaactual = Date()
        val fechaValida = validarFecha(fechaactual,dia,mesDelAnio, anio)
        if (!fechaValida){
            Toast.makeText(context, "Fecha no valida", Toast.LENGTH_SHORT).show()
        }else{
            campoFecha.text = dia.toString().plus(" / ").plus(mesDelAnio).plus(" / ").plus(anio)
            val format = SimpleDateFormat("yyyy-MM-dd")
            cosa.fechaCreacion = format.parse(fecha)!!
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun eliminarFotoCosa() {
        if(archivoFoto.exists()) {
            archivoFoto.delete()
            vistaFoto.background = resources.getDrawable(R.drawable.imagen_fondo)
            vistaFoto.setImageDrawable(null)
            botonEliminarFoto.isEnabled = false
            botonEliminarFoto.isClickable = false
        }
    }

    private fun ajusteFecha(fecha : Date) : String {
        val day = DateFormat.format("dd", fecha) as String
        val mes = DateFormat.format("MM", fecha) as String
        val anio = DateFormat.format("yyyy", fecha) as String
        return day.plus(" / ").plus(mes).plus(" / ").plus(anio)
    }

    private fun validarFecha(fecha : Date, dia:Int, mes:Int, anio: Int) : Boolean {
        val dayActual = DateFormat.format("dd", fecha) as String
        val mesActual = DateFormat.format("MM", fecha) as String
        val anioActual = DateFormat.format("yyyy", fecha) as String
        //Algoritmo para validar fecha
        if ((anio > anioActual.toInt())||(anio < 1980)){
            return false
        }
        if (anioActual.toInt() == anio ){
            if (mes > mesActual.toInt()){
                return false
            }
        }
        if (anioActual.toInt() == anio ){
            if (mes == mesActual.toInt()){
                if (dia > dayActual.toInt()){
                    return false
                }
            }
        }
        return true
    }

    companion object {
        fun nuevaInstancia(unaCosa: Cosa) : CosaFragment {
            val argumentos = Bundle().apply {
                putParcelable("COSA_RECIBIDA", unaCosa)
            }
            return CosaFragment().apply {
                arguments = argumentos
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(TextUtils.isEmpty(campoNombre.text)){
            Toast.makeText(context, "Dejaste el nombre en blanco", Toast.LENGTH_SHORT).show()
            cosa.nombreDeCosa = "Artículo sin nombrar"
        }
    }
}