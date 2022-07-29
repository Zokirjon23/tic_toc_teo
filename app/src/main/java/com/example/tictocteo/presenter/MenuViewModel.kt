package com.example.tictocteo.presenter

import kotlinx.coroutines.flow.SharedFlow

interface MenuViewModel {
    val showJoinDialog: SharedFlow<Unit>
    val exitGame: SharedFlow<Unit>
    val showCreateDialog: SharedFlow<Unit>
    val navToGameAsVisitor: SharedFlow<String>
    val navToGameAsHost: SharedFlow<String>
    val error: SharedFlow<String>
    val successCreatedRoom: SharedFlow<Unit>
    val dismissJoinDialog: SharedFlow<Unit>
    val dismissCreateDialog: SharedFlow<Unit>


    fun playClicked()
    fun exitClicked()
    fun dialogOkClicked(name: String)
    fun dialogCreateClicked(name: String,hasInternet: Boolean)
    fun dialogPlayClicked(roomName: String, hasInternet : Boolean)
    fun createClicked()
}