package com.example.posesionista

import androidx.lifecycle.ViewModel

//private const val TAG = "TablaCosas"
class TablaCosasViewModel : ViewModel() {
    val inventario = mutableListOf<Cosa>()

    fun agregaCosa(nuevaCosa: Cosa) {
        inventario.add(nuevaCosa)
    }


    //Función para elimiar una cosa
    fun eliminarCosa(posicion : Int){
        inventario.removeAt(posicion)
    }

}