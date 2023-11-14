package com.pseudoankit.androiddemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pseudoankit.androiddemo.screen.detail.DetailScreen
import com.pseudoankit.androiddemo.screen.listing.ListingScreen

const val SCREEN_LISTING = "listing"
const val SCREEN_DETAIL = "detail"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = SCREEN_LISTING) {
                composable(SCREEN_LISTING) {
                    ListingScreen(navController)
                }
                composable(SCREEN_DETAIL) {
                    DetailScreen(value = "detail")
                }
            }
        }
    }
}
