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
        goBack = goBack,
        toggleHolidayVisibility = viewModel::toggleHolidayVisibility,
        onCalendarSelected = viewModel::onCalendarSelected
    )
}

@Composable
private fun HolidayListScreenImpl(
    publicHolidays: List<ExtendedPublicHoliday> = emptyList(),
    isLoading: Boolean = false,
    goBack: () -> Unit = {},
    toggleHolidayVisibility: (Int) -> Unit = {},
    onCalendarSelected: (Int, Int) -> Unit = { _, _ -> }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Prodloužené víkendy") },
                onNavigateUp = goBack
            )
        }) { paddingValues ->

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
                            publicHolidays = publicHolidays,
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
                                            onCalendarSelected = onCalendarSelected,
                                            index = index,
                                            calendarIndex = calendarIndex
                                        )
                                    },
                                    dayContent = { day ->
                                        DayContent(
                                            day = day,
                                            localDates = localDates,
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
    publicHolidays: List<ExtendedPublicHoliday>,
    toggleHolidayVisibility: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(OrbitTheme.colors.primary.normal)
            .clickable { toggleHolidayVisibility(index) }
    ) {
        IconToggleButton(
            checked = publicHolidays[index].isVisible,
            onCheckedChange = { toggleHolidayVisibility(index) }) {
            if (publicHolidays[index].isVisible) {
                Icon(
                    painter = Icons.ArrowUp,
                    tint = OrbitTheme.colors.primary.onNormal,
                    contentDescription = "show/hide"
                )
            } else {
                Icon(
                    painter = Icons.ArrowDown,
                    tint = OrbitTheme.colors.primary.onNormal,
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
                text = publicHolidays[index].name,
                color = OrbitTheme.colors.primary.onNormal,
                style = OrbitTheme.typography.title5,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = publicHolidays[index].localName,
                color = OrbitTheme.colors.primary.onNormal,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(8.dp)
        ) {
            BadgeCircleInfo(value = publicHolidays[index].recommendedDays.size)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = publicHolidays[index].date?.toLocalDate()
                    ?.toShortString()
                    ?: "",
                color = OrbitTheme.colors.primary.onNormal,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun MonthHeader(
    month: MonthState,
    onCalendarSelected: (Int, Int) -> Unit = { _, _ -> },
    index: Int,
    calendarIndex: Int
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
        ButtonPrimarySubtle(onClick = {
            onCalendarSelected(
                index,
                calendarIndex
            )
        }) {
            Text(text = "Přidat do kalendáře")
        }
    }
}

@Composable
private fun DayContent(
    day: DayState<EmptySelectionState>,
    localDates: List<LocalDate>,
    publicHolidays: List<ExtendedPublicHoliday>
) {
    if (localDates.contains(day.date)) {
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
