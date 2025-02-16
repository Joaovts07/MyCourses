package com.example.mycourses.model.entities

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.navigation.NavType
import com.example.mycourses.extensions.toBrazilianCurrency
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import java.math.BigDecimal
import java.util.UUID

data class Course(
    val id: String = UUID.randomUUID().toString(),
    val name: String =  "",
    val category: String =  "",
    val price: String = "",
    val description: String = "",
    val image: String? = null,
    val rating: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString()!!,
        name = parcel.readString()!!,
        price = parcel.readString()!!,
        description = parcel.readString()!!,
        image = parcel.readString(),
        rating = parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeString(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Course> {
        override fun createFromParcel(parcel: Parcel): Course {
            return Course(parcel)
        }

        override fun newArray(size: Int): Array<Course?> {
            return arrayOfNulls(size)
        }
    }
}

fun getCourse(document: DocumentSnapshot) = Course(
    id = document.id,
    name = document["name"] as String,
    description = document["description"] as String,
    image = document["image"] as String?,
    price = BigDecimal(document["price"].toString()).toBrazilianCurrency(),
    rating = document["rate"].toString()
)

class CourseType : NavType<Course>(isNullableAllowed = false) {
    override val name: String
        get() = "course"

    override fun get(bundle: Bundle, key: String): Course? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Course {
        return Gson().fromJson(value, Course::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Course) {
        bundle.putParcelable(key, value)
    }
}
