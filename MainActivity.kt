package com.worldmap.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : ComponentActivity() {

    private lateinit var mapView: MapView

    private val locationPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                showMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            this,
            getSharedPreferences("world_map", MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue = packageName

        mapView = MapView(this).apply {

            setTileSource(TileSourceFactory.MAPNIK)

            setMultiTouchControls(true)

            controller.setZoom(4.5)

            // World view
            controller.setCenter(
                GeoPoint(20.0, 0.0)
            )
        }

        setContent {

            MaterialTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    AndroidView(
                        factory = {
                            mapView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun showMyLocation() {

        val locationOverlay =
            MyLocationNewOverlay(
                GpsMyLocationProvider(this),
                mapView
            )

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        mapView.overlays.add(locationOverlay)

        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
