package com.example.stepsapp.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepsapp.HealthConnectManager
import com.example.stepsapp.R
import com.example.stepsapp.SharedData.selectedSharedDay
import com.example.stepsapp.StepsTopAppBar
import com.example.stepsapp.ui.navigation.NavigationDestination
import com.example.stepsapp.viewmodel.AppViewModelProvider
import com.example.stepsapp.viewmodel.MainScreenViewModel
import com.example.stepsapp.viewmodel.RequestedRecord
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

object HomeDestination : NavigationDestination {
    override val route = "main"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Log.v("v", "MainScreen")
    val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

    val requestPermissions = rememberLauncherForActivityResult(requestPermissionActivityContract) { granted ->
        if (granted.containsAll(HealthConnectManager.PERMISSIONS)) {
            // Permissions successfully granted
        } else {
            // Lack of required permissions
        }
    }

    viewModel.readDayRecords(requestPermissions)
    val recordsUiState by viewModel.recordsUiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            StepsTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Content(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            itemList = recordsUiState.recordsList,
            requestedRecord = viewModel.requestedRecordState,
            updateDate = viewModel::updateRequestedRecordState,
            deleteDate = viewModel::deleteRecord,
            onSaveClick = viewModel::insertRecord,
            readDayRecords = viewModel.readDayRecords(requestPermissions)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    itemList: List<StepsRecord>,
    requestedRecord: RequestedRecord,
    updateDate: (RequestedRecord) -> Unit,
    deleteDate: (ZonedDateTime, ZonedDateTime) -> Unit,
    onSaveClick: (ZonedDateTime, Long) -> Unit,
    readDayRecords: Unit,
) {
    val mContext = LocalContext.current

    val mCalendar = Calendar.getInstance()
    val mYearPick = mCalendar.get(Calendar.YEAR)
    val mMonthPick = mCalendar.get(Calendar.MONTH)
    val mDayPick = mCalendar.get(Calendar.DAY_OF_MONTH)

    val selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var recordDate by remember { mutableStateOf(
        requestedRecord.selectedDay.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))) }

    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            selectedDate.set(mYear, mMonth, mDayOfMonth)

            val newDate = ZonedDateTime.ofInstant(selectedDate.toInstant(), ZoneId.systemDefault())
            selectedSharedDay = newDate
            updateDate(requestedRecord.copy(selectedDay = newDate.truncatedTo(ChronoUnit.DAYS)))
            recordDate = newDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        }, mYearPick, mMonthPick, mDayPick
    )
    mDatePickerDialog.datePicker.maxDate = mCalendar.timeInMillis

    val interactionSource = remember { MutableInteractionSource() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
        ) {
            IconButton(onClick = {
                val newDate = requestedRecord.selectedDay.minusDays(1)
                selectedSharedDay = newDate
                recordDate = newDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                updateDate(requestedRecord.copy(selectedDay = newDate))
            }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_keyboard_arrow_left_24),
                    contentDescription = "previous day"
                )
            }
            Spacer(Modifier.weight(1f))
            BasicTextField(
                value = recordDate,
                textStyle = TextStyle(Color.White),
                onValueChange = { recordDate = it },
                modifier = Modifier
                    .height(36.dp).clickable {
                        mDatePickerDialog.show()
                    },
                singleLine = true,
                interactionSource = interactionSource,
                enabled = false
            ) { innerTextField ->
                TextFieldDefaults.OutlinedTextFieldDecorationBox(
                    value = recordDate,
                    innerTextField = innerTextField,
                    enabled = false,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_calendar_today_24),
                            contentDescription = "calendar"
                        )
                    },
                    interactionSource = interactionSource,
                    contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                        top = 0.dp,
                        bottom = 0.dp
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        //For Icons
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {
                val newDate = requestedRecord.selectedDay.plusDays(1)
                selectedSharedDay = newDate
                recordDate = newDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                updateDate(requestedRecord.copy(selectedDay = newDate))
            },
                enabled = requestedRecord.selectedDay.plusDays(1)
                    .isBefore(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)) ||
                        requestedRecord.selectedDay.plusDays(1)
                            .isEqual(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS))
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_keyboard_arrow_right_24),
                    contentDescription = "next day"
                )
            }
        }

        if (itemList.isEmpty()){
            Text(
                text = "Steps: 0",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
        else {
            Text(
                text = "Steps: ${requestedRecord.stepsOverall}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }

        var newStepsData by remember { mutableStateOf("0") }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                OutlinedTextField(
                    value = newStepsData,
                    onValueChange = {
                        newStepsData = it
                    },
                    label = { Text("Steps") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    enabled = true,
                    singleLine = true
                )
            }
            Button(
                onClick = {
                    onSaveClick(selectedSharedDay, newStepsData.toLong())
                    readDayRecords
                },
                enabled = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Set steps")
            }
            Button(
                onClick = {
                    onSaveClick(selectedSharedDay, newStepsData.toLong() + requestedRecord.stepsOverall)
                },
                enabled = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add steps")
            }
            Button(
                onClick = {
                    onSaveClick(selectedSharedDay, if (requestedRecord.stepsOverall > newStepsData.toLong()) requestedRecord.stepsOverall - newStepsData.toLong() else 0)
                },
                enabled = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Remove steps")
            }
            Button(
                onClick = {
                    deleteDate(selectedSharedDay.plusDays(1).minusMinutes(1), selectedSharedDay.plusDays(1))
                },
                enabled = true,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Delete steps")
            }
        }
    }
}
