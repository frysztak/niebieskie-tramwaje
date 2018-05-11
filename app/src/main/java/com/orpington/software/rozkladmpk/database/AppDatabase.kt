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

@Database(entities = [(TransportLine::class), (Station::class), (LineStationJoin::class)], version = 1)
@TypeConverters(DateTypeConverter::class, ListOfIntTypeConverter::class, TransportTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transportLineDao(): TransportLineDao
    abstract fun stationDao(): StationDao
    abstract fun lineStationJoinDao(): LineStationJoinDao

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
                            Station(8, "Zakrzów"),
                            Station(9, "Księże Małe")
                    )
                    val lines = listOf(
                            TransportLine(0, "33",  TransportType.TRAM),
                            TransportLine(1, "3",   TransportType.TRAM),
                            TransportLine(2, "32" , TransportType.TRAM),
                            TransportLine(3, "128", TransportType.BUS),
                            TransportLine(4, "136", TransportType.BUS)
                    )
                    INSTANCE?.stationDao()?.insert(stations)
                    INSTANCE?.transportLineDao()?.insert(lines)
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(0, 0, 0))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(0, 1, 1))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(0, 2, 2))

                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(1, 3, 0))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(1, 1, 1))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(1, 9, 2))

                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(2, 4, 0))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(2, 1, 1))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(2, 5, 2))

                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(3, 0, 0))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(3, 6, 1))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(3, 1, 2))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(3, 2, 3))

                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(4, 4, 0))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(4, 6, 1))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(4, 5, 2))
                    INSTANCE?.lineStationJoinDao()?.insert(LineStationJoin(4, 7, 3))
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