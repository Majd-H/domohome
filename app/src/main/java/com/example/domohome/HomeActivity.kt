package com.example.domohome

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

data class Device(var id : String, var type : String, var availableCommands : List<String>, var opening : Int, var power : Int)
/*data class OpeningDevice(
    val id_: String,
    val type_: String,
    val commands_: List<String>,
    val opening: Int
) : Device(id_, type_, commands_)
data class PowerDevice(
    val id_: String,
    val type_: String,
    val commands_: List<String>,
    val power: Int
) : Device(id_, type_, commands_)
*/
data class Devices(
    val devices: List<Device>
)

data class Command(val command: String)

/*class DeviceTypeAdapter : JsonDeserializer<Device> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Device {
        val jsonObject = json?.asJsonObject
        val type = jsonObject?.get("type")?.asString

        return when {
            type == "opening" -> {
                val opening = jsonObject?.get("opening")?.asInt ?: 0
                val id = jsonObject?.get("id")?.asString ?: ""
                val availableCommands = context?.deserialize<List<String>>(jsonObject?.get("availableCommands"), List::class.java) ?: emptyList()
                OpeningDevice(id, type ?: "", availableCommands, opening)
            }
            type == "power" -> {
                // Si le type est "power", on crée un PowerDevice
                val power = jsonObject?.get("power")?.asInt ?: 0
                val id = jsonObject?.get("id")?.asString ?: ""
                val availableCommands = context?.deserialize<List<String>>(jsonObject?.get("availableCommands"), List::class.java) ?: emptyList()
                PowerDevice(id, type ?: "", availableCommands, power)
            }
            else -> {
                throw JsonParseException("Unknown device type")
            }
        }
    }
}*/

data class User(val userLogin: String, val owner: Number)

class HomeActivity : AppCompatActivity(),DeviceAdapter.OnDeviceClickListener {

    private var token : String = ""
    private lateinit var id : String
    private lateinit var devices : Devices
    private lateinit var users : List<User>
    private lateinit var contenuLinear : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        this.id = intent.getStringExtra("id")!!
        this.token = intent.getStringExtra("token")!!
        Log.d("Connexion", "Token reçu home : $token")
        val gestionRadio : RadioGroup = findViewById(R.id.gestionRadio)
        contenuLinear = findViewById(R.id.contenuLinear)

        gestionRadio.setOnCheckedChangeListener { _, checkedId ->
            contenuLinear.removeAllViews()
            when (checkedId) {
                R.id.peripheriquesRadio -> listDevices()
                R.id.autorRadio -> listUsers()
                else -> null
            }
        }
    }


    public fun listDevices(){
        val path ="https://polyhome.lesmoulinsdudev.com/api/houses/${this.id}/devices"
        Log.d("DEBUG_API", "Appel GET vers /devices avec id=${this.id} et token=$token")
        Log.d("Connexion", "token appelé : ${this.token}")
        Api().get<Devices?>(
            path,
            ::responseDevices,
            this.token
        )
    }
    fun listUsers(){
        val path ="https://polyhome.lesmoulinsdudev.com/api/houses/${this.id}/users"
        Log.d("path", path)
        Api().get<List<User>?>(
            path,
            ::responseUsers,
            this.token
        )
    }
    public fun responseDevices(responseCode: Int, devices: Devices?) {
        Log.d("Connexion", "devices : $devices")
        var titre : String =""
        var message : String =""
        when(responseCode) {
            200->{
                /*val gson = GsonBuilder()
                    .registerTypeAdapter(Device::class.java, DeviceTypeAdapter())
                    .create()

                val finalDevices = gson.fromJson(devices, Devices::class.java)*/
                val inflater = LayoutInflater.from(this)
                val layoutRes = R.layout.activity_home_devices
                runOnUiThread {
                    layoutRes.let {
                        val view = inflater.inflate(it, contenuLinear, false)
                        contenuLinear.addView(view)
                        if (devices != null) {
                            val adapter = DeviceAdapter(this, devices.devices, this)
                            val listView: ListView = view.findViewById(R.id.devicesList)
                            listView.adapter = adapter
                        }
                    }
                }
            }
            400->{
                titre="Erreur 400"
                message="Les données fournies sont incorrectes"
            }
            403->{
                titre="Erreur 403"
                message="Token invalide"
                Log.e("DeviceDebug", "403 détecté, on tente d’afficher l’alerte")
            }
            500->{
                titre="Erreur 500"
                message="Une erreur s'est produite au niveau du serveur"
            }
            else -> {
                Log.e("Register", "Code inconnu : $responseCode")
            }
        }
        if(titre != ""){
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle(titre)
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
    public fun responseUsers(responseCode: Int, users: List<User>?) {
        Log.d("Connexion", "users : $users")
        var titre : String =""
        var message : String =""
        when(responseCode) {
            200->{
                /*val adapter = UserAdapter(this, users)
                val inflater = LayoutInflater.from(this)
                val layoutRes = R.layout.activity_home_autorisations
                layoutRes?.let {
                    val view = inflater.inflate(it, contenuLinear, false)
                    contenuLinear.addView(view)
                }
                val listView: ListView = findViewById(R.id.usersList)
                listView.adapter = adapter*/
            }
            400->{
                titre="Erreur 400"
                message="Les données fournies sont incorrectes"
            }
            403->{
                titre="Erreur 403"
                message="Token invalide"
            }
            500->{
                titre="Erreur 500"
                message="Une erreur s'est produite au niveau du serveur"
            }
        }
        if(titre != ""){
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle(titre)
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    override fun actionDevice(deviceId: String, command: String) {
        val commande = Command(command=command)
        Api().post<Command,Command?>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/${this.id}/devices/$deviceId/command",
            commande,
            ::responseActionDevice,
            this.token
        )
    }
    public fun responseActionDevice(responseCode: Int, command: Command?) {
        Log.d("Connexion", "command : $command")
        var titre : String =""
        var message : String =""
        when(responseCode) {
            200->{
                Log.d("Connexion", "Action effectuée avec succès")
            }
            400->{
                titre="Erreur 400"
                message="Les données fournies sont incorrectes"
            }
            403->{
                titre="Erreur 403"
                message="Token invalide"
            }
            500->{
                titre="Erreur 500"
                message="Une erreur s'est produite au niveau du serveur"
            }
            else ->{
                Log.d("Connexion", "autre erreur : $responseCode")
            }
        }
        if(titre != ""){
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle(titre)
                    .setMessage(message)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }



}