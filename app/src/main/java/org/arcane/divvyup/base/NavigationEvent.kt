package org.arcane.divvyup.base

sealed class NavigationEvent {
    data object NavigateBack : NavigationEvent()
}

sealed class AddTransactionNavigationEvent : NavigationEvent() {
    data object MenuOpenedClicked : AddTransactionNavigationEvent()
}

sealed class AddGroupNavigationEvent : NavigationEvent() {
    data object MenuOpenedClicked : AddGroupNavigationEvent()
}

sealed class GroupNavigationEvent : NavigationEvent() {
    data object NavigateToAddGroup : GroupNavigationEvent()
}

sealed class HomeNavigationEvent : NavigationEvent() {
    data object NavigateToAddExpense : HomeNavigationEvent()
    data object NavigateToSeeAll : HomeNavigationEvent()
}