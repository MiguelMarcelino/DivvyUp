package org.arcane.divvyup.dbconnector

import org.arcane.divvyup.data.model.Transaction
import org.arcane.divvyup.data.identifiers.GroupIdentifier
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

object GroupExpenseConnector {

    fun getExpenseForGroup(groupIdentifier: GroupIdentifier): List<Transaction> {
        // Get all expenses for a group
        val db = FirebaseFirestore.getInstance()
        val deferred = CompletableDeferred<List<Transaction>>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val documents = db.collection("transactions")
                    .whereEqualTo("groupId", groupIdentifier.uid)
                    .get()
                    .await()
                val expens = documents.map { document ->
                    document.toObject(Transaction::class.java)
                }
                deferred.complete(expens)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }

        return runBlocking { deferred.await() }
    }
}