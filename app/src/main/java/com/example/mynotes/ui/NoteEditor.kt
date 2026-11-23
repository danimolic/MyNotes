package com.example.mynotes.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.NoteViewModel
import com.example.mynotes.R
import com.example.mynotes.data.Note
import com.example.mynotes.ui.theme.EditorTan
import com.example.mynotes.ui.theme.ErrorRed
import com.example.mynotes.ui.theme.Ink
import com.example.mynotes.ui.theme.InkSoft
import com.example.mynotes.ui.theme.NoteFont
import com.example.mynotes.ui.theme.PaperTan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun NoteEditor(noteId: Long, viewModel: NoteViewModel, onClose: () -> Unit) {
    var loaded by rememberSaveable { mutableStateOf(noteId == 0L) }
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<String?>(null) }
    var completed by rememberSaveable { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(noteId) {
        if (!loaded) {
            viewModel.findById(noteId)?.let {
                title = it.title
                content = it.content
                imageUri = it.imageUri
                completed = it.completed
            }
            loaded = true
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
                // algunos proveedores no permiten permiso persistente
            }
            imageUri = uri.toString()
        }
    }

    fun requestClose() {
        if (saved) return
        val hasData = title.isNotBlank() || content.isNotBlank() || imageUri != null
        when {
            !hasData -> onClose()
            title.isBlank() -> titleError = true
            else -> {
                saved = true
                scope.launch {
                    viewModel.save(
                        Note(
                            id = noteId,
                            title = title.trim(),
                            content = content.trim(),
                            date = System.currentTimeMillis(),
                            completed = completed,
                            imageUri = imageUri
                        )
                    ).join()
                    delay(1000)
                    onClose()
                }
            }
        }
    }

    BackHandler { requestClose() }

    val bitmap = rememberImageBitmap(imageUri)

    BoxWithConstraints(Modifier.fillMaxSize().background(EditorTan)) {
        val s = designScaleFor(maxWidth)
        val inkStyle = TextStyle(fontFamily = NoteFont, color = Ink)

        Box(
            Modifier
                .align(Alignment.TopCenter)
                .width(minOf(maxWidth, 560.dp))
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Fondo con la línea bajo el título
            Image(
                painter = painterResource(R.drawable.fondo_editor),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
            )

            // Hoja: con la guía (lineas + "Insertar título / Descripción") mientras la nota está vacía
            val showGuide = title.isEmpty() && content.isEmpty() && imageUri == null
            Crossfade(
                targetState = showGuide,
                label = "hoja",
                modifier = Modifier.offset(x = s.dp(37), y = s.dp(147))
            ) { guide ->
                Image(
                    painter = painterResource(if (guide) R.drawable.libreta_guia else R.drawable.libreta_vacia),
                    contentDescription = stringResource(R.string.hoja_de_nota),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.width(s.dp(675)).aspectRatio(675f / 987f)
                )
            }

            Column(Modifier.fillMaxSize()) {
                // Título (arriba, sobre la línea)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(s.dp(147))
                        .padding(start = s.dp(57), end = s.dp(60)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (titleError && title.isBlank()) {
                        Text(
                            stringResource(R.string.escribe_titulo),
                            style = inkStyle.copy(color = ErrorRed, fontSize = s.sp(48))
                        )
                    } else if (title.isEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.titulo_hint),
                            contentDescription = stringResource(R.string.titulo),
                            modifier = Modifier.width(s.dp(268)).aspectRatio(268f / 101f)
                        )
                    }
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it.take(60); titleError = false },
                        singleLine = true,
                        textStyle = inkStyle.copy(fontSize = s.sp(76), fontWeight = FontWeight.Bold),
                        cursorBrush = SolidColor(Ink),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Descripción (+ imagen adjunta con zoom)
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    Column(
                        Modifier
                            .padding(top = s.dp(255), start = s.dp(95), end = s.dp(60))
                            .fillMaxWidth()
                            .heightIn(max = s.dp(610))
                    ) {
                        Box(Modifier.weight(1f).fillMaxWidth()) {
                            BasicTextField(
                                value = content,
                                onValueChange = { content = it },
                                textStyle = inkStyle.copy(fontSize = s.sp(48)),
                                cursorBrush = SolidColor(Ink),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        if (bitmap != null) {
                            Spacer(Modifier.height(s.dp(10)))
                            Box(Modifier.fillMaxWidth().height(s.dp(280))) {
                                ZoomableImage(
                                    bitmap = bitmap,
                                    contentDescription = stringResource(R.string.imagen_adjunta),
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = stringResource(R.string.quitar_imagen),
                                    style = inkStyle.copy(fontSize = s.sp(36)),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(PaperTan)
                                        .clickable { imageUri = null }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Barra inferior: regresar + adjuntar archivo
                Row(Modifier.fillMaxWidth().height(s.dp(137))) {
                    Image(
                        painter = painterResource(R.drawable.btn_regresar),
                        contentDescription = stringResource(R.string.regresar),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .width(s.dp(199))
                            .fillMaxHeight()
                            .pressClickable { requestClose() }
                    )
                    Image(
                        painter = painterResource(R.drawable.btn_adjuntar),
                        contentDescription = stringResource(R.string.adjuntar_archivo),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .pressClickable { picker.launch(arrayOf("image/*")) }
                    )
                }
            }

            // "Nota guardada."
            AnimatedVisibility(visible = saved, enter = fadeIn(), exit = fadeOut()) {
                Image(
                    painter = painterResource(R.drawable.msg_nota_guardada),
                    contentDescription = stringResource(R.string.nota_guardada),
                    modifier = Modifier
                        .offset(x = s.dp(187), y = s.dp(578))
                        .size(s.dp(361), s.dp(69))
                )
            }
        }
    }
}
