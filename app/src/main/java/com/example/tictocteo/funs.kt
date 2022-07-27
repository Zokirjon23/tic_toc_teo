package com.example.tictocteo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.ImageView

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

 val String.isWin: Boolean
    get() = this == "XXX" || this == "OOO"


 fun List<String>.checkWin(pos: Int): String? {

    when (pos) {
        0 -> {
            if ((get(0) + get(1) + get(2)).isWin || (get(0) + get(3) + get(6)).isWin || (get(
                    0
                ) + get(4) + get(8)).isWin
            )
                return get(pos)
        }
        1 -> {
            if ((get(0) + get(1) + get(2)).isWin || (get(1) + get(4) + get(7)).isWin)
                return get(pos)
        }
        2 -> {
            if ((get(0) + get(1) + get(2)).isWin || (get(2) + get(5) + get(8)).isWin || (get(2) + get(
                    4
                ) + get(6)).isWin
            )
                return get(pos)
        }
        3 -> {
            if ((get(3) + get(4) + get(5)).isWin || (get(0) + get(3) + get(6)).isWin)
                return get(pos)
        }
        4 -> {
            if ((get(3) + get(4) + get(5)).isWin || (get(1) + get(4) + get(7)).isWin || (get(
                    6
                ) + get(2) + get(4)).isWin || (get(
                    0
                ) + get(4) + get(8)).isWin
            )
                return get(pos)
        }
        5 -> {
            if ((get(2) + get(5) + get(8)).isWin || (get(3) + get(4) + get(5)).isWin)
                return get(pos)
        }
        6 -> {
            if ((get(6) + get(7) + get(8)).isWin || (get(0) + get(3) + get(6)).isWin || (get(
                    6
                ) + get(2) + get(4)).isWin
            )
                return get(pos)
        }
        7 -> {
            if ((get(6) + get(7) + get(8)).isWin || (get(1) + get(4) + get(7)).isWin)
                return get(pos)
        }
        8 -> {
            if ((get(2) + get(5) + get(8)).isWin || (get(6) + get(7) + get(8)).isWin || (get(
                    0
                ) + get(4) + get(8)).isWin
            )
                return get(pos)
        }

        else -> {
            return null
        }
    }
    return null
}

fun List<ImageView>.isDraw(): Boolean {
    var k = 0
    for (i in this) {
        if (!i.isClickable) {
            k++
        }
    }
    return k == size
}

