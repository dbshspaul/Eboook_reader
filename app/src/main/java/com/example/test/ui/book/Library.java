package com.example.test.ui.book;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.FullscreenActivity;
import com.example.test.R;
import com.example.test.util.Utility;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class Library extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        LinearLayout linearLayout = findViewById(R.id.flow_layout_parent);
        LayoutInflater layoutInflater = getLayoutInflater();

        try {

            String[] bookFiles = getAssets().list("books");
            for (String bookFile : bookFiles) {
                EpubReader epubReader = new EpubReader();
                Book book = epubReader.readEpub(getAssets().open("books/" + bookFile));
                Metadata metadata = book.getMetadata();

                View frameLayout = layoutInflater.inflate(R.layout.book_card, linearLayout, false);
                TextView title = frameLayout.findViewById(R.id.book_title);
                TextView bookDescription = frameLayout.findViewById(R.id.book_description);
                ImageView coverImage = frameLayout.findViewById(R.id.cover_image);

                if (book.getCoverImage()!=null){
                    byte[] imgData = book.getCoverImage().getData();
                    Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                    coverImage.setImageBitmap(bmp);
                }else {
                    coverImage.setImageResource(R.drawable.ic_dashboard_black_24dp);
                }

                title.setText(Utility.abbreviate(metadata.getFirstTitle(),25));
                bookDescription.setText(String.join(", ", metadata.getDescriptions()));

                frameLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(Library.this, FullscreenActivity.class);
                    intent.putExtra("book", "books/" + bookFile);
                    startActivity(intent);


                });

                linearLayout.addView(frameLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}