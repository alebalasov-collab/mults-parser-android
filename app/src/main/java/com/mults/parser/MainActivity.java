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
        
        // Настройка списка
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            cartoonList
        );
        listView.setAdapter(adapter);
        
        // Обработчик клика
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String item = cartoonList.get(position);
            if (item.contains("https://")) {
                String url = item.substring(item.indexOf("https://"));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
        
        loadCartoons();
    }
    
    private void loadCartoons() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("🔄 Загрузка мультфильмов...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        
        executor.execute(() -> {
            try {
                Document doc = Jsoup.connect("https://mults.info/")
                    .userAgent("Mozilla/5.0 (Android; Mobile)")
                    .timeout(15000)
                    .get();
                
                ArrayList<String> result = new ArrayList<>();
                
                // Ищем ссылки на мультфильмы
                Elements links = doc.select("a[href*=/mults/]");
                
                for (Element link : links) {
                    String title = link.text().trim();
                    String href = link.attr("href");
                    
                    if (title.length() > 5 && 
                        !title.isEmpty() && 
                        !title.contains("<") && 
                        result.size() < 30) {
                        
                        String fullUrl = href.startsWith("http") ? href : "https://mults.info" + href;
                        result.add("🎬 " + title + "\n🔗 " + fullUrl);
                    }
                }
                
                handler.post(() -> {
                    progressDialog.dismiss();
                    cartoonList.clear();
                    if (result.isEmpty()) {
                        cartoonList.add("📭 Не найдено мультфильмов\nПроверьте подключение к интернету");
                    } else {
                        cartoonList.addAll(result);
                    }
                    ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                });
                
            } catch (Exception e) {
                handler.post(() -> {
                    progressDialog.dismiss();
                    cartoonList.clear();
                    cartoonList.add("❌ Ошибка загрузки: " + e.getMessage());
                    ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                });
            }
        });
    }
}