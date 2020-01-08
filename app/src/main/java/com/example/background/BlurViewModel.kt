/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.background.workers.BlurWorker


class BlurViewModel(application: Application) : AndroidViewModel(application) {
    
    private val workManager = WorkManager.getInstance(application)
    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null

    //kiroglue-1: viewModels are responsible from managing WorkManager and its helper classes
/*    internal fun applyBlur(blurLevel: Int){
        workManager.enqueue(OneTimeWorkRequest.from(BlurWorker::class.java))
    }*/
    
    //kiroglue-1: dynamic uri selection
    internal fun applyBlur(blurLevel: Int){
        val blurRequest = OneTimeWorkRequestBuilder<BlurWorker>()
                .setInputData(createInputDataForUri())
                .build()
        workManager.enqueue(blurRequest)
    }
    
    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let{
            builder.putString(KEY_IMAGE_URI, imageUri.toString())
        }
        return builder.build()
    }
    
    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    /**
     * Setters
     */
    internal fun setImageUri(uri: String?) {
        imageUri = uriOrNull(uri)
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }
}

/*
kiroglue-2:
There are a few WorkManager classes you need to know about:

Worker: This is where you put the code for the actual work you want to perform in the background. You'll extend this class and override the doWork() method.
WorkRequest: This represents a request to do some work. You'll pass in your Worker as part of creating your WorkRequest. When making the WorkRequest you can specify things like Constraints on when the Worker should run.
WorkManager: This class actually schedules your WorkRequest and makes it run. It schedules WorkRequests in a way that spreads out the load on system resources, while honoring the constraints you specify.
 */
