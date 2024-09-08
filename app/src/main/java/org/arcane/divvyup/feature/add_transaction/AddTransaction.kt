@file:OptIn(ExperimentalMaterial3Api::class)

package org.arcane.divvyup.feature.add_transaction

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp
import org.arcane.divvyup.R
import org.arcane.divvyup.base.AddTransactionNavigationEvent
import org.arcane.divvyup.base.NavigationEvent
import org.arcane.divvyup.utils.Utils
import org.arcane.divvyup.ui.theme.InterFontFamily
import org.arcane.divvyup.ui.theme.LightGrey
import org.arcane.divvyup.ui.theme.Typography
import org.arcane.divvyup.widget.TextView
import org.arcane.divvyup.data.model.Transaction
import org.arcane.divvyup.data.model.RecurrenceInterval
import org.arcane.divvyup.data.model.RecurrentTransaction
import org.arcane.divvyup.data.model.TransactionType
import java.util.Date
import java.util.Locale

@Composable
fun AddTransaction(
    navController: NavController,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val menuExpanded = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> navController.popBackStack()
                AddTransactionNavigationEvent.MenuOpenedClicked -> {
                    menuExpanded.value = true
                }
                else->{}
            }
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, card, topBar) = createRefs()
            Image(painter = painterResource(id = R.drawable.ic_topbar),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                .constrainAs(nameRow) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                Image(painter = painterResource(id = R.drawable.ic_back), contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            viewModel.onEvent(AddTransactionUiEvent.OnBackPressed)
                        })
                TextView(
                    text = "New balance",
                    style = Typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Image(
                        painter = painterResource(id = R.drawable.dots_menu),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                viewModel.onEvent(AddTransactionUiEvent.OnMenuClicked)
                            }
                    )
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { TextView(text = "Profile") },
                            onClick = {
                                menuExpanded.value = false
                                // Navigate to profile screen
                                // navController.navigate("profile_route")
                            }
                        )
                        DropdownMenuItem(
                            text = { TextView(text = "Settings") },
                            onClick = {
                                menuExpanded.value = false
                                // Navigate to settings screen
                                // navController.navigate("settings_route")
                            }
                        )
                    }
                }

            }
            DataForm(modifier = Modifier.constrainAs(card) {
                top.linkTo(nameRow.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }, onAddTransactionClick = { transaction, recurrentTransaction ->
                viewModel.onEvent(AddTransactionUiEvent.OnAddTransactionClicked(transaction, recurrentTransaction))
            })
        }
    }
}

