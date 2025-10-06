package com.hrbabu.tracking.helpers

import android.util.Log
import android.widget.Toast
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.helpers.LoginActivityHelper.Companion.SIGNIN
import com.hrbabu.tracking.request_response.history.HistoryResponse
import com.hrbabu.tracking.request_response.punchinpunchout.PunchinPunchoutResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeActivityHelper(val homeActivity: com.hrbabu.tracking.HomeActivity) : BaseHelperActivity() {
    companion object {
        const val GetHistory = "1"
        const val PunchIn = "2"
        const val PunchOut = "3"
        const val CheckIn = "4"
        const val CheckOut = "5"
    }
    override fun hitApi(apiKey: String) {
        showProgressDialog()

        if(apiKey == GetHistory){

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(homeActivity.applicationContext).getHistory()
                )!!.subscribeWith(object : CallbackWrapper<HistoryResponse?>() {
                    override fun onSuccess(t: HistoryResponse?) {

                        homeActivity.setHistoryData(t)

                        homeActivity.setupToggel()
                        hideProgressDialog()

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity.applicationContext,t?:"", Toast.LENGTH_SHORT).show()
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
        }else
        if(apiKey == PunchIn){
                val file = File(homeActivity.filePath)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val checkInFilePart = MultipartBody.Part.createFormData("CheckInFile", file.name, requestFile)
                val textPlain = "text/plain".toMediaTypeOrNull()
                val apiCall = getApiClientAuth(homeActivity).empPunchInOut(
                    CheckInFile = checkInFilePart,
                    CheckOutFile = null,
                    Flag = "I".toRequestBody(textPlain),  // "I" for Punch In, "O" for Punch Out
                    DeviceType = "Android".toRequestBody(textPlain),
                    CheckInLat = "0".toRequestBody(textPlain),
                    CheckOutTime = "".toRequestBody(textPlain),
                    CheckInLng = "0".toRequestBody(textPlain),
                    CheckOutLat = "".toRequestBody(textPlain),
                    CheckInTime = getCurrentUtcTime().toRequestBody(textPlain),
                    CheckOutLng = "".toRequestBody(textPlain)
                )

                disposables.add(
                    sendApiRequest(apiCall)!!.subscribeWith(object : CallbackWrapper<PunchinPunchoutResponse?>() {
                        override fun onSuccess(t: PunchinPunchoutResponse?) {
                            hideProgressDialog()
                            Toast.makeText(homeActivity, t?.msgkey ?: "Success", Toast.LENGTH_SHORT).show()
                            Log.d("PunchInRes", t.toString())
                        }

                        override fun onError(t: String?) {
                            hideProgressDialog()
                            Toast.makeText(homeActivity, t ?: "Error", Toast.LENGTH_SHORT).show()
                        }

                        override fun onTimeout() {
                            hideProgressDialog()
                            showRetryDialog(object : OnRerty {
                                override fun onRetry() {
                                    dismissDialog()
                                   // hitPunchInApi(filePath)
                                }
                            })
                        }

                        override fun onUnknownError() { hideProgressDialog() }
                        override fun onLogout() { hideProgressDialog() }
                    })
                )
            }
        else if(apiKey == PunchOut){

            val file = File(homeActivity.filePath)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val checkOutFilePart = MultipartBody.Part.createFormData("CheckOutFile", file.name, requestFile)

            val textPlain = "text/plain".toMediaTypeOrNull()

            val apiCall = getApiClientAuth(homeActivity).empPunchInOut(
                CheckInFile = null,
                CheckOutFile = checkOutFilePart,
                Flag = "U".toRequestBody(textPlain),  // "I" for Punch In, "O" for Punch Out
                DeviceType = "Android".toRequestBody(textPlain),
                CheckInLat = "0".toRequestBody(textPlain),
                CheckOutTime = getCurrentUtcTime().toRequestBody(textPlain),
                CheckInLng = "".toRequestBody(textPlain),
                CheckOutLat = "0".toRequestBody(textPlain),
                CheckInTime = "".toRequestBody(textPlain),
                CheckOutLng = "0".toRequestBody(textPlain)
            )

            disposables.add(
                sendApiRequest(apiCall)!!.subscribeWith(object : CallbackWrapper<PunchinPunchoutResponse?>() {
                    override fun onSuccess(t: PunchinPunchoutResponse?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t?.msgkey ?: "Success", Toast.LENGTH_SHORT).show()
                        Log.d("PunchInRes", t.toString())
                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t ?: "Error", Toast.LENGTH_SHORT).show()
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                // hitPunchInApi(filePath)
                            }
                        })
                    }

                    override fun onUnknownError() { hideProgressDialog() }
                    override fun onLogout() { hideProgressDialog() }
                }))
        }
        else if(apiKey == CheckIn){

            val file = File(homeActivity.filePath)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val serverFile = MultipartBody.Part.createFormData("CheckInFile", file.name, requestFile)

            val textPlain = "text/plain".toMediaTypeOrNull()

            val apiCall = getApiClientAuth(homeActivity).empCheckInCheckOut(
                CheckInFile = serverFile,
                CheckOutFile = null,
                Flag = "I".toRequestBody(textPlain),  // "I" for Punch In, "O" for Punch Out
                ClientId = homeActivity.selectedClientId.toString().toRequestBody(textPlain),
                CheckInTime = getCurrentUtcTime().toRequestBody(textPlain),
                CheckInLat = homeActivity.pendingLocation!!.latitude.toString().toRequestBody(textPlain),
                CheckInLng = homeActivity.pendingLocation!!.longitude.toString().toRequestBody(textPlain),
                CheckOutTime = "".toRequestBody(textPlain),
                CheckOutLat = "".toRequestBody(textPlain),
                CheckOutLng = "".toRequestBody(textPlain)
            )

            disposables.add(
                sendApiRequest(apiCall)!!.subscribeWith(object : CallbackWrapper<PunchinPunchoutResponse?>() {
                    override fun onSuccess(t: PunchinPunchoutResponse?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t?.msgkey ?: "Success", Toast.LENGTH_SHORT).show()
                        Log.d("PunchInRes", t.toString())
                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t ?: "Error", Toast.LENGTH_SHORT).show()
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                // hitPunchInApi(filePath)
                            }
                        })
                    }

                    override fun onUnknownError() { hideProgressDialog() }
                    override fun onLogout() { hideProgressDialog() }
                }))
        }





        }

    private fun getCurrentUtcTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }





    override fun onDestroy() {

    }
}