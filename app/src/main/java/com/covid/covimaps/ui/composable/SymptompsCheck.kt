package com.covid.covimaps.ui.composable

import android.app.Activity
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.covid.covimaps.data.model.local.General
import com.covid.covimaps.data.model.local.Symptoms
import com.covid.covimaps.ui.theme.DarkGreen
import com.covid.covimaps.ui.theme.GoogleFonts
import com.covid.covimaps.util.hideSoftKeyBoard
import com.covid.covimaps.viewmodel.MainViewModel

private const val TAG = "SymptomsCheck"
private lateinit var speakOutLoad: (Boolean, String) -> Unit
private var survey: MutableMap<String, String> = mutableMapOf()

private lateinit var onChecked: (String, String) -> Unit

@Composable
fun HealthCheck(
    viewModel: MainViewModel? = null,
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
    var viewForm by rememberSaveable { mutableStateOf(false) }

    val padding = 17.dp

    val updateQuestion: () -> Unit = {
        label = diseases[counter].name.replace("_", " ")
        disease = diseases[counter].symptom
        symptom = symptoms[counter]
    }

    val onChange: (Boolean) -> Unit = { isChecked ->
        if (!viewForm) {
            when {
                !isChecked && counter == 9 -> {
                    counter--
                    readOutLoud(false, "")
                    updateQuestion()
                }

                counter < 9 -> {
                    if (isChecked) counter++ else counter--
                    readOutLoud(false, "")
                    updateQuestion()
                }

                else -> {
                    counter++
                    viewForm = true
                    enabled = false
                }
            }
        } else if (!isChecked) {
            counter--
            updateQuestion()
            viewForm = false
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
                Icon(imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "exit app",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            survey = mutableMapOf()
                            onFinish()
                        })
                Text(
                    text = "Health Survey", style = TextStyle(
                        fontFamily = GoogleFonts.shadowsIntoLightFamily,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ), modifier = Modifier.align(Alignment.Center)
                )
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(text = "${counter + 1}", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text(text = "/11")
                }
            }
        }, bottomBar = {
            Column(modifier = Modifier.padding(padding)) {
                if (!viewForm) Text(
                    text = disease, fontWeight = FontWeight.Bold, fontSize = 15.sp
                )
                if (!viewForm) CustomCheckBox(question = disease)
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
                            .alpha(if (enabled) 1f else 0.5f)
                    ) {
                        Text(
                            text = if (!viewForm) "Next" else "Submit", fontWeight = FontWeight.Bold
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
                if (!viewForm) DiseaseCard(
                    modifier = Modifier
                        .padding(padding + 12.dp)
                        .align(Alignment.Center),
                    padding = padding,
                    label = label,
                    symptom = symptom,
                    question = disease,
                    micEnabled = false,
                    viewModel = viewModel
                ) else Form(
                    modifier = Modifier.padding(padding + 12.dp), viewModel = viewModel
                ) {
                    Log.d(TAG, "HealthCheck: I was called with $it")
                    enabled = it
                }
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
    question: String,
    micEnabled: Boolean,
    viewModel: MainViewModel? = null,
) {
    var mikeEnabled by rememberSaveable { mutableStateOf(micEnabled) }

    onChecked(question, survey[question] ?: "")

    val stop = {
        speakOutLoad(true, "")
        mikeEnabled = false
    }

    ElevatedCard(
        modifier = modifier, colors = CardDefaults.elevatedCardColors(containerColor = DarkGreen)
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
                Icon(imageVector = if (!mikeEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        if (!mikeEnabled) {
                            speakOutLoad(false, symptom)
                            mikeEnabled = true
                        } else stop()
                    })
            }
            Text(text = symptom, fontSize = 17.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Form(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel? = null,
    onFilled: (Boolean) -> Unit = {}
) {
    val questions = CovidSymptoms.entries
    val existingDiseases = LocalContext.current.resources.getStringArray(R.array.existing_diseases)
    val covidMedicines = LocalContext.current.resources.getStringArray(R.array.covid_medicines)

    val scrollState = rememberScrollState()
    var positive by rememberSaveable { mutableStateOf(false) }
    var vaccinated by rememberSaveable { mutableStateOf(false) }

    var country by rememberSaveable { mutableStateOf("Select Country") }
    var city by rememberSaveable { mutableStateOf("Select City") }

    var conditions by rememberSaveable { mutableStateOf("") }
    var medicines by rememberSaveable { mutableStateOf("") }

    var first by rememberSaveable { mutableStateOf(false) }
    var second by rememberSaveable { mutableStateOf(false) }
    var third by rememberSaveable { mutableStateOf(false) }
    var fourth by rememberSaveable { mutableStateOf(false) }
    var fifth by rememberSaveable { mutableStateOf(false) }

    val allFieldsFilled = remember {
        derivedStateOf {
            country != "Select Country" &&
                    city != "Select City" &&
                    first &&
                    second &&
                    third &&
                    fourth &&
                    fifth
        }
    }

    LaunchedEffect(allFieldsFilled.value) {
        if (allFieldsFilled.value) {
            Log.d(TAG, "All non-conditional fields are filled!")
            onFilled(true)
        }
    }

    Column {
        Column(modifier = modifier.verticalScroll(scrollState)) {
            questions.subList(0, 3).forEachIndexed { index, question ->
                Text(text = question.symptom)
                CustomCheckBox(question = question.symptom, viewForm = true) {
                    survey[question.symptom] = it
                    when (index) {
                        0 -> {
                            first =
                                survey.containsKey(questions[0].symptom) && survey[questions[0].symptom] != ""
                        }

                        1 -> {
                            second =
                                survey.containsKey(questions[1].symptom) && survey[questions[1].symptom] != ""
                        }

                        else -> {
                            third =
                                survey.containsKey(questions[2].symptom) && survey[questions[2].symptom] != ""
                        }
                    }
                }
            }
            Text(text = questions[3].symptom, fontSize = 15.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                existingDiseases.forEach {
                    CustomFilterChip(label = it) { condition ->
                        if (!conditions.contains(condition)) {
                            conditions = conditions.plus("$condition,")
                        }
                    }
                }
            }
            Text(text = questions[4].symptom)
            CustomCheckBox(question = questions[4].symptom, viewForm = true) {
                vaccinated = it == "Yes"
                survey[questions[4].symptom] = it
                fourth =
                    survey.containsKey(questions[4].symptom) && survey[questions[4].symptom] != ""
            }
            if (vaccinated) {
                Text(text = questions[6].symptom)
                CustomCheckBox(
                    question = questions[6].symptom,
                    answers = arrayOf("Covishield", "Covaxin"), viewForm = true
                ) {
                    survey[questions[6].symptom] = it
                }
            }
            Text(text = questions[5].symptom)
            CustomCheckBox(question = questions[5].symptom, viewForm = true) {
                positive = it == "Yes"
                survey[questions[5].symptom] = it
                fifth =
                    survey.containsKey(questions[5].symptom) && survey[questions[5].symptom] != ""
            }
            if (positive) {
                Text(text = questions[7].symptom, fontSize = 15.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    covidMedicines.forEach {
                        CustomFilterChip(label = it) { medicine ->
                            if (!medicines.contains(medicine)) {
                                medicines = medicines.plus("$medicine,")
                            }
                        }
                    }
                }
            }
            Text(text = General.WHICH_COUNTRY.question)
            viewModel?.let {
                OriginBox(
                    label = country,
                    viewModel = viewModel,
                    selections = viewModel.countries
                ) {
                    country = it
                    city = "Select City"
                }
                if (country != "Select Country") {
                    Text(text = General.WHICH_CITY.question)
                    OriginBox(
                        label = city,
                        viewModel = viewModel,
                        selections = viewModel.cities[country]!!
                    ) {
                        city = it
                    }
                }
            }
        }
    }
}

@Composable
fun OriginBox(
    modifier: Modifier = Modifier,
    label: String,
    viewModel: MainViewModel,
    selections: MutableList<String>,
    onSelect: (String) -> Unit = {},
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(modifier = modifier
        .padding(vertical = 5.dp)
        .clickable {
            showSheet = true
        }) {
        Text(text = label, modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp))
    }

    if (showSheet) CustomBottomSheet(
        selections = selections,
        show = showSheet,
        onSelect = onSelect,
    ) {
        showSheet = it
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    modifier: Modifier = Modifier,
    selections: MutableList<String>,
    show: Boolean,
    onSelect: (String) -> Unit = {},
    onDismiss: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context as Activity
    var showSheet by rememberSaveable { mutableStateOf(show) }
    var filter by rememberSaveable { mutableStateOf("") }

    val onFiltering: (String) -> Boolean = {
        it.lowercase().startsWith(filter.lowercase())
    }

    ModalBottomSheet(onDismissRequest = {
        showSheet = false
        onDismiss(false)
    }) {
        Column(modifier = modifier.padding(23.dp)) {
            SearchBar(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                filter = it
                if (filter == "") activity.hideSoftKeyBoard()
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(selections.filter {
                    if (filter != "") onFiltering(it)
                    else true
                }.toList().sortedBy { it }) {
                    SheetContentListItem(
                        selectable = it, onSelect = onSelect, onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
fun SheetContentListItem(
    modifier: Modifier = Modifier,
    selectable: String,
    onSelect: (String) -> Unit = {},
    onDismiss: (Boolean) -> Unit = {},
) {
    ElevatedCard(modifier = modifier
        .fillMaxWidth()
        .padding(3.dp)
        .clickable {
            onSelect(selectable)
            onDismiss(false)
        }) {
        Text(
            text = selectable,
            fontSize = 17.sp,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
        )
    }
}

@Composable
fun CustomCheckBox(
    question: String = "",
    answers: Array<String> = arrayOf("Yes", "No"),
    viewForm: Boolean = false,
    onVaccinated: (String) -> Unit = {},
) {
    var yes by rememberSaveable { mutableStateOf(false) }
    var no by rememberSaveable { mutableStateOf(false) }
    yes = survey[question] == answers[0]
    no = survey[question] == answers[1]

    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 3.dp)
        ) {
            Text(text = answers[0])
            Checkbox(checked = yes, onCheckedChange = { isChecked ->
                yes = isChecked
                if (isChecked) {
                    no = false
                    if (!viewForm) onChecked(question, answers[0])
                    onVaccinated(answers[0])
                }
            })
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 3.dp)
        ) {
            Text(text = answers[1])
            Checkbox(checked = no, onCheckedChange = { isChecked ->
                no = isChecked
                if (isChecked) {
                    yes = false
                    if (!viewForm) onChecked(question, answers[1])
                    onVaccinated(answers[1])
                }
            })
        }
    }
}

@Composable
fun CustomFilterChip(
    label: String,
    onSelect: (String) -> Unit = {},
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    FilterChip(selected = selected, onClick = {
        selected = !selected
        onSelect(label)
    }, label = { Text(text = label) }, leadingIcon = {
        if (selected) Icon(imageVector = Icons.Default.Check, contentDescription = "")
    }, modifier = Modifier.wrapContentWidth()
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