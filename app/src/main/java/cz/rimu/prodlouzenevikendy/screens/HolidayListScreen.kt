package cz.rimu.prodlouzenevikendy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import cz.rimu.prodlouzenevikendy.model.ExtendedPublicHoliday
import cz.rimu.prodlouzenevikendy.model.Recommendation
import cz.rimu.prodlouzenevikendy.model.toLocalDate
import cz.rimu.prodlouzenevikendy.model.toYearMonth
import cz.rimu.prodlouzenevikendy.presentation.HolidayListViewModel
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberCalendarState
import io.github.boguszpawlowski.composecalendar.selection.EmptySelectionState
import kiwi.orbit.compose.icons.Icons
import kiwi.orbit.compose.ui.OrbitTheme
import kiwi.orbit.compose.ui.controls.BadgeCircleInfo
import kiwi.orbit.compose.ui.controls.ButtonCriticalSubtle
import kiwi.orbit.compose.ui.controls.ButtonPrimary
import kiwi.orbit.compose.ui.controls.ButtonPrimarySubtle
import kiwi.orbit.compose.ui.controls.Card
import kiwi.orbit.compose.ui.controls.CircularProgressIndicator
import kiwi.orbit.compose.ui.controls.Icon
import kiwi.orbit.compose.ui.controls.Scaffold
import kiwi.orbit.compose.ui.controls.Text
import kiwi.orbit.compose.ui.controls.TopAppBar
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.util.Date
import java.util.Locale

@Composable
fun HolidayListScreen(goBack: () -> Unit) {
    val viewModel = getViewModel<HolidayListViewModel>()
    val state = viewModel.state.collectAsState()
    HolidayListScreenImpl(
        publicHolidays = state.value.extendedPublicHolidays,
        isLoading = state.value.isLoading,
        vacationDaysLeft = state.value.vacationDaysLeft,
        goBack = goBack,
        toggleHolidayVisibility = viewModel::toggleHolidayVisibility,
        toggleCalendarSelection = viewModel::toggleCalendarSelection
    )
}

