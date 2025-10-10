package com.hrbabu.tracking.helpers

import android.widget.Toast
import com.hrbabu.tracking.activity.ActivityLeaveList
import com.hrbabu.tracking.activity.ActivityProfile
import com.hrbabu.tracking.activity.ApplyLeaveActivity
import com.hrbabu.tracking.apiBase.BaseHelperActivity
import com.hrbabu.tracking.apiBase.CallbackWrapper
import com.hrbabu.tracking.databinding.ActivityApplyLeaveBinding
import com.hrbabu.tracking.request_response.alldropdown.GetAllDropdownResponse
import com.hrbabu.tracking.request_response.applyLeave.SaveEmpLeaveRequest
import com.hrbabu.tracking.request_response.applyLeave.SaveEmpLeaveResponse
import com.hrbabu.tracking.request_response.getLeave.GetEmpLeaveRequest
import com.hrbabu.tracking.request_response.getLeave.GetLeaveResponse
import com.hrbabu.tracking.request_response.profile.ProfileResponse
import com.hrbabu.tracking.utils.getApiClientAuth
import com.hrbabu.tracking.utils.sendApiRequest
import com.social.pe.interfaces.OnRerty

class GetLeavesHelper(val activity: ActivityLeaveList) : BaseHelperActivity() {

    companion object{
        const val GET_LEAVES_LIST = "getLeaves"
    }

    override fun hitApi(apiKey: String) {
        showProgressDialog()
        if(apiKey == GET_LEAVES_LIST) {

            disposables.add(
                sendApiRequest(
                    getApiClientAuth(activity.applicationContext).getEmployeeLeaves(
                        GetEmpLeaveRequest(
                            pageSize = 100,
                            pageNumber = 1,
                            search = ""
                        )
                    ) // no token here if your getApiClientAuth already attaches it
                )!!.subscribeWith(object : CallbackWrapper<GetLeaveResponse>() {
                    override fun onSuccess(t: GetLeaveResponse) {
                        hideProgressDialog()
                        activity.setLeaveData(t.res?.leaves)

                    }

                    override fun onError(t: String?) {
                        hideProgressDialog()
                        Toast.makeText(
                            activity.applicationContext,
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