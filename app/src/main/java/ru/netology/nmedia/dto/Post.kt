package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Long,
    val shared: Int,
    val viewed: Long
){

    fun count(value: Int): String = when(value){
        in 1000..9999 -> "${value/1000}" +
                "${if ((value%1000)/100 == 0) "" else { "." + (value%1000)/100} + "K"}"
        in 10000..999999 ->  "${value/1000}K"
        in 1000000..Int.MAX_VALUE -> "${value/1000000}" +
                "${if ((value%1000000)/100000 == 0) "" else { "." + (value%1000000)/100000} + "M"}"
        else -> value.toString()
    }
}

