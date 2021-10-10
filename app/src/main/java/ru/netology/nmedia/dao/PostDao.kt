package ru.netology.nmedia.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    fun getById(id: Long): Flow<PostEntity>

    @Query("""
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """)
    suspend fun onLikeButtonClick(id: Long)
//////////////////////////////////////////////////////////////////////////////////////////
    @Query("SELECT COUNT(viewed) as count FROM PostEntity WHERE viewed = 0")
    suspend fun  notViewedCount(): Int

    @Query("UPDATE PostEntity SET viewed = 1")
    suspend fun allViewedTrue()
////////////////////////////////////////////////////////////////////////////////////////////////
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


