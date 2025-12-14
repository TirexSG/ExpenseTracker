package com.tirexdev.expensetracker.ui.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.domain.model.Expense
import com.tirexdev.expensetracker.ui.theme.DeleteBackgroundColor
import com.tirexdev.expensetracker.util.CategoryConfig
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

private val CardCornerShape = RoundedCornerShape(20.dp)
private val MaxSwipeDistance = 80.dp
private const val SwipeDeleteThreshold = 0.6f

@Composable
fun ExpenseCard(
    expense: Expense,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = runCatching { Category.valueOf(expense.category) }.getOrNull()
    val categoryVisuals = category?.let { CategoryConfig.getVisuals(it) }
    val swipeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val maxSwipePx = with(LocalDensity.current) { MaxSwipeDistance.toPx() }
    var isSwiped by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Background for swipe-to-delete
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CardCornerShape)
                .background(DeleteBackgroundColor),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete expense",
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 28.dp)
                    .size(28.dp)
            )
        }

        Card(
            modifier = Modifier
                .offset { IntOffset(swipeOffset.value.toInt(), 0) }
                .fillMaxWidth()
                // Swipe-to-delete: handles horizontal drag gesture, animates the card, and triggers onDelete if swiped past threshold
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (swipeOffset.value < -maxSwipePx * SwipeDeleteThreshold) {
                                scope.launch {
                                    swipeOffset.animateTo(-maxSwipePx, tween(180))
                                    isSwiped = true
                                    onDelete()
                                }
                            } else {
                                scope.launch {
                                    swipeOffset.animateTo(0f, tween(180))
                                }
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset =
                                (swipeOffset.value + dragAmount).coerceIn(-maxSwipePx, 0f)
                            scope.launch { swipeOffset.snapTo(newOffset) }
                        }
                    )
                }
                .clip(CardCornerShape)
                .clickable(enabled = !isSwiped, onClick = onClick),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = CardCornerShape,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                categoryVisuals?.color?.copy(alpha = 0.2f)
                                    ?: Color.Gray.copy(alpha = 0.2f)
                            ),
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = expense.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = expense.date.format(DateTimeFormatter.ofPattern("HH:mm")),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "%.2f€".format(expense.amount),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}