package com.brewmatrix.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brewmatrix.app.data.local.dao.BeanDao
import com.brewmatrix.app.data.local.dao.BrewLogDao
import com.brewmatrix.app.data.local.dao.GrindSettingDao
import com.brewmatrix.app.data.local.dao.GrinderDao
import com.brewmatrix.app.data.local.dao.RatioPresetDao
import com.brewmatrix.app.data.local.dao.TimerPhaseDao
import com.brewmatrix.app.data.local.dao.TimerPresetDao
import com.brewmatrix.app.data.local.entity.Bean
import com.brewmatrix.app.data.local.entity.BrewLog
import com.brewmatrix.app.data.local.entity.Grinder
import com.brewmatrix.app.data.local.entity.GrindSetting
import com.brewmatrix.app.data.local.entity.RatioPreset
import com.brewmatrix.app.data.local.entity.TimerPhase
import com.brewmatrix.app.data.local.entity.TimerPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RatioPreset::class,
        TimerPreset::class,
        TimerPhase::class,
        Grinder::class,
        Bean::class,
        GrindSetting::class,
        BrewLog::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ratioPresetDao(): RatioPresetDao
    abstract fun timerPresetDao(): TimerPresetDao
    abstract fun timerPhaseDao(): TimerPhaseDao
    abstract fun grinderDao(): GrinderDao
    abstract fun beanDao(): BeanDao
    abstract fun grindSettingDao(): GrindSettingDao
    abstract fun brewLogDao(): BrewLogDao

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
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        seedDefaults(context)
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        seedDefaults(context)
                    }
                })
                .build()
        }

        private fun seedDefaults(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                prepopulateRatioPresets(database.ratioPresetDao())
                prepopulateTimerPresets(
                    database.timerPresetDao(),
                    database.timerPhaseDao(),
                )
            }
        }

        private suspend fun prepopulateRatioPresets(dao: RatioPresetDao) {
            val defaults = listOf(
                RatioPreset(name = "V60 Classic", ratio = 15.0, isDefault = true, sortOrder = 0),
                RatioPreset(name = "V60 Hoffmann", ratio = 16.667, isDefault = true, sortOrder = 1),
                RatioPreset(name = "Chemex", ratio = 17.0, isDefault = true, sortOrder = 2),
                RatioPreset(name = "AeroPress", ratio = 12.0, isDefault = true, sortOrder = 3),
                RatioPreset(name = "French Press", ratio = 15.0, isDefault = true, sortOrder = 4),
                RatioPreset(name = "Kalita Wave", ratio = 16.0, isDefault = true, sortOrder = 5),
            )
            defaults.forEach { dao.insert(it) }
        }

        private suspend fun prepopulateTimerPresets(
            presetDao: TimerPresetDao,
            phaseDao: TimerPhaseDao,
        ) {
            // V60 Standard
            val v60Id = presetDao.insert(
                TimerPreset(name = "V60 Standard", isDefault = true, sortOrder = 0),
            )
            phaseDao.insertAll(
                listOf(
                    TimerPhase(timerPresetId = v60Id, phaseName = "Bloom", durationSeconds = 45, targetWaterGrams = 50.0, sortOrder = 0),
                    TimerPhase(timerPresetId = v60Id, phaseName = "First Pour", durationSeconds = 30, targetWaterGrams = 150.0, sortOrder = 1),
                    TimerPhase(timerPresetId = v60Id, phaseName = "Second Pour", durationSeconds = 30, targetWaterGrams = 250.0, sortOrder = 2),
                    TimerPhase(timerPresetId = v60Id, phaseName = "Drawdown", durationSeconds = 90, sortOrder = 3),
                ),
            )

            // AeroPress Standard
            val aeropressId = presetDao.insert(
                TimerPreset(name = "AeroPress Standard", isDefault = true, sortOrder = 1),
            )
            phaseDao.insertAll(
                listOf(
                    TimerPhase(timerPresetId = aeropressId, phaseName = "Brew", durationSeconds = 60, sortOrder = 0),
                    TimerPhase(timerPresetId = aeropressId, phaseName = "Press", durationSeconds = 30, sortOrder = 1),
                ),
            )

            // French Press 4-Min
            val frenchPressId = presetDao.insert(
                TimerPreset(name = "French Press 4-Min", isDefault = true, sortOrder = 2),
            )
            phaseDao.insertAll(
                listOf(
                    TimerPhase(timerPresetId = frenchPressId, phaseName = "Bloom", durationSeconds = 30, targetWaterGrams = 60.0, sortOrder = 0),
                    TimerPhase(timerPresetId = frenchPressId, phaseName = "Steep", durationSeconds = 210, sortOrder = 1),
                    TimerPhase(timerPresetId = frenchPressId, phaseName = "Press", durationSeconds = 15, sortOrder = 2),
                ),
            )
        }
    }
}
