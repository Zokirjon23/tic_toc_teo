package com.example.tictocteo.domain.usescase

import com.example.tictocteo.data.model.ButtonData
import com.example.tictocteo.data.model.JoinType
import com.example.tictocteo.data.model.Response
import kotlinx.coroutines.flow.Flow
import java.util.ArrayList

interface GameUsesCase {
    fun getHostImage(roomName: String): Flow<Int>
    fun getVisitorImage(roomName: String): Flow<Int>
    fun sendDataToServer(roomName: String, buttonData: ButtonData): Flow<Boolean>
    fun insertedAndRemovedData(roomName: String): Flow<Response>
    fun checkWin(list: List<String>, pos: Int): Boolean
    fun checkDraw(list: List<String>, pos: Int): Boolean
    fun restartGame(roomName: String)
    fun sendChooseEmoji(host: Int, visitor: Int, roomName: String)
    fun removeLink(roomName: String)

    val String.isWin : Boolean
}