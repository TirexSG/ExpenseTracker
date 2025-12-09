package com.tirexdev.expensetracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddExpense) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
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
                text = "Home Screen",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Lista de gastos aquí",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = { onNavigateToEditExpense("test-id-123") }) {
                Text("Edit Expense (Test)")
            }
        }
    }
}