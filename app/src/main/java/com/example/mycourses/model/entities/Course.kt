package com.example.mycourses.model.entities

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.example.mycourses.extensions.toBrazilianCurrency
import com.google.firebase.firestore.DocumentSnapshot
import java.math.BigDecimal
import java.util.UUID

data class Course(
    val id: String = UUID.randomUUID().toString(),
    val name: String =  "",
    val category: String =  "",
    val price: String = "",
    val description: String = "",
    val image: String? = null,
    val imageUri: Uri? = null,
    val rate: Int = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString()!!,
        name = parcel.readString()!!,
        price = parcel.readString()!!,
        description = parcel.readString()!!,
        image = parcel.readString(),
        rate = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeInt(rate)
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
    rate = document["rate"] as Int
)
