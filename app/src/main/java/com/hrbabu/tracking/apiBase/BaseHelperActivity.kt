package com.hrbabu.tracking.apiBase

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.hrbabu.tracking.BuildConfig
import com.hrbabu.tracking.R
import com.hrbabu.tracking.interfaces.MessageInterface
import com.hrbabu.tracking.request_response.getResponse.GetResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.dialog.AppDialog
import com.social.pe.interfaces.OnRerty
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.io.FileWriter

abstract class BaseHelperActivity {

    protected lateinit var thisActivity: AppCompatActivity
    private var loadingView: View? = null
    private lateinit var progress: ViewGroup
    lateinit var appDialog: AppDialog

    val disposables = CompositeDisposable()

    abstract fun hitApi(apiKey: String)

    abstract fun onDestroy()

    fun init(thisActivity: AppCompatActivity) {
        this.thisActivity = thisActivity
    }

    fun log(text: String) {
        Log.d("BaseHelperActivity", text)
    }

    fun showToast(message: String) {
        Toast.makeText(thisActivity, message, Toast.LENGTH_LONG).show()
    }

    fun callMessageApi(listener: MessageInterface) {
        disposables.add(
            sendApiRequest(getApiClientAuth(thisActivity).getResponse())!!.subscribeWith(
                object : CallbackWrapper<GetResponse?>() {
                    override fun onSuccess(t: GetResponse?) {
                        t?.let { saveResponseMsg(it, listener) }
                    }

                    override fun onError(t: String?) {
                        listener.onMsgFailure()
                    }

                    override fun onTimeout() {
                        listener.onMsgFailure()
                    }

                    override fun onUnknownError() {
                        listener.onMsgFailure()
                    }

                    override fun onLogout() {
                        listener.onMsgFailure()
                    }
                })
        )
    }

    fun saveResponseMsg(t: GetResponse?, listener: MessageInterface) {
        val file = File(
            com.hrbabu.tracking.utils.Appconstant.filePath.replace(
                "#",
                BuildConfig.APPLICATION_ID
            )
        )
        val json = GsonBuilder().create().toJson(t)
        var fos: FileWriter? = null
        try {
            val directory = File(
                com.hrbabu.tracking.utils.Appconstant.fileDir.replace(
                    "#",
                    BuildConfig.APPLICATION_ID
                )
            )
            if (!directory.exists()) {
                directory.mkdir()
            }

            if (file.exists()) file.delete()
            file.createNewFile()
            fos = FileWriter(file)
            fos.write(json)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fos?.close()
            listener.onMsgSuccess()
        }
    }


    fun showProgressDialog() {
        try {
            val inflater =
                thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            if (loadingView == null) {
                loadingView = inflater.inflate(R.layout.loading, null)
            }
            if (!::progress.isInitialized) {

                progress = thisActivity.window?.decorView?.findViewById(android.R.id.content)!!

            }

            hideProgressDialog()
            progress.addView(loadingView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun hideProgressDialog() {
        if (::progress.isInitialized && loadingView != null/* && progress.childCount > 0*/) {
            progress.removeView(loadingView)
        }
    }

    fun showRetryDialog(listener: OnRerty) {
        try {
            appDialog.internetConnectivityDialog(thisActivity.applicationContext, listener)

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
    fun dismissDialog() {
        appDialog.dialogInternet.dismiss()
    }

}

