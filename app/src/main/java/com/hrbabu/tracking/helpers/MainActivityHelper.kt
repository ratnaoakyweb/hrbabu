package com.hrbabu.tracking.helpers

import android.util.Log
import android.widget.Toast
import com.hrbabu.tracking.MainActivity
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.request_response.login.LoginRequest
import com.hrbabu.tracking.request_response.login.LoginResponse
import com.hrbabu.tracking.utils.PrefKeys
import com.hrbabu.tracking.utils.PrefUtil
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty

class MainActivityHelper(val mainActivity : MainActivity) : BaseHelperActivity() {

    companion object {
        const val SIGNIN = "1"
    }
    override fun hitApi(apiKey: String) {
        showProgressDialog()
        if (apiKey == SIGNIN)
        {
            val request = LoginRequest()
            request.login = mainActivity.email
            request.password = mainActivity.password

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(mainActivity.applicationContext).empLogin(request)
                )!!.subscribeWith(object : CallbackWrapper<LoginResponse?>() {
                    override fun onSuccess(t: LoginResponse?) {
                        hideProgressDialog()
                        PrefUtil.Init(mainActivity.applicationContext).save(PrefKeys.token,t?.res?.jwtToken)
                        Toast.makeText(mainActivity.applicationContext,t?.msgkey, Toast.LENGTH_SHORT).show()
                        Log.d("Res -->",""+t)
                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(mainActivity.applicationContext,t?:"", Toast.LENGTH_SHORT).show()
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(SIGNIN)
                            }
                        })
                    }

                    override fun onUnknownError() {
                        hideProgressDialog()
                        //Toast.makeText(fragment.requireContext(),"onUnknownError", Toast.LENGTH_SHORT).show()
                    }

                    override fun onLogout() {
                        hideProgressDialog()
//                    Toast.makeText(fragment.requireContext(),"onLogout", Toast.LENGTH_SHORT).show()
                    }
                })
            )
        }
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }
}