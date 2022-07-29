package com.example.tictocteo.di

import com.example.tictocteo.domain.usescase.GameUsesCase
import com.example.tictocteo.domain.usescase.MenuUsesCase
import com.example.tictocteo.domain.usescase.impl.GameUsesCaseImpl
import com.example.tictocteo.domain.usescase.impl.MenuUsesCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UsesCaseModule {


    @Binds
    fun bindGameUsesCase(impl: GameUsesCaseImpl): GameUsesCase

    @Binds
    fun bindMenuUsesCase(impl: MenuUsesCaseImpl) : MenuUsesCase
}