package com.example.mydiscscollection.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipRow(
    availableYears: List<Int>,
    availableGenres: List<String>,
    availableLabels: List<String>,
    activeYear: Int?,
    activeGenre: String?,
    activeLabel: String?,
    onYearSelected: (Int?) -> Unit,
    onGenreSelected: (String?) -> Unit,
    onLabelSelected: (String?) -> Unit,
) {
    var openSheet by remember { mutableStateOf<FilterType?>(null) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)

    ) {
        // Chip Year
        FilterChip(
            selected = activeYear != null,
            onClick = { openSheet = FilterType.YEAR },
            label = { Text(activeYear?.toString() ?: "Year") },
            trailingIcon = if (activeYear != null) {
                { Icon(Icons.Default.Close, null, Modifier.clickable { onYearSelected(null) }) }
            } else null
        )

        // Chip Genre
        FilterChip(
            selected = activeGenre != null,
            onClick = { openSheet = FilterType.GENRE },
            label = { Text(activeGenre ?: "GENRE") },
            trailingIcon = if (activeGenre != null) {
                { Icon(Icons.Default.Close, null, Modifier.clickable { onLabelSelected(null) }) }
            } else null
        )

        // Chip Label
        FilterChip(
            selected = activeLabel != null,
            onClick = { openSheet = FilterType.LABEL },
            label = { Text(activeLabel ?: "LABEL") },
            trailingIcon = if (activeLabel != null) {
                { Icon(Icons.Default.Close, null, Modifier.clickable { onLabelSelected(null) }) }
            } else null
        )

    }
    openSheet?.let { filterType ->
        ModalBottomSheet(onDismissRequest = {openSheet = null}) {
            when (filterType) {
                FilterType.YEAR -> FilterBottomSheetContent(
                    title = "Filter by year",
                    options = availableYears.map { it.toString() },
                    selectedOption = activeYear?.toString(),
                    onOptionSelected = { year ->
                        onYearSelected(year?.toIntOrNull())
                        openSheet = null
                    }
                )
                FilterType.GENRE -> FilterBottomSheetContent(
                    title = "Filter by genre",
                    options = availableGenres,
                    selectedOption = activeGenre,
                    onOptionSelected = { genre ->
                        onGenreSelected(genre)
                        openSheet = null
                    }
                )
                FilterType.LABEL -> FilterBottomSheetContent(
                    title = "Filter by label",
                    options = availableLabels,
                    selectedOption = activeLabel,
                    onOptionSelected = { label ->
                        onLabelSelected(label)
                        openSheet = null
                    }
                )
            }

        }
    }
}

enum class FilterType { YEAR, GENRE, LABEL }
