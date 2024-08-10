package com.covid.covimaps.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.covid.covimaps.data.repository.remote.covid.FirebaseManager
import com.covid.covimaps.domain.GooglePlayServicesManager
import com.covid.covimaps.ui.GoogleFonts
import com.covid.covimaps.ui.activity.ui.theme.CoviMapsTheme
import com.covid.covimaps.ui.component.composable.CustomCountryCode
import com.covid.covimaps.ui.component.composable.Loader
import com.covid.covimaps.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginActivity"

private lateinit var firebaseManager: FirebaseManager
private lateinit var googlePlayServicesManager: GooglePlayServicesManager
private lateinit var onFinish: () -> Unit
private lateinit var startActivity: () -> Unit

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startActivity = {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            onFinish()
        }

        onFinish = { finish() }

        firebaseManager = FirebaseManager(this)
        googlePlayServicesManager = GooglePlayServicesManager(this)


        setContent {
            CoviMapsTheme {
                Main(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun Login(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel? = null,
    resendOTP: (String) -> Unit = {},
    showCountryCodes: (Boolean) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var status by rememberSaveable { mutableIntStateOf(0) }
    var heading by rememberSaveable { mutableStateOf("Can we get your number, please?") }
    var number by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("We only use phone numbers to make sure everyone on CoviMaps is real.") }
    var numberHeading by rememberSaveable { mutableStateOf("Phone number") }
    var countryCode by rememberSaveable { mutableStateOf("+91") }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textFieldEnabled by rememberSaveable { mutableStateOf(false) }
    var enabled by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val changeNumber = {
        number = ""
        status = 0
    }
    val onPhoneNumberClick = {
        if (!textFieldEnabled) {
            googlePlayServicesManager.getPhoneNumberHints {
                number = it.replace(
                    viewModel?.selectedCountryCode
                        ?: "", ""
                )
                enabled = true
            }
            textFieldEnabled = true
        }
    }
    val submitPhoneNumber: (String) -> Unit = {
        showAlertDialog = false
        heading = "Verify your number"
        title = "Enter the code we've sent by text to $it."
        numberHeading = "Code"
        number = ""
        status = 1
        firebaseManager.flag = false
        firebaseManager.sendOtp(it)
    }
    val submitOTP: () -> Unit = {
        startActivity()
    }
    val checks: List<(String) -> Boolean> = listOf({
        number = it
        it.length < 15
    }, {
        number = it
        it.length == 6
    })
    val onLoading: (Boolean) -> Unit = {
        isLoading = it
    }

    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        Scaffold(modifier = modifier.fillMaxWidth(), topBar = {
            Row(
                modifier = Modifier
                    .padding(23.dp)
                    .statusBarsPadding()
            ) {
                if (status == 0) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "exit app",
                        modifier = Modifier
                            .clickable { onFinish() }
                    )
                }
            }
        }, bottomBar =
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(23.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                        .alpha(if (enabled) 1f else 0.5f)
                        .clickable {
                            if (enabled) {
                                when (status) {
                                    0 -> {
                                        showAlertDialog = true
                                    }

                                    1 -> submitOTP()
                                    else -> resendOTP(number)
                                }
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "",
                    )
                }
            }
        }) { scaffold ->
            Box(
                modifier = Modifier
                    .padding(scaffold)
                    .fillMaxSize()
            ) {
                if (isLoading) {
                    Loader(
                        onLoading = onLoading,
                        task = "Fetching OTP",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 46.dp)
                        .alpha(if (isLoading) 0.5f else 1f)
                ) {
                    Text(
                        text = heading,
                        modifier = Modifier
                            .align(Alignment.Start),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = GoogleFonts.archivoBlackFamily
                        ),
                        fontSize = 30.sp
                    )
                    Text(
                        text = title,
                        modifier = Modifier.padding(
                            top = 10.dp,
                            bottom = if (status == 1) 0.dp else 10.dp
                        )
                    )
                    if (status == 1) {
                        Text(
                            text = "Change number",
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 10.dp)
                                .clickable { changeNumber() })
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        if (status == 0) {
                            Column {
                                Text(text = "Country")
                                OutlinedCard(modifier = Modifier.clickable { showCountryCodes(true) }) {
                                    Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "${viewModel?.selectedCountry} ${viewModel?.selectedCountryCode}")
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.padding(horizontal = 2.5.dp))
                        }
                        Column {
                            Text(text = numberHeading)
                            OutlinedCard(
                                border = BorderStroke(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Box(modifier = Modifier) {
                                    TextField(
                                        value = number,
                                        onValueChange = {
                                            number = it
                                            enabled = when (status) {
                                                0 -> checks[0](it)
                                                else -> checks[1](it)
                                            }
                                        },
                                        singleLine = true,
                                        enabled = textFieldEnabled,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.clickable {
                                            when (status) {
                                                0 -> onPhoneNumberClick()
                                                else -> {}
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (showAlertDialog) {
                AlertDialog(title = { Text(text = "We need to verify your number") }, text = {
                    Text(
                        text = "We need to make sure that $countryCode$number is your number."
                    )
                }, confirmButton = {
                    Text(
                        text = "OK",
                        modifier = Modifier.clickable {
                            submitPhoneNumber("${viewModel?.selectedCountryCode}$number")
                            isLoading = true
                        })
                }, dismissButton = {
                    Text(text = "CANCEL", modifier = Modifier.clickable { showAlertDialog = false })
                }, onDismissRequest = { showAlertDialog = false })
            }
        }
    }
}

@Composable
private fun Main(
    viewModel: UserViewModel? = null
) {
    var show by rememberSaveable { mutableStateOf(false) }
    val showScreen: (Boolean) -> Unit = {
        Log.d(TAG, "Main: CountryCodeComposable show is $show")
        show = it
    }
    if (show) {
        CustomCountryCode(viewModel = viewModel, showCodes = showScreen)
    } else {
        Login(
            viewModel = viewModel,
            showCountryCodes = showScreen
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainPreview() {
    CoviMapsTheme {
        Main()
    }
}