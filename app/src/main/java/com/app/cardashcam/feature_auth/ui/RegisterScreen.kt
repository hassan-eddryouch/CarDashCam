package com.app.cardashcam.feature_auth.ui

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.cardashcam.core.ui.components.*
import com.app.cardashcam.core.ui.components.inputs.NeonTextField
import com.app.cardashcam.core.ui.components.inputs.PasswordStrengthBar
import com.app.cardashcam.feature_auth.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(onBack: () -> Unit) {
    val vm: AuthViewModel = viewModel()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val error by vm.error.collectAsStateWithLifecycle()

    val nameValid = name.length >= 3
    val emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passValid = password.length >= 6
    val formValid = nameValid && emailValid && passValid

    AnimatedGradientBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Create Account",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(32.dp))

                    NeonTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            vm.clearError()
                        },
                        label = "Full Name"
                    )

                    Spacer(Modifier.height(16.dp))

                    NeonTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            vm.clearError()
                        },
                        label = "Email"
                    )

                    Spacer(Modifier.height(16.dp))

                    NeonTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            vm.clearError()
                        },
                        label = "Password",
                        isPassword = true
                    )

                    Spacer(Modifier.height(8.dp))
                    PasswordStrengthBar(password)

                    Spacer(Modifier.height(32.dp))

                    NeonButton(
                        text = "Register",
                        onClick = {
                            if (formValid) vm.register(name, email, password, onBack)
                        },
                        enabled = formValid
                    )

                    Spacer(Modifier.height(16.dp))

                    TextButton(onClick = onBack) {
                        Text("Back to Login", color = MaterialTheme.colorScheme.primary)
                    }

                    error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = Color.Red)
                    }
                }
            }
        }
    }
}
