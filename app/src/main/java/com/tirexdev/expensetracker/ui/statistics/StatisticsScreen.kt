package com.tirexdev.expensetracker.ui.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.tirexdev.expensetracker.domain.model.ExpenseStatistics
import com.tirexdev.expensetracker.ui.common.ErrorScreen
import com.tirexdev.expensetracker.ui.common.LoadingScreen
import com.tirexdev.expensetracker.util.CategoryConfig
import com.tirexdev.expensetracker.util.getLocalizedName
import com.tirexdev.expensetracker.util.toCategory

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        when (val state = uiState) {
            is StatisticsUiState.Loading -> LoadingScreen(modifier = Modifier.padding(padding))
            is StatisticsUiState.Error -> ErrorScreen(
                message = state.message,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(padding)
            )

            is StatisticsUiState.Success -> StatisticsContent(
                statistics = state.statistics,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun StatisticsContent(
    statistics: ExpenseStatistics,
    modifier: Modifier = Modifier,
) {
    val total = statistics.expensesByCategory.values.sum()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.statistics_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            val sortedEntries = statistics.expensesByCategory.entries.sortedByDescending { it.value }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        sortedEntries.forEachIndexed { index, (category, amount) ->
                            val percentage = if (total > 0) (amount / total * 100).toInt() else 0
                            CategoryRow(
                                category = category,
                                amount = amount,
                                percentage = percentage
                            )
                            if (index < sortedEntries.lastIndex) {
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChart(expensesByCategory: Map<String, Double>) {
    if (expensesByCategory.isEmpty()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.statistics_no_data),
                    color = MaterialTheme.colorScheme.onPrimary
                )
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
        backgroundColor = MaterialTheme.colorScheme.primary,
        activeSliceAlpha = 0.9f,
        isAnimationEnable = true,
        isSumVisible = false,
        chartPadding = 25
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.statistics_total_format, expensesByCategory.values.sum()),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            CategoryLegendGrid(expensesByCategory = expensesByCategory)
        }
    }
}

@Composable
private fun CategoryLegendGrid(expensesByCategory: Map<String, Double>) {
    val entries = expensesByCategory.entries.toList()
    val chunkedEntries = entries.chunked(2)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 16.dp)) {
        chunkedEntries.forEach { rowEntries ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowEntries.forEach { (category, _) ->
                    val categoryEnum = category.toCategory()
                    val color =
                        categoryEnum?.let { CategoryConfig.getVisuals(it).color } ?: Color.Gray
                    val displayName = categoryEnum?.getLocalizedName() ?: category

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(color, shape = CircleShape)
                        )
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                if (rowEntries.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: String,
    amount: Double,
    percentage: Int,
) {
    val categoryEnum = category.toCategory()
    val categoryVisuals = categoryEnum?.let { CategoryConfig.getVisuals(it) }
    val displayName = categoryEnum?.getLocalizedName() ?: category
    val color = categoryVisuals?.color ?: Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            categoryVisuals?.icon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = percentage / 100f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(color)
                    )
                }

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Text(
            text = stringResource(R.string.statistics_amount_format, amount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}