#!/bin/bash
./gradlew test jacocoTestReport && google-chrome */build/reports/jacoco/test/html/index.html */build/reports/tests/test/index.html
