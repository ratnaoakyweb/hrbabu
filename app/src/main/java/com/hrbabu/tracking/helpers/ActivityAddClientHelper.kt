package com.hrbabu.tracking.helpers

import android.widget.Toast
import com.hrbabu.tracking.activity.ActivityClientList
import com.hrbabu.tracking.activity.AddNewClientActivity
import com.hrbabu.tracking.adapter.ClientAdapter
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.request_response.getclient.GetClientRequest
import com.hrbabu.tracking.request_response.getclient.GetClientResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty

class ActivityAddClientHelper(val activityClientList: AddNewClientActivity) : BaseHelperActivity() {

    companion object{
        const val ADD_NEW_CLIENT = "addNewClient"
    }

    override fun hitApi(apiKey: String) {

        if(apiKey == ADD_NEW_CLIENT) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(activityClientList.applicationContext).getClients(
                        GetClientRequest()) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<GetClientResponse?>() {
                    override fun onSuccess(t: GetClientResponse?) {
                        hideProgressDialog()

                        val clients = t?.res?.clients

//                        activityClientList.setUpClients(clients)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            activityClientList.applicationContext,
                            t ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
//                            hitApi()
                            }
                        })
                    }

                    override fun onUnknownError() {
                        hideProgressDialog()
                    }

                    override fun onLogout() {
                        hideProgressDialog()
                    }
                })
            )
        }
    }

    override fun onDestroy() {

    }
}