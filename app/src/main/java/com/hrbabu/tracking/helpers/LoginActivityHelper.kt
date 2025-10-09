package com.hrbabu.tracking.helpers

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.hrbabu.tracking.HomeActivity
import com.hrbabu.tracking.LoginActivity
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.helpers.ActivityProfileHelper.Companion.GET_PROFILE
import com.hrbabu.tracking.request_response.login.LoginRequest
import com.hrbabu.tracking.request_response.login.LoginResponse
import com.hrbabu.tracking.request_response.profile.ProfileResponse
import com.hrbabu.tracking.utils.PrefKeys
import com.hrbabu.tracking.utils.PrefUtil
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty

class LoginActivityHelper(val loginActivity : LoginActivity) : BaseHelperActivity() {

    companion object {
        const val SIGNIN = "1"
    }
    override fun hitApi(apiKey: String) {
        showProgressDialog()
        if (apiKey == SIGNIN)
        {
            val request = LoginRequest()
            request.login = loginActivity.email
            request.password = loginActivity.password

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(loginActivity.applicationContext).empLogin(request)
                )!!.subscribeWith(object : CallbackWrapper<LoginResponse?>() {
                    override fun onSuccess(t: LoginResponse?) {
                        hideProgressDialog()
                        PrefUtil.Init(loginActivity.applicationContext).save(PrefKeys.token,t?.res?.jwtToken)
                        Toast.makeText(loginActivity.applicationContext,t?.msgkey, Toast.LENGTH_SHORT).show()
                        Log.d("Res -->",""+t)
                        val gson = com.google.gson.Gson()
                        val loginJson = gson.toJson(t)
                        PrefUtil.Init(loginActivity.applicationContext).save(PrefKeys.loginResponse, loginJson)
                        hitApi(GET_PROFILE)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(loginActivity.applicationContext,t?:"", Toast.LENGTH_SHORT).show()
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
        else if(apiKey == GET_PROFILE) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(loginActivity.applicationContext).getEmployeeProfile() // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<ProfileResponse?>() {
                    override fun onSuccess(t: ProfileResponse?) {
                        hideProgressDialog()
                        val gson = com.google.gson.Gson()
                        val profileResponse = gson.toJson(t)
                        PrefUtil.Init(loginActivity.applicationContext).save(PrefKeys.profileResponse, profileResponse)
//                        loginActivity.startService()
                        loginActivity.startActivity(Intent(loginActivity, HomeActivity::class.java))
                        loginActivity.finish()
                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            loginActivity.applicationContext,
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