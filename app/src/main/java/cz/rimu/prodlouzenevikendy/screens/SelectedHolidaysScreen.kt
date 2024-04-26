package cz.rimu.prodlouzenevikendy.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cz.rimu.prodlouzenevikendy.model.Recommendation
import cz.rimu.prodlouzenevikendy.presentation.SelectedHolidaysViewModel
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberCalendarState
import io.github.boguszpawlowski.composecalendar.selection.EmptySelectionState
import kiwi.orbit.compose.ui.OrbitTheme
import kiwi.orbit.compose.ui.controls.ButtonPrimary
import kiwi.orbit.compose.ui.controls.Card
import kiwi.orbit.compose.ui.controls.Scaffold
import kiwi.orbit.compose.ui.controls.Text
import kiwi.orbit.compose.ui.controls.TopAppBar
import org.koin.androidx.compose.koinViewModel
import java.time.YearMonth
import java.util.Locale

@Composable
fun SelectedHolidaysScreen(
    onBack: () -> Unit
) {
    val viewModel = koinViewModel<SelectedHolidaysViewModel>()
    val state = viewModel.states.collectAsState()
    SelectedHolidaysScreenImpl(state = state.value, onBack = onBack)
}

@Composable
fun SelectedHolidaysScreenImpl(
    state: SelectedHolidaysViewModel.State,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Prodloužené víkendy") },
                onNavigateUp = onBack
            )
        },
        actionLayout = {
            Column {
                ButtonPrimary(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    onClick = {}
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Ulozit do Google calendaru")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        },
        content = { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Selected holidays",
                        style = OrbitTheme.typography.title4,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                state.selectedHolidays?.size?.let {
                    items(it) { index ->
                        val recommendation = state.selectedHolidays[index]
                        StaticCalendar(
                            calendarState = rememberCalendarState(
                                initialMonth = YearMonth.from(recommendation.days.first())
                                    ?: YearMonth.now()
                            ),
                            monthHeader = { month ->
                                MonthHeader(
                                    month = month
                                )
                            },
                            dayContent = { day ->
                                DayContent(
                                    day = day,
                                    recommendation = recommendation
                                )
                            },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        })
}


@Composable
private fun MonthHeader(
    month: MonthState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = month.currentMonth.month.name.uppercase(Locale.getDefault()),
            style = OrbitTheme.typography.title5,
            color = OrbitTheme.colors.primary.normal
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DayContent(
    day: DayState<EmptySelectionState>,
    recommendation: Recommendation
) {
    if (recommendation.days.contains(day.date)) {
        OneDayBox(
            day.date.dayOfMonth.toString(),
            isSelected = true,
            isHoliday = true
        )
    } else {
        OneDayBox(
            day.date.dayOfMonth.toString(),
            isSelected = false
        )
    }
}

@Composable
private fun OneDayBox(
    dayText: String,
    isSelected: Boolean,
    isHoliday: Boolean = false
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                if (isHoliday) 3.dp else 1.dp,
                getDaySelectionColor(isSelected, isHoliday),
                RoundedCornerShape(8.dp)
            )

    ) {
        Text(text = dayText)
    }
}