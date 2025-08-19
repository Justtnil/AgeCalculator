package com.example.agecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agecalculator.ui.theme.AgeCalculatorTheme
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Enum to manage which screen is currently visible
enum class Screen {
    Home,
    AgeAtDate,
    DateFromAge
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgeCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AgeCalculatorApp()
                }
            }
        }
    }
}

@Composable
fun AgeCalculatorApp() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    when (currentScreen) {
        Screen.Home -> HomeScreen(
            onNavigateToAgeAtDate = { currentScreen = Screen.AgeAtDate },
            onNavigateToDateFromAge = { currentScreen = Screen.DateFromAge }
        )
        Screen.AgeAtDate -> AgeAtDateScreen(
            onNavigateBack = { currentScreen = Screen.Home }
        )
        Screen.DateFromAge -> DateFromAgeScreen(
            onNavigateBack = { currentScreen = Screen.Home }
        )
    }
}

// --- Screen 1: Home Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToAgeAtDate: () -> Unit, onNavigateToDateFromAge: () -> Unit) {
    var dobInput by remember { mutableStateOf("") }
    var calculatedAge by remember { mutableStateOf<Age?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Age Calculator") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Calculate Age from Today", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
            DateInputField(
                value = dobInput,
                onValueChange = { dobInput = it },
                label = "Enter Date of Birth"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    calculatedAge = null
                    errorMessage = null
                    val ageResult = calculateAgeFromToday(dobInput)
                    if (ageResult != null) {
                        calculatedAge = ageResult
                    } else {
                        errorMessage = "Invalid date format."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate Age")
            }
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedVisibility(visible = calculatedAge != null) {
                calculatedAge?.let { AgeResultText(it) }
            }
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { ErrorText(it) }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 32.dp))
            Text("Other Options", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onNavigateToAgeAtDate, modifier = Modifier.fillMaxWidth()) {
                Text("Check Age at a Specific Date")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onNavigateToDateFromAge, modifier = Modifier.fillMaxWidth()) {
                Text("Find Date from a Specific Age")
            }
        }
    }
}

// --- Screen 2: Age at a Specific Date Calculator ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeAtDateScreen(onNavigateBack: () -> Unit) {
    var dobInput by remember { mutableStateOf("") }
    var targetDateInput by remember { mutableStateOf("") }
    var calculatedAge by remember { mutableStateOf<Age?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // CHANGE 1: Intercept the back gesture on this screen
    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Age at Specific Date") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DateInputField(value = dobInput, onValueChange = { dobInput = it }, label = "Date of Birth")
            Spacer(modifier = Modifier.height(8.dp))
            DateInputField(value = targetDateInput, onValueChange = { targetDateInput = it }, label = "Calculate Age at this Date")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                calculatedAge = null
                errorMessage = null
                val result = calculateAgeAtDate(dobInput, targetDateInput)
                result.onSuccess { calculatedAge = it }
                result.onFailure { errorMessage = it.message }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Calculate")
            }
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedVisibility(visible = calculatedAge != null) {
                calculatedAge?.let { AgeResultText(it) }
            }
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { ErrorText(it) }
            }
        }
    }
}

// --- Screen 3: Date from a Specific Age Calculator ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DateFromAgeScreen(onNavigateBack: () -> Unit) {
    var dobInput by remember { mutableStateOf("") }
    var yearsInput by remember { mutableStateOf("") }
    var monthsInput by remember { mutableStateOf("") }
    var daysInput by remember { mutableStateOf("") }
    var futureDate by remember { mutableStateOf<LocalDate?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // CHANGE 2: Intercept the back gesture on this screen too
    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Date from Specific Age") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DateInputField(value = dobInput, onValueChange = { dobInput = it }, label = "Starting from Date of Birth")
            Spacer(modifier = Modifier.height(8.dp))
            Text("When I will be...", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = yearsInput,
                    onValueChange = { yearsInput = it.filter(Char::isDigit) },
                    label = { Text("Years") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = monthsInput,
                    onValueChange = { monthsInput = it.filter(Char::isDigit) },
                    label = { Text("Months") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = daysInput,
                    onValueChange = { daysInput = it.filter(Char::isDigit) },
                    label = { Text("Days") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                keyboardController?.hide()
                futureDate = null
                errorMessage = null
                val result = calculateDateFromAge(dobInput, yearsInput, monthsInput, daysInput)
                result.onSuccess { futureDate = it }
                result.onFailure { errorMessage = it.message }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Calculate Date")
            }
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedVisibility(visible = futureDate != null) {
                futureDate?.let {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("The date will be", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { ErrorText(it) }
            }
        }
    }
}

// --- Reusable Components, Logic, and Data Classes (No Changes Below) ---

@Composable
fun DateInputField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= 8) {
                onValueChange(it.filter(Char::isDigit))
            }
        },
        label = { Text(label) },
        placeholder = { Text("DD/MM/YYYY") },
        singleLine = true,
        visualTransformation = DateVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AgeResultText(age: Age) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Calculated Age Is", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${age.years} Years, ${age.months} Months, ${age.days} Days",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(top = 16.dp),
        textAlign = TextAlign.Center
    )
}

data class Age(val years: Int, val months: Int, val days: Int)

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3) {
                out += "/"
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

fun parseDate(dateString: String): LocalDate? {
    if (dateString.length != 8) return null
    val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
    return try {
        LocalDate.parse(dateString, formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}

fun calculateAgeFromToday(dobString: String): Age? {
    val birthDate = parseDate(dobString) ?: return null
    val currentDate = LocalDate.now()
    if (birthDate.isAfter(currentDate)) return null
    val period = Period.between(birthDate, currentDate)
    return Age(period.years, period.months, period.days)
}

fun calculateAgeAtDate(dobString: String, targetDateString: String): Result<Age> {
    val birthDate = parseDate(dobString) ?: return Result.failure(Exception("Invalid Date of Birth format."))
    val targetDate = parseDate(targetDateString) ?: return Result.failure(Exception("Invalid Target Date format."))
    if (targetDate.isBefore(birthDate)) {
        return Result.failure(Exception("This child doesn't EXIST yet."))
    }
    val period = Period.between(birthDate, targetDate)
    return Result.success(Age(period.years, period.months, period.days))
}

fun calculateDateFromAge(dobString: String, years: String, months: String, days: String): Result<LocalDate> {
    val birthDate = parseDate(dobString) ?: return Result.failure(Exception("Invalid Date of Birth format."))
    val yearsToAdd = years.toLongOrNull() ?: 0
    val monthsToAdd = months.toLongOrNull() ?: 0
    val daysToAdd = days.toLongOrNull() ?: 0
    if (yearsToAdd < 0 || monthsToAdd < 0 || daysToAdd < 0) {
        return Result.failure(Exception("Age values cannot be negative."))
    }
    val futureDate = birthDate.plusYears(yearsToAdd).plusMonths(monthsToAdd).plusDays(daysToAdd)
    return Result.success(futureDate)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AgeCalculatorTheme {
        AgeCalculatorApp()
    }
}