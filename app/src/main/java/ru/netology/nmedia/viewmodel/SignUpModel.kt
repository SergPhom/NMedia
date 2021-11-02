package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.UsersApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class SignUpModel: ViewModel() {

    fun setAuth(id: Long, token: String){
        AppAuth.getInstance().setAuth(id,token)
    }
    fun registrate(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                val response = UsersApi.retrofitService.registration(login, password, name)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                setAuth(body.id, body.token)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            } catch (e: Throwable) {
                println ("AVM e 2 $e")                                                       ////
            }
        }
    }

}