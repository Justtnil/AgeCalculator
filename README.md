#  Age Calculator  возраст 🎂

![Platform](https://img.shields.io/badge/platform-Android-brightgreen.svg)
![Language](https://img.shields.io/badge/language-Kotlin-blue.svg)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)
![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)

A modern, multi-functional Age Calculator application for Android, built entirely with Kotlin and Jetpack Compose. This app provides a sleek, intuitive, and user-friendly interface for all your age-related calculations, featuring Material You theming and smooth animations.



---

## ✨ Features

This app is more than just a simple age calculator. It's packed with features to handle various date and age-related queries.

* **Clean, Modern UI:** Built with Material 3 design principles for a beautiful and intuitive user experience.
* **Dynamic Theming:** Automatically adapts its color scheme to the user's wallpaper on Android 12+ (Material You).
* **Adaptive Layout:** The UI gracefully adjusts to fit different screen sizes, from standard phones to large tablets.
* **Smooth Animations:** All results and screen transitions are animated for a polished, fluid feel.
* **User-Friendly Input:** An auto-formatting text field allows users to input dates as a continuous string of numbers (`ddmmyyyy`), which are displayed with slashes for readability.
* **Multiple Calculation Modes:**
    * 📅 **Age from Today:** The classic age calculator based on the current date.
    * 🗓️ **Age at a Specific Date:** Find out how old someone was on a particular historical date.
    * 🎂 **Date from a Specific Age:** Calculate the exact future date when someone will reach a certain age (e.g., when they will be 50 years, 5 months, and 5 days old).
* **Robust Error Handling:** Provides clear, user-friendly error messages for invalid inputs (e.g., future dates, incorrect formats).
* **Back Gesture Support:** Proper navigation handling ensures the back gesture works as expected, returning to the home screen instead of closing the app.

---

## 📱 Screenshots

<table align="center">
  <tr>
    <td align="center"><strong>Home Screen</strong></td>
    <td align="center"><strong>Age at Date</strong></td>
    <td align="center"><strong>Date from Age</strong></td>
  </tr>
  <tr>
    <td><img src="https://i.imgur.com/gK9pZ7e.png" width="250"/></td>
    <td><img src="https://i.imgur.com/L4R1q2W.png" width="250"/></td>
    <td><img src="https://i.imgur.com/9C3r3bJ.png" width="250"/></td>
  </tr>
</table>

---

## 🛠️ Tech Stack & Libraries

This project leverages the latest in Android development to provide a modern and maintainable codebase.

* **Kotlin:** The official programming language for Android App Development.
* **Jetpack Compose:** Android’s modern toolkit for building native UI declaratively.
* **Material 3:** The latest version of Google's design system, providing components and guidelines.
* **Compose State Management:** Uses `mutableStateOf` and `remember` for efficient and predictable UI updates.
* **java.time library:** For robust and accurate date and time calculations.
* **Compose Navigation:** A simple, state-based navigation system to switch between different calculator screens.
* **Android Studio:** The official IDE for Android development.

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* Android Studio Iguana | 2023.2.1 or later.
* An Android device or emulator running API level 26 (Oreo) or higher.

### Installation

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/your-username/age-calculator.git](https://github.com/your-username/age-calculator.git)
    ```
2.  **Open in Android Studio:**
    * Open Android Studio.
    * Click on "Open" and navigate to the cloned project directory.
    * Let Android Studio sync the project and download the required Gradle dependencies.

3.  **Run the app:**
    * Select your preferred device or emulator from the dropdown menu.
    * Click the "Run" button (▶️).

---

## 📜 License

This project is distributed under the MIT License. See `LICENSE.txt` for more information.
