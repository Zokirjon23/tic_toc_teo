package com.example.tictocteo.domain.repository.impl

import android.util.Log
import android.widget.Toast
import com.example.tictocteo.data.model.JoinType
import com.example.tictocteo.domain.repository.RoomRepository
import com.example.tictocteo.ui.MenuFragmentDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(private val mainRef: DatabaseReference) :
    RoomRepository {

    override fun findRoom(roomName: String): Flow<Boolean> {
        return callbackFlow {
            mainRef.orderByChild("roomName").equalTo(roomName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        trySend(snapshot.exists())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("LLL", "onCancelled: ${error.message}")
                    }
                })
            awaitClose()
        }
    }

    override fun createLink(roomName: String): Flow<Boolean?> {
        return callbackFlow {
            mainRef.orderByChild("roomName").equalTo(roomName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            val values: MutableMap<String, Any> = HashMap()
                            values["roomName"] = roomName

                            mainRef.child(roomName.trim()).setValue(values).addOnSuccessListener {
                                trySend(true)
                            }.addOnFailureListener {
                                trySend(null)
                            }
                        } else {
                            trySend(false)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("LLL", "onCancelled: ${error.message}")
                        trySend(null)
                    }
                })
            awaitClose()
        }
    }
}