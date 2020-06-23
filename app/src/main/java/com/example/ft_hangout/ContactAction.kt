package com.example.ft_hangout

enum class ContactAction(val value: Int) {
    UNCHANGED_CONTACT(1),
    NEW_CONTACT(2),
    MODIFIED_CONTACT(3),
    DELETED_CONTACT(4)
}