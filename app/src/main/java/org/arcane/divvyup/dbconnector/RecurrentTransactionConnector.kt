package org.arcane.divvyup.dbconnector

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.arcane.divvyup.data.identifiers.IIdentifier
import org.arcane.divvyup.data.model.RecurrentTransaction
import org.arcane.divvyup.data.model.Transaction
import javax.inject.Inject

class RecurrentTransactionConnector @Inject constructor() : Connector<RecurrentTransaction>  {

    override fun <T : IIdentifier> getItem(identifier: T): RecurrentTransaction? {
        val db = FirebaseFirestore.getInstance()
        val deferred = CompletableDeferred<RecurrentTransaction?>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val document = db.collection("recurrent_transactions").document(identifier.uid).get().await()
                val transaction = document.toObject(RecurrentTransaction::class.java)
                deferred.complete(transaction)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }

        return runBlocking { deferred.await() }
    }

    override fun getItems(): List<RecurrentTransaction> {
        val db = FirebaseFirestore.getInstance()
        val deferred = CompletableDeferred<List<RecurrentTransaction>>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val documents = db.collection("recurrent_transactions").get().await()
                val transaction = documents.map { document ->
                    document.toObject(RecurrentTransaction::class.java)
                }
                deferred.complete(transaction)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }

        return runBlocking { deferred.await() }
    }

    override fun addItem(item: RecurrentTransaction) {
        val db = FirebaseFirestore.getInstance()

        // Create a new transaction object
        val recurrenceInterval = RecurrentTransaction(
            interval = item.interval,
            startDate = item.startDate,
            endDate = item.endDate
        )

        // Add a new document with a generated ID
        db.collection("recurrent_transactions")
            .add(recurrenceInterval)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Expense added successfully")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding expense", e)
            }
    }

    override fun updateItem(item: RecurrentTransaction) {
        val db = FirebaseFirestore.getInstance()

        // Create a map of fields to update
        val updates = mutableMapOf<String, Any>()

        item.interval.let { updates["interval"] = it }
        item.startDate.let { updates["startDate"] = it }
        item.endDate?.let { updates["endDate"] = it }

        // Update the document
        db.collection("recurrent_transaction")
            .document(item.uid)
            .update(updates)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Recurrent Transaction updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Error updating recurrent transaction", e)
            }
    }

    override fun <T : IIdentifier> deleteItem(identifier: T) {
        val db = FirebaseFirestore.getInstance()

        // Delete the document with the specified ID
        db.collection("recurrent_transaction")
            .document(identifier.uid)
            .delete()
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Recurrent Transaction deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error deleting Recurrent Transaction", e)
            }
    }

    override fun searchItems(names: String): List<RecurrentTransaction> {
        TODO("Not yet implemented")
    }
}