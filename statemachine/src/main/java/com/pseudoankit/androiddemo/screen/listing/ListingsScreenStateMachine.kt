package com.pseudoankit.androiddemo.screen.listing

import com.tinder.StateMachine

class ListingsScreenStateMachine(
    private val onEvent: (SideEffect) -> Unit
) {

    private val stateMachine = StateMachine.create<State, Event, SideEffect> {
        initialState(State.Idle)
        state<State.Idle> {
            on<Event.OnNavigateBack> {
                dontTransition(
                    SideEffect.NavigateBack
                )
            }
            on<Event.OnLoadInitialItems> {
                transitionTo(
                    State.Loading,
                    SideEffect.LoadInitialItems
                )
            }
            on<Event.OnRefreshItems> {
                transitionTo(
                    State.Loading,
                    SideEffect.RefreshItems
                )
            }
            on<Event.OnItemClicked> {
                transitionTo(
                    State.NavigatedToDetailsScreen,
                    SideEffect.ItemClicked(it.data)
                )
            }
        }
        state<State.Loading> {
            on<Event.OnNavigateBack> {
                dontTransition(
                    SideEffect.NavigateBack
                )
            }
            on<Event.OnItemsLoaded> {
                transitionTo(
                    State.Idle,
                    SideEffect.ItemsLoaded(it.data)
                )
            }
            on<Event.OnItemClicked> {
                transitionTo(
                    State.NavigatedToDetailsScreen,
                    SideEffect.ItemClicked(it.data)
                )
            }
        }
        state<State.NavigatedToDetailsScreen> {

        }

        onTransition {
            val transition =
                (it as? StateMachine.Transition.Valid)?.sideEffect ?: return@onTransition
            onEvent(transition)
        }
    }

    fun transition(event: Event) {
        stateMachine.transition(event)
    }

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data object NavigatedToDetailsScreen : State
    }

    sealed interface Event {
        data object OnNavigateBack : Event
        data object OnLoadInitialItems : Event
        data object OnRefreshItems : Event
        data class OnItemClicked(val data: String) : Event
        data class OnItemsLoaded(val data: List<String>) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect
        data object LoadInitialItems : SideEffect
        data object RefreshItems : SideEffect
        data class ItemClicked(val data: String) : SideEffect
        data class ItemsLoaded(val data: List<String>) : SideEffect
    }
}