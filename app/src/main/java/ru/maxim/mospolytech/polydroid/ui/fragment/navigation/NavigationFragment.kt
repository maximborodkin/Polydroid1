package ru.maxim.mospolytech.polydroid.ui.fragment.navigation

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.MvpAppCompatFragment
import ru.maxim.mospolytech.polydroid.R

class NavigationFragment : MvpAppCompatFragment(), NavigationView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.navigation)
    }
}