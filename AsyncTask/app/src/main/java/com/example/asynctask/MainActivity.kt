package com.example.asynctask

import android.os.*
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mHandler: Handler
    private val MSG_UPDATE_NUMBER = 100
    private val MSG_UPDATE_NUMBER_DONE = 101

    private var mIsCounting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listenerHandler()
        clickEvents()
    }

    private fun clickEvents() {
        button_count.setOnClickListener {
//            countNumbers()
            val asyncTask = MyAsyncTask()
            asyncTask.execute()
        }
    }

    private fun countNumbers() {
        Thread(Runnable {
            for (i in 0..10) {
                val message = Message()
                message.what = MSG_UPDATE_NUMBER
                message.arg1 = i
                mHandler.sendMessage(message)
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            mHandler.sendEmptyMessage(MSG_UPDATE_NUMBER_DONE)
        }).start()
    }

    private fun listenerHandler() {
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_UPDATE_NUMBER -> {
                        mIsCounting = true
                        text_number.text = msg.arg1.toString()
                        progressBar.visibility = View.VISIBLE
                    }
                    MSG_UPDATE_NUMBER_DONE -> {
                        text_number.text = "Done!"
                        mIsCounting = false
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
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            return "DONE"
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            progressBar.progress = values[0]!!;
        }

        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
            Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
        }

    }
}
