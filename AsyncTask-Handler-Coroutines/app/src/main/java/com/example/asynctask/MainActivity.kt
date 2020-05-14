package com.example.asynctask

import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Runnable

class MainActivity : AppCompatActivity() {

    private lateinit var mHandler: Handler
    private val MSG_UPDATE_NUMBER = 100
    private val MSG_UPDATE_NUMBER_DONE = 101
    private val DONE = "Done!"
    private val DELAY_TIME = 30L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clickEvents()
    }

    private fun clickEvents() {
        button_count.setOnClickListener {
//            implementHandler()
//            implementAsyncTask()
            implementCoroutine()
        }
    }

    private fun implementHandler() {
        countNumbers()
    }

    private fun implementAsyncTask() {
        MyAsyncTask().execute()
    }

    private fun implementCoroutine() {
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 0..100) {
                delay(DELAY_TIME)
                withContext(Dispatchers.Main) {
                    if (i == 100) {
                        progressBar.visibility = View.GONE
                        text_number.text = DONE
                        return@withContext
                    }
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = i
                    text_number.text = i.toString()
                }
            }
        }
    }

    private fun countNumbers() {
        listenHandler()
        Thread(Runnable {
            for (i in 0..100) {
                val message = Message()
                message.what = MSG_UPDATE_NUMBER
                message.arg1 = i
                mHandler.sendMessage(message)
                try {
                    Thread.sleep(DELAY_TIME)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            mHandler.sendEmptyMessage(MSG_UPDATE_NUMBER_DONE)
        }).start()
    }

    private fun listenHandler() {
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_UPDATE_NUMBER -> {
                        text_number.text = msg.arg1.toString()
                        progressBar.progress = msg.arg1
                        progressBar.visibility = View.VISIBLE
                    }
                    MSG_UPDATE_NUMBER_DONE -> {
                        text_number.text = DONE
                        progressBar.visibility = View.GONE
                    }
                    else -> {
                    }
                }
            }
        }
    }

    inner class MyAsyncTask : AsyncTask<Void, Int, String>() {
        override fun doInBackground(vararg params: Void?): String {
            for (i in 0..100) {
                publishProgress(i)
                try {
                    Thread.sleep(DELAY_TIME)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            return DONE
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progressBar.progress = values[0]!!
            text_number.text = values[0]!!.toString()
            progressBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            text_number.text = result
            progressBar.visibility = View.GONE
        }
    }
}
