package com.example.domohome

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HouseChoiceActivity : AppCompatActivity() {

    private lateinit var token : String
    private var houses = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_housechoice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        this.token = intent.getStringExtra("token")!!
        listHouses()
    }

    private fun listHouses() {
        Api().get<List<House> ?>(
            "https://polyhome.lesmoulinsdudev.com/api/houses",
            ::responseFindHouse,
            this.token
        )
    }

    private fun responseFindHouse(responseCode: Int, houses: List<House> ?) {
        var message : String = ""
        var title : String = ""
        when (responseCode) {
            200 -> {
                for (house in houses!!) {
                    if (house.owner) this.houses.add(house.id.toString())
                    break
                }
            }
            403 -> {
                title = "Accès refusé"
                message = "Token invalide"
            }
            500 -> {
                title = "Erreur 500"
                message = "Une erreur s’est produite au niveau du serveur."
            }
            else -> {
                Log.e("Register", "Code inconnu : $responseCode")
            }
        }
        if(title != ""){
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
}