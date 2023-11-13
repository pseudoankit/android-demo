package com.pseudoankit.androiddemo.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ListingsViewModel : ViewModel() {

    var state by mutableStateOf(State())
        private set

    private val _sideEffect = MutableSharedFlow<SideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val stateMachine = ListingsScreenStateMachine { event ->
        when (event) {
            is ListingsScreenStateMachine.SideEffect.ItemClicked -> {
                _sideEffect.tryEmit(SideEffect.NavigateToDetailScreen(event.data))
            }

            ListingsScreenStateMachine.SideEffect.LoadInitialItems -> {
                state = state.copy(isLoading = true)
                loadItems()
            }

            ListingsScreenStateMachine.SideEffect.NavigateBack -> {
                _sideEffect.tryEmit(SideEffect.NavigateBack)
            }

            ListingsScreenStateMachine.SideEffect.RefreshItems -> {
                loadItems()
            }
        }
    }

    init {
        onEvent(Event.OnLoadItems)
    }

    private fun loadItems() {
        viewModelScope.launch {
            delay(3000)
            state = state.copy(
                items = (1..30).map { "Item $it" },
                isLoading = false
            )
        }
    }

    fun onEvent(event: Event) {
        val stateMachineEvent = when (event) {
            is Event.OnItemClicked -> ListingsScreenStateMachine.Event.OnItemClicked(event.data)
            Event.OnLoadItems -> ListingsScreenStateMachine.Event.OnLoadInitialItems
            Event.OnNavigateBack -> ListingsScreenStateMachine.Event.OnNavigateBack
            Event.OnRefreshItems -> ListingsScreenStateMachine.Event.OnRefreshItems
        }
        stateMachine.transition(stateMachineEvent)
    }


    data class State(
        val items: List<String> = emptyList(),
        val isLoading: Boolean = false
    )

    sealed interface Event {
        data object OnLoadItems : Event
        data object OnRefreshItems : Event
        data class OnItemClicked(val data: String) : Event
        data object OnNavigateBack : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect
        data class NavigateToDetailScreen(val data: String) : SideEffect
    }
}