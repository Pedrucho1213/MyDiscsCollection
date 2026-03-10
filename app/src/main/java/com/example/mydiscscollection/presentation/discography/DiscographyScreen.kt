package com.example.mydiscscollection.presentation.discography

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mydiscscollection.presentation.components.AlbumListItem
import com.example.mydiscscollection.presentation.components.FeedbackState
import com.example.mydiscscollection.presentation.components.FeedbackStateType
import com.example.mydiscscollection.presentation.components.FilterChipRow
import com.example.mydiscscollection.presentation.components.SkeletonAlbumItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscographyScreen(
    onBackClick: () -> Unit,
    viewModel: DiscographyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discography") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is DiscographyUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(6) { SkeletonAlbumItem() }
                }
            }

            is DiscographyUiState.Success -> {
                DiscographyContent(
                    state = state,
                    listState = listState,
                    onYearSelected = viewModel::applyYearFilter,
                    onGenreSelected = viewModel::applyGenreFilter,
                    onLabelSelected = viewModel::applyLabelFilter,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is DiscographyUiState.EmptyFilter -> {
                FeedbackState(
                    type = FeedbackStateType.Empty(
                        icon = Icons.Default.FilterListOff,
                        title = "No albums found",
                        description = "No releases match the selected filters"
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is DiscographyUiState.Error -> {
                FeedbackState(
                    type = FeedbackStateType.Error(
                        title = "Failed to load discography",
                        description = state.message,
                        icon = Icons.Default.Refresh,
                        onRetry = { viewModel.retry() },
                    )
                )
            }

        }
    }
}

@Composable
private fun DiscographyContent(
    state: DiscographyUiState.Success,
    listState: LazyListState,
    onYearSelected: (Int?) -> Unit,
    onGenreSelected: (String?) -> Unit,
    onLabelSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val releases = state.filteredReleases

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stickyHeader {
            Surface(color = MaterialTheme.colorScheme.surface) {
                FilterChipRow(
                    availableYears = state.availableYears,
                    availableGenres = state.availableGenres,
                    availableLabels = state.availableLabels,
                    activeYear = state.activeYear,
                    activeGenre = state.activeGenre,
                    activeLabel = state.activeLabel,
                    onYearSelected = onYearSelected,
                    onGenreSelected = onGenreSelected,
                    onLabelSelected = onLabelSelected,
                )
            }
        }
        item {
            Text(
                text = "${releases.size} albums",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (releases.isEmpty()) {
            item {
                FeedbackState(
                    type = FeedbackStateType.Empty(
                        icon = Icons.Default.FilterListOff,
                        title = "No albums found",
                        description = "No releases match the selected filters."
                    )
                )
            }
        } else {
            itemsIndexed(
                items = releases,
                key = { index, release ->
                    "${release.id}-${release.title}-${release.year ?: -1}-${release.role.orEmpty()}-$index"
                }
            ) { _, releases ->
                AlbumListItem(release = releases)
            }
        }
        if (state.loadingMore){
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
        if (!state.hasMore){
            item {
                Text(
                    text = "All ${state.totalItems} albums loaded",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
