package com.covid.covimaps.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.covid.covimaps.R
import com.covid.covimaps.ui.theme.CoviMapsTheme
import com.covid.covimaps.viewmodel.MainViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val TAG = "CovidMapFragment"
class CovidMapFragment : Fragment() {

   private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CoviMapsTheme {
                    Main(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun Main(modifier: Modifier = Modifier, viewModel: MainViewModel? = null) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.1205395, 93.7841503), 3f)
    }
    var drawable by rememberSaveable { mutableIntStateOf(R.drawable.red_covid_icon) }
    var total by rememberSaveable { mutableIntStateOf(0) }
    var average by rememberSaveable { mutableIntStateOf(0) }

    Surface(color = MaterialTheme.colorScheme.primary) {
        Scaffold { scaffold ->
            Box(modifier = Modifier.padding(scaffold)) {
                GoogleMap(
                    modifier = modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    if (!viewModel?.loading!!) {
                        Log.d(TAG, "covid locations size: ${viewModel.covidLocations.size}")
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
                                    viewModel.currentCovidLocation = covidLocation
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainPreview() {
    Main()
}