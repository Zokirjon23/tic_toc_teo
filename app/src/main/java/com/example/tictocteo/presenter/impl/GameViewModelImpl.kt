package com.example.tictocteo.presenter.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tictocteo.data.model.ButtonData
import com.example.tictocteo.data.model.JoinType
import com.example.tictocteo.data.model.Response
import com.example.tictocteo.domain.usescase.GameUsesCase
import com.example.tictocteo.presenter.GameViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModelImpl @Inject constructor(private val usesCase: GameUsesCase) : ViewModel(),
    GameViewModel {
    override val init = MutableStateFlow(Unit)
    override val hostImage = MutableStateFlow(0)
    override val visitorImage = MutableStateFlow(1)
    override val isShowChooseDialog = MutableStateFlow(false)
    override var tagList = ArrayList<String>()
    override val win = MutableStateFlow(0)
    override val lose = MutableStateFlow(0)
    override val draw = MutableStateFlow(0)
    override val restart = MutableSharedFlow<Unit>()
    override val loadGameImage = MutableSharedFlow<ButtonData>()


    override fun onCreated(type: JoinType, roomName: String) {
        if (type == JoinType.VISITOR) {
            usesCase.getHostImage(roomName).onEach {
                hostImage.value = it
            }.launchIn(viewModelScope)

            usesCase.getVisitorImage(roomName).onEach {
                visitorImage.value = it
            }.launchIn(viewModelScope)
        } else {
                isShowChooseDialog.value = true
        }
    }

    override fun onGameStarted(roomName: String, list: List<String>, me: String) {
        tagList.clear()
        tagList.addAll(list)
        usesCase.insertedAndRemovedData(roomName).onEach {
            if (it is Response.OnChildAdded) {
                loadGameImage.emit(it.buttonData!!)
                val data = it.buttonData
                tagList[it.buttonData.pos] = data.text
                if (usesCase.checkWin(tagList, data.pos)) {
                    if (data.text == me) {
                        win.value = win.value + 1
                    } else {
                        lose.value = lose.value + 1
                    }
                } else if (usesCase.checkDraw(tagList, data.pos)) {
                    draw.value = draw.value + 1
                }
            } else {
                restart.emit(Unit)
                for (i in tagList.indices) tagList[i] = ""
            }
        }.launchIn(viewModelScope)
    }

    override fun onRestartClicked(roomName: String) {
        usesCase.restartGame(roomName)
    }

    override fun gameButtonClicked(roomName: String, buttonData: ButtonData) {
        Log.d("LLL", "gameButtonClicked: ${buttonData.pos}")
        usesCase.sendDataToServer(roomName, buttonData).onEach {
            Log.d("LLL", "gameButtonClicked: isSend: $it")
        }.launchIn(viewModelScope)
    }

    override fun onEmojiChoose(pos: Int, roomName: String) {
        isShowChooseDialog.value = false
        if (pos % 2 == 0) {
            hostImage.value = pos
            visitorImage.value = pos + 1
            usesCase.sendChooseEmoji(pos, pos + 1, roomName)
        } else {
            hostImage.value = pos - 1
            visitorImage.value = pos
            usesCase.sendChooseEmoji(pos - 1, pos, roomName)
        }
    }

    override fun onDestroy(roomName: String, type: JoinType) {
        if (type == JoinType.HOST)
        usesCase.removeLink(roomName)
    }
}
