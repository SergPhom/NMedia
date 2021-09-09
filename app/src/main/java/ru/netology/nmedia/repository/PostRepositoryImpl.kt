package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import java.lang.RuntimeException

class PostRepositoryImpl: PostRepository {

    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>){
        PostsApi.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                println("AAAA ${response.code()}")
                if (!response.isSuccessful) {
                    val counter = 5
                    if(counter > 0){
                        PostsApi.retrofitService.getAll().enqueue(this)
                    }
                    else {
                        callback.onError("${response.code()} ${response.message()}")
                        return
                    }
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(t.toString())
            }
        })
    }

    override fun savePostAsync(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : retrofit2.Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(!response.isSuccessful){
                   callback.onSuccess(post.copy(content = "${response.code()} ${response.message()}"))
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t.toString())
            }

        })
    }
//                                                                     ***LIKE/DISLIKE****
    override fun likeByIdAsync(id: Long, callback: PostRepository.Callback<Post>){
        PostsApi.retrofitService.likeById(id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                callback.onSuccess(response.body() ?: return)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t.toString())
            }

        })
      }

    override fun dislikeByIdAsync(id: Long, callback: PostRepository.Callback<Post>){
        PostsApi.retrofitService.dislikeById(id).enqueue(object : retrofit2.Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                callback.onSuccess(response.body() ?: return)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t.toString())
            }
        })
    }

//                                                                                ****REMOVE*****
    override fun removeByIdAsync(id: Long, callback: PostRepository.CallbackUnit<Long>) {
        PostsApi.retrofitService.removeById(id).enqueue(object: retrofit2.Callback<Long>{
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if(!response.isSuccessful){
                    println("AAAA work ${response.code()}")
                    callback.onError("Пост с id $id вызвал ошибку ${response.code()} ${response.message()}")
                }
                println("AAAA dontwork ${response.code()}")
                callback.onSuccess(response.body() ?: return)
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                callback.onError(t.toString())
            }
        })
    }
//                                                                             *******SHARE*****
    override fun sharePostAsync(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.shareById(id).enqueue(object : retrofit2.Callback<Post>{
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                callback.onSuccess(response.body()?: return)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t.toString())
            }
        })
    }
//                                                                 ***FILL SERVER****
    override fun fillAsync(callback: PostRepository.Callback<Post>){
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
        attachment = Attachment(type = AttachmentType.IMAGE,
            url = "https://e7.pngegg.com/pngimages/198/669/png-clipart-munchkin-cat-persian-cat-kitten-kitten-cat-like-mammal-animals.png",
//            url = "https://img11.postila.ru/data/c5/d9/65/04/c5d96504c4a5ff1c67c05de7adb44a6505ea521ee3a7a9caa41976a9dd48a28a.png",
            description = "netology image", )
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
        attachment = Attachment(type = AttachmentType.VIDEO,
            url = "https://www.youtube.com/watch?v=WhWc3b3KhnY", description = "youtube video")
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
        attachment = null
    ))