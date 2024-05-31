package com.aeke.fitnessmirror.music.ui.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CoroutineScope.launchMain(block: suspend CoroutineScope.() -> Unit): Job =
    launch(context = Dispatchers.Main, block = block)

fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit): Job =
    launch(context = Dispatchers.IO, block = block)

suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.Main, block)

suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.IO, block)