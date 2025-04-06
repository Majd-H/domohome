package com.example.domohome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.domohome.DeviceAdapter.OnDeviceClickListener

class UserAdapter (
        private val context: Context,
        private var users: List<User> = listOf<User>(),
        private val listener: OnUserClickListener
    ) : BaseAdapter() {

        private val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    interface OnUserClickListener {
        fun deleteUser(index:Int)
    }

        override fun getCount(): Int {
            return users.size
        }

        override fun getItem(position: Int): Any {
            return users[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = inflater.inflate(R.layout.activity_home_autorisations_item, parent, false)
            val loginView: TextView = view.findViewById(R.id.loginView)
            val deleteButton: Button = view.findViewById(R.id.deleteButton)

            loginView.text = users[position].userLogin
            deleteButton.setOnClickListener{
                listener.deleteUser(position)
            }
            return view
        }
}