package pl.cincior.englishreminder.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint

@Composable
fun WordListItem(
    wordId: String,
    word: String,
    translation: String,
    memorizeChance: Float,
    onNavigateToWordDetails: (String) -> Unit,
    showTranslation: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = { onNavigateToWordDetails(wordId) })
            .background(MaterialTheme.colorScheme.onSurfaceVariant),
    ) {
        Row(

        ) {
            Column(
                modifier = Modifier
                    .weight(7f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = word,
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleMedium

                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (showTranslation) {
                        Text(
                            text = translation,
                            modifier = Modifier,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            Column(

            ) {
                if (memorizeChance != -1f) {
                    Text("Chance: $memorizeChance")
                }
            }
        }



    }
}