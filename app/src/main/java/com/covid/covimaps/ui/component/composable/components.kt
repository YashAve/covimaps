package com.covid.covimaps.ui.component.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.covid.covimaps.R
import com.covid.covimaps.data.model.local.room.CovidLocation
import com.covid.covimaps.data.model.remote.disclaimer

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
    onAgree: () -> Unit = {},
    onDisagree: () -> Unit = {}
) {
    val verticalScroll = rememberScrollState()
    AlertDialog(
        onDismissRequest = { },
        confirmButton = { Text(text = "Agree", modifier = Modifier.clickable { onAgree() }) },
        dismissButton = {
            Text(
                text = "Cancel"
            )
        },
        title = {
            Text(text = "Disclaimer")
        },
        text = {
            Column(modifier = Modifier.verticalScroll(verticalScroll)) {
                disclaimer.forEach {
                    Column {
                        Text(text = it.key, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(text = it.value)
                    }
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = "Disclaimer")
        })
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var phone by rememberSaveable { mutableStateOf("") }
    val label = "We will send you an One Time Password on this mobile number"
    val particular = "One Time Password"
    val annotatedString = buildAnnotatedString {
        val start = label.indexOf(particular)
        val end = particular.length
        append(label.substring(0, start))
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(label.substring(start, end))
        }
        append(label.substring(end))
    }


    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = annotatedString,
            modifier = Modifier.padding(horizontal = 40.dp),
            textAlign = TextAlign.Center
        )
        TextField(
            value = phone,
            onValueChange = {
                if (it.length < 10) phone = it
            },
            placeholder = {
                Text(text = "+91...", fontWeight = FontWeight.Bold)
            },
            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, top = 7.dp)
                .fillMaxWidth()
        )
        FilledIconButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "this button will take you to the OTP verification screen"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapsPreview() {
    MapsModalContent()
}

@Preview(showBackground = true)
@Composable
fun DisclaimerDialogPreview() {
    DisclaimerDialog()
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}