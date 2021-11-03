package com.example.posesionista

import androidx.lifecycle.ViewModel
import java.util.*

//private const val TAG = "TablaCosas"
class TablaCosasViewModel : ViewModel() {
    val inventario = mutableListOf<Cosa>()
    private val nombres = arrayOf("Teléfono","Pan", "Playera")
    private val adjetivos = arrayOf("Gris","Suave","Cómoda")

    fun agregaCosa(nuevaCosa: Cosa) {
        inventario.add(nuevaCosa)
    }


    //Función para elimiar una cosa
    fun eliminarCosa(posicion : Int){
        inventario.removeAt(posicion)
    }

}