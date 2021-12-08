package ru.netology.nmedia.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.github.javafaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.*
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val postsApiService: PostsApiService
): PostRepository {

    override val data = Pager(
        PagingConfig(pageSize = 30, enablePlaceholders = false),
        pagingSourceFactory = {
            PostPagingSource(dao)
        }
    )
        .flow
        .catch { println(it.stackTrace) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val response = postsApiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.map { it.copy(viewed = true) }.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun newerPostsViewed(){
        dao.allViewedTrue()
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10000L)
            val response = postsApiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
            emit(dao.notViewedCount())
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun savePost(post: Post) {
        try {
//            val lessId = data.first()
//                .minOf { it.id }
//            if (post.id == 0L) {
//                dao.insert(PostEntity.fromDto(post.copy(id = lessId - 2L)))
//            } else {
                dao.insert(PostEntity.fromDto(post))
//            }
//            val _post = if (post.id < 0) post.copy(id = 0L) else post
//            val response = postsApiService.save(_post)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.onRemoveClick(post.id)
            dao.insert(PostEntity.fromDto(post))
//            postsApiService.pushes()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
    override suspend fun savePostWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment = post.copy(attachment = Attachment(media.id, "MyPhoto",AttachmentType.IMAGE))
            savePost(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    //                                                                     ***LIKE/DISLIKE****
    override suspend fun likeById(id: Long) {
        try {
//            val response = postsApiService.likeById(id)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(PostEntity.fromDto(body.copy(viewed = true)))
            dao.onLikeButtonClick(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
//            val response = postsApiService.dislikeById(id)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(PostEntity.fromDto(body).copy(viewed = true))
            dao.onLikeButtonClick(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    //                                                                                ****REMOVE*****
    override suspend fun removeById(id: Long) {
        try {
//            val response = postsApiService.removeById(id)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
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
//            val response = postsApiService.shareById(id)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(PostEntity.fromDto(body))
            dao.onShareButtonClick(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = postsApiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
    private val faker = Faker()
    var posts = emptyList<PostEntity>()
    override suspend fun fillInDb() {
        (1..200L).forEach{
               posts += PostEntity(
                    id = 200L - it,
                    authorId = 3L,
                    author = "Game of Thrones",
                    authorAvatar = "got.jpg",
                    content = faker.gameOfThrones().quote(),
                    published = 0L,
                    likedByMe = false,
                    likes = 0L,
                    viewes = 20L,
                    saved = true,
                    viewed = true,
                )
            println("repo post num $it added")
        }
        dao.insert(posts)
    }
}
