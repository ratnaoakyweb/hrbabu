package com.hrbabu.tracking.helpers

import android.app.Activity
import android.widget.Toast
import com.hrbabu.tracking.activity.AddVisitActivity
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.request_response.empvisit.SaveEmpVisitRequest
import com.hrbabu.tracking.request_response.empvisit.SaveEmpVisitResponse
import com.hrbabu.tracking.request_response.getclient.GetClientRequest
import com.hrbabu.tracking.request_response.getclient.GetClientResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty

class ActivityAddVisitHelper(val activityAddVisit: AddVisitActivity) : BaseHelperActivity() {

    companion object{
        const val GET_CLIENT_LIST = "getClientList"
        const val ADD_VISIT = "addVisit"
    }

    override fun hitApi(apiKey: String) {
        showProgressDialog()
        if(apiKey == GET_CLIENT_LIST) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(activityAddVisit.applicationContext).getClients(
                        GetClientRequest()) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<GetClientResponse?>() {
                    override fun onSuccess(t: GetClientResponse?) {
                        hideProgressDialog()

                        val clients = t?.res?.clients

                        activityAddVisit.setUpClients(clients)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            activityAddVisit.applicationContext,
                            t ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(GET_CLIENT_LIST)
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
        else  if(apiKey == ADD_VISIT) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(activityAddVisit.applicationContext).saveEmployeeVisit(
                        SaveEmpVisitRequest(
                            flag = "I",
                            visitDate = activityAddVisit.binding.etVisitDate.text.toString().trim(),
                            clientId = activityAddVisit.clientId.get(activityAddVisit.binding.spinnerClient.selectedItemPosition).toInt(),
                            purpose = activityAddVisit.binding.spinnerPurpose.selectedItem.toString(),
                            description = activityAddVisit.binding.etDescription.text.toString().trim(),
                            fromTime = activityAddVisit.selectedFromTime.toString().trim(),
                            toTime = activityAddVisit.selectedToTime.toString().trim(),
                            isPhoto = true,
                            isActive = true
                        )) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<SaveEmpVisitResponse?>() {
                    override fun onSuccess(t: SaveEmpVisitResponse?) {
                          hideProgressDialog()

                            //Toast
                        if(t?.rs == 1) {
                            Toast.makeText(
                                activityAddVisit.applicationContext,
                                t.res?.message ?: "Visit added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            activityAddVisit.setResult(Activity.RESULT_OK)
                            activityAddVisit.finish()
                        }else{
                            Toast.makeText(
                                activityAddVisit.applicationContext,
                                t?.res?.message ?: "Failed to add visit",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            activityAddVisit.applicationContext,
                            t ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(GET_CLIENT_LIST)
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