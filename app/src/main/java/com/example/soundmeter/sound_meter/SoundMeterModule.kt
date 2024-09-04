package com.example.soundmeter.sound_meter

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SoundMeterModule {

    @Provides
    @Singleton
    fun provideSoundMeterRepository(
      @ApplicationContext context: Context
    ) : SoundMeterRepository{
        return SoundMeterRepositoryImpl(context)
    }
}