package org.arcane.divvyup.feature.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.arcane.divvyup.R
import org.arcane.divvyup.feature.add_transaction.DropDownMenu
import org.arcane.divvyup.feature.home.TransactionItem
import org.arcane.divvyup.utils.Utils
import org.arcane.divvyup.feature.home.HomeViewModel
import org.arcane.divvyup.widget.TextView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionListScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state = viewModel.getTransactions()
    var filterType by remember { mutableStateOf("All") }
    var dateRange by remember { mutableStateOf("All Time") }
    var menuExpanded by remember { mutableStateOf(false) }

    val filteredTransactions = when (filterType) {
        "Expense" -> state.filter { it.type.value() < 0 }
        "Income" -> state.filter { it.type.value() > 0 }
        else -> state
    }

    val filteredByDateRange = filteredTransactions.filter { _ ->
        // TODO: Apply date range filter logic here
        true
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                // Back Button
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                )

                // Title
                TextView(
                    text = "Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )

                // Three Dots Menu
                Image(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { menuExpanded = !menuExpanded },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Content area for the transaction list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    // Dropdowns
                    AnimatedVisibility(
                        visible = menuExpanded,
                        enter = slideInVertically(initialOffsetY = { -it / 2 }),
                        exit = slideOutVertically(targetOffsetY = { -it  }),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column {
                            // Type Filter Dropdown
                            DropDownMenu(
                                listOfItems = listOf("All", "Expense", "Income"),
                                onItemSelected = { selected ->
                                    filterType = selected
                                    menuExpanded = false // Close menu after selection
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Date Range Filter Dropdown
                            DropDownMenu(
                                listOfItems = listOf( "Yesterday", "Today", "Last 30 Days", "Last 90 Days", "Last Year"),
                                onItemSelected = { selected ->
                                    dateRange = selected
                                    menuExpanded = false // Close menu after selection
                                }
                            )
                        }
                    }
                }

                items(filteredByDateRange) { item ->
                    val icon = Utils.getItemIcon(item)
                    TransactionItem(
                        title = item.title,
                        amount = item.amount.toString(),
                        icon = icon,
                        date = Utils.formatDateToHumanReadableForm(item.createdAt.seconds*1000),
                        color = if (item.type.value() > 0) Color.Green else Color.Red,
                        Modifier.animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null,
                            placementSpec = tween(100)
                        )
                    )
                }
            }
        }
    }
}
