package be.digilution.passwordgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.digilution.passwordgenerator.ui.theme.PasswordGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordGeneratorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PasswordGeneratorLayout()
                }
            }
        }
    }
}

@Composable
fun PasswordGeneratorLayout() {
    var upperCase by remember { mutableStateOf(false) }
    var lowerCase by remember { mutableStateOf(true) } 
    var numbers by remember { mutableStateOf(false) }
    var specialChars by remember { mutableStateOf(false) }
    var minNumericChars by remember { mutableStateOf("1") }
    var minSpecialChars by remember { mutableStateOf("1") }
    var numCharacters by remember { mutableStateOf("8") }
    var password by remember { mutableStateOf("") }

    // Validate input
    val numCharactersInt = numCharacters.toIntOrNull()
    val minNumericCharsInt = minNumericChars.toIntOrNull()
    val minSpecialCharsInt = minSpecialChars.toIntOrNull()
    val isNumCharactersValid = numCharactersInt != null && numCharactersInt in 2..100
    val isMinCharsSumValid = minNumericCharsInt != null && minSpecialCharsInt != null && minNumericCharsInt + minSpecialCharsInt <= numCharactersInt!! - 2
    val isInputValid = isNumCharactersValid && isMinCharsSumValid

    // If all character sets are disabled, enable lowercase letters
    if (!upperCase && !lowerCase && !numbers && !specialChars) {
        lowerCase = true
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldOption(label = "Karacters", value = numCharacters, onValueChange = { numCharacters = it })
        SwitchOption(text = "A-Z", checked = upperCase, onCheckedChange = { upperCase = it })
        SwitchOption(text = "a-z", checked = lowerCase, onCheckedChange = { lowerCase = it })
        SwitchOption(text = "0-9", checked = numbers, onCheckedChange = { numbers = it })
        SwitchOption(text = "!@#$^&*", checked = specialChars, onCheckedChange = { specialChars = it })
        TextFieldOption(label = "Minimum aantal numerieke karakters", value = minNumericChars, onValueChange = { minNumericChars = it })
        TextFieldOption(label = "Minimaal aantal speciale karakters", value = minSpecialChars, onValueChange = { minSpecialChars = it })
        Button(
            onClick = {
                password = generatePassword(
                    length = numCharacters.toInt(),
                    useUpperCase = upperCase,
                    useLowerCase = lowerCase,
                    useNumbers = numbers,
                    useSpecialChars = specialChars,
                    minNumericChars = minNumericChars.toInt(),
                    minSpecialChars = minSpecialChars.toInt()
                )
            },
            // Enable the button only if the input is valid
            enabled = isInputValid 
        ) {
            Text(text = "Genereer wachtwoord")
        }

        // Display the generated password
        Text(text = password)
    }
}

@Composable
fun SwitchOption(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun TextFieldOption(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

fun generatePassword(
    length: Int,
    useUpperCase: Boolean,
    useLowerCase: Boolean,
    useNumbers: Boolean,
    useSpecialChars: Boolean,
    minNumericChars: Int,
    minSpecialChars: Int
): String {
    val upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lowerCaseChars = "abcdefghijklmnopqrstuvwxyz"
    val numberChars = "0123456789"
    val specialChars = "!@#$%^&*"

    var charPool = ""
    if (useUpperCase) charPool += upperCaseChars
    if (useLowerCase) charPool += lowerCaseChars
    if (useNumbers) charPool += numberChars
    if (useSpecialChars) charPool += specialChars

    // Ensure minimum number of numeric and special characters
    val passwordChars = mutableListOf<Char>()
    if (useNumbers) {
        passwordChars += (1..minNumericChars).map { numberChars.random() }
    }
    if (useSpecialChars) {
        passwordChars += (1..minSpecialChars).map { specialChars.random() }
    }
    passwordChars += (1..(length - passwordChars.size)).map { charPool.random() }

    // Shuffle the characters to make the password random
    passwordChars.shuffle()

    return passwordChars.joinToString("")
}

@Preview(showBackground = true)
@Composable
fun PasswordGeneratorPreview() {
    PasswordGeneratorTheme {
        PasswordGeneratorLayout()
    }
}