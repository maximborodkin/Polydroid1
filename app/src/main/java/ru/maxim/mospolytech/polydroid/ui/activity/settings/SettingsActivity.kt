package ru.maxim.mospolytech.polydroid.ui.activity.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.repository.local.CacheManager
import java.io.File

class SettingsActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.settings)
        if (savedInstanceState == null)
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.activity_preferences)
            val notificationsKeySummaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                if (preference.text.isNullOrEmpty())
                    getString(R.string.group_or_teacher_to_notify_schedule_changes)
                else
                    preference.text
            }
            findPreference<EditTextPreference>("notifications_key")?.summaryProvider = notificationsKeySummaryProvider

            val invalidateCacheSummaryProvider = Preference.SummaryProvider<Preference> {
                val cacheSize = CacheManager.getCacheSize()
                when {
                    cacheSize > 1000 -> getString(R.string.cache_size_kbytes, (CacheManager.getCacheSize()/1000).toString())
                    cacheSize > 1000_000 -> getString(R.string.cache_size_mbytes, (CacheManager.getCacheSize()/1000_000).toString())
                    else -> getString(R.string.cache_size_bytes, CacheManager.getCacheSize().toString())
                }
            }

            findPreference<Preference>("invalidate_cache")?.apply {
                setOnPreferenceClickListener {
                    val cacheUri = context?.filesDir?.toURI()
                    if (cacheUri != null) {
                        val cacheDir = File(cacheUri)
                        if (cacheDir.isDirectory)
                            cacheDir.listFiles()?.forEach { it.delete() }
                    }
                    summaryProvider = invalidateCacheSummaryProvider
                    true
                }
                summaryProvider = invalidateCacheSummaryProvider
            }
        }
    }
}
