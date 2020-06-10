package ru.maxim.mospolytech.polydroid.ui.activity.notifications

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_notifications.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.model.Notification

class NotificationsActivity : MvpAppCompatActivity(), NotificationsView {

    @InjectPresenter
    lateinit var notificationsPresenter: NotificationsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        title = getString(R.string.notifications)
        notificationsRefreshLayout.setOnRefreshListener {
            notificationsPresenter.loadNotifications()
        }
    }

    override fun onNotificationsLoaded(notifications: List<Notification>) {
        notificationsRefreshLayout.isRefreshing = false
        notificationsProgressBar.visibility = View.GONE
        notificationsRecycler.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = NotificationsRecyclerAdapter(notifications)
        }
    }

    override fun onNotificationsLoadError() {
        notificationsRefreshLayout.isRefreshing = false
        notificationsProgressBar.visibility = View.GONE
        Toast.makeText(this, "Error occurred while loading notifications", Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        if (!notificationsRefreshLayout.isRefreshing)
            notificationsProgressBar.visibility = View.VISIBLE
    }
}