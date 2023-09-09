package com.rakuten.soundscript.ui

import androidx.navigation.NavHostController

object Destinations {
    const val Home = "home"
    const val TranscriptSummary = "transcript_summary"

    object TranscriptSummaryArgs {
        const val contentId = "contentId"
    }
}

class Actions(navController: NavHostController) {
    val gotoTranscriptSummary: (String) -> Unit = { contentId ->
        navController.navigate("${Destinations.TranscriptSummary}/${contentId}")
    }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}