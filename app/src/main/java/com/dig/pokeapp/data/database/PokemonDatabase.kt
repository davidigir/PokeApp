package com.dig.pokeapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dig.pokeapp.data.dao.PokemonDao
import com.dig.pokeapp.data.entity.PokemonEntity


@Database(
    entities = [PokemonEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase(){
    abstract fun pokemonDao(): PokemonDao

}