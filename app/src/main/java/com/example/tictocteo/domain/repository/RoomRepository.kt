package com.example.tictocteo.domain.repository

import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    fun createLink(roomName: String): Flow<Boolean>
    fun findRoom(roomName: String): Flow<Boolean>
}