package com.example.mynotes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.data.Note


@Composable
fun NotesScreen(
    notes: List<Note>?,
    onNewNote: () -> Unit,
    onOpenNote: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onToggleCompleted: (Note) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val s = designScaleFor(maxWidth)
        val isTablet = maxWidth >= 600.dp
        val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val list = notes.orEmpty()

        Image(
            painter = painterResource(R.drawable.fondo_principal),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Lista (deja libre la zona de abajo para la libreta "Nueva Nota")
        Box(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = s.dp(480) + navBottom)
        ) {
            if (isTablet) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(280.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(list, key = { it.id }) { note ->
                        NoteItem(
                            note = note,
                            onClick = { onOpenNote(note) },
                            onDelete = { onDelete(note) },
                            onToggleCompleted = { onToggleCompleted(note) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(list, key = { it.id }) { note ->
                        NoteItem(
                            note = note,
                            onClick = { onOpenNote(note) },
                            onDelete = { onDelete(note) },
                            onToggleCompleted = { onToggleCompleted(note) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }

        // "No has creado una nota"
        AnimatedVisibility(
            visible = notes != null && notes.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Image(
                painter = painterResource(R.drawable.msg_sin_notas),
                contentDescription = stringResource(R.string.sin_notas),
                modifier = Modifier
                    .statusBarsPadding()
                    .offset(x = s.dp(161), y = s.dp(380))
                    .size(s.dp(339), s.dp(158))
            )
        }

        // Botón "Nueva Nota"
        Image(
            painter = painterResource(R.drawable.btn_nueva_nota),
            contentDescription = stringResource(R.string.nueva_nota),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = s.dp(40), bottom = s.dp(20))
                .width(s.dp(672))
                .aspectRatio(672f / 465f)
                .pressClickable(onNewNote)
        )
    }
}
