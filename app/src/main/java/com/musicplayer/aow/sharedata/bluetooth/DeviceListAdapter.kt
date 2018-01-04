package com.musicplayer.aow.sharedata.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.musicplayer.aow.R

import java.util.ArrayList


class DeviceListAdapter(context: Context, private val mViewResourceId: Int, private val mDevices: ArrayList<BluetoothDevice>) : ArrayAdapter<BluetoothDevice>(context, mViewResourceId, mDevices) {

    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView = mLayoutInflater.inflate(mViewResourceId, null)

        val device = mDevices[position]

        if (device != null) {
            val deviceName = convertView!!.findViewById(R.id.tvDeviceName) as TextView
            val deviceAdress = convertView.findViewById(R.id.tvDeviceAddress) as TextView

            if (deviceName != null) {
                deviceName.text = device.name
            }
            if (deviceAdress != null) {
                deviceAdress.text = device.address
            }
        }

        return convertView
    }

}
