package com.mults.parser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    
    private ListView listView;
    private ArrayList<String> cartoonList;
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listView = findViewById(R.id.listView);
        cartoonList = new ArrayList<>();
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            cartoonList
        );
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String item = cartoonList.get(position);
            if (item.contains("http")) {
                String url = extractUrl(item);
                if (url != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }
        });
        
        loadCartoons();
    }
    
    private String extractUrl(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.startsWith("http")) {
                return line;
            }
        }
        return null;
    }
    
    private void loadCartoons() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загрузка мультфильмов...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        
        executor.execute(() -> {
            try {
                Document doc = Jsoup.connect("https://mults.info/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(15000)
                    .get();
                
                ArrayList<String> result = new ArrayList<>();
                Elements links = doc.select("a[href*=/mults/]");
                
                for (Element link : links) {
                    String title = link.text().trim();
                    String href = link.attr("href");
                    
                    if (isValidTitle(title) && result.size() < 25) {
                        String fullUrl = href.startsWith("http") ? href : "https://mults.info" + href;
                        result.add("🎬 " + title + "\n🔗 " + fullUrl);
                    }
                }
                
                handler.post(() -> {
                    progressDialog.dismiss();
                    updateUI(result);
                });
                
            } catch (Exception e) {
                handler.post(() -> {
                    progressDialog.dismiss();
                    cartoonList.clear();
                    cartoonList.add("❌ Ошибка: " + e.getMessage());
                    ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                });
            }
        });
    }
    
    private boolean isValidTitle(String title) {
        return title != null && 
               title.length() > 3 && 
               !title.contains("<") && 
               !title.contains(">") &&
               !title.toLowerCase().contains("script");
    }
    
    private void updateUI(ArrayList<String> result) {
        cartoonList.clear();
        if (result.isEmpty()) {
            cartoonList.add("📭 Мультфильмы не найдены\nПроверьте интернет соединение");
        } else {
            cartoonList.addAll(result);
        }
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
    }
}