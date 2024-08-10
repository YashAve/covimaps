package com.covid.covimaps.ui.component.composable

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.covid.covimaps.data.model.remote.covid.countrycode.CountryCodes
import com.covid.covimaps.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

    val scrollState = rememberScrollState()
    var countryCodes: MutableList<CountryCodes> = mutableListOf()

    showCountryCodes = showCodes

    val selectCountry: (String, String) -> Unit

    LaunchedEffect(Unit) {
        geoCodesAvailable = scope.async {
            countryCodes = viewModel?.getDetails() ?: mutableListOf()
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
                        Column(modifier = Modifier.verticalScroll(state = scrollState)) {
                            countryCodes
                                .filter { it.name?.common?.startsWith(value) ?: false }
                                .forEach {
                                    CountryCode(
                                        countryCodes = it
                                    ) { altSpelling, root, suffix ->
                                        viewModel?.selectedCountry = altSpelling[0]
                                        viewModel?.selectedCountryCode = "$root$suffix"
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
                } else {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
private fun CountryCode(
    modifier: Modifier = Modifier,
    countryCodes: CountryCodes? = null,
    selectCountry: (ArrayList<String>, String, String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var imageState by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }

    val imageUrl = countryCodes?.flags?.png
    val altSpelling = countryCodes?.altSpellings ?: arrayListOf()
    val suffix = countryCodes?.idd?.suffixes?.let {
        if (it.size == 1) it[0] else ""
    } ?: ""
    val root = "${countryCodes?.idd?.root}"
    val countryCode = "$root$suffix"

    LaunchedEffect(imageUrl) {
        coroutineScope.launch(Dispatchers.IO) {
            imageState = try {
                imageUrl
            } catch (e: Exception) {
                null
            } finally {
                isLoading = false
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 7.dp)
            .clickable {
                Log.d(TAG, "CountryCode: country code is selected")
                selectCountry(altSpelling, root, suffix)
                showCountryCodes(false)
            },
        horizontalArrangement = Arrangement.Center
    ) {
        imageState?.let {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .crossfade(true)
                    .build(),
                contentDescription = "Loaded Image",
                modifier = Modifier
                    .size(32.dp)
                    .weight(0.1f)
            )
        }
        Text(
            text = "${countryCodes?.name?.common}",
            fontSize = 17.sp,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(0.8f)
        )
        Text(
            text = countryCode,
            fontSize = 16.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomCountryCodePreview() {
    CustomCountryCode()
}