@Composable
fun DataForm(
    modifier: Modifier,
    onAddTransactionClick: (model: Transaction, recurrentTransaction: RecurrentTransaction?) -> Unit
) {

    val name = remember {
        mutableStateOf("")
    }
    val amount = remember {
        mutableStateOf("")
    }
    val date = remember {
        mutableLongStateOf(0L)
    }
    val dateDialogVisibility = remember {
        mutableStateOf(false)
    }
    val type = remember {
        mutableStateOf("OTHER")
    }
    val isRecurrent = remember {
        mutableStateOf(false)
    }
    val recurrenceInterval = remember {
        mutableStateOf(RecurrenceInterval.DAILY)
    }
    val recurrenceDate = remember {
        mutableLongStateOf(0L)
    }
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .shadow(16.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .heightIn(min = 800.dp)
            .animateContentSize()
    ) {
        TitleComponent(title = "name")
        OutlinedTextField(
            value = name.value,
            onValueChange = { newValue ->
                name.value = newValue
            }, textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = { TextView(text = "Enter Name") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent(title = "type")
        DropDownMenu(
            TransactionType.entries.map { it.name },
            onItemSelected = {
                type.value = it
            })
        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent("amount")
        OutlinedTextField(
            value = amount.value,
            onValueChange = { newValue ->
                amount.value = newValue.filter { it.isDigit() || it == '.' }
            }, textStyle = TextStyle(color = Color.Black),
            visualTransformation = { text ->
                val out = "$" + text.text
                val currencyOffsetTranslator = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int {
                        return offset + 1
                    }

                    override fun transformedToOriginal(offset: Int): Int {
                        return if (offset > 0) offset - 1 else 0
                    }
                }

                TransformedText(AnnotatedString(out), currencyOffsetTranslator)
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { TextView(text = "Enter amount") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
            )
        )
        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent(title = "Recurrent Expense")
        DropDownMenu(
            listOf("No", "Yes"),
            onItemSelected = {
                isRecurrent.value = it == "Yes"
            })
        Spacer(modifier = Modifier.size(24.dp))
        when(isRecurrent.value) {
            true -> {
                modifier.heightIn(min = 800.dp)
                // Recurrence Interval
                TitleComponent("interval")
                DropDownMenu(
                    RecurrenceInterval.entries.map { it.expenseName },
                    onItemSelected = {
                        recurrenceInterval.value = RecurrenceInterval.valueOf(it.uppercase(Locale.getDefault()))
                    },)
                Spacer(modifier = Modifier.size(24.dp))
                // End date
                TitleComponent("end date")
                OutlinedTextField(
                    value = if (recurrenceDate.longValue == 0L) "" else Utils.formatDateToHumanReadableForm(
                        recurrenceDate.longValue
                    ),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dateDialogVisibility.value = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                        disabledPlaceholderColor = Color.Black,
                    ),
                    placeholder = { TextView(text = "Select date") }
                )
            }
            false -> {
                // Single date
                TitleComponent("date")
                OutlinedTextField(
                    value = if (date.longValue == 0L) "" else Utils.formatDateToHumanReadableForm(
                        date.longValue
                    ),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dateDialogVisibility.value = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                        disabledPlaceholderColor = Color.Black,
                    ),
                    placeholder = { TextView(text = "Select date") }
                )
                modifier.height(500.dp)
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = {
                val model = Transaction(
                    title = name.value,
                    amount = amount.value.toDoubleOrNull() ?: 0.0,
                    type = type.value.let { TransactionType.valueOf(it) },
                    description = name.value,
                    currency = "",
                    status = "",
                    tags = listOf(),
                    share = mapOf(),
                    ownerUid = "",
                    groupUid = "",
                    userUids = listOf(),
                )
                val recurrentTransaction = when(isRecurrent.value) {
                    true ->
                        RecurrentTransaction(
                            recurrenceInterval.value,
                            Timestamp(
                                Date(recurrenceDate.longValue)
                            )
                        )
                    false -> null
                }
                onAddTransactionClick(model, recurrentTransaction)
            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        ) {
            TextView(
                text = "Add Expense",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
    if (dateDialogVisibility.value) {
        val dateToSet = if(isRecurrent.value) recurrenceDate else date
        setDate(dateToSet, dateDialogVisibility)
    }
}

@Composable
fun setDate(date: MutableLongState, dateDialogVisibility: MutableState<Boolean>) {
    ExpenseDatePickerDialog(onDateSelected = {
        date.longValue = it
        dateDialogVisibility.value = false
    }, onDismiss = {
        dateDialogVisibility.value = false
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePickerDialog(
    onDateSelected: (date: Long) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis ?: 0L
    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        TextButton(onClick = { onDateSelected(selectedDate) }) {
            TextView(text = "Confirm")
        }
    }, dismissButton = {
        TextButton(onClick = { onDateSelected(selectedDate) }) {
            TextView(text = "Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun TitleComponent(title: String) {
    TextView(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = LightGrey
    )
    Spacer(modifier = Modifier.size(10.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(listOfItems: List<String>, onItemSelected: (item: String) -> Unit) {
    val expanded = remember {
        mutableStateOf(false)
    }
    val selectedItem = remember {
        mutableStateOf(listOfItems[0])
    }
    ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
        OutlinedTextField(
            value = selectedItem.value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            textStyle = TextStyle(fontFamily = InterFontFamily, color = Color.Black),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black, disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,

            )
        )
        ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { }) {
            listOfItems.forEach {
                DropdownMenuItem(text = { TextView(text = it) }, onClick = {
                    selectedItem.value = it
                    onItemSelected(selectedItem.value)
                    expanded.value = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddExpense() {
    AddTransaction(rememberNavController())
}

