package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val author: String,
    var authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Long = 0,
    val shares: Long = 0,
    val viewed: Long = 0,
    val saved: Boolean,
    @Embedded
    val attachment: Attachment? = null
) {
    fun toDto() = Post(id, author, authorAvatar, content,
        published, likedByMe, likes, shares, viewed, saved, attachment)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.authorAvatar,
                dto.content, dto.published, dto.likedByMe, dto.likes,
            dto.shares, dto.viewed, dto.saved, dto.attachment)

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)

