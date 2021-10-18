package com.example.posesionista

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ConfirmaDialogoFragment(val listener : (flag : Boolean) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.Ok)
                .setPositiveButton(R.string.fire
                ) { _, _ ->
                    //flag = true
                    listener(true)
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    //flag = false
                    listener(false)
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}