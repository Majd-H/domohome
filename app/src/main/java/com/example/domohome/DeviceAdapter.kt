package com.example.domohome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.domohome.HouseChoiceAdapter.OnHouseClickListener

class DeviceAdapter(
    private val context: Context,
    private var devices: List<Device>,
    private val listener: OnDeviceClickListener
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    interface OnDeviceClickListener {
        fun actionDevice(deviceId: String, command: String, indice:Int)
    }

    override fun getCount(): Int {
        return devices.size
    }

    override fun getItem(position: Int): Any {
        return devices[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.activity_home_devices_item, parent, false)
        val idView = view.findViewById<TextView>(R.id.idDevice)
        val actionContainer= view.findViewById<ViewGroup>(R.id.actionsContainer)
        idView.text = devices[position].id

        val bouton: Button = view.findViewById(R.id.etatButton)
        if(devices[position].color == "green"){
            bouton.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        }else if(devices[position].color == "red"){
            bouton.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        }else if(devices[position].color == "orange"){
            bouton.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
        }else{
            bouton.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        for (command in devices[position].availableCommands) {
            val button = Button(context).apply {
                text = command
                setOnClickListener {
                    listener.actionDevice(devices[position].id, command, position)
                }
            }
            actionContainer.addView(button)
        }
        return view
    }

}