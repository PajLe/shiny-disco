package utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {
    // realtime db
    public static final String USERS = "users"; // "tables"

    private static DatabaseReference dbRef = FirebaseDatabase.getInstance("https://shiny-disco-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    public static DatabaseReference getDbRef() {
        return dbRef;
    }

    // storage
    private static StorageReference storageRef = FirebaseStorage.getInstance("gs://shiny-disco.appspot.com").getReference();

    public static StorageReference getStorageRef() {
        return storageRef;
    }
}
