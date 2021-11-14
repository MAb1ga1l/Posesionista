package com.example.posesionista

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import java.io.File

//private const val TAG = "TablaCosas"
class TablaCosasViewModel : ViewModel() {
    val inventario = mutableListOf<Cosa>()

    fun agregaCosa(nuevaCosa: Cosa) {
        inventario.add(nuevaCosa)
    }


    //Función para elimiar una cosa
    fun eliminarCosa(posicion : Int, context: Context){
        val archivoFoto = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"${inventario[posicion].idCosa}.jpg")
        if(archivoFoto.exists()) {
            archivoFoto.delete()
        }
        inventario.removeAt(posicion)
    }

}