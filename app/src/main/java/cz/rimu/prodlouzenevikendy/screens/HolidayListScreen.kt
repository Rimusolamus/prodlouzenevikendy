package cz.rimu.prodlouzenevikendy.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.rimu.prodlouzenevikendy.model.PublicHoliday
import cz.rimu.prodlouzenevikendy.presentation.AllHolidaysViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun HolidayListScreen(goBack: () -> Unit) {
    val viewModel = getViewModel<AllHolidaysViewModel>()
    val publicHolidays = viewModel.publicHolidays.collectAsState()
    HolidayListScreenImpl(publicHolidays.value, goBack)
}

@Composable
private fun HolidayListScreenImpl(
    publicHolidays: List<PublicHoliday>,
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
                            text = publicHolidays[index].date,
                            modifier = Modifier
                                .weight(0.4f)
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview
@Composable
fun HolidayListScreenPreview() {
    HolidayListScreenImpl(
        publicHolidays = listOf(
            PublicHoliday(
                date = "2021-01-01",
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                countryCode = "CZ",
                fixed = true,
                global = true,
                counties = null,
                launchYear = null,
                types = listOf("National holiday")
            ),
            PublicHoliday(
                date = "2021-01-01",
                localName = "Den obnovy samostatného českého státu státu státu státu státu státu státu státu státu státu státu státu státu státu státu státu státu státu státu státu státu",
                name = "Den obnovy samostatného českého státu",
                countryCode = "CZ",
                fixed = true,
                global = true,
                counties = null,
                launchYear = null,
                types = listOf("National holiday")
            ),
            PublicHoliday(
                date = "2021-01-01",
                localName = "Den obnovy samostatného českého státu",
                name = "Den obnovy samostatného českého státu",
                countryCode = "CZ",
                fixed = true,
                global = true,
                counties = null,
                launchYear = null,
                types = listOf("National holiday")
            )
        ),
        goBack = {}
    )
}