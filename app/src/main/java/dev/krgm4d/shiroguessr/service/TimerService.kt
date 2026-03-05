package dev.krgm4d.shiroguessr.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Service for managing countdown timers.
 *
 * Corresponds to the iOS version's `TimerService.swift`.
 * Provides a countdown timer with start, pause, resume, stop, and reset
 * operations. Fires a callback when the timer reaches zero.
 *
 * @param scope CoroutineScope for running the timer. Typically viewModelScope.
 */
class TimerService(
    private val scope: CoroutineScope,
) {

    private val _timeRemaining = MutableStateFlow(0)

    /** Remaining time in seconds. */
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _isRunning = MutableStateFlow(false)

    /** Whether the timer is currently running. */
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var onTimeout: (() -> Unit)? = null
    private var timerJob: Job? = null

    /**
     * Starts the timer with the specified duration.
     *
     * Cancels any existing timer before starting.
     *
     * @param seconds Duration in seconds
     * @param onTimeout Callback to execute when timer reaches zero
     */
    fun start(seconds: Int, onTimeout: (() -> Unit)? = null) {
        stop()
        _timeRemaining.value = seconds
        this.onTimeout = onTimeout
        _isRunning.value = true
        startTicking()
    }

    /**
     * Pauses the timer without clearing state.
     */
    fun pause() {
        if (!_isRunning.value) return
        timerJob?.cancel()
        timerJob = null
        _isRunning.value = false
    }

    /**
     * Resumes the timer from paused state.
     */
    fun resume() {
        if (_isRunning.value || _timeRemaining.value <= 0) return
        _isRunning.value = true
        startTicking()
    }

    /**
     * Stops the timer.
     */
    fun stop() {
        timerJob?.cancel()
        timerJob = null
        _isRunning.value = false
    }

    /**
     * Resets the timer to zero and stops it.
     */
    fun reset() {
        stop()
        _timeRemaining.value = 0
        onTimeout = null
    }

    /**
     * Sets the timer duration without starting it.
     *
     * @param seconds Duration in seconds
     * @param onTimeout Callback to execute when timer reaches zero
     */
    fun setTime(seconds: Int, onTimeout: (() -> Unit)? = null) {
        stop()
        _timeRemaining.value = seconds
        this.onTimeout = onTimeout
    }

    private fun startTicking() {
        timerJob = scope.launch {
            while (_isRunning.value && _timeRemaining.value > 0) {
                delay(1000L)
                if (_isRunning.value && _timeRemaining.value > 0) {
                    var reachedZero = false
                    _timeRemaining.update { current ->
                        val next = (current - 1).coerceAtLeast(0)
                        reachedZero = next == 0
                        next
                    }
                    if (reachedZero) {
                        _isRunning.value = false
                        onTimeout?.invoke()
                    }
                }
            }
        }
    }
}
