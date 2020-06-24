package ru.maxim.mospolytech.polydroid.ui.activity.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager
import ru.maxim.mospolytech.polydroid.ui.activity.initialization.InitializationActivity

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.mainContainer)
        NavigationUI.setupWithNavController(mainBottomNavigation, navController)

        if (!PreferencesManager.isInitialized){
            startActivity(Intent(this, InitializationActivity::class.java))
        }
    }
}
