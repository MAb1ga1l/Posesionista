package com.example.posesionista

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() , TablaCosasFragment.InterfazTablaDeCosas{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Primero vamos a instanciar el fragmento
        val fragmentoActtual = supportFragmentManager.findFragmentById(R.id.fragment_container)
        //Debido a que puede existir cierta persistencia con las actividades es necesario asegurar si hay o no un fragmento
        if (fragmentoActtual == null){
            val fragmento = TablaCosasFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragmento).commit()
        }
    }

    override fun onCosasSeleccionada(unaCosa: Cosa) {
       Log.d(TAG, "MainActivity recibió la Cosa: ${unaCosa.nombreDeCosa} con UUID : ${unaCosa.numSerie}")
        val fragmento = CosaFragment.nuevaInstancia(unaCosa)
        supportFragmentManager.beginTransaction().
        replace(R.id.fragment_container,fragmento)
            .addToBackStack(null).commit()
    }
}