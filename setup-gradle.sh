#!/bin/bash
# Скрипт для настройки Gradle wrapper

# Скачиваем Gradle wrapper
wget https://github.com/gradle/gradle/raw/master/gradle/wrapper/gradle-wrapper.jar -O gradle/wrapper/gradle-wrapper.jar

# Устанавливаем права
chmod +x gradlew
