//package ru.netology.nmedia.dao
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.entity.PostEntity
//import java.io.Closeable
//@Dao
//interface PostDao {
//    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
//    fun getAll(): LiveData<List<PostEntity>>
//
//    @Query("""
//        UPDATE PostEntity SET
//        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
//        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
//        WHERE id = :id
//        """)
//    fun onLikeButtonClick(id: Long)
//
//    @Query(
//        """
//           UPDATE PostEntity
//           SET  shares = shares + CASE WHEN 0 THEN 1 ELSE 1 END
//           WHERE id = :id
//        """
//    )
//    fun onShareButtonClick(id: Long)
//
//    @Query("DELETE FROM PostEntity WHERE id = :id")
//    fun onRemoveClick(id: Long)
//
//    @Insert
//    fun insert(post: PostEntity)
//    @Insert
//    fun insert(posts: List<PostEntity>)
//
//    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
//    fun updateContentById(id: Long, content: String?)
//
//    fun onSaveButtonClick(post: PostEntity) =
//        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)
//
//}


