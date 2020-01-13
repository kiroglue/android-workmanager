package com.example.background.di

import android.app.Application
import android.content.Context
import com.example.background.blur.AppSubComponents
import com.example.background.blur.BlurComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(modules = [AppSubComponents::class])
interface AppComponent{
	
	
	@Component.Factory
	// With @BindsInstance, we tell Dagger Context? Context is provided by the Android system
	interface Factory{
		fun create(@BindsInstance app: Application): AppComponent
	}
	fun BlurComponent(): BlurComponent.Factory
	
}
