package com.simply.solidwallpaper.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.simply.solidwallpaper.R
import com.simply.solidwallpaper.databinding.ItemAngleBinding
import github.hellocsl.cursorwheel.CursorWheelLayout

class AnglePickerAdapter : CursorWheelLayout.CycleWheelAdapter() {
    companion object {
        val angles = intArrayOf(0, 45, 90, 135, 180, 225, 270, 315)
    }

    override fun getView(parent: View?, position: Int): View {
        return ItemAngleBinding.inflate(LayoutInflater.from(parent?.context)).apply {
            angle.text = parent?.context?.getString(R.string.angle, angles[position])
        }.root
    }

    override fun getItem(position: Int) = angles[position]

    override fun getCount() = angles.size

}