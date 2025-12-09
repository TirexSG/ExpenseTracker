package com.tirexdev.expensetracker.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEditorScreen(
    expenseId: String?,
    onNavigateBack: () -> Unit
) {
    val isEditMode = expenseId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (isEditMode) "Edit Expense" else "New Expense")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = if (isEditMode) "Editing expense: $expenseId" else "Creating new expense",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Formulario aquí",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
