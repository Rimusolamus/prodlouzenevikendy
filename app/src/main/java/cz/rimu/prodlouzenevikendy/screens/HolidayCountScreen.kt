package cz.rimu.prodlouzenevikendy.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.rimu.prodlouzenevikendy.presentation.HolidayCountViewModel
import kiwi.orbit.compose.ui.OrbitTheme
import kiwi.orbit.compose.ui.controls.ButtonPrimary
import kiwi.orbit.compose.ui.controls.Scaffold
import kiwi.orbit.compose.ui.controls.Text
import kiwi.orbit.compose.ui.controls.TextField
import kiwi.orbit.compose.ui.controls.TopAppBar
import org.koin.androidx.compose.koinViewModel


@Composable
fun HolidayCountScreen(goToHolidayList: () -> Unit) {
    val viewModel = koinViewModel<HolidayCountViewModel>()
    val state by viewModel.states.collectAsState()
    HolidayCountScreenImpl(
        state.holidayCount,
        goToHolidayList,
        viewModel::onHolidayCountChanged,
    )
}

@Composable
private fun HolidayCountScreenImpl(
    holidayCount: Int,
    goToHolidayList: () -> Unit,
    onHolidayCountChanged: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Prodloužené víkendy na rok!") }
            )
        },
        actionLayout = {
            if (holidayCount == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OrbitTheme.colors.surface.disabled)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Zadejte počet dní dovolené",
                            style = OrbitTheme.typography.title5,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                }
            } else {
                Column {
                    ButtonPrimary(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        onClick = goToHolidayList
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "Jdeme na to")
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Kolik máte volna?")
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Mám narok na ")
                var text by remember { mutableStateOf("20") }
                TextField(
                    value = holidayCount.toString(),
                    onValueChange = { newValue ->
                        text = newValue.filter { it.isDigit() }.also {
                            onHolidayCountChanged(it.toIntOrNull() ?: 0)
                        }
                    },
                    modifier = Modifier.width(64.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )
                Text(text = "dní.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OrbitTheme {
        HolidayCountScreenImpl(
            onHolidayCountChanged = { },
            goToHolidayList = { },
            holidayCount = 20
        )
    }
}