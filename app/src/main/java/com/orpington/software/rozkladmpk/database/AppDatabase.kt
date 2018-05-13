package com.orpington.software.rozkladmpk.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.util.Log
import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory
import com.orpington.software.rozkladmpk.database.converters.DateTypeConverter
import com.orpington.software.rozkladmpk.database.converters.ListOfIntTypeConverter
import java.util.concurrent.Executors

// TODO: move to utils
fun ioThread(f: () -> Unit) {
    Executors.newSingleThreadExecutor().execute(f)
}

@Database(
    entities = [(Route::class), (RouteType::class),
        (Stop::class), (StopTime::class),
        (Trip::class), VariantStop::class ],
    version = 1
)
@TypeConverters(DateTypeConverter::class, ListOfIntTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao
    abstract fun stopDao(): StopDao
    abstract fun routeTypeDao(): RouteTypeDao
    abstract fun stopTimeDao(): StopTimeDao
    abstract fun tripDao(): TripDao
    abstract fun variantStopDao(): VariantStopDao
    //abstract fun lineStationJoinDao(): LineStationJoinDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private var rdc: RoomDatabase.Callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                /*
                ioThread {
                    val stations = listOf(
                            Stop(0, "Pilczyce"),
                            Stop(1, "Kwiska"),
                            Stop(2, "pl. Grunwaldzki", "brzydko"),
                            Stop(3, "Leśnica"),
                            Stop(4, "Kozanów", "<3"),
                            Stop(5, "Gaj"),
                            Stop(6, "Grosz"),
                            Stop(7, "Tarnogaj"),
                            Stop(8, "Zakrzów"),
                            Stop(9, "Księże Małe")
                    )
                    val lines = listOf(
                            TransportLine(0, "33",  TransportType.TRAM),
                            TransportLine(1, "3",   TransportType.TRAM),
                            TransportLine(2, "32" , TransportType.TRAM),
                            TransportLine(3, "128", TransportType.BUS),
                            TransportLine(4, "136", TransportType.BUS)
                    )
                    INSTANCE?.stationDao()?.insert(stations)
                    INSTANCE?.routeDao()?.insert(lines)
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
                    */
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gtfs.db"
                ).openHelperFactory(AssetSQLiteOpenHelperFactory()).addCallback(rdc).allowMainThreadQueries().build()
                val currentDBPath = context.getDatabasePath("gtfs.db").absolutePath
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