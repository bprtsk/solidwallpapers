package com.simply.solidwallpaper

import android.app.WallpaperManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.simply.solidwallpaper.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_set_wall){
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.are_you_sure))
                .setMessage(getString(R.string.set_as_a_wallpaper))
                .setPositiveButton(R.string.yes) { _, _ ->
                    val wallpaperManager =
                        WallpaperManager.getInstance(this)

                    val mainFragment = supportFragmentManager.findFragmentById(R.id.container) as MainFragment

                    val wallpaperBitmap = mainFragment.getWallpaperBitmap()
                    wallpaperManager.setBitmap(wallpaperBitmap)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()

        }
        return super.onOptionsItemSelected(item)
    }
}