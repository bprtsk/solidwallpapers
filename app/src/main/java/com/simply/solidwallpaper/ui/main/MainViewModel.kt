package com.simply.solidwallpaper.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simply.solidwallpaper.model.Event

class MainViewModel : ViewModel() {
    private val _colors = MutableLiveData<MutableList<Int>>(mutableListOf())
    val colors: LiveData<MutableList<Int>> = _colors

    private val _angle = MutableLiveData<Int>(0)
    val angle: LiveData<Int> = _angle

    private val _colorHasChangedEvent = MutableLiveData<Event<Pair<Int, Int>>>()
    val colorHasChangedEvent: LiveData<Event<Pair<Int, Int>>> = _colorHasChangedEvent

    private val _invalidateColorsEvent = MutableLiveData<Event<Boolean>>()
    val invalidateColorsEvent: LiveData<Event<Boolean>> = _invalidateColorsEvent

    fun setColor(index: Int, color: Int) {
        val colorsList: MutableList<Int>
        if (colors.value != null) {
            colorsList = _colors.value!!
        } else {
            return
        }

        var add = false
        if (colorsList.size < index + 1) {
            add = true
        }

        _colors.value = colorsList.apply {
            if (add) {
                this.add(color)

                _invalidateColorsEvent.value = Event(true)
            } else {
                this[index] = color

                _colorHasChangedEvent.value = Event(Pair(index, color))
            }
        }

    }

    fun setAngle(angle: Int) {
        _angle.value = angle
    }

    fun removeColor(index: Int) {
        _colors.value = _colors.value?.apply {
            removeAt(index)
        }

        _invalidateColorsEvent.value = Event(true)
    }
}