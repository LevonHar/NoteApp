package com.example.noteapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.database.NoteEntity
import com.example.noteapp.ui.theme.ubuntu
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val notes by viewModel.notes.collectAsState()
    val searchedNotes by viewModel.searchedNotes.collectAsState()

    val (dialogOpen, setDialogOpen) = remember {
        mutableStateOf(false)
    }

    if (dialogOpen) {
        val controller = rememberColorPickerController()
        controller.setWheelRadius(7.dp)
        val (note, setNote) = remember {
            mutableStateOf("")
        }

        val color by animateColorAsState(targetValue = controller.selectedColor.value)

        Dialog(onDismissRequest = { setDialogOpen(false) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(
                        horizontal = 8.dp,
                        vertical = 12.dp
                    )
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    value = note,
                    onValueChange = {
                        setNote(it)
                    },
                    label = {
                        Text(text = "Note")
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        focusedLabelColor = Color.White,
                        focusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Note Color:", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), controller = controller,
                    initialColor = Color(0xff1dffc0)
                )
                Spacer(modifier = Modifier.height(10.dp))
                AlphaSlider(
                    controller = controller,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    borderRadius = 6.dp,
                    wheelRadius = 7.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                BrightnessSlider(
                    controller = controller,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    borderRadius = 6.dp,
                    wheelRadius = 7.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (note.isNotEmpty()) {
                            viewModel.addNote(
                                NoteEntity(
                                    note = note,
                                    color = color.toArgb()
                                )
                            )
                            setDialogOpen(false)
                        }
                    },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color
                    )
                ) {
                    Text(text = "Add Note", color = Color.White, fontFamily = ubuntu)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { setDialogOpen(true) },
                contentColor = Color.White,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = paddings.calculateBottomPadding(),
                    top = 18.dp,
                    start = 12.dp,
                    end = 12.dp
                )
        ) {
            val (searchOpen, setSearchOpen) = remember {
                mutableStateOf(false)
            }
            val (searchQuery, setSearchQuery) = remember {
                mutableStateOf("")
            }
            LaunchedEffect(searchQuery) {
                viewModel.searchNotes(searchQuery)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = !searchOpen,
                    enter = fadeIn(tween(500)) + expandHorizontally(tween(500)),
                    exit = fadeOut(tween(500)) + shrinkHorizontally(tween(500))
                ) {
                    Text(
                        text = "My Notes",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = 8.dp),
                    visible = searchOpen,
                    enter = fadeIn(tween(500)) + expandHorizontally(tween(500)),
                    exit = fadeOut(tween(500)) + shrinkHorizontally(tween(500))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TextField(value = searchQuery, onValueChange = {
                            setSearchQuery(it)
                        }, modifier = Modifier.fillMaxWidth(), placeholder = {
                            Text(text = "Search", color = Color.White)
                        }, colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White
                        )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable {
                            setSearchOpen(!searchOpen)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row {
                        AnimatedVisibility(
                            visible = !searchOpen,
                            enter = scaleIn(tween(500)),
                            exit = scaleOut(tween(500))
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Row {
                        AnimatedVisibility(
                            visible = searchOpen,
                            enter = scaleIn(tween(500)),
                            exit = scaleOut(tween(500))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Spacer(modifier = Modifier.height(28.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchedNotes ?: notes, key = {
                    it.id
                }) { note ->
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(note.color))
                        .fillMaxWidth()
                        .padding()
                        .animateItemPlacement(
                            tween(500)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = {
                                viewModel.deleteNote(note)
                            })
                        }
                    ) {
                        Text(text = note.note, color = Color.White, modifier = Modifier.padding(5.dp))
                    }
                }
            }
        }
    }
}