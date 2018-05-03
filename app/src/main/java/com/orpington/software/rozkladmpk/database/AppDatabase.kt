package com.orpington.software.rozkladmpk.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.util.Log
import com.orpington.software.rozkladmpk.database.converters.DateTypeConverter
import com.orpington.software.rozkladmpk.database.converters.ListOfIntTypeConverter
import com.orpington.software.rozkladmpk.database.converters.ListOfStringTypeConverter
import com.orpington.software.rozkladmpk.database.converters.TransportTypeConverter
import java.util.*
import java.util.concurrent.Executors

// TODO: move to utils
fun ioThread(f: () -> Unit) {
    Executors.newSingleThreadExecutor().execute(f)
}

@Database(entities = [(TransportLine::class), (Station::class)], version = 1)
@TypeConverters(DateTypeConverter::class, ListOfIntTypeConverter::class, TransportTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transportLineDao(): TransportLineDao
    abstract fun stationDao(): StationDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private var rdc: RoomDatabase.Callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                ioThread {
                    val stations = listOf(
                            Station(0, "Pilczyce"),
                            Station(1, "Kwiska"),
                            Station(2, "pl. Grunwaldzki", "brzydko"),
                            Station(3, "Leśnica"),
                            Station(4, "Kozanów", "<3"),
                            Station(5, "Gaj"),
                            Station(6, "Grosz"),
                            Station(7, "Tarnogaj"),
                            Station(8, "Zakrzów")
                    )
                    val lines = listOf(
                            TransportLine("33",  TransportType.TRAM, Date(), listOf(0, 1, 2)),
                            TransportLine("3",   TransportType.TRAM, Date(), listOf(3, 1, 2)),
                            TransportLine("32" , TransportType.TRAM, Date(), listOf(4, 1, 5)),
                            TransportLine("128", TransportType.BUS,  Date(), listOf(0, 6, 8)),
                            TransportLine("136", TransportType.BUS,  Date(), listOf(4, 6, 7))
                    )
                    INSTANCE?.stationDao()?.insert(stations)
                    INSTANCE?.transportLineDao()?.insert(lines)
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "appdatabase.db").addCallback(rdc).allowMainThreadQueries().build()
                val currentDBPath = context.getDatabasePath("appdatabase.db").absolutePath
                Log.v("AppDatabase", currentDBPath)
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun getInMemoryDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java).allowMainThreadQueries().build()
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}