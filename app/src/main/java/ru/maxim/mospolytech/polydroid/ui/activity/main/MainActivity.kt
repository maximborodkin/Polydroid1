package ru.maxim.mospolytech.polydroid.ui.activity.main

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.ui.activity.initialization.InitializationActivity

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.mainContainer)
        NavigationUI.setupWithNavController(mainBottomNavigation, navController)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (!preferences.getBoolean("is_initialized", false)){
            startActivity(Intent(this, InitializationActivity::class.java))
        }
    }
}
