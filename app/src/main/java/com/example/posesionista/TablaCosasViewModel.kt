package com.example.posesionista

import androidx.lifecycle.ViewModel
import java.util.*

//private const val TAG = "TablaCosas"
class TablaCosasViewModel : ViewModel() {
    val inventario = mutableListOf<Cosa>()
    private val nombres = arrayOf("Teléfono","Pan", "Playera")
    private val adjetivos = arrayOf("Gris","Suave","Cómoda")
    init {
        for (i in 0 until 100){
            val cosa = Cosa()
            val nombreR = nombres.random()
            val adjetivoR = adjetivos.random()
            val precioR = Random().nextInt(1000)
            cosa.nombreDeCosa = "$nombreR $adjetivoR"
            cosa.valorPesos = precioR
            inventario += cosa
        }
    }

    //Función para elimiar una cosa
    fun eliminarCosa(posicion : Int){
        inventario.removeAt(posicion)
    }

    //Función para reacomodar el inventario


}