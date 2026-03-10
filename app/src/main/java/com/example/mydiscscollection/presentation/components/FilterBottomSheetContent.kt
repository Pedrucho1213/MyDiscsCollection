package com.example.mydiscscollection.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FilterBottomSheetContent(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier.padding(bottom = 32.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text("All") },
            leadingContent = {
                RadioButton(
                    selected = selectedOption == null,
                    onClick = { onOptionSelected(null) })
            },
            modifier = Modifier.clickable { onOptionSelected(null) }
        )
        options.forEach { options ->
            ListItem(
                headlineContent = { Text(options) },
                leadingContent = {
                    RadioButton(
                        selected = options == selectedOption,
                        onClick = { onOptionSelected(options) }
                    )
                },
                modifier = Modifier.clickable { onOptionSelected(options) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowFilterBottomSheet(){
    FilterBottomSheetContent(
        title = "Year",
        options = listOf("2023", "2022", "2021"),
        selectedOption = "2023",
        onOptionSelected = {}
    )
}