package com.example.mydiscscollection.presentation.detail

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mydiscscollection.R
import com.example.mydiscscollection.domain.model.Artist
import com.example.mydiscscollection.domain.model.ArtistDetail
import com.example.mydiscscollection.presentation.components.BandMemberChip
import com.example.mydiscscollection.presentation.components.FeedbackState
import com.example.mydiscscollection.presentation.components.FeedbackStateType
import com.example.mydiscscollection.presentation.components.SkeletonArtistDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    onViewAlbumsClick: (artistId: Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ArtistsDetailViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            val title = (uiState as? ArtistDetailUiState.Success)
                ?.artistDetail?.name
                .orEmpty()
            TopAppBar(
                title = {Text(title)},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ArtistDetailUiState.Loading -> {
                SkeletonArtistDetail(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is ArtistDetailUiState.Success -> {
                ArtistDetailContent(
                    artist =  state.artistDetail,
                    onViewAlbumsClick = { onViewAlbumsClick(state.artistDetail.id)},
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                )
            }
            is ArtistDetailUiState.Error -> {
                FeedbackState(
                    type = FeedbackStateType.Error(
                        title = "Failed to load artist",
                        description = state.message,
                        icon = Icons.Default.Refresh,
                        onRetry = { viewModel.retry()},
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ArtistDetailContent(
    artist: ArtistDetail,
    onViewAlbumsClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = artist.imageUrl,
            contentDescription = artist.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            error = painterResource(R.drawable.person_24)
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = artist.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (artist.biography.isNotBlank()){
                Text(
                    text = artist.biography,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (artist.members.isNotEmpty()){
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Band Members",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    artist.members.forEach { member ->
                        BandMemberChip(member = member)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onViewAlbumsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null,
                    modifier = Modifier.size(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View Albums",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}