package com.brewmatrix.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brewmatrix.app.data.local.dao.RatioPresetDao
import com.brewmatrix.app.data.local.entity.RatioPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [RatioPreset::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ratioPresetDao(): RatioPresetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "brewmatrix_database",
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).ratioPresetDao()
                            val defaults = listOf(
                                RatioPreset(
                                    name = "V60 Classic",
                                    ratio = 15.0,
                                    isDefault = true,
                                    sortOrder = 0,
                                ),
                                RatioPreset(
                                    name = "V60 Hoffmann",
                                    ratio = 16.667,
                                    isDefault = true,
                                    sortOrder = 1,
                                ),
                                RatioPreset(
                                    name = "Chemex",
                                    ratio = 17.0,
                                    isDefault = true,
                                    sortOrder = 2,
                                ),
                                RatioPreset(
                                    name = "AeroPress",
                                    ratio = 12.0,
                                    isDefault = true,
                                    sortOrder = 3,
                                ),
                                RatioPreset(
                                    name = "French Press",
                                    ratio = 15.0,
                                    isDefault = true,
                                    sortOrder = 4,
                                ),
                                RatioPreset(
                                    name = "Kalita Wave",
                                    ratio = 16.0,
                                    isDefault = true,
                                    sortOrder = 5,
                                ),
                            )
                            defaults.forEach { dao.insert(it) }
                        }
                    }
                })
                .build()
        }
    }
}
