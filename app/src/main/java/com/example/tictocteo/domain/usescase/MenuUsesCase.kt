package com.example.tictocteo.domain.usescase

import kotlinx.coroutines.flow.Flow

interface MenuUsesCase {
    fun createRoom(roomName: String) : Flow<Boolean?>
    fun searchRoom(roomName: String) : Flow<Boolean>
}