package com.example.newsapi_recyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface{

    // API Key(news) : df606e2cb8384d5f93a967dcad590aa9
    private RecyclerView newsRV, categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private ArrayList<CategoryRVModel> categoryRVModelArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private NewsRVAdapter newsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get views in MainActivity  xml
        newsRV = findViewById(R.id.idRV_News);
        categoryRV = findViewById(R.id.idCategories);
        loadingPB = findViewById(R.id.idPB_Loading);

        // Initialize articles and categories Lists
        articlesArrayList = new ArrayList<>();
        categoryRVModelArrayList = new ArrayList<>();

        // Passing Lists to Adapters
        newsRVAdapter = new NewsRVAdapter(articlesArrayList, this);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModelArrayList, this, this::onCategoryClick);

        // Setting Adapters to Recycler Views
        newsRV.setAdapter(newsRVAdapter);
        categoryRV.setAdapter(categoryRVAdapter);

        getCategories();
        getNews("All");
        newsRVAdapter.notifyDataSetChanged();
    }


    private void getCategories() {
        categoryRVModelArrayList.add(new CategoryRVModel("All",getString(R.string.all_category_url)));
        categoryRVModelArrayList.add(new CategoryRVModel("Technology",getString(R.string.technology_category_url)));
        categoryRVModelArrayList.add(new CategoryRVModel("Science",getString(R.string.science_category_url)));
        categoryRVModelArrayList.add(new CategoryRVModel("Sports",getString(R.string.sports_category_url)));
        categoryRVModelArrayList.add(new CategoryRVModel("General",getString(R.string.general_category_url)));
        categoryRVModelArrayList.add(new CategoryRVModel("Business",getString(R.string.business_category_url)));
        categoryRVModelArrayList.add(new CategoryRVModel("Entertainment",getString(R.string.entertainment_category_url)));
        categoryRVModelArrayList.add(new CategoryRVModel("Health",getString(R.string.health_category_url)));

        categoryRVAdapter.notifyDataSetChanged();
    }

    private void getNews(String category){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String categoryUrl = "https://newsapi.org/v2/top-headlines?country=in&category="+category +"&apiKey=df606e2cb8384d5f93a967dcad590aa9";
        String url = "http://newsapi.org/v2/top-headlines?country=in&excludeDomains=stackoverflow.com&sortBy=publishedAt&language=en&apiKey=df606e2cb8384d5f93a967dcad590aa9";
        String BASE_URL = "http://newsapi.org/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModel> call;
        if(category.equals("All")){
            call = retrofitAPI.getAllNews(url);
        }else{
            call = retrofitAPI.getNewsByCategory(categoryUrl);
        }

        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                NewsModel newsModel = response.body();
                loadingPB.setVisibility(View.GONE);
                ArrayList<Articles> articles = newsModel.getArticles();
                for(int i = 0; i<articles.size();i++){
                    articlesArrayList.add(new Articles(
                            articles.get(i).getTitle(),
                            articles.get(i).getDescription(),
                            articles.get(i).getUrlToImage(),
                            articles.get(i).getUrl(),
                            articles.get(i).getContent()));
                }
                newsRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail to Get News ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCategoryClick(int position){
        String category = categoryRVModelArrayList.get(position).getCategory();
        getNews(category);

    }

}