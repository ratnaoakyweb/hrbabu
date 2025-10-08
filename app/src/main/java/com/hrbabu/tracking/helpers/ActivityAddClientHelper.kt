package com.hrbabu.tracking.helpers

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.hrbabu.tracking.activity.ActivityClientList
import com.hrbabu.tracking.activity.AddNewClientActivity
import com.hrbabu.tracking.adapter.ClientAdapter
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.request_response.addclient.AddClientRequest
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
                    getApiClientAuth(activityClientList.applicationContext).saveClients(
                        AddClientRequest(
                            clientName = activityClientList.clientName,
                            email = activityClientList.email,
                            phone = activityClientList.phone,
                            address = activityClientList.address,
                            locationLat = activityClientList.locationLat,
                            locationLong = activityClientList.locationLong
                        )) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<GetClientResponse?>() {
                    override fun onSuccess(t: GetClientResponse?) {
                        hideProgressDialog()
                        if(t?.rs == 1) {
                            Toast.makeText(
                                activityClientList.applicationContext,
                                t.res?.message ?: "",
                                Toast.LENGTH_SHORT
                            ).show()
                            val resultIntent = Intent()
                            resultIntent.putExtra("callback", true)
                            activityClientList.setResult(Activity.RESULT_OK, resultIntent)
                            activityClientList.finish()
//                            activityClientList.setResult(Activity.RESULT_OK)
//                            activityClientList.pu
//                            activityClientList.finish()
                        }
//                        t.rs==1.let {
//                            Toast.makeText(
//                                activityClientList.applicationContext,
//                                t? ?: "",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            activityClientList.setResult(ActivityClientList.RESULT_OK)
//                            activityClientList.finish()
//                        } ?: run {
//                            Toast.makeText(
//                                activityClientList.applicationContext,
//                                t?.msgkey ?: "",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }

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