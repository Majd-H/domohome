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

data class House(var houseId : Int, var owner : Boolean)

class HouseChoiceActivity : AppCompatActivity(),HouseChoiceAdapter.OnHouseClickListener {

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
        Log.d("Connexion", "Token reçu house choice : $token")
        listHouses()
        val adapter = HouseChoiceAdapter(this, houses, this)
        val listView : ListView =findViewById(R.id.houseList)
        listView.adapter = adapter
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
                    if (house.owner) this.houses.add(house.houseId.toString())
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

    override fun intentHome(house : Int) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("id", houses[house])
        intent.putExtra("token", this.token)
        startActivity(intent)
    }
}