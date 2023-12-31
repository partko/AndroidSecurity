package com.example.inventory

import android.widget.TextView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle

/**
 * Provides the list of dummy contacts.
 * This sample implements this as constants, but real-life apps should use a database and such.
 */
data class Contact(val name: String) {

    //val icon = R.mipmap.logo_avatar
    val icon = R.mipmap.ic_launcher
    //val icon = Icons.Filled.AccountCircle

    companion object {
        /**
         * Representative invalid contact ID.
         */
        val invalidId = -1

        /**
         * The contact ID.
         */
        val id = "contact_id"

        /**
         * The list of dummy contacts.
         */
        val contacts = arrayOf(
            Contact("Tereasa"),
            Contact("Chang"),
            Contact("Kory"),
            Contact("Clare"),
            Contact("Landon"),
            Contact("Kyle"),
            Contact("Deana"),
            Contact("Daria"),
            Contact("Melisa"),
            Contact("Sammie")
        )

        /**
         * Finds a [Contact] specified by a contact ID.
         *
         * @param id The contact ID. This needs to be a valid ID.
         * @return A [Contact]
         */
        fun byId(id: Int) = contacts[id]
    }
}

fun Contact.bind(textView: TextView) {
    with(textView) {
        text = name
        //setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0)
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0)
    }
}