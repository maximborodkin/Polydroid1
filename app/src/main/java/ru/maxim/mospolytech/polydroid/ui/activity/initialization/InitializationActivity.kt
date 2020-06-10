package ru.maxim.mospolytech.polydroid.ui.activity.initialization

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_initialization.*
import ru.maxim.mospolytech.polydroid.R

class InitializationActivity : AppCompatActivity() {

    private lateinit var preferencesEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initialization)
        preferencesEditor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        initializationOk.setOnClickListener {
            val text = initializationKey.text.toString()
            if (text.isEmpty()){
                initializationKeyLayout.error = getString(R.string.please_fill_this_field)
            }
            else{
                preferencesEditor.putString("notifications_target",
                    initializationKey.text.toString()).apply()
                preferencesEditor.putBoolean("is_initialized", true).apply()
                finish()
            }
        }
        initializationSkip.setOnClickListener {
            preferencesEditor.putBoolean("is_initialized", true).apply()
            finish()
        }
    }
}