package com.tirexdev.expensetracker.ui.editor.components.input

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tirexdev.expensetracker.R

@Composable
fun EditableAmountField(
    amount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    val textState = rememberTextFieldState(amount)

    SyncTextState(amount, textState)

    val displayText = textState.text.toString().ifEmpty { "0" }
    val textWidth = rememberTextWidth(displayText)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.editor_amount_header),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
        )

        AmountAnimatedRow(
            displayText = displayText,
            textState = textState,
            textWidth = textWidth
        )

        ErrorMessage(errorMessage = errorMessage)
    }

    LaunchedEffect(textState.text) {
        val value = textState.text.toString()
        if (value != amount) {
            onAmountChange(value)
        }
    }
}

@Composable
private fun SyncTextState(
    amount: String,
    textState: TextFieldState
) {
    LaunchedEffect(amount) {
        if (textState.text.toString() != amount) {
            textState.edit {
                replace(0, length, amount)
            }
        }
    }
}

@Composable
private fun rememberTextWidth(text: String): Dp {
    val measurer = rememberTextMeasurer()
    val layout = measurer.measure(
        AnnotatedString(text),
        style = MaterialTheme.typography.displayLarge
    )
    return with(LocalDensity.current) {
        layout.size.width.toDp()
    }
}

@Composable
private fun AmountAnimatedRow(
    displayText: String,
    textState: TextFieldState,
    textWidth: Dp
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(tween(300)) { it / 2 } + fadeIn(tween(300)),
            exit = ExitTransition.None
        ) {
            Box {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                CompositionLocalProvider(
                    LocalTextSelectionColors provides TextSelectionColors(
                        handleColor = MaterialTheme.colorScheme.onPrimary,
                        backgroundColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    BasicTextField(
                        state = textState,
                        modifier = Modifier.width(textWidth),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        textStyle = MaterialTheme.typography.displayLarge.copy(
                            color = Color.Transparent
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary)
                    )
                }
            }
        }

        Spacer(Modifier.width(4.dp))

        Text(
            text = "€",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
private fun ErrorMessage(errorMessage: String?) {
    AnimatedContent(
        targetState = errorMessage,
        transitionSpec = {
            fadeIn(animationSpec = tween(150)) + slideInVertically(
                initialOffsetY = { -20 },
                animationSpec = tween(150)
            ) togetherWith fadeOut(animationSpec = tween(150))
        },
        label = "errorAnimation"
    ) { error ->
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}