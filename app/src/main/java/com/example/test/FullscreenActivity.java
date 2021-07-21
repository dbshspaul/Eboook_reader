package com.example.test;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.databinding.ReaderPlaceBinding;
import com.example.test.ui.book.ChapterContent;
import com.example.test.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.index_item);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    String titleHash;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReaderPlaceBinding binding = ReaderPlaceBinding.inflate(getLayoutInflater());
        FrameLayout root = binding.getRoot();
        setContentView(root);

        RecyclerView chaptersView = findViewById(R.id.index_content);

        EpubReader epubReader = new EpubReader();
        Book book = null;
        List<TOCReference> tocReferences = new ArrayList<>();
        try {
            Intent intent = getIntent();
            book = epubReader.readEpub(getAssets().open(intent.getStringExtra("book")));
            Resources resources = book.getResources();
            Collection<String> allHrefs = resources.getAllHrefs();

            titleHash = Utility.digest(book.getTitle().getBytes());

            for (String href : allHrefs) {
                Resource resource = resources.getByHref(href);
                byte[] data = resource.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    writeDataInFile(data, href, titleHash);
                }
            }


            tocReferences.addAll(book.getTableOfContents().getTocReferences());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RecyclerView.Adapter<MyViewHolder> adapter = new RecyclerView.Adapter<MyViewHolder>() {

            @NonNull
            @NotNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                LayoutInflater mInflater = LayoutInflater.from(FullscreenActivity.this);
                View view = mInflater.inflate(R.layout.index_item_fragment, null, false);

                MyViewHolder myViewHolder = new MyViewHolder(view);
                myViewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(FullscreenActivity.this, ChapterContent.class);
                    intent.putExtra("href", titleHash + "/" + tocReferences.get(myViewHolder.getAdapterPosition()).getCompleteHref());
                    startActivity(intent);
                });

                return myViewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
                TOCReference tocReference = tocReferences.get(position);
                TextView view = holder.getTextView();
                view.setText(tocReference.getTitle());
            }

            @Override
            public int getItemCount() {
                return tocReferences.size();
            }
        };


        chaptersView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        chaptersView.setLayoutManager(llm);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeDataInFile(byte[] data, String fileName, String title) throws IOException {
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        String location = directory.getAbsolutePath() + "/book/" + title + "/" + fileName;
        if (!Files.exists(Paths.get(location.substring(0, location.lastIndexOf("/"))))) {
            Files.createDirectories(Paths.get(location.substring(0, location.lastIndexOf("/"))));
        }
        if (!Files.exists(Paths.get(location))) {
            Files.write(Paths.get(location), data, StandardOpenOption.CREATE_NEW);
            System.out.println("File created = " + location);
        }
    }
}