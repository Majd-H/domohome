package com.example.domohome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class HouseChoiceAdapter(
    context: Context,
    private var houses: MutableList<String> = mutableListOf<String>(),
    private val listener: OnHouseClickListener
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    interface OnHouseClickListener {
        fun intentHome(house: Int)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItem(position: Int): String {
        return houses[position]
    }
    override fun getCount(): Int {
        return houses.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.activity_housechoice_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.idHouse)
        val button= view.findViewById<Button>(R.id.accessButton)

        textView.text = houses[position]
        button.setOnClickListener{
            listener.intentHome(position)
        }
        return view
    }

}