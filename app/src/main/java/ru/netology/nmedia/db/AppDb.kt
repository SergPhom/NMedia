package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.javafaker.Faker
import kotlinx.coroutines.runBlocking
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity


@Database(entities = [PostEntity::class], version = 1, exportSchema = false)

abstract  class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    companion object{
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
