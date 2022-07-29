package com.example.tictocteo.domain.usescase.impl

import com.example.tictocteo.domain.repository.RoomRepository
import com.example.tictocteo.domain.usescase.MenuUsesCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MenuUsesCaseImpl @Inject constructor(private val roomRepository: RoomRepository) :
    MenuUsesCase {
    override fun createRoom(roomName: String) = roomRepository.createLink(roomName)

    override fun searchRoom(roomName: String) = roomRepository.findRoom(roomName)

}