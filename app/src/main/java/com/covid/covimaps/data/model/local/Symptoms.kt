package com.covid.covimaps.data.model.local

enum class Symptoms(val symptom: String) {
    FEVER("Do you have a fever or chills?"),
    COUGH("Are you experiencing a persistent cough?"),
    SHORTNESS_OF_BREATH("Are you feeling shortness of breath or difficulty breathing?"),
    TASTE_OR_SMELL("Have you noticed a loss of taste or smell?"),
    MUSCLE_ACHE("Do you have muscle or body aches?"),
    SORE_THROAT("Are you experiencing a sore throat?"),
    HEADACHE("Do you have a headache?"),
    FATIGUE("Are you experiencing fatigue?"),
    NAUSEA("Have you had nausea or vomiting?"),
    DIARRHEA("Are you experiencing diarrhea?")
}

enum class CovidSymptoms(val symptom: String) {
    CLOSE_CONTACT("Have you been in close contact with someone who has tested positive for COVID-19 in the last 14 days?"),
    TRAVELLED("Have you recently traveled to an area with a high number of COVID-19 cases?"),
    EVER_TESTED("Have you tested positive for COVID-19 before?"),
    EXISTING_DISEASE("Do you have any pre-existing medical conditions?"),
    VACCINATED_OR_NOT("Have you been vaccinated against COVID-19?"),
    EVER_TESTED_POSITIVE("Have you ever been tested positive?")
}