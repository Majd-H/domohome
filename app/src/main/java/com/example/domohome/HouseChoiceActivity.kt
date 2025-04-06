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
    private lateinit var userLogin : String

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
        this.userLogin = intent.getStringExtra("userLogin")!!
        Log.d("Connexion", "userLogin reçu house choice : $userLogin")
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
                accessSearch(houses)
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

    private fun accessSearch(houses: List<House>?) {
        for(house in houses!!){
            Api().get<List<User> ?>(
                "https://polyhome.lesmoulinsdudev.com/api/houses/${house.houseId}/users",
                { code, users -> responseAccess(code, users, house) },
                this.token
            )
        }
    }

    private fun responseAccess(responseCode: Int, users: List<User> ?, house:House){
        var title=""
        var message=""
        when(responseCode){
            200->{
                for(user in users!!){
                    if(user.userLogin == this.userLogin){
                        this.houses.add(house.houseId.toString())
                    }
                }
            }
            400 -> {
                title = "Accès refusé"
                message = "Données incorrectes"
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
        if(title != "") {
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