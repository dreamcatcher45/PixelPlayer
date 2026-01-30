package com.theveloper.pixelplay.presentation.utils

import com.theveloper.pixelplay.R

object GenreIconProvider {
    
    // Provide a list of default common genres
    val DEFAULT_GENRES = listOf(
        "Rock", "Pop", "Jazz", "Classical", "Electronic", "Hip Hop",
        "Country", "Blues", "Reggae", "Metal", "Folk", "R&B", "Punk", "Indie",
        "Alternative", "Latino", "Reggaeton", "Salsa", "Bachata", "Merengue", "Cumbia",
        "Oldies", "Soundtrack", "Gaming", "Sleep", "Workout", "Party", "Focus"
    )

    fun getGenreImageResource(genreId: String): Any {
        return when (genreId.lowercase()) {
            "rock", "hard rock", "alternative rock", "classic rock" -> R.drawable.rock
            "pop", "pop rock", "k-pop", "dance pop" -> R.drawable.pop_mic
            "jazz", "smooth jazz", "bebop" -> R.drawable.sax
            "classical", "orchestra", "symphony", "piano" -> R.drawable.clasic_piano
            "electronic", "edm", "techno", "house", "trance", "dubstep", "electro" -> R.drawable.electronic_sound
            "hip hop", "hip-hop", "rap", "trap", "gangsta rap" -> R.drawable.rapper
            "country", "bluegrass", "americana" -> R.drawable.banjo
            "blues", "rhythm & blues" -> R.drawable.harmonica
            "reggae", "ska", "dancehall" -> R.drawable.maracas
            "metal", "heavy metal", "death metal", "black metal", "thrash metal" -> R.drawable.metal_guitar
            "folk", "acoustic", "singer-songwriter" -> R.drawable.accordion
            "r&b / soul", "rnb", "soul", "funk", "motown" -> R.drawable.synth_piano
            "punk", "punk rock", "pop punk", "grunge" -> R.drawable.punk
            "indie", "indie rock", "indie pop", "lo-fi" -> R.drawable.idk_indie_ig
            "folk & acoustic" -> R.drawable.acoustic_guitar
            "alternative", "alt-rock" -> R.drawable.alt_video
            "latino", "latin", "latin pop", "urbano latino" -> R.drawable.star_angle
            "reggaeton" -> R.drawable.rapper
            "salsa" -> R.drawable.conga
            "bachata" -> R.drawable.bongos
            "merengue" -> R.drawable.drum
            "cumbia" -> R.drawable.maracas
            "oldies", "retro", "80s", "90s" -> R.drawable.rounded_schedule_24
            "soundtrack", "score", "movie tunes" -> R.drawable.rounded_tv_24
            "gaming", "video game music" -> R.drawable.rounded_touch_app_24
            "sleep", "relax", "meditation", "ambient" -> R.drawable.rounded_alarm_24
            "workout", "gym", "fitness" -> R.drawable.electronic_sound
            "party", "club" -> R.drawable.rounded_celebration_24
            "focus", "study" -> R.drawable.rounded_edit_24
            "unknown" -> R.drawable.rounded_question_mark_24
            else -> R.drawable.rounded_library_music_24
        }
    }
}
