package com.smartweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangpeng on 2017/12/1.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    @SerializedName("air")
    public Air air;

    @SerializedName("flu")
    public Flu flu;

    public class Flu {

        @SerializedName("txt")
        public String info;
    }

    public class Air {

        @SerializedName("txt")
        public String info;
    }

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;

    }

    public class Sport {

        @SerializedName("txt")
        public String info;

    }
}
