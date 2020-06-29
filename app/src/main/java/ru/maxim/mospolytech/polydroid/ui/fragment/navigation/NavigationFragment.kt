package ru.maxim.mospolytech.polydroid.ui.fragment.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ru.maxim.mospolytech.polydroid.R


class NavigationFragment : MvpAppCompatFragment(), NavigationView, OnMapReadyCallback {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.navigation)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val bs = LatLng(55.7812605, 37.709014)
        googleMap.addMarker(MarkerOptions().position(bs).title("Большая Семеновская, 38"))
        val az = LatLng(55.7048178, 37.6403933)
        googleMap.addMarker(MarkerOptions().position(az).title("Автозаводская, 16"))
        val pk = LatLng(55.819112, 37.6607553)
        googleMap.addMarker(MarkerOptions().position(pk).title("Павла Корчагина, 22"))
        val pr = LatLng(55.8340086, 37.5425184)
        googleMap.addMarker(MarkerOptions().position(pr).title("Прянишникова, 2А"))
        val mh = LatLng(55.8374804, 37.5300266)
        googleMap.addMarker(MarkerOptions().position(mh).title("Михалковская, 7"))
        val ss = LatLng(55.7712082, 37.6374729)
        googleMap.addMarker(MarkerOptions().position(ss).title("Садовая-Спасская, 6"))
        val lv = LatLng(55.7570266, 37.6934322)
        googleMap.addMarker(MarkerOptions().position(lv).title("Лефортовский вал, 26"))
        val db = LatLng(55.7200687, 37.6703579)
        googleMap.addMarker(MarkerOptions().position(db).title("1-я Дубровская, 16а."))

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(bs))
    }
}