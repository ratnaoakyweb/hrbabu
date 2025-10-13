package com.hrbabu.tracking.activity

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.hrbabu.tracking.BaseActivity
import com.hrbabu.tracking.adapter.LeaveAdapter
import com.hrbabu.tracking.adapter.LeaveBalanceAdapter
import com.hrbabu.tracking.databinding.ActivityGetLeaveBinding
import com.hrbabu.tracking.helpers.GetLeavesHelper
import com.hrbabu.tracking.request_response.getLeave.LeavesItem
import com.hrbabu.tracking.request_response.leavebalance.LeaveBalancesItem
import com.hrbabu.tracking.utils.CameraState
import com.hrbabu.tracking.utils.MarginItemDecoration

class ActivityLeaveList : BaseActivity(){
    private lateinit var binding: ActivityGetLeaveBinding
    private lateinit var leaveAdapter: LeaveAdapter
    private lateinit var leaveBalanceAdapter: LeaveBalanceAdapter
    private val leaveList = mutableListOf<LeavesItem?>()
    private val leaveBalanceList = mutableListOf<LeaveBalancesItem?>()
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
        }
        helper = GetLeavesHelper(this)
        helper.init(this)


        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Leave Balance"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Leave History"))
// Handle tab selection
        binding. tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        // Leave Balance
                       binding.layoutLeaveBalance.visibility = View.VISIBLE
                       binding.recyclerView.visibility = View.GONE
                       if(leaveBalanceList.isEmpty())
                        helper.hitApi(GetLeavesHelper.GET_LEAVE_BALANCE)
                    }
                    1 -> {
                        // Leave History
                        binding.layoutLeaveBalance.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        if(leaveList.isEmpty())
                        helper.hitApi(GetLeavesHelper.GET_LEAVES_LIST)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}

        })

        setupRecyclerView()

        helper.hitApi(GetLeavesHelper.GET_LEAVE_BALANCE)

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
        binding.recyclerViewLeaveBalance.addItemDecoration(MarginItemDecoration(16))

        leaveBalanceAdapter = LeaveBalanceAdapter(leaveBalanceList)
        binding.recyclerViewLeaveBalance.apply {
            layoutManager = LinearLayoutManager(this@ActivityLeaveList)
            adapter = leaveBalanceAdapter
        }

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
        if(leaves.isNullOrEmpty()){
            binding.recyclerViewLeaveBalance.visibility = View.GONE
            binding.layoutEmptyLeave.visibility = View.VISIBLE

        }else{
            binding.recyclerViewLeaveBalance.visibility = View.VISIBLE
            binding.layoutEmptyLeave.visibility = View.GONE
        }
        leaveAdapter.notifyDataSetChanged()

    }

    fun setLeaveBalanceData(leaves: List<LeaveBalancesItem?>?) {
        leaveBalanceList.clear()
        leaves?.filterNotNull()?.let { leaveBalanceList.addAll(it) }

        leaveBalanceAdapter.notifyDataSetChanged()
    }

}

