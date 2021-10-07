package ru.netology.nmedia.repository

import androidx.lifecycle.*
import retrofit2.*
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.*
import java.io.IOException

class PostRepositoryImpl(private val dao: PostDao): PostRepository {
    override val data = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll(){
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun savePost(post: Post) {
        try {
            val lessId = data.value?.minOf { it.id }
            if(post.id == 0L && lessId != null){
                dao.insert(PostEntity.fromDto(post.copy(id = lessId - 2L)))
            }else dao.insert(PostEntity.fromDto(post))
            val _post = if(post.id < 0) post.copy(id = 0L) else post
            val response = PostsApi.retrofitService.save(_post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            println("${post.id}")                                                              //
            println("${data.value}")                                                         //
            dao.onRemoveClick(post.id)
            dao.insert(PostEntity.fromDto(body))
            println("${data.value}")                                                         //
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
//                                                                     ***LIKE/DISLIKE****
    override suspend fun likeById(id: Long){
    try {
        val response = PostsApi.retrofitService.likeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }

        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(PostEntity.fromDto(body))
    } catch (e: IOException) {
        throw NetworkError
    } catch (e: Exception) {
        throw UnknownError
    }
}

    override suspend fun dislikeById(id: Long){
        try {
            val response = PostsApi.retrofitService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

//                                                                                ****REMOVE*****
    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.onRemoveClick(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
//                                                                             *******SHARE*****
    override suspend fun sharePost(id: Long) {
        try {
            val response = PostsApi.retrofitService.shareById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
//                                                                 ***FILL SERVER****
    override suspend fun fill(){
//        postsDefault.forEach{
//            savePostAsync(it, callback)
//        }
    }
}
val postsDefault = listOf(
    Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        authorAvatar = "",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась " +
                "с интенсивов по онлайн-маркетингу. " +
//                "Затем появились курсы по дизайну, " +
//                "разработке, аналитике и управлению. Мы растём сами и помогаем расти " +
//                "студентам: от новичков до уверенных профессионалов. Но самое важное " +
//                "остаётся с нами: мы верим, что в каждом уже есть сила, которая " +
//                "заставляет хотеть больше, целиться выше, бежать быстрее." +
                " Наша миссия — " +
                "помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = 21052020L,
        likedByMe = false,
        likes = 105,
        shares = 26,
        viewed = 500,
        attachment = Attachment(type = AttachmentType.IMAGE,
            url = "https://e7.pngegg.com/pngimages/198/669/png-clipart-munchkin-cat-persian-cat-kitten-kitten-cat-like-mammal-animals.png",
//            url = "https://img11.postila.ru/data/c5/d9/65/04/c5d96504c4a5ff1c67c05de7adb44a6505ea521ee3a7a9caa41976a9dd48a28a.png",
            description = "netology image", )
    ), Post(
        id = 2,
        author = "Нетология. Университет интернет-профессий будущего",
        authorAvatar = "",
        content = "Знаний хватит на всех: на следующей неделе разбираемся с " +
                "разработкой мобильных приложений, учимся рассказывать истории " +
                "и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
        published = 18101012L,
        likedByMe = false,
        likes = 155,
        shares = 22456,
        viewed = 550,
        attachment = Attachment(type = AttachmentType.VIDEO,
            url = "https://www.youtube.com/watch?v=WhWc3b3KhnY", description = "youtube video")
    ),
    Post(
        id = 3,
        author = "Нетология. Университет интернет-профессий будущего",
        authorAvatar = "",
        content = "Привет, это новая Нетология! " +
//                "Когда-то Нетология начиналась " +
//                "с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, " +
//                "разработке, аналитике и управлению. Мы растём сами и помогаем расти " +
//                "студентам: от новичков до уверенных профессионалов. Но самое важное " +
//                "остаётся с нами: мы верим, что в каждом уже есть сила, которая " +
//                "заставляет хотеть больше, целиться выше, бежать быстрее. " +
                "Наша миссия — " +
                "помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = 21051836L,
        likedByMe = false,
        likes = 105,
        shares = 22487576,
        viewed = 500,
        attachment = null
    ))