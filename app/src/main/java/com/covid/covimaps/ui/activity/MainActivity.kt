package com.covid.covimaps.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.covid.covimaps.data.model.local.room.CovidLocation
import com.covid.covimaps.data.model.remote.CovidDataUiState
import com.covid.covimaps.ui.theme.CoviMapsTheme
import com.covid.covimaps.viewmodel.MainViewModel
import com.covid.covimaps.viewmodel.insertDatabase
import com.covid.covimaps.viewmodel.retrieveLocations
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.DisappearingScaleBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var covidDataUiStates: List<CovidDataUiState>
    private lateinit var locations: List<CovidLocation?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val coordinates: MutableList<LatLng> = mutableListOf()
        lifecycleScope.launch {
            covidDataUiStates = viewModel.getCovidDataUiState()
            Log.d(TAG, "onCreate: ${covidDataUiStates.size}")
            locations = viewModel.getLocations()
            locations.forEach {
                it?.let { insertDatabase(it, this@MainActivity) }
            }
            val covidLocations = retrieveLocations(this@MainActivity)
            Log.d(TAG, "onCreate: covidLocation's size ${covidLocations.size}")
            locations.forEach {
                    location -> location?.let { coordinates.add(LatLng(it.latitude, it.longitude)) }
            }
        }

        setContent {
            Maps(coordinates = coordinates)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Maps(modifier: Modifier = Modifier, coordinates: List<LatLng> = listOf(LatLng(1.35, 103.87))) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(coordinates[0], 10f)
    }
    CoviMapsTheme {
        Scaffold { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                GoogleMap(
                    modifier = modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    coordinates.forEach { Marker(state = MarkerState(it)) }
                }
                DisappearingScaleBar(
                    modifier = Modifier
                        .padding(top = 5.dp, end = 15.dp)
                        .align(Alignment.TopStart),
                    cameraPositionState = cameraPositionState
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapsPreview() {
    Maps()
}