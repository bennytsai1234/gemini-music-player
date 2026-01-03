package com.pulse.music.player.di

import com.pulse.music.domain.repository.MusicController
import com.pulse.music.player.manager.MusicServiceConnection
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindMusicController(
        musicServiceConnection: MusicServiceConnection
    ): MusicController
}
