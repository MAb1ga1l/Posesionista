package com.example.posesionista

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.fragment.app.Fragment
import java.io.File

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
    private var respuestaCamara = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //Se manda el activity result para poder ecibir el resultado
        resultado ->
        if (resultado.resultCode == Activity.RESULT_OK){
            val datos = resultado.data
            vistaFoto.setImageBitmap(BitmapFactory.decodeFile(archivoFoto.absolutePath))
            //vistaFoto.setImageBitmap(datos?.extras?.get("data") as Bitmap)
        }
    }

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
                }
                if(s.hashCode() == campoPrecio.text.hashCode()){
                    if (s!=null){
                        if (s.isEmpty()){
                            cosa.valorPesos = 0
                        }else{
                            cosa.valorPesos = s.toString().toInt()
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
                    Log.d(TAG,"No se encontro la c??mara")
                }
            }
        }
    }

    private fun obtenerArchivoFoto(file: String): File {
        val pathFoto = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(pathFoto,file)
    }

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
        campoFecha.text = cosa.fechaCreacion.toString()
        vistaFoto = vista.findViewById(R.id.fotoCosa)
        botonCamara = vista.findViewById(R.id.botonCamara)
        archivoFoto = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"${cosa.idCosa}.jpg")
        vistaFoto.setImageBitmap(BitmapFactory.decodeFile(archivoFoto.absolutePath))

        return vista
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
}