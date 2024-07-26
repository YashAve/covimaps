package com.covid.covimaps.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import com.covid.covimaps.ui.activity.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.fragment.CovidListFragment
import com.covid.covimaps.ui.fragment.CovidMapFragment

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CoviMapsTheme {
                Main()
            }
        }
    }
}

@Composable
private fun Main(modifier: Modifier = Modifier) {
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
    val covidMapFragment: CovidMapFragment by lazy { CovidMapFragment() }
    val covidListFragment: CovidListFragment by lazy { CovidListFragment() }
    val context = LocalContext.current
    val activity = context as FragmentActivity

    val fragmentTransaction: (Int) -> Unit = {
        activity.supportFragmentManager.beginTransaction().apply {
            replace(id, if (it == 0) covidMapFragment else covidListFragment)
        }.commit()
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(modifier = modifier.fillMaxSize(), bottomBar = {
            NavigationBar {
                menu.forEachIndexed { index, item ->
                    NavigationBarItem(selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index }, icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = ""
                            )
                            fragmentTransaction(selectedItemIndex)
                        },
                        label = {
                            Text(text = item.title)
                        })
                }
            }
        }) {
            AndroidView(
                factory = { context ->
                    FragmentContainerView(context).apply {
                        setId(id)
                    }
                }, modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainPreview() {
    Main()
}