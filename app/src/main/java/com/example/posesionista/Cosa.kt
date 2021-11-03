package com.example.posesionista

import android.os.Parcel
import android.os.Parcelable
import java.util.*

//Basicmente esta clase es para definir una estructura base de un "objeto" y en los parentesis
//van los elementos que tendrá la base en este caso Cosa
class Cosa():Parcelable {
    var nombreDeCosa: String = ""
    var valorPesos: Int = 0
    //var numSerie: String = UUID.randomUUID().toString().substring(0,6)
    var numSerie: String = ""
    var fechaCreacion: Date = Date()
    var idCosa = UUID.randomUUID().toString().substring(0,6)

    constructor(parcel: Parcel):this(){
        nombreDeCosa = parcel.readString().toString()
        valorPesos = parcel.readInt()
        numSerie = parcel.readString().toString()
        fechaCreacion = parcel.readSerializable() as Date
        idCosa = parcel.readString().toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(nombreDeCosa)
        dest.writeInt(valorPesos)
        dest.writeString(numSerie)
        dest.writeSerializable(fechaCreacion)
    }

    companion object CREATOR : Parcelable.Creator<Cosa>{
        override fun createFromParcel(source: Parcel): Cosa {
            return  Cosa(source)
        }

        override fun newArray(size: Int): Array<Cosa?> {
            return arrayOfNulls(size)
        }

    }
}