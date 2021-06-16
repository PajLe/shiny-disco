package ui.MainActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.shinydisco.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import data.User;
import ui.HomeActivity.HomeActivity;
import utils.Firebase;

import static android.content.ContentValues.TAG;

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
        firebaseAuth = Firebase.getFirebaseAuth();
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
        signUpButton.setOnClickListener(signupButtonOnClickListener);
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
                && !nameInput.isEmpty()
                && passwordInput.length() > 6);
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
                            imageButton.setImageBitmap(img);
                        }
                    }
                }
            });

    private View.OnClickListener imageButtonOnClickListener = v -> {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        mGetContent.launch(intent);
    };

    private View.OnClickListener signupButtonOnClickListener = v -> {
        signUpButton.setEnabled(false);

        String username = usernameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();

        Bitmap image = ((BitmapDrawable)imageButton.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String uid = user.getUid();

                        User dbUser = new User();
                        dbUser.setUid(uid);
                        dbUser.setEmail(email);
                        dbUser.setName(name);
                        dbUser.setUsername(username);
                        dbUser.setPassword(password);

                        Firebase.getStorageRef().child(Firebase.STORAGE_USER_PHOTOS).child(username + "_" + new Date() + ".jpg").putBytes(imageBytes)
                                .addOnFailureListener(e ->  {
                                    Toast.makeText(getActivity(), "Couldn't upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    signUpButton.setEnabled(true);
                                })
                                .addOnSuccessListener(ts -> {
                                    ts.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(dUrl -> {
                                        dbUser.setImageUrl(dUrl.toString());
                                        Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).setValue(dbUser).addOnSuccessListener(unused -> {
                                            updateUI(user);
                                        });
                                    });
                                });

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        signUpButton.setEnabled(true);
                        Toast.makeText(getActivity(), "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    };

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            getActivity().finish();
            startActivity(intent);
        }
    }
}