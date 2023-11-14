package com.pseudoankit.androiddemo.screen.listing

import com.pseudoankit.androiddemo.util.sLog
import com.tinder.StateMachine

class ListingsScreenStateMachine(
    private val onEvent: (SideEffect) -> Unit
) {

    private val stateMachine = StateMachine.create<State, Event, SideEffect> {
        initialState(State.Idle)

        // idle state
        state<State.Idle> {
            // when idle we want to load items
            on<Event.OnLoadInitialItems> {
                transitionTo(
                    State.Loading,
                    SideEffect.LoadInitialItems
                )
            }
            // when idle we want to refresh items, but in that process we want to change state to loading
            on<Event.OnRefreshItems> {
                transitionTo(
                    State.Loading,
                    SideEffect.RefreshItems
                )
            }
            // when idle we want items to be clicked
            on<Event.OnItemClicked> {
                transitionTo(
                    State.NavigatedToDetailsScreen,
                    SideEffect.ItemClicked(it.data)
                )
            }
        }

        // defines loading state of screen
        state<State.Loading> {

            // when loading we want to act when items are loaded,
            // we can also handle loading success and failure separately if needed
            on<Event.OnItemsLoaded> {
                transitionTo(
                    State.Idle,
                    SideEffect.ItemsLoaded(it.data)
                )
            }

            // when loading we will handle item clicks,
            // as for swipe to refresh there can be items already visible
            on<Event.OnItemClicked> {
                transitionTo(
                    State.NavigatedToDetailsScreen,
                    SideEffect.ItemClicked(it.data)
                )
            }
        }

        // navigated to detail screen i.e., current screen is not active
        // we need this state as we don't want any event to be handled when user is in another screen
        state<State.NavigatedToDetailsScreen> {

            // but there can be a case to handle items loaded event, as while loading state
            // if user moves to another screen, then also we need to update our viewmodel with new data
            on<Event.OnItemsLoaded> {
                transitionTo(
                    State.Idle,
                    SideEffect.ItemsLoaded(it.data)
                )
            }

            // callback triggered once user is back to the screen it's important bcz
            // when user navigated to another screen state is changed to NavigatedToDetailsScreen
            // but once back if user tries to refresh it's won't work as it's not we are not in Idle state
            // so we need to reset the state to avoid such issues
            on<Event.OnScreenResumed> {
                transitionTo(
                    State.Idle,
                )
            }
        }

        onTransition {
            val transition =
                (it as? StateMachine.Transition.Valid)?.sideEffect ?: return@onTransition
            sLog("after state=${it.toState}, event=${it.sideEffect}")
            onEvent(transition)
        }
    }

    fun transition(event: Event) {
        sLog("before : state=${stateMachine.state}, event=$event")
        stateMachine.transition(event)
    }

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data object NavigatedToDetailsScreen : State
    }

    sealed interface Event {
        data object OnLoadInitialItems : Event
        data object OnRefreshItems : Event
        data class OnItemClicked(val data: String) : Event
        data class OnItemsLoaded(val data: List<String>) : Event
        data object OnScreenResumed : Event
    }

    sealed interface SideEffect {
        data object LoadInitialItems : SideEffect
        data object RefreshItems : SideEffect
        data class ItemClicked(val data: String) : SideEffect
        data class ItemsLoaded(val data: List<String>) : SideEffect
    }
}