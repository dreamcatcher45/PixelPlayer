package com.theveloper.pixelplay.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theveloper.pixelplay.data.model.Song
import com.theveloper.pixelplay.ui.theme.ExpTitleTypography
import com.theveloper.pixelplay.ui.theme.GoogleSansRounded
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AiPlaylistSheet(
    onDismiss: () -> Unit,
    onGenerateClick: (prompt: String, minLength: Int, maxLength: Int, manualJson: String?) -> Unit,
    isGenerating: Boolean,
    error: String?,
    availableSongs: List<Song> = emptyList()
) {
    var prompt by remember { mutableStateOf("") }
    var minLength by remember { mutableStateOf("5") }
    var maxLength by remember { mutableStateOf("15") }
    
    // Manual Mode State
    var isManualMode by remember { mutableStateOf(false) }
    var manualJsonInput by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val colors = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = colors.surfaceContainerHigh,
        unfocusedContainerColor = colors.surfaceContainerHigh,
        disabledContainerColor = colors.surfaceContainerHigh,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    )

    val smoothCornerShape = AbsoluteSmoothCornerShape(
        cornerRadiusTL = 16.dp,
        smoothnessAsPercentBL = 60,
        cornerRadiusTR = 16.dp,
        smoothnessAsPercentBR = 60,
        cornerRadiusBL = 16.dp,
        smoothnessAsPercentTL = 60,
        cornerRadiusBR = 16.dp,
        smoothnessAsPercentTR = 60
    )

    val promptFieldShape = AbsoluteSmoothCornerShape(
        cornerRadiusTL = 24.dp,
        smoothnessAsPercentBL = 60,
        cornerRadiusTR = 24.dp,
        smoothnessAsPercentBR = 60,
        cornerRadiusBL = 24.dp,
        smoothnessAsPercentTL = 60,
        cornerRadiusBR = 24.dp,
        smoothnessAsPercentTR = 60
    )

    // Animation for AI icon when generating
    val infiniteTransition = rememberInfiniteTransition(label = "ai_animation")
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Button press animation state
    var isPressed by remember { mutableStateOf(false) }
    
    // Animated scale for the button - shrinks when pressed, bounces back when released
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )
    
    // Animated corner radius - more squared when pressed
    val buttonCornerRadius by animateDpAsState(
        targetValue = if (isPressed || isGenerating) 24.dp else 50.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonCorner"
    )
    
    val buttonShape = AbsoluteSmoothCornerShape(
        cornerRadiusTL = buttonCornerRadius,
        smoothnessAsPercentBL = 60,
        cornerRadiusTR = buttonCornerRadius,
        smoothnessAsPercentBR = 60,
        cornerRadiusBL = buttonCornerRadius,
        smoothnessAsPercentTL = 60,
        cornerRadiusBR = buttonCornerRadius,
        smoothnessAsPercentTR = 60
    )

    // Helper functions for manual mode
    fun constructPromptData(): String {
        val minLengthInt = minLength.toIntOrNull() ?: 5
        val maxLengthInt = maxLength.toIntOrNull() ?: 15
        
        return """
PLAYLIST GENERATION REQUEST
==========================

SETTINGS:
- Desired Playlist Size: $minLengthInt to $maxLengthInt songs
- Mood/Vibe: ${if (prompt.isBlank()) "Any" else prompt}

INSTRUCTIONS FOR AI:
1. Analyze the user's mood and settings above.
2. Select songs from the provided available songs that best match the mood and constraints.
3. Return ONLY a valid JSON array of song IDs.

EXPECTED RESPONSE FORMAT:
["song_id_1", "song_id_2", "song_id_3", ...]

===========================
        """.trimIndent()
    }

    fun constructAvailableSongsJson(): String {
        if (availableSongs.isEmpty()) return ""

        val sample = availableSongs.take(80)
        val items = sample.joinToString(separator = ",\n") { song ->
            val title = song.title.replace("\"", "'")
            val artist = song.displayArtist.replace("\"", "'")
            val genre = (song.genre ?: "unknown").replace("\"", "'")
            """
                {
                    "id": "${song.id}",
                    "title": "$title",
                    "artist": "$artist",
                    "genre": "$genre",
                    "relevance_score": 0
                }
            """.trimIndent()
        }

        return "[\n$items\n]"
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with AI Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated AI Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .scale(if (isGenerating) iconScale else 1f)
                        .rotate(if (isGenerating) iconRotation else 0f)
                        .background(
                            color = colors.primaryContainer,
                            shape = AbsoluteSmoothCornerShape(
                                cornerRadiusTL = 20.dp,
                                smoothnessAsPercentBL = 60,
                                cornerRadiusTR = 20.dp,
                                smoothnessAsPercentBR = 60,
                                cornerRadiusBL = 20.dp,
                                smoothnessAsPercentTL = 60,
                                cornerRadiusBR = 20.dp,
                                smoothnessAsPercentTR = 60
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = colors.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI",
                        style = ExpTitleTypography.displayMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.primary
                    )
                    Text(
                        text = "Playlist Generator",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = GoogleSansRounded,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Description text
            Text(
                text = "Describe the vibe, mood, or activity and let AI curate the perfect playlist from your library.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            // Number inputs in styled container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = smoothCornerShape,
                color = colors.surfaceContainer,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Playlist size",
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = GoogleSansRounded,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = minLength,
                            onValueChange = { minLength = it.filter { char -> char.isDigit() } },
                            label = { Text("Min songs") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = smoothCornerShape,
                            colors = textFieldColors,
                        )
                        OutlinedTextField(
                            value = maxLength,
                            onValueChange = { maxLength = it.filter { char -> char.isDigit() } },
                            label = { Text("Max songs") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = smoothCornerShape,
                            colors = textFieldColors,
                        )
                    }
                }
            }

            // Prompt field
            OutlinedTextField(
                value = prompt,
                shape = promptFieldShape,
                colors = textFieldColors,
                onValueChange = { prompt = it },
                placeholder = { 
                    Text(
                        "e.g. Chill evening vibes, upbeat workout energy...",
                        color = colors.onSurfaceVariant.copy(alpha = 0.6f)
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                singleLine = false,
                minLines = 2,
                maxLines = 4
            )

            // Manual Mode Toggle
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = smoothCornerShape,
                color = colors.surfaceContainer,
                tonalElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isManualMode = !isManualMode }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = colors.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Manual Mode",
                            style = MaterialTheme.typography.titleSmall,
                            fontFamily = GoogleSansRounded,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Copy prompt data or paste AI response",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isManualMode,
                        onCheckedChange = { isManualMode = it }
                    )
                }
            }

            // Manual Mode Content
            AnimatedVisibility(
                visible = isManualMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceContainerHighest
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = smoothCornerShape
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Copy Data Button
                        Button(
                            onClick = {
                                val promptData = constructPromptData()
                                val songsJson = constructAvailableSongsJson()
                                val dataToCopy = if (songsJson.isNotBlank()) {
                                    "$promptData\n\nAVAILABLE_SONGS_JSON:\n$songsJson"
                                } else promptData

                                clipboardManager.setText(AnnotatedString(dataToCopy))
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.secondary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = smoothCornerShape
                        ) {
                            Icon(
                                Icons.Default.ContentCopy, 
                                contentDescription = null, 
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Copy Data to Clipboard",
                                fontFamily = GoogleSansRounded,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            text = "The data copied above is exactly what is sent to the AI. You can paste the AI's JSON response below to process it without an API call.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )

                        // Load Response Input
                        OutlinedTextField(
                            value = manualJsonInput,
                            onValueChange = { manualJsonInput = it },
                            label = { Text("Paste AI Response JSON") },
                            placeholder = { Text("{\"songs\": [...]}") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            shape = smoothCornerShape,
                            colors = textFieldColors
                        )
                    }
                }
            }

            // Error message
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = smoothCornerShape,
                    color = colors.errorContainer
                ) {
                    Text(
                        text = error ?: "",
                        color = colors.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Generate Button with bouncy animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp)
                    .scale(buttonScale)
                    .clip(buttonShape)
                    .background(
                        if ((prompt.isBlank() && !isManualMode) || (isManualMode && manualJsonInput.isBlank() && prompt.isBlank()))
                            colors.surfaceContainerHighest 
                        else 
                            colors.primaryContainer
                    )
                    .pointerInput(prompt, isGenerating, isManualMode, manualJsonInput) {
                        detectTapGestures(
                            onPress = {
                                val canGenerate = if (isManualMode) {
                                    manualJsonInput.isNotBlank() || prompt.isNotBlank()
                                } else {
                                    prompt.isNotBlank()
                                }
                                
                                if (canGenerate && !isGenerating) {
                                    isPressed = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    tryAwaitRelease()
                                    isPressed = false
                                    
                                    val minLengthInt = minLength.toIntOrNull() ?: 5
                                    val maxLengthInt = maxLength.toIntOrNull() ?: 15
                                    val manualResponse = if (isManualMode && manualJsonInput.isNotBlank()) 
                                        manualJsonInput 
                                    else 
                                        null
                                        
                                    onGenerateClick(prompt, minLengthInt, maxLengthInt, manualResponse)
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp,
                            color = colors.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Generating...",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = GoogleSansRounded,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onPrimaryContainer
                        )
                    } else {
                        val buttonEnabled = if (isManualMode) {
                            manualJsonInput.isNotBlank() || prompt.isNotBlank()
                        } else {
                            prompt.isNotBlank()
                        }
                        
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (buttonEnabled)
                                colors.onPrimaryContainer 
                            else 
                                colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isManualMode && manualJsonInput.isNotBlank())
                                "Process Manual Data" 
                            else 
                                "Generate Playlist",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = GoogleSansRounded,
                            fontWeight = FontWeight.SemiBold,
                            color = if (buttonEnabled)
                                colors.onPrimaryContainer 
                            else 
                                colors.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
