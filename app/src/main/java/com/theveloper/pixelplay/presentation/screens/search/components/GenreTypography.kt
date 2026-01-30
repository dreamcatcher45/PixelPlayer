package com.theveloper.pixelplay.presentation.screens.search.components

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import com.theveloper.pixelplay.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontVariation
import kotlin.math.abs

// FALTA: genre_variable.ttf (Variable Font)
// Por favor, coloca el archivo de fuente variable (ej. RobotoFlex-VariableFont_GRAD,XTRA,YOPQ,YTAS,YTDE,YTFI,YTLC,YTUC,opsz,slnt,wdth,wght.ttf)
// en la carpeta: app/src/main/res/font/
// Y renómbralo a: genre_variable.ttf

object GenreTypography {

    /**
     * Generates a deterministic random TextStyle for a given Genre ID using variable font settings.
     * Note: This requires a variable font resource to be present in res/font/genre_variable.ttf
     */
@OptIn(ExperimentalTextApi::class)
    fun getGenreStyle(genreId: String): TextStyle {
        val hash = abs(genreId.hashCode())
        val seed = hash % 100

        // Strategy Distribution:
        // 0-14: Original (Simple Bold) -> 15%
        // 15-24: Monospace -> 10%
        // 25-64: Roboto Flex (Wild Variable) -> 40%
        // 65-99: Google Sans Flex (Rounded/Expressive) -> 35%
        
        return when {
            seed < 15 -> { // Original
                 TextStyle(
                    fontFamily = FontFamily.Default, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            seed < 25 -> { // Monospace
                TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    letterSpacing = (-0.5).sp // Tighter look for mono
                )
            }
            seed < 65 -> { // Roboto Flex (Wild)
                // Distinctive "Wild" look
                // Weight: 200 (Thin) to 900 (Black)
                val weightVal = 200 + (hash % 700) 
                
                // Width: 50 (Condensed) to 150 (Expanded) - wider range for wildness
                val widthVal = 60f + (hash % 90) 
                
                // Slant: -10 (Backslant) to 0 (Upright) to 10 (Italic implicit? Slant is usually angle)
                // Roboto Flex 'slnt' is usually -10 to 0. 
                val slantVal = if (hash % 5 == 0) -10f else 0f

                val family = FontFamily(
                    Font(
                        resId = R.font.genre_variable,
                        variationSettings = FontVariation.Settings(
                            FontVariation.weight(weightVal),
                            FontVariation.width(widthVal),
                            FontVariation.slant(slantVal)
                        )
                    )
                )

                // Geometric Transform: "Crazy" scaleX
                // Range: 0.8 to 1.6
                val scaleXVal = 0.8f + ((hash % 8) / 10f)

                TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight(weightVal), // Fallback
                    fontSize = (22 + (hash % 12)).sp, // 22sp to 33sp
                    letterSpacing = if (widthVal > 110) (-1).sp else 0.sp, // Tighten expanded text
                    textGeometricTransform = androidx.compose.ui.text.style.TextGeometricTransform(scaleX = scaleXVal)
                )
            }
            else -> { // Google Sans Flex (Rounded/Expressive)
                 // Assuming gflex_variable supports standard axes + potentially others.
                 // We focus on the "Roundness" vibe here.
                 
                 val weightVal = 300 + (hash % 500) // 300-800
                 // "ROND" axis might not be standard, but we'll try standard opsz/grade if available, 
                 // or just rely on the font's character.
                 
                 // If the user said "permite redondez", it's likely inherent or controlled by weight/style.
                 // We'll trust the font resource.
                 
                 val family = FontFamily(
                    Font(
                        resId = R.font.gflex_variable,
                        variationSettings = FontVariation.Settings(
                            FontVariation.weight(weightVal),
                            // FontVariation.grade(0) // Standard grade
                        )
                    )
                 )
                 
                 // ScaleX: slightly less wild than Roboto, closer to 1.0 but still expressive
                 val scaleXVal = 0.9f + ((hash % 5) / 10f) // 0.9 to 1.3

                 TextStyle(
                    fontFamily = family,
                    fontWeight = FontWeight(weightVal),
                    fontSize = (20 + (hash % 8)).sp,
                    textGeometricTransform = androidx.compose.ui.text.style.TextGeometricTransform(scaleX = scaleXVal)
                )
            }
        }
    }
}
