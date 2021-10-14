package com.example.posesionista

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.argb
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "TablaCosasFragment"
class TablaCosasFragment : Fragment() {

    //Apuntador para poder "hablar" con la vista
    private lateinit var cosaRecyclerView : RecyclerView
    private var adaptador : CosaAdapter? = null
    private var callbackinterfaz : InterfazTablaDeCosas? = null

    interface InterfazTablaDeCosas{
        fun onCosasSeleccionada(unaCosa : Cosa)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackinterfaz = context as InterfazTablaDeCosas?
    }

    override fun onDetach() {
        super.onDetach()
        callbackinterfaz = null
    }

    private fun actualizaUI(){
        //Liga al inventario
        val inventario = tablaCosasViewModel.inventario
        adaptador = CosaAdapter(inventario)
        cosaRecyclerView.adapter = adaptador
    }

    private val tablaCosasViewModel : TablaCosasViewModel by lazy {
        ViewModelProvider(this).get(TablaCosasViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"Total de cosas : ${tablaCosasViewModel.inventario.size}")
    }

    companion object{
       /* fun nuevaInstancia() : TablaCosasFragment{
            return TablaCosasFragment()
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflamos
        val vista = inflater.inflate(R.layout.lista_cosas_fragment,container,false)
        cosaRecyclerView = vista.findViewById(R.id.cosa_recycler_view) as RecyclerView
        //indicamos el layout manager (para definir como se acomodan las cosas)
        cosaRecyclerView.layoutManager = LinearLayoutManager(context)
        actualizaUI()
        return vista
    }

    //Clase interna
    private inner class CosaHolder(vista : View) : RecyclerView.ViewHolder(vista), View.OnClickListener{

        //Apuntador a cada uno de los campos
        //itemView es para buscar los elementos en la vista que se recibe
        val nombreTextView : TextView = itemView.findViewById(R.id.label_nombreCosa)
        val precioTextView : TextView = itemView.findViewById(R.id.label_precioCosa)
        val numSerieTextView : TextView = itemView.findViewById(R.id.label_numSerie)
        private lateinit var cosa:Cosa

        @SuppressLint("SetTextI18n")
        fun binding(cosa : Cosa){
            this.cosa = cosa
            nombreTextView.text =cosa.nombreDeCosa
            precioTextView.text = "$" + cosa.valorPesos
            numSerieTextView.text = cosa.numSerie
        }

        override fun onClick(v: View?) {
            //Toast.makeText(context,"${nombreTextView.text} fue seleccionada", Toast.LENGTH_SHORT).show()
            callbackinterfaz?.onCosasSeleccionada(cosa)
        }

        init {
            itemView.setOnClickListener(this)
        }


    }

    //union del inventario
    private inner class CosaAdapter(var inventario : List<Cosa>) : RecyclerView.Adapter<CosaHolder>(){
        //generamos un ViewHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CosaHolder {
            val holder = layoutInflater.inflate(R.layout.cosa_layout,parent,false)
            return CosaHolder(holder)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: CosaHolder, position: Int) {
            //Vamos a poblar
            val cosa = inventario[position]
            holder.binding(cosa)
            holder.itemView.setBackgroundColor(determinarColor(cosa.valorPesos))
        }

        //Cantidad de items en la lista
        override fun getItemCount(): Int {
            return inventario.size
        }

    }

    //función para determinar el color
    private fun determinarColor(valorPesos: Int): Int {
        var min = 0
        var max = 99
        var r = 0
        var a = 255
        var flag = true
        for (i in 0..10) {
            if (flag) {
                //verificar si esta en el rango min - max
                if ( valorPesos <= max) {
                    flag = false
                } else {
                    min += 100
                    max += 100
                    r += 25
                    a -= 25
                }
            }
        }
        return argb(a,r, 0, 255)
    }

}