package com.tirexdev.expensetracker.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.ui.common.ErrorScreen
import com.tirexdev.expensetracker.ui.common.LoadingScreen
import com.tirexdev.expensetracker.ui.home.components.GroupedExpenseItem
import com.tirexdev.expensetracker.util.toCategory
import java.time.LocalDate

@Composable
fun HomeScreen(
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = { AddExpenseFab(onClick = onNavigateToAddExpense) }
    ) { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingScreen(modifier = Modifier.padding(padding))
            is HomeUiState.Success -> HomeContent(
                state = state,
                onCategorySelected = { category -> viewModel.selectCategory(category) },
                onExpenseClick = onNavigateToEditExpense,
                onDeleteExpense = viewModel::deleteExpense,
                modifier = Modifier.padding(padding)
            )

            is HomeUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun AddExpenseFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.5.dp),
        modifier = Modifier.border(
            1.5.dp,
            MaterialTheme.colorScheme.outline,
            RoundedCornerShape(16.dp)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_expense_description)
        )
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onCategorySelected: (Category?) -> Unit,
    onExpenseClick: (String) -> Unit,
    onDeleteExpense: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredExpenses = state.selectedCategory?.let { cat ->
        state.expenses.filter { it.category.toCategory() == cat }
    } ?: state.expenses

    val grouped = filteredExpenses
        .groupBy { it.date.toLocalDate() }
        .toSortedMap(compareByDescending { it })

    if (state.expenses.isEmpty()) {
        EmptyState(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SummaryCard(totalThisMonth = state.totalThisMonth) }
            item {
                CategoryHeader(
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }
            item {
                GroupedExpensesList(
                    grouped = grouped,
                    onExpenseClick = onExpenseClick,
                    onDeleteExpense = onDeleteExpense
                )
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
) {
    Column {
        CategoryFilterRow(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )
        Text(
            text = stringResource(R.string.home_recent_transactions),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun GroupedExpensesList(
    grouped: Map<LocalDate, List<Expense>>,
    onExpenseClick: (String) -> Unit,
    onDeleteExpense: (String) -> Unit,
) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    val groupedState = rememberUpdatedState(grouped)
    val dateExpenseList = remember(groupedState.value) { buildDateExpenseList(groupedState.value) }
    val animatedItems = remember { mutableStateListOf<Triple<LocalDate, Expense?, Boolean>>() }

    val onExpenseClickState = rememberUpdatedState(onExpenseClick)
    val onDeleteExpenseState = rememberUpdatedState(onDeleteExpense)

    LaunchedEffect(dateExpenseList) {
        syncAnimatedItems(animatedItems, dateExpenseList)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        animatedItems.forEach { (date, expense, visible) ->
            key(date to (expense?.id ?: "")) {
                GroupedExpenseItem(
                    date = date,
                    expense = expense,
                    visible = visible,
                    today = today,
                    yesterday = yesterday,
                    onExpenseClick = onExpenseClickState.value,
                    onDeleteExpense = onDeleteExpenseState.value
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    totalThisMonth: Double,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.primary
        ),
        start = Offset.Infinite.copy(x = Float.POSITIVE_INFINITY, y = 0f),
        end = Offset(0.5f, Float.POSITIVE_INFINITY)
    )
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(gradient)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                modifier = modifier
                    .padding(24.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.home_total_this_month),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.home_total_amount, totalThisMonth),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )

            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
) {
    val categories = listOf<Category?>(null) + Category.entries
    val shape = RoundedCornerShape(10.dp)

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            Surface(
                shape = shape,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                border = if (!isSelected) BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                ) else null,
                modifier = Modifier
                    .height(40.dp)
                    .clip(shape)
                    .clickable { onCategorySelected(category) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = if (category == null) stringResource(R.string.home_category_all) else stringResource(
                            category.displayNameRes
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.home_empty_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.home_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}