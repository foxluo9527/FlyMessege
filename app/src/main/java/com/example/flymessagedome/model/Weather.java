package com.example.flymessagedome.model;

import java.util.List;

/**
 * {
 *   "code": 200,
 *   "msg": "success",
 *   "newslist": [
 *     {
 *       "area": "彰化",
 *       "date": "2021-04-02",
 *       "week": "星期五",
 *       "weather": "晴",
 *       "weatherimg": "qing.png",
 *       "real": "32℃",
 *       "lowest": "21℃",
 *       "highest": "32℃",
 *       "wind": "西北风",
 *       "winddeg": "326",
 *       "windspeed": "8",
 *       "windsc": "1-2级",
 *       "sunrise": "05:47",
 *       "sunset": "18:14",
 *       "moonrise": "23:15",
 *       "moondown": "09:13",
 *       "pcpn": "0.0",
 *       "pop": "2",
 *       "uv_index": "10",
 *       "vis": "24",
 *       "humidity": "84",
 *       "tips": "天气炎热，适宜着短衫、短裙、短裤、薄型T恤衫、敞领短袖棉衫等夏季服装。晴天紫外线等级较高，外出注意补水防晒。"
 *     }
 *   ]
 * }
 */
public class Weather {
    private int code;

    private String msg;

    private List<Newslist> newslist;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setNewslist(List<Newslist> newslist) {
        this.newslist = newslist;
    }

    public List<Newslist> getNewslist() {
        return this.newslist;
    }

    public static class Newslist {
        private String weather;

        private String real;

        private String weatherimg;

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getReal() {
            return real;
        }

        public void setReal(String real) {
            this.real = real;
        }

        public String getWeatherimg() {
            return weatherimg;
        }

        public void setWeatherimg(String weatherimg) {
            this.weatherimg = weatherimg;
        }
    }
}