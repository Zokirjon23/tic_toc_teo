package com.example.tictocteo.domain.usescase.impl

import com.example.tictocteo.data.model.ButtonData
import com.example.tictocteo.data.model.JoinType
import com.example.tictocteo.domain.repository.GameRepository
import com.example.tictocteo.domain.usescase.GameUsesCase
import javax.inject.Inject

class GameUsesCaseImpl @Inject constructor(private val gameRepository: GameRepository) :
    GameUsesCase {
    override val String.isWin: Boolean
        get() = this == "XXX" || this == "OOO"

    override fun getHostImage(roomName: String) = gameRepository.hostImage(roomName)

    override fun getVisitorImage(roomName: String) = gameRepository.visitorImage(roomName)

    override fun sendDataToServer(roomName: String, buttonData: ButtonData) =
        gameRepository.sendButtonData(roomName, buttonData)

    override fun insertedAndRemovedData(roomName: String) =
        gameRepository.gameEvent(roomName)

    override fun checkWin(list: List<String>, pos: Int): Boolean {
        list.apply {
            when (pos) {
                0 -> {
                    if ((get(0) + get(1) + get(2)).isWin || (get(0) + get(3) + get(6)).isWin || (get(
                            0
                        ) + get(4) + get(8)).isWin
                    )
                        return true
                }
                1 -> {
                    if ((get(0) + get(1) + get(2)).isWin || (get(1) + get(4) + get(7)).isWin)
                        return true
                }
                2 -> {
                    if ((get(0) + get(1) + get(2)).isWin || (get(2) + get(5) + get(8)).isWin || (get(
                            2
                        ) + get(
                            4
                        ) + get(6)).isWin
                    )
                        return true
                }
                3 -> {
                    if ((get(3) + get(4) + get(5)).isWin || (get(0) + get(3) + get(6)).isWin)
                        return true
                }
                4 -> {
                    if ((get(3) + get(4) + get(5)).isWin || (get(1) + get(4) + get(7)).isWin || (get(
                            6
                        ) + get(2) + get(4)).isWin || (get(
                            0
                        ) + get(4) + get(8)).isWin
                    )
                        return true
                }
                5 -> {
                    if ((get(2) + get(5) + get(8)).isWin || (get(3) + get(4) + get(5)).isWin)
                        return true
                }
                6 -> {
                    if ((get(6) + get(7) + get(8)).isWin || (get(0) + get(3) + get(6)).isWin || (get(
                            6
                        ) + get(2) + get(4)).isWin
                    )
                        return true
                }
                7 -> {
                    if ((get(6) + get(7) + get(8)).isWin || (get(1) + get(4) + get(7)).isWin)
                        return true
                }
                8 -> {
                    if ((get(2) + get(5) + get(8)).isWin || (get(6) + get(7) + get(8)).isWin || (get(
                            0
                        ) + get(4) + get(8)).isWin
                    )
                        return true
                }

                else -> {
                    return false
                }
            }
        }
        return false
    }

    override fun checkDraw(list: List<String>, pos: Int): Boolean {
        var k = 0
        for (i in list) if (i != "") k++
        return k == list.size
    }


    override fun restartGame(roomName: String) = gameRepository.removeGameData(roomName)

    override fun sendChooseEmoji(host: Int, visitor: Int, roomName: String) =
        gameRepository.sendImages(host, visitor, roomName)

    override fun removeLink(roomName: String) =
        gameRepository.removeLink(roomName)
}