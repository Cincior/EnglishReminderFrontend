package pl.cincior.englishreminder.core.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WordListItem(
    wordId: String,
    word: String,
    translation: String,
    memorizeChance: Float,
    onNavigateToWordDetails: (String) -> Unit,
    showTranslation: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToWordDetails(wordId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleMedium
                )
                if (showTranslation) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = translation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            MemorizeChanceIndicator(
                memorizeChance = memorizeChance
            )
        }
    }
}

@Composable
private fun MemorizeChanceIndicator(
    memorizeChance: Float,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(start = 8.dp)
    ) {
        if (memorizeChance != -1f) {
            val progressValue = if (memorizeChance > 1f) (memorizeChance / 100f).coerceIn(0f, 1f) else memorizeChance.coerceIn(0f, 1f)
            var targetProgress by remember { mutableFloatStateOf(0f) }

            LaunchedEffect(progressValue) {
                targetProgress = progressValue
            }

            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(durationMillis = 1000),
                label = "memorizeChanceAnimation"
            )

            Text(
                text = "Chance: $memorizeChance",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(36.dp),
                strokeWidth = 3.5.dp
            )
        } else {
            Text(
                text = "Chance unknown",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
