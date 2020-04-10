package ru.maxim.mospolytech.polydroid.ui.activity.notifications

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import ru.maxim.mospolytech.polydroid.R

class NotificationsActivity : MvpAppCompatActivity(), NotificationsView {

    @InjectPresenter
    lateinit var notificationsPresenter: NotificationsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        title = getString(R.string.notifications)
    }
}