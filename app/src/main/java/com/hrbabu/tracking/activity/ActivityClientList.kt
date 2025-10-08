package com.hrbabu.tracking.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrbabu.tracking.BaseActivity
import com.hrbabu.tracking.adapter.ClientAdapter
import com.hrbabu.tracking.databinding.ActivityClientListBinding
import com.hrbabu.tracking.helpers.ActivityClientListHelper
import com.hrbabu.tracking.helpers.ActivityClientListHelper.Companion.GET_CLIENT_LIST
import com.hrbabu.tracking.request_response.getclient.ClientsItem

class ActivityClientList : BaseActivity() {

    private lateinit var binding: ActivityClientListBinding
    private lateinit var adapter: ClientAdapter
    var clientList: List<ClientsItem?> = listOf()
    private  lateinit var activityClientListHelper : ActivityClientListHelper
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getBooleanExtra("callback", false)
            if(data ?: true){
            {
                activityClientListHelper.hitApi(GET_CLIENT_LIST)

            }
        }
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityClientListHelper = ActivityClientListHelper(this)
        activityClientListHelper.init(this)
        setupRecyclerView()

        activityClientListHelper.hitApi(GET_CLIENT_LIST)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddNewClient.setOnClickListener {


            startForResult.launch(Intent(this, AddNewClientActivity::class.java))
        }

    }




    private fun setupRecyclerView() {
        binding.recyclerViewClients.layoutManager = LinearLayoutManager(this)
    }

    fun setUpClients(clients: List<ClientsItem?>?) {

        if (!clients.isNullOrEmpty()) {
            clientList = clients
            adapter = ClientAdapter(clientList,object : ClientAdapter.OnClientClickListener {
                override fun onClientClick(client: ClientsItem?) {
                    Toast.makeText(this@ActivityClientList, "Selected: ${client?.clientId}", Toast.LENGTH_SHORT).show()
                    //send to previus activity on result
                    val intent = intent
                    intent.putExtra("clientId", client?.clientId)
                    setResult(RESULT_OK, intent)
                    finish()
                // Handle client click event here
                }

            })
            binding.recyclerViewClients.adapter = adapter

            setupSearch()
        } else {
            Toast.makeText(
                this,
                "No clients found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = clientList.filter {
                    it?.clientName!!.contains(newText ?: "", ignoreCase = true)
                }
                adapter.filterList(filteredList)
                return true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}