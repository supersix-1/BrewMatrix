package com.brewmatrix.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.brewmatrix.app.data.dao.BeanDao
import com.brewmatrix.app.data.dao.BrewLogDao
import com.brewmatrix.app.data.dao.GrindSettingDao
import com.brewmatrix.app.data.dao.GrinderDao
import com.brewmatrix.app.data.dao.RatioPresetDao
import com.brewmatrix.app.data.dao.TimerPhaseDao
import com.brewmatrix.app.data.dao.TimerPresetDao
import com.brewmatrix.app.data.entity.Bean
import com.brewmatrix.app.data.entity.BrewLog
import com.brewmatrix.app.data.entity.GrindSetting
import com.brewmatrix.app.data.entity.Grinder
import com.brewmatrix.app.data.entity.RatioPreset
import com.brewmatrix.app.data.entity.TimerPhase
import com.brewmatrix.app.data.entity.TimerPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Grinder::class,
        Bean::class,
        GrindSetting::class,
        RatioPreset::class,
        TimerPreset::class,
        TimerPhase::class,
        BrewLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BrewMatrixDatabase : RoomDatabase() {
    abstract fun grinderDao(): GrinderDao
    abstract fun beanDao(): BeanDao
    abstract fun grindSettingDao(): GrindSettingDao
    abstract fun ratioPresetDao(): RatioPresetDao
    abstract fun timerPresetDao(): TimerPresetDao
    abstract fun timerPhaseDao(): TimerPhaseDao
    abstract fun brewLogDao(): BrewLogDao

    companion object {
        @Volatile
        private var INSTANCE: BrewMatrixDatabase? = null

        fun getInstance(context: Context): BrewMatrixDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): BrewMatrixDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BrewMatrixDatabase::class.java,
                "brewmatrix.db"
            )
                .addCallback(PrepopulateCallback { INSTANCE })
                .build()
        }
    }

    private class PrepopulateCallback(private val provider: () -> BrewMatrixDatabase?) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = provider() ?: return@launch
                prepopulateRatioPresets(database.ratioPresetDao())
                prepopulateTimerPresets(
                    timerPresetDao = database.timerPresetDao(),
                    timerPhaseDao = database.timerPhaseDao()
                )
            }
        }

        private suspend fun prepopulateRatioPresets(dao: RatioPresetDao) {
            val presets = listOf(
                RatioPreset(name = "V60 Classic", ratio = 15.0, isDefault = true, sortOrder = 0),
                RatioPreset(name = "V60 Hoffmann", ratio = 16.667, isDefault = true, sortOrder = 1),
                RatioPreset(name = "Chemex", ratio = 17.0, isDefault = true, sortOrder = 2),
                RatioPreset(name = "AeroPress", ratio = 12.0, isDefault = true, sortOrder = 3),
                RatioPreset(name = "French Press", ratio = 15.0, isDefault = true, sortOrder = 4),
                RatioPreset(name = "Kalita Wave", ratio = 16.0, isDefault = true, sortOrder = 5)
            )
            dao.insertAll(presets)
        }

        private suspend fun prepopulateTimerPresets(
            timerPresetDao: TimerPresetDao,
            timerPhaseDao: TimerPhaseDao
        ) {
            // V60 Standard
            val v60Id = timerPresetDao.insert(
                TimerPreset(name = "V60 Standard", isDefault = true, sortOrder = 0)
            )
            timerPhaseDao.insertAll(
                listOf(
                    TimerPhase(timerPresetId = v60Id, phaseOrder = 0, name = "Bloom", durationSeconds = 45),
                    TimerPhase(timerPresetId = v60Id, phaseOrder = 1, name = "First Pour", durationSeconds = 30),
                    TimerPhase(timerPresetId = v60Id, phaseOrder = 2, name = "Second Pour", durationSeconds = 30),
                    TimerPhase(timerPresetId = v60Id, phaseOrder = 3, name = "Drawdown", durationSeconds = 90)
                )
            )

            // AeroPress Standard
            val aeropressId = timerPresetDao.insert(
                TimerPreset(name = "AeroPress Standard", isDefault = true, sortOrder = 1)
            )
            timerPhaseDao.insertAll(
                listOf(
                    TimerPhase(timerPresetId = aeropressId, phaseOrder = 0, name = "Brew", durationSeconds = 60),
                    TimerPhase(timerPresetId = aeropressId, phaseOrder = 1, name = "Press", durationSeconds = 30)
                )
            )

            // French Press 4-Min
            val frenchPressId = timerPresetDao.insert(
                TimerPreset(name = "French Press 4-Min", isDefault = true, sortOrder = 2)
            )
            timerPhaseDao.insertAll(
                listOf(
                    TimerPhase(timerPresetId = frenchPressId, phaseOrder = 0, name = "Bloom", durationSeconds = 30),
                    TimerPhase(timerPresetId = frenchPressId, phaseOrder = 1, name = "Steep", durationSeconds = 210),
                    TimerPhase(timerPresetId = frenchPressId, phaseOrder = 2, name = "Press", durationSeconds = 15)
                )
            )
        }
    }
}
