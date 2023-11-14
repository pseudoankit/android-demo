package com.pseudoankit.androiddemo.screen.listing

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
                viewModelScope.launch {
                    _sideEffect.emit(SideEffect.NavigateToDetailScreen(event.data))
                }
            }

            ListingsScreenStateMachine.SideEffect.LoadInitialItems -> {
                state = state.copy(isLoading = true)
                loadItems()
            }

            ListingsScreenStateMachine.SideEffect.RefreshItems -> {
                loadItems()
            }

            is ListingsScreenStateMachine.SideEffect.ItemsLoaded -> {
                state = state.copy(
                    items = event.data,
                    isLoading = false
                )
            }
        }
    }

    init {
        onEvent(Event.OnLoadItems)
    }

    private fun loadItems() {
        viewModelScope.launch {
            delay(3000)
            stateMachine.transition(ListingsScreenStateMachine.Event.OnItemsLoaded((1..30).map { "Item $it" }))
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnItemClicked -> {
                stateMachine.transition(ListingsScreenStateMachine.Event.OnItemClicked(event.data))
            }

            Event.OnLoadItems -> {
                stateMachine.transition(ListingsScreenStateMachine.Event.OnLoadInitialItems)
            }

            Event.OnNavigateBack -> {
                viewModelScope.launch {
                    _sideEffect.emit(SideEffect.NavigateBack)
                }
            }

            Event.OnRefreshItems -> {
                stateMachine.transition(ListingsScreenStateMachine.Event.OnRefreshItems)
            }
        }
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