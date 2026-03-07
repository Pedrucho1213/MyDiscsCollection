package com.example.mydiscscollection.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

sealed class FeedbackStateType{
    data class Empty (
        val icon : ImageVector,
        val title: String,
        val description: String,
    ): FeedbackStateType()

    data class Error(
        val icon: ImageVector,
        val title: String,
        val description: String,
        val onRetry: () -> Unit
    ): FeedbackStateType()
}
@Composable
fun EmptyState(
  type: FeedbackStateType,
  modifier: Modifier = Modifier
){
    val (icon, title, description) = when(type){
        is FeedbackStateType.Empty -> Triple(type.icon, type.title, type.description)
        is FeedbackStateType.Error -> Triple(type.icon, type.title, type.description)
    }

    val iconContainerColor = when(type){
        is FeedbackStateType.Empty -> MaterialTheme.colorScheme.surfaceContainer
        is FeedbackStateType.Error -> MaterialTheme.colorScheme.errorContainer
    }
    val iconTint = when(type){
        is FeedbackStateType.Empty -> MaterialTheme.colorScheme.onSurfaceVariant
        is FeedbackStateType.Error -> MaterialTheme.colorScheme.onErrorContainer
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = iconContainerColor,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                tint = iconTint
            )
        }
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (type is FeedbackStateType.Error){
            Button(
                onClick = type.onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}


@Preview (showBackground = true)
@Composable
private fun ShowEmptyState(){
    FeedbackStateType.Empty(
        icon = Icons.AutoMirrored.Filled.NoteAdd,
        title = "No artists found",
        description = "We couldn't find any artists with the given search term."
    )
    FeedbackStateType.Error(
        icon = Icons.AutoMirrored.Filled.NoteAdd,
        title = "Failed to load artists",
        description = "Something went wrong while loading the artists.",
        onRetry = {}
    )
}