package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Long = 0,
    val shares: Long = 0,
    val viewed: Long = 0,
    val video: String? = null
) {
    fun toDto() = Post(id, author, content, published, likedByMe, likes, shares, viewed, video)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likedByMe, dto.likes,
            dto.shares, dto.viewed, dto.video)

    }
}

