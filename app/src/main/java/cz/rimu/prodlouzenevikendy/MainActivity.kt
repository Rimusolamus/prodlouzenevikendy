package cz.rimu.prodlouzenevikendy

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
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
import java.time.ZoneOffset


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
                            }, onOpenCalendar = { dates ->
                                // add dates to calendar
                                val intent: Intent = Intent(Intent.ACTION_INSERT)
                                    .setData(Events.CONTENT_URI)
                                    .putExtra(
                                        CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                        dates.first().atStartOfDay().toInstant(ZoneOffset.UTC)
                                            .toEpochMilli()
                                    )
                                    .putExtra(
                                        CalendarContract.EXTRA_EVENT_END_TIME,
                                        dates.last().atStartOfDay().toInstant(ZoneOffset.UTC)
                                            .toEpochMilli()
                                    )
                                    .putExtra(Events.TITLE, "Dovolená")
                                    .putExtra(Events.DESCRIPTION, "made by Prodloužené víkendy app")
                                    .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
                                startActivity(intent)
                            })
                        }
                    }
                }
            }
        }
    }
}