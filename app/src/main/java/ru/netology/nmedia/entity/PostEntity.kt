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
    val viewes: Long = 0,
    val saved: Boolean,
    val viewed: Boolean,
    @Embedded
    val attachment: Attachment? = null
) {
    fun toDto() = Post(id = id,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        shares = shares,
        viewes = viewes,
        saved= saved,
        viewed = viewed,
        attachment = attachment)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(id =dto.id,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                content = dto.content,
                published = dto.published,
                likedByMe = dto.likedByMe,
                likes = dto.likes,
                shares = dto.shares,
                viewes = dto.viewes,
                viewed = dto.viewed,
                saved = dto.saved,
                attachment = dto.attachment)

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)

