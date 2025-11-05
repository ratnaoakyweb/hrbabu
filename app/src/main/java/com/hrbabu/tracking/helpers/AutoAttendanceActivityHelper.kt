package com.hrbabu.tracking.helpers

import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.R
import com.example.awesomedialog.body
import com.example.awesomedialog.icon
import com.example.awesomedialog.title
import com.hrbabu.tracking.AutoAttendanceActivity
import com.hrbabu.tracking.HomeActivity
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.request_response.emptoggel.ResponseGetEmployeeActivityToggle
import com.hrbabu.tracking.request_response.history.HistoryResponse
import com.hrbabu.tracking.request_response.punchinpunchout.PunchinPunchoutResponse
import com.hrbabu.tracking.utils.AppUpdateState
import com.hrbabu.tracking.utils.ButtonState
import com.hrbabu.tracking.utils.checkAppUpdate
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.getCurrentUtcTime
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AutoAttendanceActivityHelper(val homeActivity: AutoAttendanceActivity) : BaseHelperActivity() {
    companion object {
//        const val GetHistory = "1"
        const val KEY_PunchIn = "2"
        const val PunchOut = "3"
        const val CheckIn = "4"
        const val CheckOut = "5"
//        const val GetToggel = "6"
    }
    override fun hitApi(apiKey: String) {
        showProgressDialog()

//        if(apiKey == GetHistory){
//
//            disposables.add(
//                sendApiRequest(
//                    getApiClientAuth(homeActivity.applicationContext).getHistory()
//                )!!.subscribeWith(object : CallbackWrapper<HistoryResponse?>() {
//                    override fun onSuccess(t: HistoryResponse?) {
//
////                        homeActivity.setHistoryData(t)
//
//
//                        hideProgressDialog()
//
//                        //homeActivity.getCurrentLocation()
//
//                    }
//
//                    override fun onError(t: String?) {
//                        hideProgressDialog()
//                        Toast.makeText(homeActivity.applicationContext,t?:"", Toast.LENGTH_SHORT).show()
//                        showRetryDialog(object : OnRerty {
//                            override fun onRetry() {
//                                dismissDialog()
//                                hitApi(GetHistory)
//                            }
//                        })
//                    }
//
//                    override fun onTimeout() {
//                        hideProgressDialog()
//                        showRetryDialog(object : OnRerty {
//                            override fun onRetry() {
//                                dismissDialog()
//                                hitApi(GetHistory)
//                            }
//                        })
//                    }
//
//                    override fun onUnknownError() {
//                        hideProgressDialog()
//                        showRetryDialog(object : OnRerty {
//                            override fun onRetry() {
//                                dismissDialog()
//                                hitApi(GetHistory)
//                            }
//                        })
//                        //Toast.makeText(fragment.requireContext(),"onUnknownError", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onLogout() {
//                        hideProgressDialog()
////                    Toast.makeText(fragment.requireContext(),"onLogout", Toast.LENGTH_SHORT).show()
//                    }
//                })
//            )
//        }else
        if(apiKey == KEY_PunchIn){
                val file = File(homeActivity.filePath)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val checkInFilePart = MultipartBody.Part.createFormData("CheckInFile", file.name, requestFile)
                val textPlain = "text/plain".toMediaTypeOrNull()
                val apiCall = getApiClientAuth(homeActivity).empPunchInOutManually(
                    EmployeeId = homeActivity.binding.edEmpId.text.toString().toRequestBody(textPlain),
                    CheckInFile = checkInFilePart,
                    CheckOutFile = null,
                    Flag = "I".toRequestBody(textPlain),  // "I" for Punch In, "O" for Punch Out
                    DeviceType = "".toRequestBody(textPlain),
                    CheckInLat = (homeActivity.pendingLocation?.latitude.toString() ?: "").toRequestBody(textPlain),
                    CheckInLng = (homeActivity.pendingLocation?.longitude.toString() ?: "").toRequestBody(textPlain),
                    CheckInTime = getCurrentUtcTime().toRequestBody(textPlain),
                    CheckOutTime = "".toRequestBody(textPlain),
                    CheckOutLat = "".toRequestBody(textPlain),
                    CheckOutLng = "".toRequestBody(textPlain),
                    isMockLocation = if (homeActivity.pendingLocation?.isFromMockProvider == true)
                        "1".toRequestBody(textPlain)
                    else
                        "0".toRequestBody(textPlain)
                )

                disposables.add(
                    sendApiRequest(apiCall)!!.subscribeWith(object : CallbackWrapper<PunchinPunchoutResponse?>() {
                        override fun onSuccess(t: PunchinPunchoutResponse?) {
                            hideProgressDialog()
//                            homeActivity.setButtonState(ButtonState.CHECK_IN)
//                            homeActivity.setToggelState(true)
//                            hitApi(GetHistory)
                            Toast.makeText(homeActivity, t?.msgkey ?: "Success", Toast.LENGTH_SHORT).show()
                            homeActivity.binding.edEmpId.setText("")
                            val dialog  = AwesomeDialog.build(homeActivity)
                                .body("Attendance marked successfully!")
                                .icon(R.drawable.ic_congrts)

                            dialog.show()
                            homeActivity.lifecycleScope.launch {
                                delay(2000)
                                dialog.dismiss()
                            }

                        }

                        override fun onError(t: String?) {
                            hideProgressDialog()
                            Toast.makeText(homeActivity, t ?: "Error", Toast.LENGTH_SHORT).show()
                            showRetryDialog(object : OnRerty {
                                override fun onRetry() {
                                    dismissDialog()
                                    hitApi(KEY_PunchIn)
                                }
                            })
                        }

                        override fun onTimeout() {
                            hideProgressDialog()
                            showRetryDialog(object : OnRerty {
                                override fun onRetry() {
                                    dismissDialog()
                                    hitApi(KEY_PunchIn)
                                   // hitPunchInApi(filePath)
                                }
                            })
                        }

                        override fun onUnknownError() { hideProgressDialog()
                            showRetryDialog(object : OnRerty {
                                override fun onRetry() {
                                    dismissDialog()
                                    hitApi(KEY_PunchIn)
                                }
                            })
                        }
                        override fun onLogout() { hideProgressDialog() }
                    })
                )
            }
        else if(apiKey == PunchOut){

            val file = File(homeActivity.filePath)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val checkOutFilePart = MultipartBody.Part.createFormData("CheckOutFile", file.name, requestFile)

            val textPlain = "text/plain".toMediaTypeOrNull()

            val apiCall = getApiClientAuth(homeActivity).empPunchInOutManually(
                CheckInFile = null,
                CheckOutFile = checkOutFilePart,
                EmployeeId = homeActivity.binding.edEmpId.text.toString().toRequestBody(textPlain),
                Flag = "U".toRequestBody(textPlain),  // "I" for Punch In, "O" for Punch Out
                DeviceType = "Android Attendance App".toRequestBody(textPlain),
                CheckInLat = "0".toRequestBody(textPlain),
                CheckOutTime = getCurrentUtcTime().toRequestBody(textPlain),
                CheckInLng = "".toRequestBody(textPlain),
                CheckInTime = "".toRequestBody(textPlain),
                CheckOutLat = homeActivity.pendingLocation?.latitude.toString().toRequestBody(textPlain),
                CheckOutLng = homeActivity.pendingLocation?.longitude.toString().toRequestBody(textPlain),
                isMockLocation = if (homeActivity.pendingLocation?.isFromMockProvider == true)
                    "1".toRequestBody(textPlain)
                else
                    "0".toRequestBody(textPlain)

            )

            disposables.add(
                sendApiRequest(apiCall)!!.subscribeWith(object : CallbackWrapper<PunchinPunchoutResponse?>() {
                    override fun onSuccess(t: PunchinPunchoutResponse?) {
                        hideProgressDialog()
//                        homeActivity.setButtonState(ButtonState.INACTIVE)
//                        homeActivity.setToggelState(false)
//                        hitApi(GetHistory)
                        Toast.makeText(homeActivity, t?.msgkey ?: "Success", Toast.LENGTH_SHORT).show()
                        Log.d("PunchInRes", t.toString())
                        homeActivity.binding.edEmpId.setText("")

                        val dialog  = AwesomeDialog.build(homeActivity)
                            .body("Attendance marked successfully!")
                            .icon(R.drawable.ic_congrts)

                        dialog.show()
                        homeActivity.lifecycleScope.launch {
                            delay(2000)
                            dialog.dismiss()
                        }


                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t ?: "Error", Toast.LENGTH_SHORT).show()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(PunchOut)
                            }
                        })
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(PunchOut)
                                // hitPunchInApi(filePath)
                            }
                        })
                    }

                    override fun onUnknownError() { hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(PunchOut)
                            }
                        })}
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
                CheckOutLng = "".toRequestBody(textPlain),
                VisitId = homeActivity.selectedVisitId.toString().toRequestBody(textPlain),
                VisitCheckInId="".toRequestBody(textPlain),
                isMockLocation = if (homeActivity.pendingLocation?.isFromMockProvider == true)
                    "1".toRequestBody(textPlain)
                else
                    "0".toRequestBody(textPlain)
            )

            disposables.add(
                sendApiRequest(apiCall)!!.subscribeWith(object : CallbackWrapper<PunchinPunchoutResponse?>() {
                    override fun onSuccess(t: PunchinPunchoutResponse?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t?.msgkey ?: "Success", Toast.LENGTH_SHORT).show()
                        Log.d("PunchInRes", t.toString())
//                        homeActivity.setButtonState(ButtonState.CHECK_OUT)
                        homeActivity.selectedVisitCheckInId=t?.res?.VisitCheckInId ?: -1
                        homeActivity.selectedVisitCheckInTime=t?.res?.activityTime ?: ""
//                        hitApi(GetHistory)
                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t ?: "Error", Toast.LENGTH_SHORT).show()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(CheckIn)
                            }
                        })
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(CheckIn)
                                // hitPunchInApi(filePath)
                            }
                        })
                    }

                    override fun onUnknownError() { hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(CheckIn)
                            }
                        })}
                    override fun onLogout() { hideProgressDialog() }
                }))
        }
        else if(apiKey == CheckOut){

            val file = File(homeActivity.filePath)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val serverFile = MultipartBody.Part.createFormData("CheckOutFile", file.name, requestFile)

            val textPlain = "text/plain".toMediaTypeOrNull()

            val apiCall = getApiClientAuth(homeActivity).empCheckInCheckOut(
                CheckInFile = null,
                CheckOutFile = serverFile,
                Flag = "U".toRequestBody(textPlain),  // "I" for Punch In, "O" for Punch Out
                ClientId =  homeActivity.selectedClientId.toString().toRequestBody(textPlain),
                CheckInTime = "".toRequestBody(textPlain),
                CheckInLat = "".toRequestBody(textPlain),
                CheckInLng = "".toRequestBody(textPlain),
                CheckOutLat = homeActivity.pendingLocation!!.latitude.toString().toRequestBody(textPlain),
                CheckOutLng = homeActivity.pendingLocation!!.longitude.toString().toRequestBody(textPlain),
                CheckOutTime =getCurrentUtcTime().toRequestBody(textPlain),
                VisitId = homeActivity.selectedVisitId.toString().toRequestBody(textPlain),
                VisitCheckInId= homeActivity.selectedVisitCheckInId.toString().toRequestBody(textPlain),
                isMockLocation = if (homeActivity.pendingLocation?.isFromMockProvider == true)
                    "1".toRequestBody(textPlain)
                else
                    "0".toRequestBody(textPlain)
            )

            disposables.add(
                sendApiRequest(apiCall)!!.subscribeWith(object : CallbackWrapper<PunchinPunchoutResponse?>() {
                    override fun onSuccess(t: PunchinPunchoutResponse?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t?.res?.message ?: "", Toast.LENGTH_SHORT).show()
                        if(t?.rs ==1) {
                            Log.d("PunchInRes", t.toString())
//                            homeActivity.setButtonState(ButtonState.CHECK_IN)
//                            hitApi(GetHistory)
                        }
                        }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(homeActivity, t ?: "Error", Toast.LENGTH_SHORT).show()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(CheckOut)
                            }
                        })
                    }

                    override fun onTimeout() {
                        hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(CheckIn)
                                // hitPunchInApi(filePath)
                            }
                        })
                    }

                    override fun onUnknownError() { hideProgressDialog()
                        showRetryDialog(object : OnRerty {
                            override fun onRetry() {
                                dismissDialog()
                                hitApi(CheckOut)
                            }
                        })}
                    override fun onLogout() { hideProgressDialog() }
                }))
        }
