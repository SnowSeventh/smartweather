package com.smartweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangpeng on 2017/12/1.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    @SerializedName("lat")
    public String latitude;

    @SerializedName("lon")
    public String longtitude;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }
}

