package com.example.mynotes

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.mynotes.ui.NoteEditor
import com.example.mynotes.ui.NotesScreen
import com.example.mynotes.ui.theme.MyNotesTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Iconos de la barra de estado siempre oscuros: los fondos son claros.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            MyNotesTheme { MyNotesApp(viewModel) }
        }
    }
}

@Composable
fun MyNotesApp(viewModel: NoteViewModel) {
    val notes by viewModel.notes.collectAsState()
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf(0L) } // 0 = nota nueva

    Crossfade(targetState = editorOpen, animationSpec = tween(300), label = "pantallas") { open ->
        if (open) {
            NoteEditor(
                noteId = editingId,
                viewModel = viewModel,
                onClose = { editorOpen = false }
            )
        } else {
            NotesScreen(
                notes = notes,
                onNewNote = { editingId = 0L; editorOpen = true },
                onOpenNote = { editingId = it.id; editorOpen = true },
                onDelete = { viewModel.delete(it) },
                onToggleCompleted = { viewModel.toggleCompleted(it) }
            )
        }
    }
}
