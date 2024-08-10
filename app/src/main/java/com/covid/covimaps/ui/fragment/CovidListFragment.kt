package com.covid.covimaps.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.covid.covimaps.data.model.local.room.CovidLocation
import com.covid.covimaps.data.repository.remote.covid.FirebaseManager
import com.covid.covimaps.ui.GoogleFonts
import com.covid.covimaps.ui.component.composable.DynamicTabSelector
import com.covid.covimaps.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.theme.LightGreen
import com.covid.covimaps.viewmodel.MainViewModel

private const val TAG = "CovidListFragment"
class CovidListFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var firebaseManager: FirebaseManager

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

            firebaseManager = FirebaseManager(requireActivity())
            firebaseManager.sendOtp("+918826034572")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Main(
    modifier: Modifier = Modifier, viewModel: MainViewModel? = null, covidLocations: List<CovidLocation> = listOf(
        CovidLocation(
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
    )
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Surface(color = MaterialTheme.colorScheme.primary) {
        Scaffold(topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = LightGreen,
                titleContentColor = Color.White
            ), title = {
                Text(text = "CoviMaps", fontFamily = GoogleFonts.archivoBlackFamily)
            }, actions = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "allows you to filter the list"
                    )
                }
            })
        }) { scaffold ->
            Column(modifier = Modifier.padding(scaffold)) {
                Log.d(TAG, "covid locations size: ${viewModel?.covidLocations?.size}")
                LazyColumn {
                    items(viewModel?.covidLocations ?: covidLocations) { covidLocation ->
                        ListItem(covidLocation = covidLocation)
                    }
                }
                if (expanded) {
                    ModalBottomSheet(onDismissRequest = { /*TODO*/ }) {
                        FilterData()
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterData(
    modifier: Modifier = Modifier,
    sortBy: List<String> = listOf(
        "deceased(district)",
        "recovered(district)",
        "covishields(district)",
        "covaxins(district)"
    )
) {
    val textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(30.dp)
    ) {
        Text(text = "Sort By", style = textStyle)
        LazyColumn {
            items(sortBy) {
                SortByFilterChip(label = it)
            }
        }
    }
}

@Composable
private fun SortByFilterChip(modifier: Modifier = Modifier, label: String) {
    var selected by rememberSaveable { mutableStateOf(false) }
    FilterChip(selected = selected, onClick = { selected = !selected }, label = {
        Text(text = label)
    }, leadingIcon = {
        if (selected) {
            Icon(imageVector = Icons.Filled.Done, contentDescription = "$label selected")
        }
    })
}

@Composable
private fun ListItem(
    modifier: Modifier = Modifier, covidLocation: CovidLocation = CovidLocation(
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
) {
    var selectedOption by rememberSaveable { mutableIntStateOf(0) }
    val information: Map<String, Int> = mapOf(
        "deceased" to if (selectedOption == 0) covidLocation.deceased else covidLocation.totalDeceased,
        "recovered" to if (selectedOption == 0) covidLocation.recovered else covidLocation.totalRecovered,
        "vaccinated from covishield" to if (selectedOption == 0) covidLocation.covishields else covidLocation.totalCovishields,
        "vaccinated from covaxin" to if (selectedOption == 0) covidLocation.covaxin else covidLocation.totalCovaxin
    )
    val oldGrey = TextStyle(color = Color.DarkGray)
    val boldAndBlack = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold)
    ElevatedCard(
        modifier = modifier.padding(7.dp),
        onClick = { /*TODO*/ },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column {
                Text(text = covidLocation.district, style = oldGrey)
                Text(
                    text = covidLocation.state,
                    style = boldAndBlack,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
                DynamicTabSelector(
                    tabs = listOf("District", "State"),
                    selectedOption = selectedOption
                ) {
                    selectedOption = it
                }
                Column(modifier = Modifier.padding(10.dp)) {
                    information.entries.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${it.key}: ", style = oldGrey)
                            Text(text = "${it.value}", style = boldAndBlack)
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

@Preview(showBackground = true)
@Composable
private fun FilterDataPreview() {
    //FilterData()
}

@Preview(showBackground = true)
@Composable
private fun ListItemPreview() {
    ListItem()
}