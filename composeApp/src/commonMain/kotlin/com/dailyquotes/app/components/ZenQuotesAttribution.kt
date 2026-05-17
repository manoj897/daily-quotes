package com.dailyquotes.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

const val ZEN_QUOTES_URL = "https://zenquotes.io/"

fun StringBuilder.appendZenQuotesSource() {
    append("\n\nSource: ZenQuotes")
    append("\n")
    append(ZEN_QUOTES_URL)
}

@Composable
fun ZenQuotesAttribution(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        AttributionText("Source: ")
        AttributionText(
            text = "ZenQuotes",
            modifier = Modifier.clickable { uriHandler.openUri(ZEN_QUOTES_URL) },
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
fun SourceShareToggle(
    includeSourceInShare: Boolean,
    onIncludeSourceInShareChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (includeSourceInShare) {
            ZenQuotesAttribution()
        } else {
            Text(
                text = "Hidden from shared text",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }

        IconButton(
            onClick = { onIncludeSourceInShareChange(!includeSourceInShare) }
        ) {
            Icon(
                imageVector = if (includeSourceInShare) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (includeSourceInShare) "Remove source from shared text" else "Include source in shared text",
                tint = if (includeSourceInShare) Color.White else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RowScope.AttributionText(
    text: String,
    modifier: Modifier = Modifier,
    textDecoration: TextDecoration? = null
) {
    Text(
        text = text,
        modifier = modifier,
        color = Color.Gray,
        style = MaterialTheme.typography.bodySmall,
        textDecoration = textDecoration
    )
}
