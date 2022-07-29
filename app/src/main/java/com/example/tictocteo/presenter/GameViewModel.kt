package com.example.tictocteo.presenter

import com.example.tictocteo.data.model.ButtonData
import com.example.tictocteo.data.model.JoinType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface GameViewModel {

    val init: MutableStateFlow<Unit>
    val hostImage: StateFlow<Int>
    val visitorImage: StateFlow<Int>
    val showChooseDialog: SharedFlow<Unit>
    val restart: SharedFlow<Unit>
    val loadGameImage: SharedFlow<ButtonData>
    var tagList: ArrayList<String>
    val win: StateFlow<Int>
    val lose: StateFlow<Int>
    val draw: StateFlow<Int>

    fun onCreated(type: JoinType, roomName: String)
    fun gameButtonClicked(roomName: String, buttonData: ButtonData)
    fun onRestartClicked(roomName: String)
    fun onGameStarted(roomName: String, list: List<String>, me: String)
    fun onEmojiChoose(pos : Int, roomName: String)
    fun onDestroy(roomName: String,type: JoinType)
}