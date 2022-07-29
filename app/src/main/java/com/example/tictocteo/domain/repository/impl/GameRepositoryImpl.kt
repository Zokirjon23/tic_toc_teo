package com.example.tictocteo.domain.repository.impl

import android.util.Log
import com.example.tictocteo.data.model.ButtonData
import com.example.tictocteo.data.model.JoinType
import com.example.tictocteo.data.model.Response
import com.example.tictocteo.domain.repository.GameRepository
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(private val mainRef: DatabaseReference) :
    GameRepository {

    override fun hostImage(roomName: String): Flow<Int> {
        return callbackFlow {
            mainRef.child("${roomName}/emoji/host")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            trySend(snapshot.getValue(Int::class.java)!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            awaitClose()
        }.flowOn(Dispatchers.IO)
    }

    override fun visitorImage(roomName: String): Flow<Int> {
        return callbackFlow {
            mainRef.child("${roomName}/emoji/visitor")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists())
                            trySend(snapshot.getValue(Int::class.java)!!)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            awaitClose()
        }.flowOn(Dispatchers.IO)
    }

    override fun sendButtonData(roomName: String, buttonData: ButtonData): Flow<Boolean> {
        return callbackFlow {
            mainRef.child("$roomName/game/${System.currentTimeMillis()}")
                .setValue(buttonData).addOnSuccessListener {
                    trySend(true)
                }.addOnFailureListener {
                    trySend(false)
                    Log.d("LLL", "sendButtonData: error: ${it.message}")
                }
            awaitClose()
        }
    }

    override fun gameEvent(roomName: String): Flow<Response> {
        return callbackFlow {
            mainRef.child("$roomName/game").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    trySend(Response.OnChildAdded(snapshot.getValue(ButtonData::class.java)))
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    trySend(Response.OnRemoved(Unit))
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
            awaitClose()
        }.flowOn(Dispatchers.IO)
    }

    override fun removeGameData(roomName: String) {
        mainRef.child("$roomName/game").setValue(null)
    }

    override fun sendImages(host: Int, visitor: Int, roomName: String) {
        val values: MutableMap<String, Any> = HashMap()
        values["host"] = host
        values["visitor"] = visitor
        mainRef.child("${roomName}/emoji").setValue(values)
    }

    override fun removeLink(roomName: String) {
        mainRef.child(roomName).removeValue()
    }
}
