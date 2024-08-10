package com.covid.covimaps.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import com.covid.covimaps.ui.activity.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.fragment.CovidListFragment
import com.covid.covimaps.ui.fragment.CovidMapFragment
import com.covid.covimaps.viewmodel.MainViewModel
import com.covid.covimaps.viewmodel.OnDataReadyCallback
import com.covid.covimaps.viewmodel.getLocationsCount
import com.covid.covimaps.viewmodel.insertDatabase
import com.covid.covimaps.viewmodel.retrieveLocations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private const val TAG = "StatisticsActivity"

@AndroidEntryPoint
class StatisticsActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this, "current activity name: ${this.javaClass.simpleName}", Toast.LENGTH_SHORT).show()

        enableEdgeToEdge()
        setContent {
            CoviMapsTheme {
                Main(viewModel = viewModel)
            }
        }
    }
}

suspend fun generate(context: Context, viewModel: MainViewModel) {
    val callback = object : OnDataReadyCallback {
        override fun onDataReady(status: String) {
            Log.d(TAG, "onDataReady: getCovidGeocode status $status")
        }
    }

    if (getLocationsCount(context) > 0) {
        viewModel.covidLocations = retrieveLocations(context).toMutableList()
    } else {
        viewModel.getCovidDataUiState()
        viewModel.getLocations(callback)
        insertDatabase(viewModel.covidLocations.toTypedArray(), context)
    }
    viewModel.covidLocations = viewModel.covidLocations.toSet().toMutableList()
    viewModel.covidLocations.shuffle()
}

@Composable
private fun Main(modifier: Modifier = Modifier, viewModel: MainViewModel? = null) {
    var loading by rememberSaveable { mutableStateOf(true ) }
    val scope = rememberCoroutineScope()

    val menu = listOf(
        BottomNavigationItem(
            title = "Visual",
            selectedIcon = Icons.Filled.Place,
            unselectedIcon = Icons.Default.Place
        ),
        BottomNavigationItem(
            title = "Data",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Default.Info
        )
    )

    val id by rememberSaveable { mutableIntStateOf(View.generateViewId()) }
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val covidMapFragment = CovidMapFragment()
    val covidListFragment = CovidListFragment()
    val context = LocalContext.current
    val activity = context as FragmentActivity

    val fragmentTransaction: (Int) -> Unit = {
        activity.supportFragmentManager.beginTransaction().apply {
            replace(id, if (it == 0) covidMapFragment else covidListFragment)
            addToBackStack(null)
        }.commit()
    }

    if (loading) {
        LaunchedEffect(Unit) {
            loading = scope.async {
                delay(10000)
                viewModel?.let {
                    generate(context = context, viewModel = viewModel)
                    viewModel.loading = false
                    Log.d(TAG, "covid locations list size: ${viewModel.covidLocations.size}")
                }
                false
            }.await()
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(modifier = modifier.fillMaxSize(), bottomBar = {
            NavigationBar {
                menu.forEachIndexed { index, item ->
                    NavigationBarItem(selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index }, icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = "switched to ${item.title} screen"
                            )
                            fragmentTransaction(selectedItemIndex)
                        },
                        label = {
                            Text(text = item.title)
                        })
                }
            }
        }) {
            Box(modifier = Modifier.padding(it)) {
                AndroidView(
                    factory = { context ->
                        FragmentContainerView(context).apply {
                            setId(id)
                        }
                    }, modifier = Modifier
                        .fillMaxSize()
                )

                if (loading) {
                    GenerateDialog {
                        context.finish()
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerateDialog(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(text = "Retrieving data...")
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
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