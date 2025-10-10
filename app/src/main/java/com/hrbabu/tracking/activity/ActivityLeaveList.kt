package com.hrbabu.tracking.activity

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrbabu.tracking.BaseActivity
import com.hrbabu.tracking.adapter.LeaveAdapter
import com.hrbabu.tracking.databinding.ActivityGetLeaveBinding
import com.hrbabu.tracking.helpers.GetLeavesHelper
import com.hrbabu.tracking.request_response.getLeave.LeavesItem
import com.hrbabu.tracking.utils.CameraState
import com.hrbabu.tracking.utils.MarginItemDecoration

class ActivityLeaveList : BaseActivity(){
    private lateinit var binding: ActivityGetLeaveBinding
    private lateinit var leaveAdapter: LeaveAdapter
    private val leaveList = mutableListOf<LeavesItem?>()
    private lateinit var helper : GetLeavesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnApplyLeave.setOnClickListener {
            //ApplyLeaveActivity
            launcher.launch(Intent(this, ApplyLeaveActivity::class.java))
//            startActivity(ApplyLeaveActivity.newIntent(this))
        }

        setupRecyclerView()

        helper = GetLeavesHelper(this)
        helper.init(this)
        helper.hitApi(GetLeavesHelper.GET_LEAVES_LIST)

    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            helper.hitApi(GetLeavesHelper.GET_LEAVES_LIST)
        }
    }


    private fun setupRecyclerView() {

        binding.recyclerView.addItemDecoration(MarginItemDecoration(16))

        leaveAdapter = LeaveAdapter(leaveList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ActivityLeaveList)
            adapter = leaveAdapter
        }
    }

    // ✅ This method will be called after API response
    fun setLeaveData(leaves: List<LeavesItem?>?) {
        leaveList.clear()
        leaves?.filterNotNull()?.let { leaveList.addAll(it) }
        leaveAdapter.notifyDataSetChanged()
    }

}

