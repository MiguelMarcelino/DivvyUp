package org.arcane.divvyup.dbconnector

import org.arcane.divvyup.data.identifiers.IIdentifier

interface Connector<A> {

    fun  <T : IIdentifier> getItem(identifier: T): A?

    fun getItems(): List<A>

    fun addItem(item: A)

    fun updateItem(item: A)

    fun <T : IIdentifier> deleteItem(identifier: T)
}
