package com.example.ciudadalerta.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ReporteEntity::class,
        UsuarioEntity::class,
        MultimediaEntity::class,
        NotificacionEntity::class,
        HistorialEstatusEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reporteDao(): ReporteDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun multimediaDao(): MultimediaDao
    abstract fun notificacionDao(): NotificacionDao
    abstract fun historialDao(): HistorialDao

    companion object {
        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "ciudad_alerta.db")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}