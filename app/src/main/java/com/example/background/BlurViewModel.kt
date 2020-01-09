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
import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.background.workers.BlurWorker
import com.example.background.workers.CleanupWorker
import com.example.background.workers.SaveImageToFileWorker


class BlurViewModel(application: Application) : AndroidViewModel(application) {
    
    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    internal val outputWorkInfoItems: LiveData<List<WorkInfo>>
    private val workManager: WorkManager = WorkManager.getInstance(application)
    
    init {
        // This transformation makes sure that whenever the current work Id changes the WorkStatus
        // the UI is listening to changes
        outputWorkInfoItems = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    //kiroglue-1: viewModels are responsible from managing WorkManager and its helper classes
/*    internal fun applyBlur(blurLevel: Int){
        workManager.enqueue(OneTimeWorkRequest.from(BlurWorker::class.java))
    }*/
    
    //kiroglue-1: dynamic uri selection
    internal fun applyBlur(blurLevel: Int){
        
        //kiroglue-3 this is like not synchronized threads
        /*var continuation = workManager
                .beginWith(OneTimeWorkRequest.from(CleanupWorker::class.java))
        */
        //kiroglue-3 ensuring unique work
        var continuation = workManager
                .beginUniqueWork(
                        IMAGE_MANIPULATION_WORK_NAME,
                        ExistingWorkPolicy.REPLACE, // can be REPLACE, KEEP or APPEND
                        OneTimeWorkRequest.from(CleanupWorker::class.java)
                )
        
        for(i in  0 until blurLevel){
            val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()
    
            // Input the Uri if this is the first blur operation
            // After the first blur operation the input will be the output of previous
            // blur operations.
            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri())
            }
    
            continuation = continuation.then(blurBuilder.build())
        }
        
        val save = OneTimeWorkRequest
                .Builder(SaveImageToFileWorker::class.java)
                .addTag(TAG_OUTPUT) //kiroglue-4: We are tagging because we will get it with same tag later.
                .build()
        
        continuation = continuation.then(save)
    
        continuation.enqueue()
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
