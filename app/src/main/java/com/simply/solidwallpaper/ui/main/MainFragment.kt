package com.simply.solidwallpaper.ui.main

import android.app.WallpaperManager
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import com.simply.solidwallpaper.R
import com.simply.solidwallpaper.databinding.ContentAnglePickerBinding
import com.simply.solidwallpaper.databinding.ContentColorPickerBinding
import com.simply.solidwallpaper.databinding.MainFragmentBinding
import com.simply.solidwallpaper.model.EventObserver
import com.simply.solidwallpaper.ui.adapter.AnglePickerAdapter
import com.simply.solidwallpaper.ui.setBackgroundColorFilter


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()

        const val ID_BASIS = 1234
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)

        binding.gradientAngleButton.setOnClickListener {
            showAnglePicker()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.colorHasChangedEvent.observe(viewLifecycleOwner, EventObserver {
            val index = it.first
            val color = it.second

            val colorView = binding.colorsContainer.findViewById<CardView>(ID_BASIS + index)
            colorView.setCardBackgroundColor(color)

            binding.wallpaperCanvas.setColors(viewModel.colors.value)
        })

        viewModel.invalidateColorsEvent.observe(viewLifecycleOwner, EventObserver {
            invalidateColorViews()

            binding.wallpaperCanvas.setColors(viewModel.colors.value)
        })

        viewModel.angle.observe(viewLifecycleOwner, Observer {
            binding.wallpaperCanvas.setAngle(it)

            binding.gradientAngleButton.text = getString(R.string.gradient_angle, it)
        })

        viewModel.setColor(0, resources.getColor(R.color.colorAccent))

        viewModel.setAngle(0)
    }

    private fun invalidateColorViews() {
        binding.colorsContainer.removeAllViews()
        viewModel.colors.value?.let { colors ->
            val context = requireContext()
            val size = context.resources.getDimensionPixelSize(R.dimen.color_size)
            val marginEnd = context.resources.getDimensionPixelSize(R.dimen.color_margin_end)

            val layoutParams = FrameLayout.LayoutParams(size, size)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.marginEnd = marginEnd
            } else {
                layoutParams.rightMargin = marginEnd
            }

            for (i in 0 until (if (colors.size < 5) colors.size + 1 else 5)) {
                val colorView = CardView(context)
                colorView.layoutParams = layoutParams

                val color: Int
                if (i == colors.size && colors.size <= 5) {
                    color = Color.RED
                    colorView.addView(
                        TextView(context).apply {
                            text = "+"
                            textSize = 16F
                            gravity = Gravity.CENTER
                        }
                    )

                    colorView.setCardBackgroundColor(context.resources.getColor(R.color.add_color_bg))
                } else {
                    color = colors[i]
                    colorView.id = ID_BASIS + i

                    colorView.setCardBackgroundColor(colors[i])
                }

                colorView.setOnClickListener {
                    showColorPicker(index = i, color = color)
                }

                binding.colorsContainer.addView(colorView)
            }
        }
    }

    private fun showAnglePicker(){
        val context = requireContext()
        val anglePickerBinding = ContentAnglePickerBinding.inflate(layoutInflater)

        anglePickerBinding.anglePicker.setAdapter(AnglePickerAdapter())
//        anglePickerBinding.anglePicker.setSelectedAngle(viewModel.angle.value?.toDouble() ?: 0.0)

        anglePickerBinding.anglePicker.setSelection(AnglePickerAdapter.angles.indexOf(viewModel.angle.value ?: 0))

        anglePickerBinding.anglePicker.setOnMenuSelectedListener { _, _, index ->
            viewModel.setAngle(AnglePickerAdapter.angles[index])
        }

        val alertDialogBuilder = AlertDialog.Builder(context)
            .setView(anglePickerBinding.root)
            .setTitle(R.string.choose_gradient_angle)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)

        val dialog = alertDialogBuilder.show()
        dialog.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
    }

    private fun showColorPicker(index: Int, color: Int) {
        val context = requireContext()
        val colorPickerBinding = ContentColorPickerBinding.inflate(layoutInflater)
        colorPickerBinding.colorIndicator.setBackgroundColorFilter(color)

        val alertDialogBuilder = AlertDialog.Builder(context)
            .setView(colorPickerBinding.root)
            .setTitle(R.string.choose_color)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)

        if (index == viewModel.colors.value?.size){
            viewModel.setColor(index, color)
        }

        if (index != viewModel.colors.value?.size && (viewModel.colors.value?.size ?: 0) > 1) {
            alertDialogBuilder
                .setNegativeButton(R.string.remove) { dialog, _ ->
                    viewModel.removeColor(index)
                    dialog.dismiss()
                }
        }

        colorPickerBinding.colorPicker.setColor(color)
        colorPickerBinding.colorPicker.setColorSelectionListener(object :
            SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                super.onColorSelected(color)

                colorPickerBinding.colorIndicator.setBackgroundColorFilter(color)
                viewModel.setColor(index, color)
            }
        })

        val dialog = alertDialogBuilder.show()
        dialog.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
    }

    fun getWallpaperBitmap(): Bitmap? {
        return binding.wallpaperCanvas.getWallpaperBitmap()
    }


}