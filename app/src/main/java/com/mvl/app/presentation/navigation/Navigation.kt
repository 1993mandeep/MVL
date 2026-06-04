package com.mvl.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mvl.app.presentation.screen1.MapScreen
import com.mvl.app.presentation.screen1.MapViewModel
import com.mvl.app.presentation.screen2.NicknameScreen
import com.mvl.app.presentation.screen3.BookingConfirmScreen
import com.mvl.app.presentation.screen4.HistoryScreen
import com.mvl.app.presentation.screen5.CachePickerScreen

sealed class Screen(val route: String) {
    data object Map : Screen("map")
    data object Nickname : Screen("nickname/{which}") {
        fun withArg(which: String) = "nickname/$which"
    }
    data object BookingConfirm : Screen("booking_confirm")
    data object History : Screen("history")
    data object CachePicker : Screen("cache_picker/{which}") {
        fun withArg(which: String) = "cache_picker/$which"
    }
}

@Composable
fun MVLNavHost(navController: NavHostController = rememberNavController()) {
    // Shared MapViewModel scoped to the nav graph — all screens share the same instance
    val mapViewModel: MapViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Screen.Map.route) {

        composable(Screen.Map.route) {
            MapScreen(
                viewModel = mapViewModel,
                onLabelClick = { which ->
                    // Label tapped with location already set → go to nickname screen
                    navController.navigate(Screen.Nickname.withArg(which))
                },
                onUnsetLabelClick = { which ->
                    // Label tapped before it's set → go to cache picker (optional feature)
                    navController.navigate(Screen.CachePicker.withArg(which))
                },
                onBookClick = {
                    navController.navigate(Screen.BookingConfirm.route)
                }
            )
        }

        composable(Screen.Nickname.route) { backStackEntry ->
            val which = backStackEntry.arguments?.getString("which") ?: "a"
            NicknameScreen(
                which = which,
                viewModel = mapViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BookingConfirm.route) {
            BookingConfirmScreen(
                viewModel = mapViewModel,
                onNextClick = {
                    navController.navigate(Screen.History.route)
                },
                onBackPressed = {
                    mapViewModel.resetState()
                    navController.popBackStack(Screen.Map.route, inclusive = false)
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onItemClick = { booking ->
                    mapViewModel.loadFromHistory(booking)
                    navController.popBackStack(Screen.Map.route, inclusive = false)
                }
            )
        }

        composable(Screen.CachePicker.route) { backStackEntry ->
            val which = backStackEntry.arguments?.getString("which") ?: "a"
            CachePickerScreen(
                which = which,
                onLocationSelected = { point ->
                    mapViewModel.setLocationFromCache(which, point)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