//        else
//            if(apiKey == GetToggel){
//
//            disposables.add(
//                sendApiRequest(
//                    getApiClientAuth(homeActivity.applicationContext).getEmployeeActivityToggle()
//                )!!.subscribeWith(object : CallbackWrapper<ResponseGetEmployeeActivityToggle?>() {
//                    override fun onSuccess(t: ResponseGetEmployeeActivityToggle?) {
//
//                        var isPunchIn = false
//                        var isVisitCheckIn = false
//
//                        // Get the list safely
////                        val list = t?.rc ?: emptyList()
////                        list.forEach { item ->
////                            when (item?.activityType?.trim()?.lowercase()) {
////                                "punch in" -> isPunchIn = true
////                                "visit check in" -> {
////                                    isVisitCheckIn = true
////                                    homeActivity.selectedClientId=item.clientId ?: -1
////                                    homeActivity.selectedVisitId=item.visitId ?: -1
////                                    homeActivity.selectedVisitCheckInId=item.visitCheckInId ?: -1
////                                    homeActivity.selectedVisitCheckInTime = item.activityTime ?: ""
////
////                                }
////                            }
////                        }
//
//                        val data = t?.res
//
//                       data?.activityToggle?.forEach {
//
//                            when(it?.activityType.toString().trim().lowercase()){
//                                "punch in" -> {
//                                    isPunchIn = true
//                                }
//                                "visit check in" -> {
//                                    isVisitCheckIn = true
//                                    homeActivity.selectedClientId=it?.clientId ?: -1
//                                    homeActivity.selectedVisitId=it?.visitId ?: -1
//                                    homeActivity.selectedVisitCheckInId=it?.visitCheckInId ?: -1
//                                    homeActivity.selectedVisitCheckInTime = it?.activityTime ?: ""
//
//                                }
//                            }
//                        }
//
//
//
//
//                        //manage app update
//                        val serverVersion = t?.res?.appVersion?.minimumVersion
//
//                        val appVersion = homeActivity.getAppVersionName(homeActivity)
//                        checkAppUpdate(serverVersion?:"", appVersion?:"") .let {
//                            if(it == AppUpdateState.FORCE_UPDATE){
//                                homeActivity.showUpdateDialog("A new version of the app is available. Please update to continue using the app.", true)
//                            }else if(it == AppUpdateState.OPTIONAL_UPDATE){
//                                homeActivity.showUpdateDialog("A new version of the app is available. Would you like to update?", false)
//                            }
//                        }
//
//
////                        homeActivity.setHistoryData(t)
////
//                        homeActivity.setupToggel(isPunchIn , isVisitCheckIn)
////                        hideProgressDialog()
//
////                        hitApi(GetHistory)
//
//                    }
//
//                    override fun onError(t: String?) {
//                        hideProgressDialog()
//                        Toast.makeText(homeActivity.applicationContext,t?:"", Toast.LENGTH_SHORT).show()
//                        showRetryDialog(object : OnRerty {
//                            override fun onRetry() {
//                                dismissDialog()
//                                hitApi(GetToggel)
//                            }
//                        })
//                    }
//
//                    override fun onTimeout() {
//                        hideProgressDialog()
//                        showRetryDialog(object : OnRerty {
//                            override fun onRetry() {
//                                dismissDialog()
//                                hitApi(GetToggel)
//                            }
//                        })
//                    }
//
//                    override fun onUnknownError() {
//                        hideProgressDialog()
//                        showRetryDialog(object : OnRerty {
//                            override fun onRetry() {
//                                dismissDialog()
//                                hitApi(GetToggel)
//                            }
//                        })
//                        //Toast.makeText(fragment.requireContext(),"onUnknownError", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onLogout() {
//                        hideProgressDialog()
////                    Toast.makeText(fragment.requireContext(),"onLogout", Toast.LENGTH_SHORT).show()
//                    }
//                })
//            )
//        }






        }







    override fun onDestroy() {
        disposables.dispose()
    }
}