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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
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
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
fun SelectedHolidaysScreen(
    onBack: () -> Unit,
    onOpenCalendar: (List<LocalDate>) -> Unit
) {
    val viewModel = koinViewModel<SelectedHolidaysViewModel>()
    val state = viewModel.states.collectAsState()
    SelectedHolidaysScreenImpl(state = state.value, onBack = onBack, onOpenCalendar = onOpenCalendar)
}

@Composable
fun SelectedHolidaysScreenImpl(
    state: SelectedHolidaysViewModel.State,
    onBack: () -> Unit,
    onOpenCalendar: (List<LocalDate>) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = cz.rimu.prodlouzenevikendy.R.string.app_name)) },
                onNavigateUp = onBack
            )
        },
        content = { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (state.selectedHolidays?.isEmpty() == true) stringResource(cz.rimu.prodlouzenevikendy.R.string.no_results) else stringResource(cz.rimu.prodlouzenevikendy.R.string.selected_vacations),
                        style = OrbitTheme.typography.title4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                state.selectedHolidays?.let {
                    itemsIndexed(it) { _, item ->
                        StaticCalendar(
                            calendarState = rememberCalendarState(
                                initialMonth = YearMonth.from(item.days.first())
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
                                    recommendation = item
                                )
                            },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ButtonPrimary (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            onClick = {
                                onOpenCalendar(item.days)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = stringResource(cz.rimu.prodlouzenevikendy.R.string.save_to_calendar))
                            }
                        }
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