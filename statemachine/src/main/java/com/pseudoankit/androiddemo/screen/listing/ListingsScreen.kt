package com.pseudoankit.androiddemo.screen.listing

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pseudoankit.androiddemo.SCREEN_DETAIL
import com.pseudoankit.androiddemo.util.ComposeLifecycle

@Composable
fun ListingScreen(navController: NavHostController) {
    val viewModel: ListingsViewModel = viewModel()

    ComposeLifecycle(
        onResume = {
            viewModel.onEvent(ListingsViewModel.Event.OnScreenResumed)
        }
    )

    viewModel.CollectSideEffect(navController)

    val state = viewModel.state

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (state.isLoading) {
            items(10) {
                Item(data = "", isLoading = true, {})
            }
        }
        items(state.items) { item ->
            Item(
                data = item,
                isLoading = false,
                onClick = {
                    viewModel.onEvent(ListingsViewModel.Event.OnItemClicked(it))
                }
            )
        }
    }
}

@Composable
private fun ListingsViewModel.CollectSideEffect(navController: NavHostController) {
    LaunchedEffect(Unit) {
        sideEffect.collect { event ->
            when (event) {
                ListingsViewModel.SideEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                is ListingsViewModel.SideEffect.NavigateToDetailScreen -> {
                    navController.navigate(SCREEN_DETAIL)
                }
            }
        }
    }
}

@Composable
private fun Item(data: String, isLoading: Boolean, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White)
            .shimmerEffect(isLoading)
            .clickable {
                onClick(data)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = data, color = Color.Black)
    }
}

fun Modifier.shimmerEffect(isLoading: Boolean): Modifier = composed {
    if (!isLoading) return@composed this

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}
