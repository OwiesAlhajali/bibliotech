package com.bibliotech.app.ui.screens.bookreader

import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    bookId: String,
    onBackClick: () -> Unit
) {

    var isLoading by remember { mutableStateOf(true) }
    val readerUrl = "https://openlibrary.org${bookId.trim()}"


    val webViewAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "WebViewAlpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(webViewAlpha),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.supportZoom()


                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)

                        val cookieDomain = "https://archive.org"
                        cookieManager.setCookie(cookieDomain, "; Domain=.archive.org; Path=/; Secure; HttpOnly")
                        cookieManager.setCookie(cookieDomain, "; Domain=.archive.org; Path=/; Secure; HttpOnly")
                        cookieManager.flush()

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)

                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)


                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        var container = document.querySelector('.editionCover');
                                        if (container) {
                                            var containerHtml = container.innerHTML;
                                            document.body.innerHTML = '<div class="custom-reader-container">' + containerHtml + '</div>';
                                            
                                            var style = document.createElement('style');
                                            style.innerHTML = `
                                                body {
                                                    margin: 0 !important;
                                                    padding: 24px !important;
                                                    background-color: window !important;
                                                    display: flex !important;
                                                    justify-content: center !important;
                                                    align-items: center !important;
                                                    font-family: sans-serif;
                                                }
                                                .custom-reader-container {
                                                    text-align: center !important;
                                                    width: 100% !important;
                                                    max-width: 400px !important;
                                                    margin: 0 auto !important;
                                                }
                                                .custom-reader-container img {
                                                    max-width: 85% !important;
                                                    height: auto !important;
                                                    border-radius: 12px !important;
                                                    box-shadow: 0px 4px 16px rgba(0,0,0,0.15) !important;
                                                }
                                                #read, .Tools, .cta-button-container {
                                                    margin-top: 24px !important;
                                                    width: 100% !important;
                                                }
                                              
                                                #read a, .Tools a, .btn, button, input[type="button"] {
                                                    display: block !important;
                                                    width: 100% !important;
                                                    padding: 14px 0 !important;
                                                    background-color: 0xFFD7C6F1!important; 
                                                    color: #ffffff !important;
                                                    text-decoration: none !important;
                                                    border-radius: 8px !important;
                                                    font-weight: bold !important;
                                                    font-size: 16px !important;
                                                    text-align: center !important;
                                                    border: none !important;
                                                    box-shadow: 0px 2px 6px rgba(103, 80, 164, 0.3) !important;
                                                }
                                                
                                                .searchInsideBox, #searchInside {
                                                    display: none !important;
                                                }
                                            `;
                                            document.head.appendChild(style);
                                        }
                                        
                                       
                                        setTimeout(function() {
                                            
                                            window.localLoadingFinished = true;
                                        }, 100);
                                    })();
                                    """.trimIndent(), null
                                )


                                isLoading = false
                            }
                        }

                        loadUrl(readerUrl)
                    }
                }
            )


            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}