package ru.maxim.mospolytech.polydroid.ui.activity.initialization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_initialization.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.repository.local.PreferencesManager

class InitializationActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initialization)
        initializationOk.setOnClickListener {
            val text = initializationKey.text.toString()
            if (text.isEmpty()){
                initializationKeyLayout.error = getString(R.string.please_fill_this_field)
            }
            else{
                PreferencesManager.notificationsTarget = initializationKey.text.toString()
                PreferencesManager.isInitialized = true
                finish()
            }
        }
        initializationSkip.setOnClickListener {
            PreferencesManager.isInitialized = true
            finish()
        }
    }
}