package com.tirexdev.expensetracker.ui.editor.components.category

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tirexdev.expensetracker.domain.model.Category
import com.tirexdev.expensetracker.util.CategoryConfig

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visuals = CategoryConfig.getVisuals(category)
    val shape = RoundedCornerShape(16.dp)
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.25f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(
                if (isSelected) visuals.color.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.secondary
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) visuals.color else MaterialTheme.colorScheme.outline,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = visuals.icon,
            contentDescription = null,
            tint = if (isSelected) visuals.color else MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .size(32.dp)
                .scale(iconScale)
        )
        Text(
            text = stringResource(category.displayNameRes),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) visuals.color else MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}