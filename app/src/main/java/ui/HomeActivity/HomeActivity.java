package ui.HomeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.shinydisco.R;
import com.google.firebase.auth.FirebaseAuth;

import ui.MainActivity.MainActivity;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();

        Button b = findViewById(R.id.button);
        b.setOnClickListener(l -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(HomeActivity.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}