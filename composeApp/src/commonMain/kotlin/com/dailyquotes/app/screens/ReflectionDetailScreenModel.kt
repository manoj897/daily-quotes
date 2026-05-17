package com.dailyquotes.app.screens

import cafe.adriel.voyager.core.model.ScreenModel
import com.dailyquotes.shared.ShareManager

class ReflectionDetailScreenModel(
    private val shareManager: ShareManager
) : ScreenModel {
    fun shareReflection(shareText: String) {
        shareManager.shareText(shareText)
    }
}
