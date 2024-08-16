package com.covid.covimaps.ui.composable

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.covid.covimaps.R
import com.covid.covimaps.data.model.local.CovidSymptoms
import com.covid.covimaps.data.model.local.Symptoms
import com.covid.covimaps.ui.theme.DarkGreen
import com.covid.covimaps.ui.theme.GoogleFonts

private const val TAG = "SymptomsCheck"
private lateinit var speakOutLoad: (Boolean, String) -> Unit
private var survey: MutableMap<String, String> = mutableMapOf()

private lateinit var onChecked: (String, String) -> Unit

@Composable
fun HealthCheck(
    readOutLoud: (Boolean, String) -> Unit = { _, _ -> },
    onFinish: () -> Unit = {},
) {

    val context = LocalContext.current
    val symptoms = context.resources.getStringArray(R.array.symptoms)
    val diseases = Symptoms.entries

    var counter by rememberSaveable { mutableIntStateOf(0) }
    var label by rememberSaveable { mutableStateOf(diseases[counter].name.replace("_", " ")) }
    var disease by rememberSaveable { mutableStateOf(diseases[counter].symptom) }
    var symptom by rememberSaveable { mutableStateOf(symptoms[counter]) }
    var enabled by rememberSaveable { mutableStateOf(false) }
    val opacity by rememberSaveable { mutableFloatStateOf(if (enabled) 1f else 0.5f) }
    var submit by rememberSaveable { mutableStateOf(false) }

    val padding = 17.dp

    val onChange: (Boolean) -> Unit = {
        Log.d(TAG, "HealthCheck: counter $counter with ${diseases[counter].symptom}")
        if (counter < 9) {
            if (it) counter++ else counter--
            label = diseases[counter].name.replace("_", " ")
            disease = diseases[counter].symptom
            symptom = symptoms[counter]
        }
    }

    onChecked = { question, answer ->
        survey[question] = answer
        enabled = survey[question] != ""
    }

    speakOutLoad = readOutLoud

    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        Scaffold(topBar = {
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
                        .clickable {
                            survey = mutableMapOf()
                            onFinish() }
                )
                Text(
                    text = "Health Survey",
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
                Column(modifier = Modifier.padding(padding)) {
                    if (counter < 10) Text(
                        text = disease,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (counter < 10) CustomCheckBox(question = disease)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (counter > 0) ElevatedButton(
                            onClick = { onChange(false) },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Text(text = "Previous", fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(
                            onClick = { onChange(true) },
                            enabled = enabled,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .alpha(opacity)
                        ) {
                            Text(
                                text = if (counter < 10) "Next" else "Submit",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }) { scaffold ->
            Box(
                modifier = Modifier
                    .padding(scaffold)
                    .fillMaxSize()
            ) {
                if (counter < 10) DiseaseCard(
                    modifier = Modifier
                        .padding(padding + 12.dp)
                        .align(Alignment.Center),
                    padding = padding,
                    label = label,
                    symptom = symptom,
                    question = disease
                ) else Form(
                    modifier = Modifier
                        .padding(padding + 12.dp)
                )
            }
        }
    }
}

@Composable
fun DiseaseCard(
    modifier: Modifier = Modifier,
    padding: Dp,
    label: String,
    symptom: String,
    question: String
) {
    var mikeEnabled by rememberSaveable { mutableStateOf(false) }

    onChecked(question, survey[question] ?: "")

    val stop = {
        speakOutLoad(true, "")
        mikeEnabled = false
    }

    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = DarkGreen)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    fontSize = 27.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(7.dp)
                )
                Icon(
                    imageVector = if (!mikeEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        if (!mikeEnabled) {
                            speakOutLoad(false, symptom)
                            mikeEnabled = true
                        } else stop()
                    }
                )
            }
            Text(text = symptom, fontSize = 17.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Form(
    modifier: Modifier = Modifier,
) {
    val questions = CovidSymptoms.entries
    val existingDiseases = LocalContext.current.resources.getStringArray(R.array.existing_diseases)
    val covidMedicines = LocalContext.current.resources.getStringArray(R.array.covid_medicines)

    val scrollState = rememberScrollState()
    var positive by rememberSaveable { mutableStateOf(false) }

    Column {
        Column(modifier = modifier.verticalScroll(scrollState)) {
            questions.subList(0, 3).forEach { question ->
                Text(text = question.symptom)
                CustomCheckBox(question = question.symptom)
            }
            Text(
                text = questions[3].symptom,
                fontSize = 15.sp
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                existingDiseases.forEach {
                    CustomFilterChip(label = it)
                }
            }
            Text(text = questions[4].symptom)
            CustomCheckBox(
                question = questions[4].symptom,
                another = true
            )
            Text(text = questions[5].symptom)
            CustomCheckBox(
                question = questions[5].symptom,
                another = false
            )
            if (positive) {
                Log.d(TAG, "Form: $survey")
                Text(
                    text = questions[5].symptom,
                    fontSize = 15.sp
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    covidMedicines.forEach {
                        CustomFilterChip(label = it)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCheckBox(
    question: String = "",
    another: Boolean = false,
) {
    var yes by rememberSaveable { mutableStateOf(false) }
    var no by rememberSaveable { mutableStateOf(false) }
    var partially by rememberSaveable { mutableStateOf(false) }

    yes = survey[question] == "Yes"
    no = survey[question] == "No"
    partially = survey[question] == "Partially"

    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 3.dp)
        ) {
            Text(text = "Yes")
            Checkbox(checked = yes, onCheckedChange = {
                yes = it
                no = false
                if (another) partially = false
                onChecked(question, "Yes")
            })
        }
        if (another) Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 3.dp)
        ) {
            Text(text = "Partially")
            Checkbox(checked = yes, onCheckedChange = {
                partially = it
                yes = false
                no = false
                onChecked(question, "Partially")
            })
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 3.dp)
        ) {
            Text(text = "No")
            Checkbox(checked = no, onCheckedChange = {
                no = it
                yes = false
                if (another) partially = false
                onChecked(question, "No")
            })
        }
    }
}

@Composable
fun CustomFilterChip(
    label: String,
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    FilterChip(
        selected = selected,
        onClick = { selected = !selected },
        label = { Text(text = label) },
        leadingIcon = {
            if (selected) Icon(imageVector = Icons.Default.Check, contentDescription = "")
        },
        modifier = Modifier.wrapContentWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun HealthCheckPreview() {
    HealthCheck()
}

@Preview(showBackground = true)
@Composable
private fun CustomCheckBoxPreview() {
    CustomCheckBox()
}

@Preview(showBackground = true)
@Composable
private fun FormPreview() {
    Form()
}