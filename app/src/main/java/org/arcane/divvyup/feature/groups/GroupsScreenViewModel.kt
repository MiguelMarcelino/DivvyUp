package org.arcane.divvyup.feature.groups

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.GroupNavigationEvent
import org.arcane.divvyup.base.UiEvent
import org.arcane.divvyup.data.model.Group
import org.arcane.divvyup.dbconnector.GroupConnector
import org.arcane.divvyup.feature.add_group.AddGroupUiEvent
import javax.inject.Inject

@HiltViewModel
class GroupsScreenViewModel @Inject constructor(private val groupConnector: GroupConnector) : BaseViewModel() {
    override fun onEvent(event: UiEvent) {
        when (event) {
            is GroupsUiEvent.OnAddGroupClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(GroupNavigationEvent.NavigateToAddGroup)
                }
            }
        }
    }

    fun getGroups(): List<Group> {
        // TODO: Get only groups by user
        return groupConnector.getItems()
    }

}

sealed class GroupsUiEvent : UiEvent() {
    data object OnAddGroupClicked : GroupsUiEvent()
}