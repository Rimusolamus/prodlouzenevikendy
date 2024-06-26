package cz.rimu.prodlouzenevikendy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.rimu.prodlouzenevikendy.screens.HolidayCountScreen
import cz.rimu.prodlouzenevikendy.screens.HolidayListScreen
import cz.rimu.prodlouzenevikendy.screens.SelectedHolidaysScreen
import kiwi.orbit.compose.ui.OrbitTheme
import kiwi.orbit.compose.ui.controls.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrbitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavRoute.HolidayCount.path
                    ) {
                        composable(NavRoute.HolidayCount.path) {
                            HolidayCountScreen(goToHolidayList = {
                                navController.navigate(NavRoute.HolidayList.path)
                            })
                        }
                        composable(NavRoute.HolidayList.path) {
                            HolidayListScreen(goBack = {
                                navController.popBackStack()
                            }, openSelectedHolidays = {
                                navController.navigate(NavRoute.SelectedHolidays.path)
                            })
                        }
                        composable(NavRoute.SelectedHolidays.path) {
                            SelectedHolidaysScreen(onBack = {
                                navController.popBackStack()
                            })
                        }
                    }
                }
            }
        }
    }
}