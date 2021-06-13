package utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {
    // realtime db
    public static final String DB_USERS = "users"; // "tables"
    public static final String DB_DISCOS = "discos"; // "tables"

    private static DatabaseReference dbRef = FirebaseDatabase.getInstance("https://shiny-disco-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    public static DatabaseReference getDbRef() {
        return dbRef;
    }

    // storage
    public static final String STORAGE_USER_PHOTOS = "users/photos";

    private static StorageReference storageRef = FirebaseStorage.getInstance("gs://shiny-disco.appspot.com").getReference();

    public static StorageReference getStorageRef() {
        return storageRef;
    }

    // auth
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}
