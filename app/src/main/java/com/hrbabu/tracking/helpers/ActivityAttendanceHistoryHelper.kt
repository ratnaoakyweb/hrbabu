package com.hrbabu.tracking.helpers

import android.widget.Toast
import com.hrbabu.tracking.activity.ActivityAttendanceHistory
import com.hrbabu.tracking.activity.ActivityClientList
import com.hrbabu.tracking.adapter.ClientAdapter
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.request_response.attendanceHistory.GetAttendanceRequest
import com.hrbabu.tracking.request_response.attendanceHistory.GetAttendanceResponse
import com.hrbabu.tracking.request_response.getclient.GetClientRequest
import com.hrbabu.tracking.request_response.getclient.GetClientResponse
import com.hrbabu.tracking.request_response.halfday.SaveHalfDayRequest
import com.hrbabu.tracking.request_response.halfday.SaveHalfDayResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityAttendanceHistoryHelper(val activityAttendanceHistory: ActivityAttendanceHistory) : BaseHelperActivity() {

    companion object{
        const val GET_ATTENDANCE_LIST = "getAttendanceList"
    }

    fun hitApi(apiKey: String,fromDate: String = "", toDate: String = "") {

        if(apiKey == GET_ATTENDANCE_LIST) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(activityAttendanceHistory.applicationContext).getEmployeeAttendanceNew(
                        GetAttendanceRequest(
                            fromDate = fromDate,
                            toDate = toDate
                        )) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<GetAttendanceResponse?>() {
                    override fun onSuccess(t: GetAttendanceResponse?) {
                        hideProgressDialog()

                        val attendance = t?.res?.attendance

                        activityAttendanceHistory.setUpRecyclerView(attendance)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            activityAttendanceHistory.applicationContext,
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

    override fun hitApi(apiKey: String) {

        if(apiKey == GET_ATTENDANCE_LIST) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(activityAttendanceHistory.applicationContext).getEmployeeAttendanceNew(
                        GetAttendanceRequest(
                            fromDate=getCurrentApiDate(),
                            toDate = getCurrentApiDate()
                        )) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<GetAttendanceResponse?>() {
                    override fun onSuccess(t: GetAttendanceResponse?) {
                        hideProgressDialog()

                        val attendance = t?.res?.attendance

                        activityAttendanceHistory.setUpRecyclerView(attendance)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            activityAttendanceHistory.applicationContext,
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

    fun saveHalfDayRequest(reason: String) {
        showProgressDialog()
        disposables.add(
            sendApiRequest(
                getApiClientAuth(activityAttendanceHistory.applicationContext).saveHalfDayRequest(
                    SaveHalfDayRequest(
                        reason = reason
                    )) // no token here if your getApiClientAuth already attaches it
            )!!.subscribeWith(object : CallbackWrapper<SaveHalfDayResponse?>() {
                override fun onSuccess(t: SaveHalfDayResponse?) {
                    hideProgressDialog()

                    if(t?.res?.statusCode==1){

                        Toast.makeText(
                            activityAttendanceHistory.applicationContext,
                            t.res.message ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                       hitApi(GET_ATTENDANCE_LIST)
                    }else{
                        Toast.makeText(
                            activityAttendanceHistory.applicationContext,
                            t?.res?.message ?: "",
                            Toast.LENGTH_SHORT
                        ).show()
                    }



                }

                override fun onError(t: String?) {
                    hideProgressDialog()
                    Toast.makeText(
                        activityAttendanceHistory.applicationContext,
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

    override fun onDestroy() {

    }
    public fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }


    private fun getCurrentApiDate(): String {
        return getCurrentDate() + "T00:00:00.000Z"
    }
}