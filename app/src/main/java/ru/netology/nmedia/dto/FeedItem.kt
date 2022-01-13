package ru.netology.nmedia.dto

import android.os.Parcel
import android.os.Parcelable
import ru.netology.nmedia.enumeration.AttachmentType

sealed interface FeedItem {
    abstract val id: Long
}

data class DateHeader(
    override val id: Long,
    val date: String
): FeedItem

data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
): FeedItem

data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Long = 0,
    val shares: Long = 0,
    val viewes: Long = 0,
    val viewed: Boolean,
    val saved: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
): Parcelable, FeedItem {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable<Attachment>(Attachment.javaClass.classLoader),
        parcel.readByte() != 0.toByte(),
    )

    fun count(value: Long): String = when(value){
        in 1000..9999 -> "${value/1000}" +
                "${if ((value%1000L)/100L == 0L) "" else { "." + (value%1000)/100} + "K"}"
        in 10000..999999 ->  "${value/1000}K"
        in 1000000..Int.MAX_VALUE -> "${value/1000000}" +
                "${if ((value%1000000L)/100000L == 0L) "" else { "." + (value%1000000)/100000} + "M"}"
        else -> value.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(id)
        parcel.writeString(author)
        parcel.writeString(authorAvatar)
        parcel.writeString(content)
        parcel.writeLong(published)
        parcel.writeByte(if (likedByMe) 1 else 0)
        parcel.writeLong(likes)
        parcel.writeLong(shares)
        parcel.writeLong(viewes)
        parcel.writeByte(if (viewed) 1 else 0)
        parcel.writeByte(if (saved) 1 else 0)
        parcel.writeParcelable(attachment,1)
        parcel.writeByte(if (ownedByMe) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("type")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attachment> {
        override fun createFromParcel(parcel: Parcel): Attachment {
            return Attachment(parcel)
        }

        override fun newArray(size: Int): Array<Attachment?> {
            return arrayOfNulls(size)
        }
    }
}
