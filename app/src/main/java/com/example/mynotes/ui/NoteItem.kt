package com.example.mynotes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.data.Note
import com.example.mynotes.ui.theme.DeleteRed
import com.example.mynotes.ui.theme.Ink
import com.example.mynotes.ui.theme.InkSoft
import com.example.mynotes.ui.theme.NoteFont
import com.example.mynotes.ui.theme.PaperTan
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

private val CardHeight = 132.dp

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    // rememberSaveable: al hacer scroll la tarjeta no vuelve a animar su entrada.
    var visible by rememberSaveable { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }
    LaunchedEffect(deleting) {
        if (deleting) {
            visible = false      // dispara fadeOut + shrinkVertically
            delay(350)
            onDelete()           // y hasta entonces se borra de Room
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(350)) + expandVertically(tween(350)),
        exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
    ) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { target ->
                if (target != SwipeToDismissBoxValue.Settled) deleting = true
                true
            }
        )
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = { DeleteBackground(dismissState.dismissDirection) },
            modifier = Modifier
                .fillMaxWidth()
                .height(CardHeight)
        ) {
            NoteCard(note, onClick, onToggleCompleted)
        }
    }
}

/** El botón "Eliminar" queda por debajo de la nota y se descubre al deslizarla. */
@Composable
private fun DeleteBackground(direction: SwipeToDismissBoxValue) {
    if (direction == SwipeToDismissBoxValue.Settled) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeleteRed)
            .padding(horizontal = 6.dp),
        contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd)
            Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Image(
            painter = painterResource(R.drawable.btn_eliminar),
            contentDescription = stringResource(R.string.eliminar),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .aspectRatio(301f / 234f)
        )
    }
}

@Composable
private fun NoteCard(note: Note, onClick: () -> Unit, onToggleCompleted: () -> Unit) {
    val textAlpha by animateFloatAsState(if (note.completed) 0.55f else 1f, label = "completed")
    val dateText = remember(note.date) {
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(note.date))
    }

    Box(Modifier.fillMaxSize().clickable(onClick = onClick)) {
        Image(
            painter = painterResource(R.drawable.card_nota),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.weight(0.514f).fillMaxWidth().padding(start = 6.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = note.completed,
                    onCheckedChange = { onToggleCompleted() },
                    colors = CheckboxDefaults.colors(checkedColor = Ink, uncheckedColor = Ink, checkmarkColor = PaperTan),
                    modifier = Modifier.alpha(0.9f)
                )
                Text(
                    text = note.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (note.completed) TextDecoration.LineThrough else TextDecoration.None,
                    style = TextStyle(fontFamily = NoteFont, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Ink),
                    modifier = Modifier.alpha(textAlpha)
                )
            }
            Column(
                modifier = Modifier.weight(0.486f).fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)
            ) {
                if (note.content.isNotBlank()) {
                    Text(
                        text = note.content,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(fontFamily = NoteFont, fontSize = 15.sp, color = Ink)
                    )
                }
                Text(
                    text = dateText,
                    style = TextStyle(fontSize = 11.sp, color = InkSoft)
                )
            }
        }
    }
}

