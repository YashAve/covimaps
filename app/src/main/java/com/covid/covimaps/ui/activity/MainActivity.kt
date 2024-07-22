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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.covid.covimaps.ui.theme.CoviMapsTheme
import com.covid.covimaps.viewmodel.MainViewModel
import com.covid.covimaps.viewmodel.OnDataReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.DisappearingScaleBar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Maps(viewModel = viewModel)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Maps(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    var isLoading by rememberSaveable { mutableStateOf(true) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(1.35, 103.87), 10f)
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
                    val callback = object : OnDataReadyCallback {
                        override fun onDataReady(coordinates: List<LatLng>) {
                            Log.d(TAG, "onDataReady: getCovidGeocode coordinates size ${coordinates.size}")
                        }
                    }

                LaunchedEffect(Unit) {
                    viewModel.getLocations(callback)
                    Log.d(TAG, "Maps: getCovidGeocode locations are loaded")
                    Log.d(TAG, "Maps: getCovidGeocode isLoading is $isLoading")
                    isLoading = false
                }

                if (!isLoading) {
                    Log.d(TAG, "Maps: getCovidGeocode coordinates size ${viewModel.coordinates.size}")
                    viewModel.coordinates.forEach {
                        Log.d(TAG, "Maps: getCovidGeocode $it")
                        Marker(state = MarkerState(it))
                    }
                }
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

}