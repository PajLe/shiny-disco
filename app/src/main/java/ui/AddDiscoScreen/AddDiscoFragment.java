package ui.AddDiscoScreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shinydisco.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import data.Disco;
import data.Rating;
import data.User;
import utils.Firebase;
import utils.MultiSpinner;

import static android.content.ContentValues.TAG;

public class AddDiscoFragment extends Fragment {

    private TextView latitudeValueTextView;
    private TextView longitudeValueTextView;
    private EditText nameEditText;
    private MultiSpinner musicSpinner;
    private Spinner pricingSpinner;
    private Spinner ratingSpinner;
    private ImageButton discoImageButton;
    private Button addDiscoButton;
    private LatLng currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentLocation = arguments.getParcelable("location");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_disco, container, false);;

        latitudeValueTextView = rootView.findViewById(R.id.disco_latitude_value_textview);
        longitudeValueTextView = rootView.findViewById(R.id.disco_longitude_value_textview);
        nameEditText = rootView.findViewById(R.id.disco_name_input);
        musicSpinner = rootView.findViewById(R.id.spinner_disco_music);
        pricingSpinner = rootView.findViewById(R.id.spinner_disco_price);
        ratingSpinner = rootView.findViewById(R.id.spinner_disco_rating);
        discoImageButton = rootView.findViewById(R.id.disco_image_button);
        addDiscoButton = rootView.findViewById(R.id.save_added_disco_button);

        musicSpinner.addOnItemsSelectedListener((selected, items) -> {
            String nameInput = nameEditText.getText().toString().trim();

            addDiscoButton.setEnabled(selected.contains(true) && !nameInput.isEmpty());
        });
        nameEditText.addTextChangedListener(addDiscoTextWatcher);
        addDiscoButton.setOnClickListener(addDiscoButtonOnClickListener);
        discoImageButton.setOnClickListener(imageButtonOnClickListener);

        if (currentLocation != null) {
            latitudeValueTextView.setText(currentLocation.latitude + "");
            longitudeValueTextView.setText(currentLocation.longitude + "");
        }

        return rootView;
    }

    private TextWatcher addDiscoTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nameInput = nameEditText.getText().toString().trim();
            List<String> musicInput = musicSpinner.getSelected();

            addDiscoButton.setEnabled(!nameInput.isEmpty()
                    && !musicInput.isEmpty());
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
                            discoImageButton.setImageBitmap(img);
                        }
                    }
                }
            });

    private View.OnClickListener imageButtonOnClickListener = v -> {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        mGetContent.launch(intent);
    };

    private View.OnClickListener addDiscoButtonOnClickListener = v -> {
        v.setEnabled(false);
        double latitude = Double.parseDouble(latitudeValueTextView.getText().toString());
        double longitude = Double.parseDouble(longitudeValueTextView.getText().toString());
        String name = nameEditText.getText().toString();
        List<String> selectedMusic = musicSpinner.getSelected();
        String pricing = pricingSpinner.getSelectedItem().toString();
        String ratingNumber = ratingSpinner.getSelectedItem().toString();

        Bitmap image = ((BitmapDrawable)discoImageButton.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        FirebaseUser loggedInUser = Firebase.getFirebaseAuth().getCurrentUser();
        if (loggedInUser != null) {
            String currentUserId = loggedInUser.getUid();

            Firebase.getDbRef().child(Firebase.DB_USERS).child(currentUserId).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting user data", task.getException());
                    v.setEnabled(true);
                } else {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        user.increaseRankPoints();

                        Rating rating = new Rating();
                        rating.setRatedById(currentUserId);
                        rating.setRating(Double.parseDouble(ratingNumber));

                        Disco disco = new Disco();
                        disco.setName(name);
                        disco.setLat(latitude);
                        disco.setLon(longitude);
                        disco.setPricing(pricing);
                        disco.setMusicGenres(selectedMusic);
                        disco.setRatings(Collections.singletonList(rating));
                        disco.setAverageRating(rating.getRating());

                        Firebase.getStorageRef().child(Firebase.STORAGE_DISCO_PHOTOS).child(name + "_" + new Date() + ".jpg").putBytes(imageBytes)
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Couldn't upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    v.setEnabled(true);
                                })
                                .addOnSuccessListener(ts -> {
                                    ts.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(dUrl -> {
                                        disco.setImageUrl(dUrl.toString());

                                        // finally add disco to db
                                        String key = Firebase.getDbRef().push().getKey();
                                        disco.setId(key);
                                        Firebase.getDbRef().child(Firebase.DB_DISCOS).child(key).setValue(disco);

                                        // update user's rank in db
                                        Firebase.getDbRef().child(Firebase.DB_USERS).child(currentUserId).setValue(user);
                                        updateUI();
                                    });
                                });
                    }
                }
            });
        }
    };

    private void updateUI() {
        Toast.makeText(getContext(), "Successfully added a disco", Toast.LENGTH_SHORT).show();
        getParentFragmentManager()
                .popBackStack();
//        getParentFragmentManager()
//                .popBackStack();
    }
}