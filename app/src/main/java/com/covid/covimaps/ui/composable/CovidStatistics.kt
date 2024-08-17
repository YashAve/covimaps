package com.covid.covimaps.ui.composable

import android.app.Activity
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.covid.covimaps.R
import com.covid.covimaps.data.model.room.CovidLocation
import com.covid.covimaps.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.theme.GoogleFonts
import com.covid.covimaps.util.hideSoftKeyBoard
import com.covid.covimaps.viewmodel.MainViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

private const val TAG = "CovidStatistics"

private var covidLocations: Map<Int, CovidLocation> = mutableMapOf()
private val covid: CovidLocation = CovidLocation(
    state = "Andaman and Nicobar Islands",
    district = "Nicobars",
    latitude = 7.1205395,
    longitude = 93.7841503,
    totalDeceased = 129,
    totalRecovered = 7518,
    totalCovishields = 294001,
    totalCovaxin = 200157,
    deceased = 0,
    recovered = 0,
    covishields = 25394,
    covaxin = 20313
)

private lateinit var first: LatLng

@Composable
fun Statistics(
    viewModel: MainViewModel? = null,
    onFinish: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var loading by rememberSaveable { mutableStateOf(true) }
    var selectedOption by rememberSaveable { mutableIntStateOf(0) }
    var rotated by rememberSaveable { mutableStateOf(false) }
    val rotationY by animateFloatAsState(targetValue = if (rotated) 180f else 0f, label = "")
    val frontVisible = rotationY <= 90f || rotationY >= 270f
    val lazyListState = rememberLazyListState()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var lastSelected by rememberSaveable { mutableIntStateOf(selectedOption) }
    var specificIndex by rememberSaveable { mutableIntStateOf(-1) }

    var expanded by rememberSaveable { mutableStateOf(false) }

    val padding = 25.dp

    LaunchedEffect(Unit) {
        loading = scope.async {
            delay(3000)
            covidLocations = viewModel?.covidMap ?: mutableMapOf()
            first = viewModel?.first!!
            false
        }.await()
    }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex > 0) {
            specificIndex = selectedIndex
            selectedOption = 1
            rotated = !rotated
            delay(100)
            lazyListState.scrollToItem(selectedIndex - 1)
            expanded = true
        }
    }

    CoviMapsTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Scaffold(
                topBar = {
                    Box(
                        modifier = Modifier
                            .padding(23.dp)
                            .statusBarsPadding()
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "exit app",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .clickable { onFinish() }
                        )
                        Text(
                            text = "Statistics",
                            style = TextStyle(
                                fontFamily = GoogleFonts.shadowsIntoLightFamily,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                bottomBar = {
                    Box(modifier = Modifier.padding(padding)) {
                        if (!loading) DynamicTabSelector(
                            tabs = listOf("Map", "List"),
                            selectedOption = selectedOption,
                            modifier = Modifier.padding(30.dp)
                        ) {
                            selectedOption = it
                            if (lastSelected != selectedOption) {
                                rotated = !rotated
                            }
                            lastSelected = selectedOption
                        }
                    }
                }
            ) { scaffold ->
                Box(
                    modifier = Modifier
                        .padding(scaffold)
                        .fillMaxSize()
                ) {
                    if (loading) {
                        Loader(modifier = Modifier.align(Alignment.Center)) {
                            loading = false
                        }
                    } else {
                        ElevatedCard(
                            modifier = Modifier
                                .padding(horizontal = padding)
                                .fillMaxSize()
                                .graphicsLayer {
                                    this.rotationY = rotationY
                                    cameraDistance = 12f * density
                                    // Flip the back side content to prevent mirroring
                                    if (!frontVisible) {
                                        scaleX = -1f
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (frontVisible) {
                                    MapView(modifier = Modifier.fillMaxSize()) {
                                        selectedIndex = it
                                        //selected = true
                                    }
                                } else {
                                    ListView(
                                        modifier = Modifier.fillMaxSize(),
                                        lazyListState = lazyListState,
                                        specificIndex = if (selectedIndex > 0) specificIndex else -1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapView(modifier: Modifier = Modifier, onClick: (Int) -> Unit = {}) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            first, 7f
        )
    }
    var drawable by rememberSaveable { mutableIntStateOf(R.drawable.red_covid_icon) }
    var shouldLoad by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        shouldLoad = scope.async {
            delay(200)
            true
        }.await()
    }

    Box {
        GoogleMap(
            modifier = modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            if (shouldLoad) covidLocations.forEach { (key, value) ->
                drawable =
                    R.drawable.red_covid_icon
                Marker(
                    state = MarkerState(
                        LatLng(
                            value.latitude,
                            value.longitude
                        )
                    ),
                    title = "${value.district}, ${value.state}",
                    icon = BitmapDescriptorFactory.fromResource(drawable),
                    onInfoWindowClick = {
                        Log.d(TAG, "MapView: ")
                        onClick(key)
                    }
                )
            }
        }
    }
}

@Composable
fun ListView(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    specificIndex: Int = -1,
) {
    val context = LocalContext.current
    val activity = context as Activity
    var filter by rememberSaveable { mutableStateOf("") }
    var shouldLoad by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        shouldLoad = scope.async {
            delay(200)
            true
        }.await()
    }

    val onFiltering: (CovidLocation) -> Boolean = {
        it.district.lowercase().startsWith(filter.lowercase())
    }

    if (shouldLoad) Column {
        SearchBar {
            filter = it
            if (filter == "") activity.hideSoftKeyBoard()
        }
        LazyColumn(state = lazyListState, modifier = modifier) {
            items(
                covidLocations
                    .filter {
                        if (filter != "")
                            onFiltering(it.value)
                        else true
                    }
                    .toList()
                    .sortedBy { it.first }) { item ->
                if (item.first != specificIndex) {
                    CardContent(
                        covidLocation = item.second
                    )
                } else CardContent(
                    covidLocation = item.second,
                    specificIndex = specificIndex
                )
            }
        }
    }
}

@Composable
private fun CardContent(covidLocation: CovidLocation = covid, specificIndex: Int = -1) {
    var selectedOption by rememberSaveable { mutableIntStateOf(0) }
    val information: Map<String, Int> = mapOf(
        "deceased" to if (selectedOption == 0) covidLocation.deceased else covidLocation.totalDeceased,
        "recovered" to if (selectedOption == 0) covidLocation.recovered else covidLocation.totalRecovered,
        "vaccinated from covishield" to if (selectedOption == 0) covidLocation.covishields else covidLocation.totalCovishields,
        "vaccinated from covaxin" to if (selectedOption == 0) covidLocation.covaxin else covidLocation.totalCovaxin
    )
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        expanded = specificIndex != -1
    }

    ElevatedCard(modifier = Modifier.padding(5.dp)) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "District: ${covidLocation.district}")
                        Text(
                            text = "State: ${covidLocation.state}",
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = ""
                        )
                    }
                }
                if (expanded) {
                    DynamicTabSelector(
                        tabs = listOf("District", "State"),
                        selectedOption = selectedOption
                    ) {
                        selectedOption = it
                    }
                    Column(modifier = Modifier.padding(10.dp)) {
                        information.entries.forEach {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "${it.key}: ")
                                Text(text = "${it.value}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsPreview() {
    Statistics()
}

@Preview(
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    name = "GreetingPreviewDark"
)
@Composable
private fun CardContentPreview() {
    CardContent()
}