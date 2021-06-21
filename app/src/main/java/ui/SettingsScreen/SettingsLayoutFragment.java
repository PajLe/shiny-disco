package ui.SettingsScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.shinydisco.R;

import org.jetbrains.annotations.NotNull;

import services.BackgroundLocationService;

public class SettingsLayoutFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat discoNotificationToggle;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey);

    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        discoNotificationToggle = findPreference("notifications");
        if (discoNotificationToggle != null)
            discoNotificationToggle.setOnPreferenceChangeListener(discoNotificationToggleChangeListener);

        return rootView;
    }

    Preference.OnPreferenceChangeListener discoNotificationToggleChangeListener = (preference, newValue) -> {
        boolean newVal = (boolean) newValue;

        Activity a = getActivity();
        if (newVal) {
            if (a != null) {
                Intent serv = new Intent(a, BackgroundLocationService.class);
                a.startService(serv);
            }
        } else {
            if (a != null) {
                Intent serv = new Intent(a, BackgroundLocationService.class);
                a.stopService(serv);
            }
        }

        return false;
    };

}
