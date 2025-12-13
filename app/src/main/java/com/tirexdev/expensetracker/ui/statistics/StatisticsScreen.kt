package com.tirexdev.expensetracker.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.tirexdev.expensetracker.R
import com.tirexdev.expensetracker.ui.common.ErrorScreen
import com.tirexdev.expensetracker.ui.common.LoadingScreen
import com.tirexdev.expensetracker.util.CategoryConfig
import com.tirexdev.expensetracker.util.getLocalizedName
import com.tirexdev.expensetracker.util.toCategory

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is StatisticsUiState.Loading -> LoadingScreen()
        is StatisticsUiState.Error -> ErrorScreen(
            message = state.message,
            onRetry = viewModel::retry
        )
        is StatisticsUiState.Success -> StatisticsContent(statistics = state.statistics)
    }
}

@Composable
private fun StatisticsContent(
    statistics: com.tirexdev.expensetracker.domain.model.ExpenseStatistics
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.statistics_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        if (statistics.expensesByCategory.isNotEmpty()) {
            item {
                CategoryChart(expensesByCategory = statistics.expensesByCategory)
            }
            item {
                Text(
                    text = stringResource(R.string.statistics_by_category),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            items(statistics.expensesByCategory.entries.sortedByDescending { it.value }) { (category, amount) ->
                CategoryRow(category = category, amount = amount)
            }
        }
    }
}

@Composable
private fun CategoryChart(expensesByCategory: Map<String, Double>) {
    if (expensesByCategory.isEmpty()) {
        Card(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.statistics_no_data))
            }
        }
        return
    }

    val slices = expensesByCategory.entries.map { (category, amount) ->
        val categoryEnum = category.toCategory()
        val color = categoryEnum?.let { CategoryConfig.getVisuals(it).color } ?: Color.Gray

        PieChartData.Slice(
            label = "",
            value = amount.toFloat(),
            color = color
        )
    }

    val pieChartConfig = PieChartConfig(
        labelVisible = false,
        strokeWidth = 75f,
        backgroundColor = Color.Transparent,
        activeSliceAlpha = 0.9f,
        isAnimationEnable = true,
        isSumVisible = false,
        chartPadding = 25
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    modifier = Modifier.fillMaxSize(),
                    pieChartData = PieChartData(slices, PlotType.Donut),
                    pieChartConfig = pieChartConfig
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.statistics_total_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "€%.0f".format(expensesByCategory.values.sum()),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            expensesByCategory.entries.map { (category, amount) ->
                val categoryEnum = category.toCategory()
                val color = categoryEnum?.let { CategoryConfig.getVisuals(it).color } ?: Color.Gray
                val displayName = categoryEnum?.getLocalizedName() ?: category

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(color, shape = MaterialTheme.shapes.small)
                        )
                        Text(displayName, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        text = "€%.2f".format(amount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(category: String, amount: Double) {
    val categoryEnum = category.toCategory()
    val categoryVisuals = categoryEnum?.let { CategoryConfig.getVisuals(it) }
    val displayName = categoryEnum?.getLocalizedName() ?: category

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(categoryVisuals?.color?.copy(alpha = 0.2f) ?: Color.Gray.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    categoryVisuals?.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = categoryVisuals.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = "€%.2f".format(amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

