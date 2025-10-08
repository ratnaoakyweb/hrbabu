package com.hrbabu.tracking.helpers

import android.widget.Toast
import com.hrbabu.tracking.activity.ActivityVisitList
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.request_response.empvisit.EmpVisitResponse
import com.hrbabu.tracking.request_response.getclient.GetClientRequest
import com.hrbabu.tracking.request_response.getclient.GetClientResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.getTodayUtcEndTime
import com.hrbabu.tracking.utils.getTodayUtcStartTime
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty

class ActivityVisitListHelper(val activityVisitList: ActivityVisitList) : BaseHelperActivity() {

    companion object{
        const val GET_VISIT_LIST = "getVisitList"
    }

    override fun hitApi(apiKey: String) {
        showProgressDialog()
        if(apiKey == GET_VISIT_LIST) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(activityVisitList.applicationContext).getEmployeeVisits(
                        com.hrbabu.tracking.request_response.empvisit.EmpVisitRequest(100, 1, true, null, getTodayUtcStartTime(), getTodayUtcEndTime())
                    ) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<EmpVisitResponse?>() {
                    override fun onSuccess(t: EmpVisitResponse?) {
                        hideProgressDialog()

                        activityVisitList.setUpVisits(t?.res?.visits)
//                        activityClientList.setUpClients(clients)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            activityVisitList.applicationContext,
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