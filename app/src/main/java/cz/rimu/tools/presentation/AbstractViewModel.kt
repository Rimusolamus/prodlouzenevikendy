package cz.rimu.tools.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AbstractViewModel<S : AbstractViewModel.State>(initialState: S) : ViewModel() {
    private val _mutableStates = MutableStateFlow(initialState)

    protected var state: S
        get() = _mutableStates.value
        set(value) {
            _mutableStates.value = value
        }

    val states: StateFlow<S> = _mutableStates.asStateFlow()

    interface State
}