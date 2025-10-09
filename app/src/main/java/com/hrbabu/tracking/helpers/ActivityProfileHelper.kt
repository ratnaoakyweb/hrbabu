package com.hrbabu.tracking.helpers

import android.widget.Toast
import com.hrbabu.tracking.activity.ActivityProfile
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.request_response.profile.ProfileResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty

class ActivityProfileHelper(val activityProfile: ActivityProfile) : BaseHelperActivity() {

    companion object{
        const val GET_PROFILE = "getProfile"
    }

    override fun hitApi(apiKey: String) {
        showProgressDialog()
        if(apiKey == GET_PROFILE) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(activityProfile.applicationContext).getEmployeeProfile() // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<ProfileResponse?>() {
                    override fun onSuccess(t: ProfileResponse?) {
                        hideProgressDialog()
                        activityProfile.setUpData(t?.res)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            activityProfile.applicationContext,
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