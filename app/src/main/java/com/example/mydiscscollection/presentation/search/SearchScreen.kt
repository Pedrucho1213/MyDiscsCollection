package com.example.mydiscscollection.presentation.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mydiscscollection.presentation.components.ArtistListItem
import com.example.mydiscscollection.presentation.components.EmptyState
import com.example.mydiscscollection.presentation.components.FeedbackStateType
import com.example.mydiscscollection.presentation.components.SearchTopBar
import com.example.mydiscscollection.presentation.components.SkeletonListItem

@Composable
fun SearchScreen(
    onArtistClick: (artistId: Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChanged = { newQuery ->
                    query = newQuery
                    viewModel.onQueryChanged(newQuery)
                },
                onClearQuery = {
                    query = ""
                    viewModel.onQueryChanged("")
                }
            )
        }
    ) { paddingValues ->

        when (val state = uiState) {
            is SearchUiState.Idle -> {
                EmptyState(
                    type = FeedbackStateType.Empty(
                        icon = Icons.Default.MusicNote,
                        title = "Discover Artists",
                        description = "Search for your favorite artists to explore their discography.",
                    ),
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is SearchUiState.Loading -> {
                SearchLoadingSkeleton(
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is SearchUiState.Results -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        Text(
                            text = "${state.totalItem} results",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }
                    items(
                        items = state.artists,
                        key = {artist -> artist.id}
                    ){ artist ->
                        ArtistListItem(
                            artist = artist,
                            onClick = { onArtistClick(artist.id) }
                        )
                    }
                }
            }
            is SearchUiState.Empty -> {
                EmptyState(
                    type = FeedbackStateType.Empty(
                        icon = Icons.Default.SearchOff,
                        title = "No artists found",
                        description = "We couldn't find any artists with the given search term."
                    ),
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is SearchUiState.Error -> {
                EmptyState(
                    type = FeedbackStateType.Error(
                        title = "Failed to load artists",
                        description = state.message,
                        onRetry = { viewModel.retry(query) },
                        icon = Icons.Default.SearchOff
                    ),
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

    }

}


@Composable
private fun SearchLoadingSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        repeat(15) {
            SkeletonListItem()
            HorizontalDivider()
        }
    }
}
