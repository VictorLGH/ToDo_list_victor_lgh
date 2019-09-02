package com.isep.simov.todo.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDBHelper {

    static FirebaseFirestore getDB() {
        return FirebaseFirestore.getInstance();
    }
}
