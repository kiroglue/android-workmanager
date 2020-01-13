package com.example.background

import android.app.Application
import com.example.background.di.AppComponent
import com.example.background.di.DaggerAppComponent
import timber.log.Timber

open class MyApplication : Application() {
	
/*	val appComponent: AppComponent by lazy {
		DaggerAppComponent.factory().create()
	}*/
	
	val appComponent: AppComponent by lazy {
		// Creates an instance of AppComponent using its Factory constructor
		// We pass the applicationContext that will be used as Context in the graph
		DaggerAppComponent.factory().create(this)
	}
	
	override fun onCreate() {
		super.onCreate()
		
		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		}
	}
	
}