package com.smartweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangpeng on 2017/12/1.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("wind")
    public Wind wind;

    public class Wind {

        @SerializedName("sc")
        public String info;
    }

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

    }

}
