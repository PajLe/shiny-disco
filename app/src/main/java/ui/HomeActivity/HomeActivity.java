package ui.HomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shinydisco.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import data.User;
import services.BackgroundLocationService;
import ui.MainActivity.MainActivity;
import ui.SettingsScreen.SettingsFragment;
import ui.ViewProfileScreen.ViewProfileFragment;
import utils.Firebase;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;

    FragmentManager fragmentManager;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView emailTextView;

    private User user;

    @Override
    protected void onStart() {
        super.onStart();
        initializeUserInNavBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        profileImageView = navigationView.getHeaderView(0).findViewById(R.id.nav_profile_image);
        usernameTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        emailTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_email);

        fragmentManager = getSupportFragmentManager();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavBar,
                R.string.closeNavBar
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, HomeFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean locServiceStart = sharedPreferences.getBoolean("notifications", false);
        if (locServiceStart) {
            Intent serv = new Intent(this, BackgroundLocationService.class);
            startService(serv);
        }
    }

    private void initializeUserInNavBar() {
        FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
        if (loggedInUser != null) {
            String uid = loggedInUser.getUid();

            Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    user = task.getResult().getValue(User.class);
                    if (user != null) {
                        Picasso.get().load(user.getImageUrl()).into(profileImageView);
                        usernameTextView.setText(user.getUsername());
                        emailTextView.setText(user.getEmail());
                    }
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu_item:
                firebaseAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.view_profile_menu_item:
                Bundle bundle = new Bundle();
                if (user != null)
                    bundle.putString(ViewProfileFragment.ARG_USER_ID, user.getUid());
                drawerLayout.closeDrawer(Gravity.LEFT, true);
                fragmentManager.beginTransaction()
                        .replace(R.id.home_fragment_container, ViewProfileFragment.class, bundle)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.settings_menu_item:
                drawerLayout.closeDrawer(Gravity.LEFT, true);
                fragmentManager.beginTransaction()
                        .replace(R.id.home_fragment_container, SettingsFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                break;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Fragment f = fragmentManager.findFragmentById(R.id.home_fragment_container);

                    if (f instanceof HomeFragment) {
                        HomeFragment hf = (HomeFragment) f;
                        hf.getGoogleMapFriends().setMyLocationEnabled(true);
                        hf.zoomToLastKnownLocation(hf.getGoogleMapFriends());
                    }
                }
            }
        }

    }
}