package com.hrbabu.tracking.helpers

import android.widget.Toast
import com.hrbabu.tracking.activity.ActivityProfile
import com.hrbabu.tracking.activity.ApplyLeaveActivity
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.databinding.ActivityApplyLeaveBinding
import com.hrbabu.tracking.request_response.alldropdown.GetAllDropdownResponse
import com.hrbabu.tracking.request_response.applyLeave.SaveEmpLeaveRequest
import com.hrbabu.tracking.request_response.applyLeave.SaveEmpLeaveResponse
import com.hrbabu.tracking.request_response.profile.ProfileResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty

class ApplyLeaveHelper(val applyLeaveActivity: ApplyLeaveActivity) : BaseHelperActivity() {

    companion object{
        const val GET_LEAVE_TYPE = "getLeaveType"
        const val SAVE_EMP_LEAVE = "saveEmpLeave"
    }

    override fun hitApi(apiKey: String) {
        showProgressDialog()
        if(apiKey == GET_LEAVE_TYPE) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(applyLeaveActivity.applicationContext).getAllDropDown() // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<GetAllDropdownResponse>() {
                    override fun onSuccess(t: GetAllDropdownResponse?) {
                        hideProgressDialog()
                        applyLeaveActivity.setUpData(t?.rc)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            applyLeaveActivity.applicationContext,
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
        }else if(apiKey == SAVE_EMP_LEAVE) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(applyLeaveActivity.applicationContext).saveEmployeeLeave(
                        SaveEmpLeaveRequest(
                            flag = "I",
                            startDate = applyLeaveActivity.startDate,
                            endDate = applyLeaveActivity.endDate,
                            leaveTypeId = applyLeaveActivity.leaveId,
                            reason = applyLeaveActivity.reason,
                            totalDays = applyLeaveActivity.totalDays
                        )
                    ) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<SaveEmpLeaveResponse>() {
                    override fun onSuccess(t: SaveEmpLeaveResponse?) {
                        hideProgressDialog()
                        applyLeaveActivity.parseLeaveResponse(t)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            applyLeaveActivity.applicationContext,
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