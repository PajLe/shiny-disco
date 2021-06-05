package ui.MainActivity;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shinydisco.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private EditText nameEditText;
    private Button signUpButton;
    private ImageButton imageButton;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameEditText = getView().findViewById(R.id.register_username_input);
        passwordEditText = getView().findViewById(R.id.register_password_input);
        emailEditText = getView().findViewById(R.id.register_email_input);
        nameEditText = getView().findViewById(R.id.register_name_input);
        signUpButton = getView().findViewById(R.id.register_button);
        imageButton = getView().findViewById(R.id.register_image_button);

        // listeners
        usernameEditText.addTextChangedListener(signUpTextWatcher);
        passwordEditText.addTextChangedListener(signUpTextWatcher);
        emailEditText.addTextChangedListener(signUpTextWatcher);
        nameEditText.addTextChangedListener(signUpTextWatcher);

        imageButton.setOnClickListener(imageButtonOnClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    private TextWatcher signUpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = usernameEditText.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();
            String emailInput = emailEditText.getText().toString().trim();
            String nameInput = nameEditText.getText().toString().trim();

            signUpButton.setEnabled(!usernameInput.isEmpty()
                && !passwordInput.isEmpty()
                && !emailInput.isEmpty()
                && !nameInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Object d = extras.get("data");
                        if (d != null) {
                            Bitmap img = (Bitmap)d;
                            if (img != null)
                                imageButton.setImageBitmap(img);
                        }
                    }
                }
            });

    private View.OnClickListener imageButtonOnClickListener = v -> {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        mGetContent.launch(intent);
    };
}