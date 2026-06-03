package com.bibliotech.app.data.utils
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bibliotech.app.R

class DownloadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {

            context?.let { ctx ->

                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if (downloadId != -1L) {

                    Toast.makeText(
                        ctx,
                        ctx.getString(R.string.download_success),
                        Toast.LENGTH_LONG
                    ).show()


                }
            }
        }
    }
}