package com.example.smartcampuscompanion.data

import com.example.smartcampuscompanion.R
import com.example.smartcampuscompanion.model.Department

object CampusData {
    val departments = listOf(
        Department(
            name = "Computer Science",
            contact = "cs@university.edu",
            imageRes = R.drawable.ccs
        ),
        Department(
            name = "Engineering",
            contact = "eng@university.edu",
            imageRes = R.drawable.eng
        ),
        Department(
            name = "Business",
            contact = "business@university.edu",
            imageRes = R.drawable.business
        ),
        Department(
            name = "Registrar",
            contact = "registrar@university.edu",
            imageRes = R.drawable.registrar
        )
    )
}
