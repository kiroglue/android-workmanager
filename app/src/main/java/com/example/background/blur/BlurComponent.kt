package com.example.background.blur

import com.example.background.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface BlurComponent{
	
	@Subcomponent.Factory
	interface Factory{
		fun create(): BlurComponent
	}
	
	fun inject(activity: BlurActivity)
	
}