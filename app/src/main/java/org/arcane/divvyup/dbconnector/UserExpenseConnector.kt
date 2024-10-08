package org.arcane.divvyup.dbconnector

import android.content.ContentValues.TAG
import android.util.Log
import org.arcane.divvyup.data.identifiers.UserIdentifier
import com.google.firebase.firestore.FirebaseFirestore

object UserExpenseConnector {

    fun getExpenseForUser(userIdentifier: UserIdentifier) {
        // Get all expenses for a user
        val db = FirebaseFirestore.getInstance()

        db.collection("transactions")
            .whereEqualTo("userId", userIdentifier.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
    }

}