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
            var counter = 1
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    if(counter > 0){
                            counter--
                            PostsApi.retrofitService.getAll().enqueue(this)
                    }
                    else {
                        callback.onError("loading ${response.code()} ${response.message()}")
                        return
                    }
                }else{
                    callback.onSuccess(response.body()
                        ?: throw RuntimeException("AAAA body is null"))
                }
            }
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError("loading ${t.toString()}")
            }
        })
    }

    override fun savePostAsync(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : retrofit2.Callback<Post>{
            var counter = 1
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(!response.isSuccessful){
                    if(counter > 0){
                        counter--
                        PostsApi.retrofitService.save(post).enqueue(this)
                    }
                    else {
                        callback.onError("saving ${response.code()} ${response.message()}")
                        return
                    }
                }
                else{
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError("saving ${t.toString()}")
            }

        })
    }
//                                                                     ***LIKE/DISLIKE****
    override fun likeByIdAsync(id: Long, callback: PostRepository.Callback<Post>){
        PostsApi.retrofitService.likeById(id).enqueue(object : retrofit2.Callback<Post> {
            var counter = 1
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(!response.isSuccessful){
                    if(counter > 0){
                        counter--
                        PostsApi.retrofitService.likeById(id).enqueue(this)
                    }
                    else {
                        callback.onError("liking ${response.code()} ${response.message()}")
                        return
                    }
                }
                else{
                    callback.onSuccess(response.body() ?:
                    throw RuntimeException("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError("liking ${t.toString()}")
            }

        })
      }

    override fun dislikeByIdAsync(id: Long, callback: PostRepository.Callback<Post>){
        PostsApi.retrofitService.dislikeById(id).enqueue(object : retrofit2.Callback<Post>{
            var counter = 1
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(!response.isSuccessful){
                    if(counter > 0){
                        counter--
                        PostsApi.retrofitService.dislikeById(id).enqueue(this)
                    }
                    else {
                        callback.onError("disliking ${response.code()} ${response.message()}")
                        return
                    }
                }
                else{
                    callback.onSuccess(response.body() ?:
                    throw RuntimeException("body is null"))
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError("disliking ${t.toString()}")
            }
        })
    }

//                                                                                ****REMOVE*****
    override fun removeByIdAsync(id: Long, callback: PostRepository.CallbackUnit<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(object: retrofit2.Callback<Unit>{
            var counter = 1
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if(!response.isSuccessful){
                    if(counter > 0){
                        counter--
                        PostsApi.retrofitService.removeById(id).enqueue(this)
                    }
                    else {
                        callback.onError("removing ${response.code()} ${response.message()}")
                        return
                    }
                }
                else{
                    callback.onSuccess()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError("removing ${t.toString()}")
            }
        })
    }
//                                                                             *******SHARE*****
    override fun sharePostAsync(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.shareById(id).enqueue(object : retrofit2.Callback<Post>{
            var counter = 1
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(!response.isSuccessful){
                    if(counter > 0){
                        counter--
                        PostsApi.retrofitService.shareById(id).enqueue(this)
                    }
                    else {
                        callback.onError("sharing ${response.code()} ${response.message()}")
                        return
                    }
                }
                else{
                    callback.onSuccess(response.body() ?:
                    throw RuntimeException("body is null"))
                }
            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError("sharing ${t.toString()}")
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