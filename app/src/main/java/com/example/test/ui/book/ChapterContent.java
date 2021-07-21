package com.example.test.ui.book;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.test.R;
import com.example.test.databinding.ActivityChapterContentBinding;

import java.io.File;

public class ChapterContent extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityChapterContentBinding binding = ActivityChapterContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WebView webView = findViewById(R.id.ch_content);

        Intent intent = getIntent();
//        TOCReference tocReference = (TOCReference) intent.getSerializableExtra("resource");
        String href = intent.getStringExtra("href");
        try {
            ContextWrapper cw = new ContextWrapper(this);
            File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            String location = directory.getAbsolutePath() + "/book/" + href;
            webView.getSettings().setAllowContentAccess(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.loadUrl("file://" + location);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    System.out.println("WebView "+ "Attempting to load URL: " + url);
                    view.loadUrl(url);
                    return true;
                }
            });

//            textView.setText(HtmlCompat.fromHtml(new String(tocReference.getResource().getData()), HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}