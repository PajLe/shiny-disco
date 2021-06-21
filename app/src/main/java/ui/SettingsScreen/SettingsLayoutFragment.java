package ui.SettingsScreen;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.shinydisco.R;

public class SettingsLayoutFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey);
    }
}
