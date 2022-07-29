package com.example.tictocteo.presenter.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tictocteo.domain.usescase.MenuUsesCase
import com.example.tictocteo.presenter.MenuViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModelImpl @Inject constructor(private val menuUsesCase: MenuUsesCase) : ViewModel(),
    MenuViewModel {
    override val showJoinDialog = MutableSharedFlow<Unit>()
    override val exitGame = MutableSharedFlow<Unit>()
    override val showCreateDialog = MutableSharedFlow<Unit>()
    override val navToGameAsVisitor = MutableSharedFlow<String>()
    override val navToGameAsHost = MutableSharedFlow<String>()
    override val error = MutableSharedFlow<String>()
    override val successCreatedRoom = MutableSharedFlow<Unit>()
    override val dismissJoinDialog = MutableSharedFlow<Unit>()
    override val dismissCreateDialog = MutableSharedFlow<Unit>()


    override fun playClicked() {
        viewModelScope.launch { showJoinDialog.emit(Unit) }
    }

    override fun exitClicked() {
        viewModelScope.launch { exitGame.emit(Unit) }
    }

    override fun createClicked() {
        viewModelScope.launch { showCreateDialog.emit(Unit) }
    }

    override fun dialogPlayClicked(roomName: String, hasInternet: Boolean) {
        if (!hasInternet) {
            viewModelScope.launch { error.emit("No Internet") }
            return
        } else if (roomName.isEmpty()) {
            viewModelScope.launch { error.emit("Enter name") }
        } else {
            menuUsesCase.searchRoom(roomName).onEach {
                if (it){
                    dismissJoinDialog.emit(Unit)
                    navToGameAsVisitor.emit(roomName)
                }
                else error.emit("Error link")
            }.launchIn(viewModelScope)
        }
    }

    override fun dialogOkClicked(name: String) {
        viewModelScope.launch { dismissCreateDialog.emit(Unit) }
        viewModelScope.launch { navToGameAsHost.emit(name) }
    }

    override fun dialogCreateClicked(name: String, hasInternet: Boolean) {
        if (!hasInternet) {
            viewModelScope.launch { error.emit("No Internet") }
            return
        } else if (name.isEmpty()) {
            viewModelScope.launch { error.emit("Enter name") }
        } else {
            menuUsesCase.createRoom(name).onEach {
                if (it) successCreatedRoom.emit(Unit)
                else error.emit("No Internet")
            }.launchIn(viewModelScope)
        }
    }
}