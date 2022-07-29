package com.example.tictocteo.data.model

sealed class Response(buttonData: ButtonData?, event: Unit?) {
    data class OnChildAdded(val buttonData: ButtonData?) : Response(buttonData, null)
    data class OnRemoved(val event: Unit) : Response(null, event)
}