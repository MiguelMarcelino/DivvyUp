package org.arcane.divvyup.dbconnector

import android.content.ContentValues.TAG
import android.util.Log
import org.arcane.divvyup.data.model.Transaction
import org.arcane.divvyup.data.identifiers.IIdentifier
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionConnector @Inject constructor() : Connector<Transaction>  {

    override fun <T : IIdentifier> getItem(identifier: T): Transaction? {
        val db = FirebaseFirestore.getInstance()
        val deferred = CompletableDeferred<Transaction?>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val document = db.collection("transactions").document(identifier.uid).get().await()
                val transaction = document.toObject(Transaction::class.java)
                deferred.complete(transaction)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }

        return runBlocking { deferred.await() }
    }

    override fun getItems(): List<Transaction> {
        val db = FirebaseFirestore.getInstance()
        val deferred = CompletableDeferred<List<Transaction>>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val documents = db.collection("transactions").get().await()
                val transaction = documents.map { document ->
                    document.toObject(Transaction::class.java)
                }
                deferred.complete(transaction)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }

        return runBlocking { deferred.await() }
    }

    override fun addItem(item: Transaction) {
        val db = FirebaseFirestore.getInstance()

        // Create a new transaction object
        val transaction = Transaction(
            title = item.title,
            amount = item.amount,
            type = item.type,
            description = item.description,
            currency = item.currency,
            status = item.status,
            tags = item.tags,
            share = item.share,
            ownerUid = item.ownerUid,
            groupUid = item.groupUid,
            userUids = item.userUids,
        )

        // Add a new document with a generated ID
        db.collection("transactions")
            .add(transaction)
            .addOnSuccessListener {
                Log.d(TAG, "transaction added successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding transaction", e)
            }
    }

    override fun updateItem(item: Transaction) {
        val db = FirebaseFirestore.getInstance()

        // Create a map of fields to update
        val updates = mutableMapOf<String, Any>()

        item.amount.let { updates["amount"] = it }
        item.description?.let { updates["description"] = it }
        item.currency.let { updates["currency"] = it }
        item.status.let { updates["status"] = it }
        item.tags.let { updates["tags"] = it }
        item.ownerUid.let { updates["ownerId"] = it }
        item.groupUid.let { updates["groupId"] = it }

        // Update the document
        db.collection("transaction")
            .document(item.uid)
            .update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "transaction updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating transaction", e)
            }
    }

    override fun <T : IIdentifier> deleteItem(identifier: T) {
        val db = FirebaseFirestore.getInstance()

        // Delete the document with the specified ID
        db.collection("transaction")
            .document(identifier.uid)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "transaction deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting transaction", e)
            }
    }

    override fun searchItems(names: String): List<Transaction> {
        TODO("Not yet implemented")
    }
}