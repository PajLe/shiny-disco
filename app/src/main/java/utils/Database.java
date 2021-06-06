package utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    public static final String USERS = "users"; // "tables"

    private static DatabaseReference dbRef = FirebaseDatabase.getInstance("https://shiny-disco-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    public DatabaseReference getDbRef() {
        return  dbRef;
    }
}
