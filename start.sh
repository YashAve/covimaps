#!/bin/bash
./gradlew uninstallDebug
./gradlew installDebug
adb shell am start -n com.covid.covimaps/.ui.activity.StatisticsActivity
