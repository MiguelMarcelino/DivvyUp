package org.arcane.divvyup.feature.add_group

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.arcane.divvyup.base.AddGroupNavigationEvent
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.NavigationEvent
import org.arcane.divvyup.base.UiEvent
import org.arcane.divvyup.data.model.Group
import org.arcane.divvyup.dbconnector.GroupConnector
import javax.inject.Inject

class AddGroupViewModel @Inject constructor(private val groupConnector: GroupConnector) :
    BaseViewModel() {

    private fun addGroup(group: Group): Boolean {
        return try {
            groupConnector.addItem(group)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AddGroupUiEvent.OnAddGroupClicked -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val result = addGroup(event.group)
                        if (result) {
                            _navigationEvent.emit(NavigationEvent.NavigateBack)
                        }
                    }
                }
            }

            is AddGroupUiEvent.OnBackPressed -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            }

            is AddGroupUiEvent.OnMenuClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(AddGroupNavigationEvent.MenuOpenedClicked)
                }
            }
        }
    }
}

sealed class AddGroupUiEvent : UiEvent() {
    data class OnAddGroupClicked(
        val group: Group
    ) : AddGroupUiEvent()

    data object OnBackPressed : AddGroupUiEvent()
    data object OnMenuClicked : AddGroupUiEvent()
}

