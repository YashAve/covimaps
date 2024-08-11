package com.covid.covimaps.ui.component.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.covid.covimaps.data.model.local.room.CountryCodeUiState
import com.covid.covimaps.viewmodel.UserViewModel
import kotlinx.coroutines.async

private const val TAG = "CountryCodeComposable"
private lateinit var showCountryCodes: (Boolean) -> Unit

@Composable
fun CustomCountryCode(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel? = null,
    showCodes: (Boolean) -> Unit = {}
) {
    var value by rememberSaveable { mutableStateOf("") }
    var available by rememberSaveable { mutableStateOf(true) }
    var geoCodesAvailable by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var countryCodes: MutableList<CountryCodeUiState> = mutableListOf()

    showCountryCodes = showCodes

    val selectCountry: (String, String) -> Unit

    LaunchedEffect(Unit) {
        geoCodesAvailable = scope.async {
            countryCodes = viewModel?.getDetails()?.toMutableList() ?: mutableListOf()
            true
        }.await()
    }

    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        Scaffold(topBar = {
            Column(
                modifier = modifier
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(value = value, onValueChange = {
                    value = it
                }, leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        modifier = Modifier.clickable { showCountryCodes(false) })
                }, trailingIcon = {
                    if (value != "") {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { available = false }
                        )
                    }
                }, placeholder = {
                    Text(text = "Search")
                }, maxLines = 1, modifier = Modifier.fillMaxWidth())
            }
        }) { scaffold ->
            Box(
                modifier = Modifier
                    .padding(scaffold)
                    .fillMaxSize()
            ) {
                if (geoCodesAvailable) {
                    if (!available) {
                        Text(
                            text = "We couldn't find that country, please try again",
                            textAlign = TextAlign.Center,
                            fontSize = 17.sp,
                            modifier = Modifier
                                .padding(23.dp)
                                .fillMaxWidth()
                                .align(Alignment.CenterStart)
                        )
                    } else {
                        LazyColumn {
                            items(
                                countryCodes
                                    .filter { it.country.startsWith(value) }) {
                                CountryCode(
                                    countryCodeUiState = it
                                ) { altSpelling, countryCode ->
                                    viewModel?.selectedCountry = altSpelling
                                    viewModel?.selectedCountryCode = countryCode
                                }
                                Spacer(
                                    modifier = modifier
                                        .height(0.3.dp)
                                        .background(color = Color.LightGray)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryCode(
    modifier: Modifier = Modifier,
    countryCodeUiState: CountryCodeUiState? = null,
    selectCountry: (String, String) -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 7.dp)
            .clickable {
                selectCountry(countryCodeUiState?.code!!, countryCodeUiState.countryCode)
                showCountryCodes(false)
            },
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            bitmap = countryCodeUiState?.flag!!, contentDescription = "", modifier = Modifier
                .size(32.dp)
                .weight(0.1f)
        )
        Text(
            text = countryCodeUiState.country,
            fontSize = 17.sp,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(0.8f)
        )
        Text(
            text = countryCodeUiState.countryCode,
            fontSize = 16.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomCountryCodePreview() {
    CustomCountryCode()
}