package com.example.posesionista

import androidx.lifecycle.ViewModel
import java.util.*

class TablaCosasViewModel : ViewModel() {
    val inventario = mutableListOf<Cosa>()
    val nombres = arrayOf("Teléfono","Pan", "Playera")
    val adjetivos = arrayOf("Gris","Suave","Cómoda")
    init {
        for (i in 0 until 100){
            val cosa = Cosa()
            val nombreR = nombres.random()
            val adjetivoR = adjetivos.random()
            val precioR = Random().nextInt(100)
            cosa.nombreDeCosa = "$nombreR $adjetivoR"
            cosa.valorPesos = precioR
            inventario += cosa
        }
    }
}