@Composable
private fun HolidayListScreenImpl(
    publicHolidays: List<ExtendedPublicHoliday> = emptyList(),
    isLoading: Boolean = false,
    goBack: () -> Unit = {},
    toggleHolidayVisibility: (Int) -> Unit = {},
    toggleCalendarSelection: (Int, Int, Boolean) -> Unit = { _, _, _ -> },
    vacationDaysLeft: Int = 0
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Prodloužené víkendy") },
                onNavigateUp = goBack
            )
        },
        actionLayout = {
            Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                Text(
                    text = "Zbyva: $vacationDaysLeft",
                    style = OrbitTheme.typography.title5,
                    color = OrbitTheme.colors.primary.normal,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                ButtonPrimary(onClick = {}) {
                    Text(text = "Podivat se na vysledky")
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(publicHolidays.size) { index ->
                    if (publicHolidays[index].recommendedDays.isNotEmpty()) {
                        HolidayRow(
                            index = index,
                            isVisible = publicHolidays[index].isVisible,
                            name = publicHolidays[index].name,
                            localName = publicHolidays[index].localName,
                            numberOfDays = publicHolidays[index].recommendedDays.size,
                            date = publicHolidays[index].date,
                            toggleHolidayVisibility = toggleHolidayVisibility
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (publicHolidays[index].isVisible) {
                            publicHolidays[index].recommendedDays.fastForEachIndexed { calendarIndex, localDates ->
                                StaticCalendar(
                                    calendarState = rememberCalendarState(
                                        initialMonth = publicHolidays[index].date?.toYearMonth()
                                            ?: YearMonth.now()
                                    ),
                                    monthHeader = { month ->
                                        MonthHeader(
                                            month = month,
                                            toggleCalendarSelection = toggleCalendarSelection,
                                            index = index,
                                            calendarIndex = calendarIndex,
                                            isSelected = localDates.isSelected
                                        )
                                    },
                                    dayContent = { day ->
                                        DayContent(
                                            day = day,
                                            recommendation = localDates,
                                            publicHolidays = publicHolidays
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HolidayRow(
    index: Int,
    isVisible: Boolean,
    name: String,
    localName: String,
    numberOfDays: Int,
    date: Date?,
    toggleHolidayVisibility: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(OrbitTheme.colors.info.subtle)
            .clickable { toggleHolidayVisibility(index) }
    ) {
        IconToggleButton(
            checked = isVisible,
            onCheckedChange = { toggleHolidayVisibility(index) }) {
            if (isVisible) {
                Icon(
                    painter = Icons.ArrowUp,
                    tint = OrbitTheme.colors.content.normal,
                    contentDescription = "show/hide"
                )
            } else {
                Icon(
                    painter = Icons.ArrowDown,
                    tint = OrbitTheme.colors.content.normal,
                    contentDescription = "show/hide"
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1.0f)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                color = OrbitTheme.colors.content.normal,
                style = OrbitTheme.typography.title5,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = localName,
                color = OrbitTheme.colors.content.normal,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(8.dp)
        ) {
            BadgeCircleInfo(value = numberOfDays)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = date?.toLocalDate()
                    ?.toShortString()
                    ?: "",
                color = OrbitTheme.colors.content.normal,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun MonthHeader(
    month: MonthState,
    toggleCalendarSelection: (Int, Int, Boolean) -> Unit = { _, _, _ -> },
    index: Int,
    calendarIndex: Int,
    isSelected: Boolean
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
        if (isSelected) {
            ButtonCriticalSubtle(onClick = {
                toggleCalendarSelection(
                    index,
                    calendarIndex,
                    false
                )
            }) {
                Text(text = "Odebrat z kalendáře")
            }
        } else {
            ButtonPrimarySubtle(onClick = {
                toggleCalendarSelection(
                    index,
                    calendarIndex,
                    true
                )
            }) {
                Text(text = "Přidat do kalendáře")
            }
        }
    }
}

@Composable
private fun DayContent(
    day: DayState<EmptySelectionState>,
    recommendation: Recommendation,
    publicHolidays: List<ExtendedPublicHoliday>
) {
    if (recommendation.days.contains(day.date)) {
        if (publicHolidays.find { it.date?.toLocalDate() == day.date } != null) {
            OneDayBox(
                day.date.dayOfMonth.toString(),
                isSelected = true,
                isHoliday = true
            )
        } else {
            OneDayBox(
                day.date.dayOfMonth.toString(),
                isSelected = true
            )
        }
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

private fun LocalDate.toShortString(): String {
    return "${this.dayOfMonth} ${this.month}".lowercase(Locale.getDefault())
}

@Composable
private fun getDaySelectionColor(isSelected: Boolean, isHoliday: Boolean): Color {
    return if (isHoliday && isSelected) OrbitTheme.colors.primary.strong
    else if (isSelected) OrbitTheme.colors.primary.normal
    else Color.Transparent
}

@Preview
@Composable
fun HolidayListScreenPreview() {
    HolidayListScreenImpl(
        publicHolidays = listOf(
            ExtendedPublicHoliday(
                date = Date(),
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                recommendedDays = listOf(),
                isVisible = false
            ),
            ExtendedPublicHoliday(
                date = Date(),
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                recommendedDays = listOf(),
                isVisible = false
            ),
            ExtendedPublicHoliday(
                date = Date(),
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                recommendedDays = listOf(),
                isVisible = false
            ),
            ExtendedPublicHoliday(
                date = Date(),
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                recommendedDays = listOf(),
                isVisible = false
            )
        ),
        isLoading = false,
        goBack = {}
    )
}

@Preview
@Composable
fun HolidayListScreenPreviewLoading() {
    HolidayListScreenImpl(
        isLoading = true
    )
}
