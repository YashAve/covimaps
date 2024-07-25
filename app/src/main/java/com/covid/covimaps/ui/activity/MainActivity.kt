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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.covid.covimaps.R
import com.covid.covimaps.data.repository.local.DataStoreManager
import com.covid.covimaps.data.repository.local.totalDeaths
import com.covid.covimaps.ui.component.composable.LoginScreen
import com.covid.covimaps.ui.component.composable.MapsModalContent
import com.covid.covimaps.ui.theme.CoviMapsTheme
import com.covid.covimaps.viewmodel.MainViewModel
import com.covid.covimaps.viewmodel.OnDataReadyCallback
import com.covid.covimaps.viewmodel.getLocationsCount
import com.covid.covimaps.viewmodel.insertDatabase
import com.covid.covimaps.viewmodel.retrieveLocations
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
            Log.d(TAG, "onCreate: dataStore: Main() is being called ")
            Main(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Maps(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val context = LocalContext.current
    var isLoading by rememberSaveable { mutableStateOf(true) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.1205395, 93.7841503), 3f)
    }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by rememberSaveable { mutableStateOf(false) }

    var drawable by rememberSaveable { mutableIntStateOf(R.drawable.red_covid_icon) }
    var total by rememberSaveable { mutableIntStateOf(0) }
    var average by rememberSaveable { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            val callback = object : OnDataReadyCallback {
                override fun onDataReady(status: String) {
                    Log.d(TAG, "onDataReady: getCovidGeocode status $status")
                }
            }

            LaunchedEffect(Unit) {
                if (getLocationsCount(context) > 0) {
                    viewModel.covidLocations = retrieveLocations(context).toMutableList()
                    Log.d(
                        TAG,
                        "Maps: getCovidGeocode database is full ${viewModel.covidLocations.size}"
                    )
                    viewModel.covidLocations.forEach {
                        Log.d(
                            TAG,
                            "Maps: getCovidGeocode database $it"
                        )
                    }
                } else {
                    viewModel.getCovidDataUiState()
                    viewModel.getLocations(callback)
                    insertDatabase(viewModel.covidLocations.toTypedArray(), context)
                    Log.d(TAG, "Maps: getCovidGeocode locations are loaded")
                    Log.d(TAG, "Maps: getCovidGeocode isLoading is $isLoading")
                }
                total = viewModel.covidLocations.totalDeaths()
                average = total / viewModel.covidLocations.size
                isLoading = false
            }

            if (!isLoading) {
                Log.d(
                    TAG,
                    "Maps: getCovidGeocode coordinates size ${viewModel.coordinates.size}"
                )
                viewModel.covidLocations.shuffle()
                viewModel.covidLocations.forEach { covidLocation ->
                    drawable =
                        if (covidLocation.totalDeceased == average) R.drawable.green_covid_icon else if (covidLocation.totalDeceased < average) R.drawable.yellow_covid_icon else R.drawable.red_covid_icon
                    Marker(
                        state = MarkerState(
                            LatLng(
                                covidLocation.latitude,
                                covidLocation.longitude
                            )
                        ),
                        title = "${covidLocation.district}, ${covidLocation.state}",
                        icon = BitmapDescriptorFactory.fromResource(drawable),
                        onInfoWindowClick = {
                            showSheet = true
                            viewModel.currentCovidLocation = covidLocation
                        }
                    )
                }
            }
        }

        DisappearingScaleBar(
            modifier = Modifier
                .padding(top = 5.dp, end = 15.dp)
                .align(Alignment.TopStart),
            cameraPositionState = cameraPositionState
        )

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                MapsModalContent(covidLocation = viewModel.currentCovidLocation)
            }
        }
    }
}

@Composable
fun Main(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStoreManager = DataStoreManager(context = context)

    dataStoreManager.isAgreedToDisclaimer()
    var agree by rememberSaveable {
        mutableStateOf(
            dataStoreManager.isAgree
        )
    }

    Log.d(TAG, "Main: isAgree = ${dataStoreManager.isAgree}")

    CoviMapsTheme {
        Scaffold { innerPadding ->
            LoginScreen(modifier = Modifier.padding(innerPadding))
            /*if (agree) {
                Maps(modifier = modifier.padding(innerPadding), viewModel = viewModel)
            } else {
                DisclaimerDialog(
                    modifier = modifier.padding(innerPadding),
                    onAgree = {
                        scope.launch {
                            dataStoreManager.agreeToDisclaimer()
                            agree = dataStoreManager.isAgree
                            Log.d(TAG, "Main: dataStore isAgree = ${dataStoreManager.isAgree} after onAgree is clicked")
                        }
                    }
                )
            }*/
        }
    }
}