package com.example.domohome

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat

data class Device(var id : String, var type : String, var availableCommands : List<String>, var opening : Int, var power : Int, var color:String)
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

data class User(val userLogin: String, val owner: Number)

class HomeActivity : AppCompatActivity(),DeviceAdapter.OnDeviceClickListener {

    private var token : String = ""
    private lateinit var id : String
    private lateinit var devices : Devices
    private lateinit var dAdapter : DeviceAdapter
    private lateinit var users : List<User>
    private lateinit var contenuLinear : LinearLayout
    private var index : Int = 0

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
        Api().get<Devices?>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/${this.id}/devices",
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
                val inflater = LayoutInflater.from(this)
                val layoutRes = R.layout.activity_home_devices
                runOnUiThread {
                    layoutRes.let {
                        val view = inflater.inflate(it, contenuLinear, false)
                        contenuLinear.addView(view)
                        if (devices != null) {
                            this.devices = devices
                            val adapter = DeviceAdapter(this, this.devices.devices, this)
                            val openButton: Button = view.findViewById(R.id.openButton)
                            val closeButton: Button = view.findViewById(R.id.closeButton)
                            val turnonButton: Button = view.findViewById(R.id.turnonButton)
                            val turnoffButton: Button = view.findViewById(R.id.turnoffButton)

                            openButton.setOnClickListener {
                                var i=0
                                for (device in devices.devices) {
                                    if(device.availableCommands.contains("OPEN")){ actionDevice(device.id, "OPEN", i) }
                                    i++
                                }
                            }
                            closeButton.setOnClickListener {
                                var i=0
                                for (device in devices.devices) {
                                    if(device.availableCommands.contains("OPEN")){ actionDevice(device.id, "CLOSE", i) }
                                    i++
                                }
                            }
                            turnonButton.setOnClickListener {
                                var i=0
                                for (device in devices.devices) {
                                    if(device.availableCommands.contains("TURN ON")){ actionDevice(device.id, "TURN ON", i) }
                                    i++
                                }
                            }
                            turnoffButton.setOnClickListener {
                                var i=0
                                for (device in devices.devices) {
                                    if(device.availableCommands.contains("TURN OFF")){ actionDevice(device.id, "TURN OFF", i) }
                                    i++
                                }
                            }
                            val listView: ListView = view.findViewById(R.id.devicesList)
                            listView.adapter = adapter
                            this.dAdapter = adapter
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

    override fun actionDevice(deviceId: String, command: String, indice:Int) {
        val commande = Command(command=command)
        Api().post<Command,Command?>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/${this.id}/devices/$deviceId/command",
            commande,
            ::responseActionDevice,
            this.token
        )
        if(command == "OPEN" || command == "TURN ON"){
            devices.devices[indice].color = "green"
        }else if(command == "CLOSE" || command == "TURN OFF"){
            devices.devices[indice].color = "red"
        }else{
            devices.devices[indice].color = "orange"
        }
        this.dAdapter.notifyDataSetChanged()



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