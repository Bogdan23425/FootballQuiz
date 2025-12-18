<h1 align="center">Football Quiz</h1>
<p align="center">
  Футбольный квиз на Kotlin + Jetpack Compose с библиотекой терминов, таймером и статистикой.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Compose-Material%203-2E7D32?logo=android&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Min%20SDK-27-3DDC84?logo=android&logoColor=white" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-3DDC84?logo=android&logoColor=white" alt="Target SDK" />
  <img src="https://img.shields.io/badge/Gradle-8.13-02303A?logo=gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/AGP-8.13.1-3DDC84?logo=android&logoColor=white" alt="AGP" />
</p>

## Что внутри
- 25 вопросов на квиз, случайная выборка из большой базы.
- 3 жизни, таймер на вопрос, очки за правильные ответы.
- Итог по очкам (мало/средне/много) и по жизням.
- Музыка в меню и в квизе + SFX ответов.
- Библиотека терминов для подготовки.
- Статистика попыток и лучший результат по темам.
- Настройки музыки и эффектов с сохранением в SharedPreferences.

## Экраны
- Главная: быстрый вход в квиз, библиотеку, статистику и настройки.
- Выбор квиза: темы или микс из всех.
- Квиз: таймер, очки, жизни, подсветка ответа.
- Итоги: оценка по очкам + по жизням.
- Библиотека: поиск и фильтрация по темам.
- Настройки: музыка, эффекты и громкость.

## Сборка
Открой проект в Android Studio и дождись синхронизации Gradle.

CLI:
```bash
./gradlew :app:assembleDebug
```

Release c минификацией:
```bash
./gradlew :app:assembleRelease
```

## Структура
- `app/src/main/java/com/example/footballquiz` — экраны, навигация, логика квиза.
- `app/src/main/res/raw` — музыка и звуковые эффекты.
- `app/src/main/res/xml` — правила бэкапа.

## Требования
- Kotlin 2.1.0
- Jetpack Compose (Material 3)
- Min SDK 27 / Target & Compile SDK 36
- Gradle 8.13, Android Gradle Plugin 8.13.1
- Java 17
