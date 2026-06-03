package com.bibliotech.app.data.utils
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.bibliotech.app.R
import java.io.File

class AndroidDownloadManager(private val context: Context) {

    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    /**

     * @param url
     * @param bookTitle
     */
    fun downloadBook(url: String, bookTitle: String): Long {
        val uri = Uri.parse(url)


        val sanitizedTitle = bookTitle.replace(Regex("[^a-zA-Z0-9_\\-\\s]"), "")
        val fileName = "$sanitizedTitle.pdf"

        val request = DownloadManager.Request(uri).apply {

            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)


            setTitle(context.getString(R.string.download_notification_title, bookTitle))
            setDescription(context.getString(R.string.download_notification_description))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setMimeType("application/pdf")
            addRequestHeader("Cookie","" )

            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "BiblioTech" + File.separator + fileName
            )


            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }


        return downloadManager.enqueue(request)
    }
}