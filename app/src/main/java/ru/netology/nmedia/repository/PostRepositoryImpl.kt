package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit



class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback){
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.LikeCallback){
        val request: Request = Request.Builder()
            .post(id.toString().toRequestBody())
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.LikeCallback){
        val request: Request = Request.Builder()
            .delete(id.toString().toRequestBody())
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun savePostAsync(post: Post, callback: PostRepository.LikeCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun sharePostAsync(id: Long, callback: PostRepository.LikeCallback) {
        val request: Request = Request.Builder()
            .post(id.toString().toRequestBody())
            .url("${BASE_URL}/api/slow/posts/$id/shares")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }
    override fun fillAsync(callback: PostRepository.LikeCallback){
        postsDefault.forEach{
            savePostAsync(it, callback)
        }
    }
}
val postsDefault = listOf(
    Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась " +
                "с интенсивов по онлайн-маркетингу. " +
//                "Затем появились курсы по дизайну, " +
//                "разработке, аналитике и управлению. Мы растём сами и помогаем расти " +
//                "студентам: от новичков до уверенных профессионалов. Но самое важное " +
//                "остаётся с нами: мы верим, что в каждом уже есть сила, которая " +
//                "заставляет хотеть больше, целиться выше, бежать быстрее." +
                " Наша миссия — " +
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
        content = "Привет, это новая Нетология! " +
//                "Когда-то Нетология начиналась " +
//                "с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, " +
//                "разработке, аналитике и управлению. Мы растём сами и помогаем расти " +
//                "студентам: от новичков до уверенных профессионалов. Но самое важное " +
//                "остаётся с нами: мы верим, что в каждом уже есть сила, которая " +
//                "заставляет хотеть больше, целиться выше, бежать быстрее. " +
                "Наша миссия — " +
                "помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
        likedByMe = false,
        likes = 105,
        shares = 22487576,
        viewed = 500,
        video = null
    ))