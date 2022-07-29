package com.example.tictocteo.di

import com.example.tictocteo.domain.repository.GameRepository
import com.example.tictocteo.domain.repository.RoomRepository
import com.example.tictocteo.domain.repository.impl.GameRepositoryImpl
import com.example.tictocteo.domain.repository.impl.RoomRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun gameRepository(impl: GameRepositoryImpl): GameRepository

    @Binds
    fun roomRepository(impl: RoomRepositoryImpl): RoomRepository
}
