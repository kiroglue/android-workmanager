package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import timber.log.Timber

class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
	
	override fun doWork(): Result {
		val appContext  = applicationContext
		val resourceUri = inputData.getString(KEY_IMAGE_URI)
		makeStatusNotification("Blurring image", appContext)
		
		return try {
			
			if (TextUtils.isEmpty(resourceUri)){
				Timber.e("Invalid input uri")
				throw IllegalArgumentException("invalid input uri")
			}
			
			val resolver = appContext.contentResolver
			val picture = BitmapFactory.decodeStream(
					resolver.openInputStream(Uri.parse(resourceUri)))
			
			
			val output = blurBitmap(picture, appContext)
			val outputUri = writeBitmapToFile(appContext, output)
			val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
			makeStatusNotification("Blurring complete", appContext)
			Result.success(outputData)
		}catch (throwable: Throwable){
			Timber.e(throwable)
			Result.failure()
		}
	}
}