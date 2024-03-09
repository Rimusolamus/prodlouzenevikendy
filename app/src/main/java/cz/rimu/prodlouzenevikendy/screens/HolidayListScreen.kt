package cz.rimu.prodlouzenevikendy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import cz.rimu.prodlouzenevikendy.model.ExtendedPublicHoliday
import cz.rimu.prodlouzenevikendy.model.toLocalDate
import cz.rimu.prodlouzenevikendy.model.toYearMonth
import cz.rimu.prodlouzenevikendy.presentation.HolidayListViewModel
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import io.github.boguszpawlowski.composecalendar.rememberCalendarState
import kiwi.orbit.compose.icons.Icons
import kiwi.orbit.compose.ui.OrbitTheme
import kiwi.orbit.compose.ui.controls.BadgeCircleInfo
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
        toggleHolidayVisibility = viewModel::toggleHolidayVisibility
    )
}

@Composable
private fun HolidayListScreenImpl(
    publicHolidays: List<ExtendedPublicHoliday> = emptyList(),
    isLoading: Boolean = false,
    goBack: () -> Unit = {},
    toggleHolidayVisibility: (Int) -> Unit = {}
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
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.padding(
                        top = paddingValues.calculateTopPadding()
                    )
                ) {
                    items(publicHolidays.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OrbitTheme.colors.primary.normal)
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
                        if (publicHolidays[index].isVisible) {
                            publicHolidays[index].recommendedDays.forEach { localDates ->
                                StaticCalendar(
                                    calendarState = rememberCalendarState(
                                        initialMonth = publicHolidays[index].date?.toYearMonth()
                                            ?: YearMonth.now()
                                    ),
                                    dayContent = { day ->
                                        if (localDates.contains(day.date) || publicHolidays[index].date?.toLocalDate() == day.date) {
                                            OneDayBox(
                                                day.date.dayOfMonth.toString(),
                                                isSelected = true
                                            )
                                        } else {
                                            OneDayBox(
                                                day.date.dayOfMonth.toString(),
                                                isSelected = false
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OneDayBox(dayText: String, isSelected: Boolean) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (isSelected) OrbitTheme.colors.primary.normal else Color.Transparent,
                RoundedCornerShape(8.dp)
            )

    ) {
        Text(text = dayText)
    }
}

private fun LocalDate.toShortString(): String {
    return "${this.dayOfMonth} ${this.month}".lowercase(Locale.getDefault())
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
