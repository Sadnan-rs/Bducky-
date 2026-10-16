package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MacroCategory
import com.example.model.MacroDifficulty
import com.example.model.MacroItem
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextWhite
import com.example.viewmodel.BduckyUiState

@Composable
fun LibraryTab(
    state: BduckyUiState,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (MacroCategory) -> Unit,
    onPlatformChange: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onLoadInEditor: (String) -> Unit,
    onExecuteMacro: (MacroItem, Context) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val filteredList = state.libraryMacros.filter { macro ->
        val matchesSearch = state.librarySearchQuery.isBlank() ||
                macro.name.contains(state.librarySearchQuery, ignoreCase = true) ||
                macro.description.contains(state.librarySearchQuery, ignoreCase = true)
        val matchesCategory = state.libraryCategoryFilter == MacroCategory.ALL ||
                macro.category == state.libraryCategoryFilter
        val matchesPlatform = state.libraryPlatformFilter == "ALL" ||
                macro.platform == "ALL" ||
                macro.platform.contains(state.libraryPlatformFilter, ignoreCase = true)

        matchesSearch && matchesCategory && matchesPlatform
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value = state.librarySearchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search macros, tools, text expanders...", fontSize = 13.sp, color = Color(0xFF64748B)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = CyanNeon)
                    },
                    trailingIcon = {
                        if (state.librarySearchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSlate)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("library_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard
                    ),
                    singleLine = true
                )
            }

            // Platform & Category filter chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Platform Chips
                    val scrollStatePlatform = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollStatePlatform),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "WIN", "LNX", "MAC").forEach { plat ->
                            val isSelected = state.libraryPlatformFilter == plat
                            FilterChip(
                                selected = isSelected,
                                onClick = { onPlatformChange(plat) },
                                label = { Text(plat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanNeon,
                                    selectedLabelColor = DarkBg,
                                    containerColor = DarkCard,
                                    labelColor = TextWhite
                                ),
                                border = BorderStroke(1.dp, if (isSelected) CyanNeon else DarkBorder),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // Category Chips
                    val scrollStateCategory = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollStateCategory),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            MacroCategory.ALL,
                            MacroCategory.PRODUCTIVITY,
                            MacroCategory.MEDIA,
                            MacroCategory.SYSTEM,
                            MacroCategory.CUSTOM
                        ).forEach { cat ->
                            val isSelected = state.libraryCategoryFilter == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategoryChange(cat) },
                                label = { Text(cat.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0E2843),
                                    selectedLabelColor = CyanNeon,
                                    containerColor = DarkCard,
                                    labelColor = TextSlate
                                ),
                                border = BorderStroke(1.dp, if (isSelected) CyanNeon else DarkBorder),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // List of Payload/Macro Cards
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No macros match your search.\nTry another keyword or filter chip.",
                            fontSize = 13.sp,
                            color = TextSlate,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { macro ->
                    LibraryMacroCard(
                        macro = macro,
                        onToggleFavorite = { onToggleFavorite(macro.id) },
                        onLoadInEditor = { onLoadInEditor(macro.code) },
                        onExecute = { onExecuteMacro(macro, context) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryMacroCard(
    macro: MacroItem,
    onToggleFavorite: () -> Unit,
    onLoadInEditor: () -> Unit,
    onExecute: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("macro_card_${macro.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Name + Badges + Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = macro.name,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Difficulty / Risk Badge
                    DifficultyBadge(macro.difficulty)

                    Spacer(modifier = Modifier.width(6.dp))

                    // Platform chip
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0F1B2F),
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Text(
                            text = macro.platform,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyanNeon,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (macro.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (macro.isFavorite) StatusAmber else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = macro.description,
                fontSize = 12.sp,
                color = TextSlate,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Code Preview Snippet
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0A101C),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = macro.code.lines().take(2).joinToString("\n"),
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(8.dp),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onExecute,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = DarkBg),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "RUN DIRECTLY", fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                }

                OutlinedButton(
                    onClick = onLoadInEditor,
                    border = BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(0.8f)
                        .height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "EDIT", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: MacroDifficulty) {
    val (bgColor, textColor) = when (difficulty) {
        MacroDifficulty.EASY -> Color(0xFF064E3B) to StatusGreen
        MacroDifficulty.MEDIUM -> Color(0xFF451A03) to StatusAmber
        MacroDifficulty.ADVANCED -> Color(0xFF4C0519) to StatusRed
        MacroDifficulty.CRITICAL -> Color(0xFF4C0519) to Color(0xFFFB7185)
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        Text(
            text = difficulty.label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}
