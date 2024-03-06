package cz.rimu.prodlouzenevikendy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import cz.rimu.prodlouzenevikendy.model.ExtendedPublicHoliday
import cz.rimu.prodlouzenevikendy.model.toLocalDate
import cz.rimu.prodlouzenevikendy.model.toYearMonth
import cz.rimu.prodlouzenevikendy.presentation.AllHolidaysViewModel
import org.koin.androidx.compose.getViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HolidayListScreen(goBack: () -> Unit) {
    val viewModel = getViewModel<AllHolidaysViewModel>()
    val publicHolidays = viewModel.extendedPublicHolidays.collectAsState()
    HolidayListScreenImpl(publicHolidays.value, goBack)
}

@Composable
private fun HolidayListScreenImpl(
    publicHolidays: List<ExtendedPublicHoliday>,
    goBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.secondary,
            ) {
                IconButton(onClick = goBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Menu"
                    )
                }
                Text(text = "Prodloužené víkendy")
            }
        }) { paddingValues ->
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.padding(
                    top = paddingValues.calculateTopPadding(),
                    start = 8.dp,
                    end = 8.dp
                )
            ) {
                items(publicHolidays.size) { index ->
                    Card(
                        modifier = Modifier
                            .clickable { }
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1.0f)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = publicHolidays[index].name,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Text(
                                    text = publicHolidays[index].localName,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Text(
                                text = publicHolidays[index].date.toString(),
                                modifier = Modifier
                                    .weight(0.4f)
                                    .align(Alignment.CenterVertically)
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val daysOfWeek = daysOfWeek()
                    publicHolidays[index].recommendedDays.forEach { localDates ->
                        Column {
                            DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                            MyCalendar(publicHolidays[index], localDates)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MyCalendar(publicHoliday: ExtendedPublicHoliday, recommendedDates: List<LocalDate>) {
    val currentMonth = remember { publicHoliday.date?.toYearMonth() ?: YearMonth.now() }
    val startMonth =
        remember {
            currentMonth?.minusMonths(currentMonth.monthValue.toLong() - 1) ?: YearMonth.now()
        } // Adjust as needed
    val endMonth =
        remember {
            currentMonth?.plusMonths(12L - currentMonth.monthValue) ?: YearMonth.now()
        } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )
    HorizontalCalendar(
        state = state,
        // Draw the day content gradient.
        monthBody = { _, content ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                content() // Render the provided content!
            }
        },
        // Add the corners/borders and month width.
        monthContainer = { _, container ->
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            Column(
                modifier = Modifier
                    .width(screenWidth * 0.98f)
                    .padding(8.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .border(
                        color = Color.Black,
                        width = 1.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                container() // Render the provided container!
            }
        },
        userScrollEnabled = true,
        calendarScrollPaged = true,
        dayContent = { day ->
            if (day.date == publicHoliday.date?.toLocalDate() || recommendedDates.contains(day.date)) {
                Day(day, isSelected = true)
            } else {
                Day(day)
            }
        }
    )
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}

@Composable
fun Day(day: CalendarDay, isSelected: Boolean = false) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)// This is important for square sizing!
            .background(color = if (isSelected) MaterialTheme.colors.onSurface else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString())
    }
}

@Preview
@Composable
fun HolidayListScreenPreview() {
    HolidayListScreenImpl(
        publicHolidays = listOf(
            ExtendedPublicHoliday(
                    date = null,
                    localName = "Den obnovy samostatného českého státu",
                    name = "Den obnovy samostatného českého státu",
                    recommendedDays = listOf()
            ),
            ExtendedPublicHoliday(
                date = null,
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                recommendedDays = listOf()
            ),
            ExtendedPublicHoliday(
                date = null,
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                recommendedDays = listOf()
            ),
            ExtendedPublicHoliday(
                date = null,
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                recommendedDays = listOf()
            )
        ),
        goBack = {}
    )
}