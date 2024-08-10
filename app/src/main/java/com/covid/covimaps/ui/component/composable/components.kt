package com.covid.covimaps.ui.component.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.covid.covimaps.R
import com.covid.covimaps.data.model.local.room.CovidLocation
import com.covid.covimaps.data.model.remote.covid.disclaimer
import com.covid.covimaps.ui.theme.DarkGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MapsModalContent(
    modifier: Modifier = Modifier,
    covidLocation: CovidLocation = CovidLocation(
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(7.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            DynamicTabSelector(
                tabs = listOf("District", "State"),
                selectedOption = selectedOption
            ) {
                selectedOption = it
            }
        }
        val mapping: Map<String, Int> = mapOf(
            "Maximum Deaths" to R.drawable.red_covid_icon,
            "Average Deaths" to R.drawable.green_covid_icon,
            "Minimum Deaths" to R.drawable.yellow_covid_icon
        )
        mapping.forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = it.value), contentDescription = it.key)
                Text(text = it.key, color = Color.Black)
            }
        }

        val districts = mapOf(
            "District:" to covidLocation.district,
            "Deaths:" to covidLocation.deceased,
            "Recoveries:" to covidLocation.recovered,
            "CoviShields:" to covidLocation.covishields,
            "Covaxins:" to covidLocation.covaxin
        )
        val states = mapOf(
            "State:" to covidLocation.state,
            "Deaths:" to covidLocation.totalDeceased,
            "Recoveries:" to covidLocation.totalRecovered,
            "CoviShields:" to covidLocation.totalCovishields,
            "Covaxins:" to covidLocation.totalCovaxin
        )

        (if (selectedOption == 0) districts else states).forEach {
            Text(
                text = "${it.key} ${it.value}",
                modifier = Modifier.align(Alignment.Start), color = Color.Black
            )
        }
    }
}

@Composable
fun DisclaimerDialog(
    modifier: Modifier = Modifier,
    readOutLoud: (Boolean, String) -> Unit = { _, _ -> },
    onAgree: () -> Unit = {},
    onDisagree: () -> Unit = {}
) {
    val verticalScroll = rememberScrollState()
    var mikeEnabled by rememberSaveable { mutableStateOf(false) }
    val text by rememberSaveable { mutableStateOf(StringBuilder()) }
    val stop = {
        readOutLoud(false, "")
        mikeEnabled = false
    }

    AlertDialog(
        onDismissRequest = {
            stop()
            onDisagree()
        },
        confirmButton = {
            Text(text = "Agree", modifier = Modifier.clickable {
                stop()
                onAgree()
            })
        },
        dismissButton = {
            Text(
                text = "Cancel", modifier = Modifier.clickable {
                    stop()
                    onDisagree()
                }
            )
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Disclaimer")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Speak out loud")
                    Icon(
                        imageVector = if (!mikeEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            if (!mikeEnabled) {
                                readOutLoud(true, text.toString())
                                mikeEnabled = true
                            } else stop()
                        }
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(verticalScroll)) {
                disclaimer.forEach {
                    text.append(it.key).append(it.value)
                    Column {
                        Text(text = it.key, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = it.value, fontSize = 14.sp)
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize(),
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Disclaimer")
        })
}

@Composable
fun Loader(
    modifier: Modifier = Modifier,
    task: String = "Loading",
    onLoading: (Boolean) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var loadingMessage by rememberSaveable { mutableStateOf("") }
    var counter by rememberSaveable { mutableIntStateOf(0) }
    var count by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        scope.launch {
            while (count < 3) {
                while (counter < 3) {
                    loadingMessage = "${".".repeat(counter)}$task"
                    delay(500)
                    counter++
                }
                counter = 0
                count++
            }
            onLoading(false)
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = DarkGreen,
            contentColor = Color.White
        ), modifier = modifier
    ) {
        Text(
            text = loadingMessage,
            fontSize = 17.sp,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 5.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoaderPreview() {
    Loader()
}

@Preview(showBackground = true)
@Composable
private fun MapsPreview() {
    MapsModalContent()
}

@Preview(showBackground = true)
@Composable
private fun DisclaimerDialogPreview() {
    DisclaimerDialog()
}