package com.allybros.superego.activity.userpage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.allybros.superego.unit.State
import com.allybros.superego.unit.User
import com.allybros.superego.util.SessionManager

class UserPageVM(application: Application) : AndroidViewModel(application) {

    val currentUser: User = SessionManager.getInstance().user


    val state: State
        get() = when {
        currentUser.scores.size >= 6 -> State.COMPLETE
        currentUser.scores.size >= 1 -> State.PARTIAL
        else -> State.NONE
    }

}