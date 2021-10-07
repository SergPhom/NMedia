package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.Closeable
@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("""
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """)
    suspend fun onLikeButtonClick(id: Long)

    @Query(
        """
           UPDATE PostEntity
           SET  shares = shares + CASE WHEN 0 THEN 1 ELSE 1 END
           WHERE id = :id
        """
    )
    suspend fun onShareButtonClick(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun onRemoveClick(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String?)

    suspend fun onSaveButtonClick(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)


}


