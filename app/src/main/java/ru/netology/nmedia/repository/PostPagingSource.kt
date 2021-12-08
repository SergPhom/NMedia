package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import retrofit2.HttpException
import ru.netology.nmedia.POST_STARTING_PAGE_INDEX
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.lang.Exception

class PostPagingSource(
    private val apiService: PostsApiService,
    private val dao: PostDao
): PagingSource<Long, Post>() {

    override val keyReuseSupported = true
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? {
        return null
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try{
            val response = when (params) {
                is LoadParams.Refresh -> {
                    apiService.getLatest(params.loadSize)
                }
                is LoadParams.Append -> {
                    apiService.getBefore(params.key, params.loadSize)
                }
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )
            }
            if (!response.isSuccessful) {
                error(HttpException(response))
            }
            val posts = response.body().orEmpty()

//            var posts = dao.getAll().map { postEntity -> postEntity.toDto() }

            return LoadResult.Page(posts, params.key, posts.lastOrNull()?.id)
        }catch (e: Throwable){
            println("Load err ${e.stackTrace}")
            return LoadResult.Error(e)
        }
    }
}