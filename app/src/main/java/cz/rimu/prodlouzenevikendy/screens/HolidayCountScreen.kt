package cz.rimu.prodlouzenevikendy.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.rimu.prodlouzenevikendy.presentation.HolidayCountViewModel
import cz.rimu.prodlouzenevikendy.ui.theme.ProdlouzeneVikendyTheme
import org.koin.androidx.compose.getViewModel


@Composable
fun HolidayCountScreen(goToHolidayList: () -> Unit) {
    val viewModel = getViewModel<HolidayCountViewModel>()
    HolidayCountScreenImpl(
        goToHolidayList,
        viewModel::onHolidayCountChanged
    )
}

@Composable
private fun HolidayCountScreenImpl(
    goToHolidayList: () -> Unit,
    onHolidayCountChanged: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.secondary,
                elevation = 0.dp
            ) {
                Text(text = "Prodloužené víkendy na rok!")
            }
        },
        bottomBar = {
            Column {
                Button(
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
                var text by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = text,
                    onValueChange = { newValue ->
                        text = newValue.filter { it.isDigit() }.also {
                            onHolidayCountChanged(it.toIntOrNull() ?: 0)
                        }
                    },
                    label = { Text(text = "20") },
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
    ProdlouzeneVikendyTheme {
        HolidayCountScreenImpl(
            onHolidayCountChanged = { },
            goToHolidayList = { }
        )
    }
}