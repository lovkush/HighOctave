package com.academy.octave

import java.io.Serializable

class UserData : Serializable {
    var name: String? = null
    var addLine1: String? = null
    var addLine2: String? = null
    var state: String? = null
    var email: String? = null
    var contact: String? = null
    var category: String? = null
    var city: String? = null


    constructor(
        city: String?,
        name: String?,
        addLine1: String?,
        addLine2: String?,
        state: String?,
        email: String?,
        contact: String?,
        category: String?,
    ) {
        this.name = name
        this.addLine1 = addLine1
        this.addLine2 = addLine2
        this.state = state
        this.email = email
        this.contact = contact
        this.category = category
        this.city = city
    }

    constructor() {}
}