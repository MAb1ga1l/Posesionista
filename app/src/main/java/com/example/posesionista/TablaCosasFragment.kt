package com.example.posesionista

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.argb
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

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

    override fun onStart() {
        super.onStart()
        val barraActividad = activity as AppCompatActivity
        barraActividad.supportActionBar?.setTitle(R.string.app_name)
    }

    override fun onDetach() {
        super.onDetach()
        callbackinterfaz = null
    }

    private fun actualizaUI(context: Context){
        //Liga al inventario
        val inventario = tablaCosasViewModel.inventario
        adaptador = CosaAdapter(inventario)
        val swipeGesture = object : SwipeGesture(context) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT -> {
                        val dialogoFragment = ConfirmaDialogoFragment{flag ->  showDialogFragment(flag,viewHolder)}
                        dialogoFragment.show(childFragmentManager, dialogoFragment.tag)

                    }
                }
            }
        }



        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(cosaRecyclerView)

        cosaRecyclerView.adapter = adaptador
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDialogFragment(flag : Boolean, viewHolder : RecyclerView.ViewHolder) {
        if(flag){
            tablaCosasViewModel.eliminarCosa(viewHolder.absoluteAdapterPosition)
        }
        adaptador!!.notifyDataSetChanged()
    }



    private val tablaCosasViewModel : TablaCosasViewModel by lazy {
        ViewModelProvider(this).get(TablaCosasViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d(TAG,"Total de cosas : ${tablaCosasViewModel.inventario.size}")
    }

    companion object;

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
        actualizaUI(requireContext().applicationContext)
        val dragManageAdapter = DragManageAdapter(adaptador,context,ItemTouchHelper.UP or ItemTouchHelper.DOWN,ItemTouchHelper.LEFT)
        val helper = ItemTouchHelper(dragManageAdapter)
        helper.attachToRecyclerView(cosaRecyclerView)
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

        //Ordenar arreglo de nuevo después del drag
        fun swapItems(fromPosition: Int, toPosition: Int) {
            val nombreDeCosaAyuda : String = inventario[fromPosition].nombreDeCosa
            val valorEnPesosAyuda : Int = inventario[fromPosition].valorPesos
            val fechaDeCreacionAyuda : Date = inventario[fromPosition].fechaCreacion//El toString() puede que no sirva
            val numeroDeSerieAyuda : String = inventario[fromPosition].numSerie


            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {//Antes eb vez del until era ..
                    inventario[i].nombreDeCosa = inventario[i+1].nombreDeCosa
                    inventario[i].valorPesos = inventario[i+1].valorPesos
                    inventario[i].numSerie = inventario[i+1].numSerie
                    inventario[i].fechaCreacion = inventario[i+1].fechaCreacion
                    //inventario.set(i, inventario.set(i+1, inventario.get(i)));
                }
            } else {
                for (i in fromPosition..toPosition + 1) {
                    inventario[i].nombreDeCosa = inventario[i-1].nombreDeCosa
                    inventario[i].valorPesos = inventario[i-1].valorPesos
                    inventario[i].numSerie = inventario[i-1].numSerie
                    inventario[i].fechaCreacion = inventario[i-1].fechaCreacion
                    //inventario.set(i, inventario.set(i-1, inventario.get(i)));
                }
            }

            inventario[toPosition].nombreDeCosa = nombreDeCosaAyuda
            inventario[toPosition].valorPesos = valorEnPesosAyuda
            inventario[toPosition].fechaCreacion = fechaDeCreacionAyuda
            inventario[toPosition].numSerie = numeroDeSerieAyuda
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_tabla_de_cosas,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.nueva_cosa -> {
                //en caso de que se seleccione la opción del menu para agregar una nueva cosa
                val nuevaCosa = Cosa()
                //Se crea y se llena la cosa vacía
                tablaCosasViewModel.agregaCosa(nuevaCosa)
                callbackinterfaz?.onCosasSeleccionada(nuevaCosa)
                true
            }else -> return super.onOptionsItemSelected(item)
        }
        //return super.onOptionsItemSelected(item)
    }



    @Suppress("UNUSED_PARAMETER")
    private inner class DragManageAdapter(adapter: CosaAdapter?, context: Context?, dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs){
        var nameAdapter = adapter


        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            nameAdapter!!.swapItems(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
            adaptador!!.notifyItemMoved(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
            adaptador!!.notifyItemRangeChanged(0, kotlin.math.max(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition), Any())
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

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