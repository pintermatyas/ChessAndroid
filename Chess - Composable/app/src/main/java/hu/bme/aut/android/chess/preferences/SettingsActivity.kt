package hu.bme.aut.android.chess.preferences

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chess.Chess.Companion.repository
import hu.bme.aut.android.chess.R
import kotlin.concurrent.thread


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.hide()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val resetBtn = preferenceManager.findPreference<Preference>("reset_data")
            resetBtn?.setOnPreferenceClickListener {
                reset()
            }
        }

        private fun reset(): Boolean {
            thread {
                repository.getAllGames()
            }
            Snackbar.make(requireView(), "Reset successful", Snackbar.LENGTH_SHORT).show()
            return true
        }
    }


}