package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import ru.netology.nmedia.dto.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
    companion object {
        val DDL = """
        CREATE TABLE ${PostColumns.TABLE} (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
            ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_SHARES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_VIEWES} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_VIDEO} TEXT DEFAULT NULL
        );
        """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_SHARES = "shares"
        const val COLUMN_VIEWES = "views"
        const val COLUMN_VIDEO = "video"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_LIKED_BY_ME,
            COLUMN_LIKES,
            COLUMN_SHARES,
            COLUMN_VIEWES,
            COLUMN_VIDEO
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(makePostFromCursor(it))
            }
            it.moveToFirst()
        }
        return posts
    }

    override fun onSaveButtonClick(post: Post):Post {
        val values = ContentValues( ).apply {
            if (post.id == 0L) {
                put(PostColumns.COLUMN_AUTHOR, "Me")
                put(PostColumns.COLUMN_CONTENT, post.content)
                put(PostColumns.COLUMN_PUBLISHED, "now")
            } else {
                put(PostColumns.COLUMN_ID,post.id)
                put(PostColumns.COLUMN_AUTHOR, post.author)
                put(PostColumns.COLUMN_CONTENT, post.content)
                put(PostColumns.COLUMN_PUBLISHED, post.published)
                put(PostColumns.COLUMN_LIKED_BY_ME, post.likedByMe)
                put(PostColumns.COLUMN_LIKES, post.likes)
                put(PostColumns.COLUMN_SHARES, post.shares)
                put(PostColumns.COLUMN_VIEWES, post.viewed)
                put(PostColumns.COLUMN_VIDEO, post.video)
            }
        }
        val id = db.replace(PostColumns.TABLE,  null, values)
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        ).use {
            it.moveToNext()
            return makePostFromCursor(it)
        }
        return post
    }

    override fun onLikeButtonClick(id: Long) {
        db.execSQL(
            """
           UPDATE posts SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }

    override fun onRemoveClick (id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun onShareButtonClick(id: Long) {
        db.execSQL(
            """
           UPDATE posts 
           SET  shares = shares + CASE WHEN 0 THEN 1 ELSE 1 END
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }

    private fun makePostFromCursor(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                likes = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
                shares = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_SHARES)),
                viewed = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_VIEWES)),
                video = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO))
            )
        }
    }

    override fun default(){
        postsDefault.forEach {
            val values = ContentValues().apply {
                put(PostColumns.COLUMN_ID, it.id)
                put(PostColumns.COLUMN_AUTHOR, it.author)
                put(PostColumns.COLUMN_CONTENT, it.content)
                put(PostColumns.COLUMN_PUBLISHED, it.published)
                put(PostColumns.COLUMN_LIKED_BY_ME, it.likedByMe)
                put(PostColumns.COLUMN_LIKES, it.likes)
                put(PostColumns.COLUMN_SHARES, it.shares)
                put(PostColumns.COLUMN_VIEWES, it.viewed)
                put(PostColumns.COLUMN_VIDEO, it.video)
            }
            val id = db.replace(PostColumns.TABLE, null, values)
            db.query(
                PostColumns.TABLE,
                PostColumns.ALL_COLUMNS,
                "${PostColumns.COLUMN_ID} = ?",
                arrayOf(id.toString()),
                null,
                null,
                null,
            ).use {
                it.moveToLast()
            }
        }

    }
    val postsDefault = listOf(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась " +
                    "с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, " +
                    "разработке, аналитике и управлению. Мы растём сами и помогаем расти " +
                    "студентам: от новичков до уверенных профессионалов. Но самое важное " +
                    "остаётся с нами: мы верим, что в каждом уже есть сила, которая " +
                    "заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — " +
                    "помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 105,
            shares = 26,
            viewed = 500,
            video = null
        ), Post(
            id = 2,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех: на следующей неделе разбираемся с " +
                    "разработкой мобильных приложений, учимся рассказывать истории " +
                    "и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
            published = "18 сентября в 10:12",
            likedByMe = false,
            likes = 155,
            shares = 22456,
            viewed = 550,
            video = "https://www.youtube.com/watch?v=WhWc3b3KhnY"
        ),
        Post(
            id = 3,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась " +
                    "с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, " +
                    "разработке, аналитике и управлению. Мы растём сами и помогаем расти " +
                    "студентам: от новичков до уверенных профессионалов. Но самое важное " +
                    "остаётся с нами: мы верим, что в каждом уже есть сила, которая " +
                    "заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — " +
                    "помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 105,
            shares = 22487576,
            viewed = 500,
            video = null
        ))
}
