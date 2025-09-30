package com.social.pe.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import com.hrbabu.tracking.databinding.LayoutInternetConnectivityBinding
import com.social.pe.interfaces.OnRerty

class AppDialog {
    lateinit var layoutInternetConnectivityBinding: LayoutInternetConnectivityBinding
    lateinit var dialogInternet: Dialog

    fun internetConnectivityDialog(context: Context, listener: OnRerty) {
        layoutInternetConnectivityBinding =
            LayoutInternetConnectivityBinding.inflate(LayoutInflater.from(context))

        if (!::dialogInternet.isInitialized) {
            dialogInternet = Dialog(context)
            dialogInternet.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialogInternet.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogInternet.setContentView(layoutInternetConnectivityBinding.root)
            dialogInternet.setCancelable(false)
        }


        layoutInternetConnectivityBinding.tvRetry.setOnClickListener {
            try {

                listener.onRetry()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dialogInternet.show()
    }

}