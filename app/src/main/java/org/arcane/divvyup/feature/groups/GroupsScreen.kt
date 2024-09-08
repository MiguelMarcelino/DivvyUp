package org.arcane.divvyup.feature.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import org.arcane.divvyup.R
import org.arcane.divvyup.base.GroupNavigationEvent
import org.arcane.divvyup.base.NavigationEvent
import org.arcane.divvyup.data.model.Group
import org.arcane.divvyup.ui.theme.LightGrey
import org.arcane.divvyup.ui.theme.Typography
import org.arcane.divvyup.ui.theme.Zinc
import org.arcane.divvyup.utils.Utils
import org.arcane.divvyup.widget.TextView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(navController: NavController, viewModel: GroupsScreenViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> navController.popBackStack()
                GroupNavigationEvent.NavigateToAddGroup -> {
                    navController.navigate("/add_group")
                }

                else -> {}
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, card, topBar, add) = createRefs()
            val searchQuery = remember {
                mutableStateOf("")
            }
            var state = viewModel.getGroups()

            // Top Image
            Image(painter = painterResource(id = R.drawable.ic_topbar), contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

            // Welcoming message
            Spacer(modifier = Modifier.size(8.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                .constrainAs(nameRow) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    TextView(
                        text = "Build your Groups!",
                        style = Typography.bodyMedium,
                        color = Color.White
                    )
                    TextView(
                        text = "CodeWithFK",
                        style = Typography.titleLarge,
                        color = Color.White
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                    .constrainAs(card) {
                        top.linkTo(nameRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(card.start)
                    },
                color = Color.Transparent,
            ) {
                SearchBar(
                    query = searchQuery.value,
                    onQueryChange = {
                        searchQuery.value = it
                    },
                    onSearch = {
                        searchQuery.value = it
                        state = viewModel.searchGroupsByName(it)
                    },
                    active = false,
                    onActiveChange = {
                        viewModel.getGroups()
                    },
                    placeholder = {
                        TextView(text = "Search for a group", color = LightGrey)
                    },
                ) {}
            }

            // Group list
            GroupList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(list) {
                        top.linkTo(card.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
                list = state
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(add) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }, contentAlignment = Alignment.BottomEnd
            ) {
                AddGroupActionButton(modifier = Modifier) {
                    viewModel.onEvent(GroupsUiEvent.OnAddGroupClicked)
                }
            }
        }
    }
}

@Composable
fun GroupList(
    modifier: Modifier,
    list: List<Group>
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = list,
            key = { item -> item.uid }) { item ->
            val icon = Utils.getGroupIcon(item)

            GroupItem(
                name = item.name,
                description = item.description,
                icon = icon,
                ownerUid = item.ownerUid,
                updatedAt = item.updatedAt
            )
        }
    }

}

@Composable
fun GroupItem(
    name: String,
    description: String?,
    icon: Int,
    ownerUid: String,
    updatedAt: Timestamp
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(51.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                TextView(text = name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.size(6.dp))
                when (description) {
                    null -> Spacer(modifier = Modifier.size(0.dp))
                    else -> {
                        TextView(
                            text = description,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                    }
                }
                TextView(
                    text = Utils.formatDateToHumanReadableForm(updatedAt.seconds * 1000),
                    fontSize = 13.sp,
                    color = LightGrey
                )
            }
        }
        TextView(
            text = ownerUid,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
    Spacer(modifier = Modifier.size(16.dp))
}

@Composable
fun AddGroupActionButton(
    modifier: Modifier,
    onAddGroupClicked: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main FAB
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = Zinc)
                    .clickable {
                        onAddGroupClicked.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_addgroup),
                    contentDescription = "small floating action button",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
