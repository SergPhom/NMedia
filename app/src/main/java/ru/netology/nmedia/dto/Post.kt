package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Long,
    val shared: Long,
    val viewed: Long
){

    fun count(value: Long): String = when(value){
        in 1000..9999 -> "${value/1000}" +
                "${if ((value%1000L)/100L == 0L) "" else { "." + (value%1000)/100} + "K"}"
        in 10000..999999 ->  "${value/1000}K"
        in 1000000..Int.MAX_VALUE -> "${value/1000000}" +
                "${if ((value%1000000L)/100000L == 0L) "" else { "." + (value%1000000)/100000} + "M"}"
        else -> value.toString()
    }
}

