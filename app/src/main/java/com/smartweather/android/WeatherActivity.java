package com.smartweather.android;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartweather.android.gson.Forecast;
import com.smartweather.android.gson.Weather;
import com.smartweather.android.service.AutoUpdateService;
import com.smartweather.android.util.HttpUtil;
import com.smartweather.android.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    //    添加经纬度
    private TextView posiTion;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private TextView WindSpeed;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;
    //    添加空气质量
    private TextView qultText;

    private TextView airText;
    //    添加空气质量评价和流感建议
    private TextView comfortText;

    private TextView fluText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        // 初始化各控件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
//        初始化经纬度控件
        posiTion = (TextView) findViewById(R.id.position);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
//        初始化风速控件
        WindSpeed = (TextView) findViewById(R.id.wind_speed);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
//        初始化空气质量控件
        qultText = (TextView)findViewById(R.id.quality_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
//        初始化空气质量评价控件和流感指数控件
        airText = (TextView) findViewById(R.id.air_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        fluText = (TextView) findViewById(R.id.flu_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>起始页加载图片<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<,
        this.loadBingPic();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>下拉更新背景<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                loadBingPic();
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        /*String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }*/
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=0f8c05c1007a416aab5fcf7a468a0abf";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>加载随机图片<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    /*private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }*/

    private void loadBingPic(){

        ArrayList imageset=new ArrayList();
        imageset.add(R.drawable.a);imageset.add(R.drawable.b);imageset.add(R.drawable.c);imageset.add(R.drawable.d);
        imageset.add(R.drawable.e);imageset.add(R.drawable.f);imageset.add(R.drawable.g);imageset.add(R.drawable.h);
        imageset.add(R.drawable.i);imageset.add(R.drawable.j);imageset.add(R.drawable.k);imageset.add(R.drawable.l);
        imageset.add(R.drawable.m);imageset.add(R.drawable.n);imageset.add(R.drawable.o);imageset.add(R.drawable.p);
        imageset.add(R.drawable.q);imageset.add(R.drawable.r);

        //随机得到图片
        Random random=new Random();
        int index= random.nextInt(18);
        imageset.get(index);

        //随机设置背景
      /* Resources resources = WeatherActivity.this.getResources();
       Drawable drawable = resources.getDrawable();
       weatherLayout.setBackground(drawable);*/
        bingPicImg.setImageResource((Integer) imageset.get(index)); //图片资源
    }


    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
//        取出经纬度数据
        String position = "经度: "+weather.basic.longtitude+" "+"纬度: "+weather.basic.latitude;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
//        取出风速数据
        String windSpeed = weather.now.wind.info;
        titleCity.setText(cityName);
//        展示经纬度
        posiTion.setText(position);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
//        展示风速
        WindSpeed.setText(windSpeed);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
//          展示空气质量
            qultText.setText(weather.aqi.city.qlty);
            pm25Text.setText(weather.aqi.city.pm25);
        }
//        取出空气质量评价和流感指数数据
        String air ="空气质量: " + weather.suggestion.air.info;
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String flu = "流感指数: " + weather.suggestion.flu.info;
        Log.w("WeatherActivity", comfort);
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
//        展示空气质量评价和流感指数
        airText.setText(air);
        fluText.setText(flu);
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}