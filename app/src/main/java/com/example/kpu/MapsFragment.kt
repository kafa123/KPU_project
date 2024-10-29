package com.example.kpu

import androidx.fragment.app.DialogFragment
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : DialogFragment(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    private var currentLatLng: LatLng? = null
    private var locationListener: LocationListener? = null

    // Define the launcher for requesting location permissions
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[ACCESS_FINE_LOCATION] == true ||
                permissions[ACCESS_COARSE_LOCATION] == true) {
                // Permission granted
                getCurrentLocation()
            } else {
                // Permission denied
                // Handle what to do if the user denies the permission
            }
        }

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        getCurrentLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        // Initialize SupportMapFragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

            // Launch permission request using the new API
            requestPermissionLauncher.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                )
            )

            return
        }

        googleMap.isMyLocationEnabled = true

        // Get the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLatLng = LatLng(it.latitude, it.longitude)
                googleMap.addMarker(MarkerOptions().position(currentLatLng!!).title("Posisimu"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 15f))
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        currentLatLng?.let {
            Log.e("p", "onDismiss: ", )
            locationListener?.onLocationSelected(it)
        }
    }

    interface LocationListener {
        fun onLocationSelected(latLng: LatLng)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationListener = context as? LocationListener
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        getCurrentLocation()
    }


}
