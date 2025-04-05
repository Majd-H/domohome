package com.example.domohome

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class RequestData(var login: String, var password: String)
data class Token(var token: String)
data class House(var id : Int, var owner : Boolean)

class LoginActivity : AppCompatActivity() {

    private lateinit var idView : TextView
    private lateinit var mdpView : TextView
    private lateinit var radioGroup : RadioGroup
    private lateinit var button : Button
    private var token= Token(token = "")
    private var houseId : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        idView = findViewById(R.id.idView)
        mdpView = findViewById(R.id.mdpView)
        radioGroup = findViewById(R.id.radioGroup)
        button = findViewById(R.id.button)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.coRadio -> updateConnexion()
                R.id.regRadio -> updateRegister()
            }
        }
    }

    private fun updateConnexion() {
        idView.text = "Identifiant"
        mdpView.text = "Mot de passe"
        button.text = "Connexion"
        button.setOnClickListener{
            connection()
        }
    }

    private fun updateRegister() {
        idView.text = "Identifiant du nouveau compte"
        mdpView.text = "Mot de passe du nouveau compte"
        button.text = "Inscription"
        button.setOnClickListener{
            register()
        }
    }

    private fun connection(){
        val loginEditText : EditText = findViewById<EditText>(R.id.idEdit)
        val mdpEditText : EditText = findViewById<EditText>(R.id.mdpEdit)
        val connectionData = RequestData(
            login = loginEditText.text.toString(),
            password = mdpEditText.text.toString()
        )
        Api().post<RequestData,Token ?>(
            "https://polyhome.lesmoulinsdudev.com/api/users/auth",
            connectionData,
            ::reponseConnection
        )
    }
    private fun register(){
        val loginEditText : EditText = findViewById<EditText>(R.id.idEdit)
        val mdpEditText : EditText = findViewById<EditText>(R.id.mdpEdit)
        val requestData = RequestData(
            login = loginEditText.text.toString(),
            password = mdpEditText.text.toString()
        )
        Api().post<RequestData>(
            "https://polyhome.lesmoulinsdudev.com/api/users/register",
            requestData,
            ::reponseRegister
        )
    }

    private fun reponseRegister(responseCode: Int) {
        var message : String = ""
        var title : String = ""
        when (responseCode) {
            200 -> {
                title = "✅ Succès"
                message = "Le compte a bien été créé."
            }
            400 -> {
                title = "Erreur 400"
                message = "Les données fournies sont incorrectes."
            }
            409 -> {
                title = "Erreur 409"
                message = "Le login est déjà utilisé par un autre compte."
            }
            500 -> {
                title = "Erreur 500"
                message = "Une erreur s’est produite au niveau du serveur."
            }
            else -> {
                Log.e("Register", "Code inconnu : $responseCode")
            }
        }
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

    private fun reponseConnection(responseCode: Int, token: Token ?) {
        var message : String = ""
        var title : String = ""
        when (responseCode) {
            200 -> {
                if (token != null) {
                    this.token = token
                    Log.d("Connexion", "Token reçu : $token")
                    intentHouseChoice()
                }
            }
            400 -> {
                title = "Erreur 400"
                message = "Les données fournies sont incorrectes."
            }
            409 -> {
                title = "Erreur 409"
                message = "Aucun utilisateur ne correspond aux identifiants donnés."
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

    private fun intentHouseChoice() {
        val intent = Intent(this, HouseChoiceActivity::class.java)
        intent.putExtra("token", this.token.token)
        startActivity(intent)
    }

}