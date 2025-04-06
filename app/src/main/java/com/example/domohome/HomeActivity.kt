package com.example.domohome

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

data class Device(var id : String, var type : String, var availableCommands : List<String>)

class HomeActivity : AppCompatActivity() {

    private lateinit var token : String
    private lateinit var id : String
    private lateinit var devices : MutableList<Device>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_housechoice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        /*this.token = intent.getStringExtra("token")!!
        this.id = intent.getStringExtra("id")!!
        listDevices()
        val adapter = HouseChoiceAdapter(this, devices, this)
        val listView : ListView =findViewById(R.id.houseList)
        listView.adapter = adapter*/
    }

    /*public fun listDevices(){
        Api().get<List<Device>>(<RequestData>(
            "https://polyhome.lesmoulinsdudev.com/api/users/register",
            requestData,
            ::reponseRegister
        )
    }*/

}