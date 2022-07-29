package com.example.tictocteo.domain.repository

import com.example.tictocteo.data.model.ButtonData
import com.example.tictocteo.data.model.JoinType
import com.example.tictocteo.data.model.Response
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun hostImage(roomName: String): Flow<Int>
    fun visitorImage(roomName: String): Flow<Int>
    fun sendButtonData(roomName: String, buttonData: ButtonData): Flow<Boolean>
    fun gameEvent(roomName: String): Flow<Response>
    fun removeGameData(roomName: String)
    fun sendImages(host: Int, visitor: Int, roomName: String)
    fun removeLink(roomName: String)

}
