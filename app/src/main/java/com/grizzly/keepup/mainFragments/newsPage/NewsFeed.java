package com.grizzly.keepup.mainFragments.newsPage;

import java.util.Date;

/**
 * Created by kubek on 1/27/2018.
 */

public class NewsFeed {

    private Long run_date;
    private String specific_run_image;
    private String distance;
    private int time;

    public NewsFeed(String specific_run_image, String distance, int time) {
        this.specific_run_image = specific_run_image;
        this.distance = distance;
        this.time = time;
        run_date = new Date().getTime();
    }

    public NewsFeed() {

    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public int getTime() {
        return time;
    }

    public Long getRun_date() {
        return run_date;
    }

    public String getSpecific_run_image() {
        return specific_run_image;
    }

    public void setRun_date(Long run_date) {
        this.run_date = run_date;
    }

    public void setSpecific_run_image(String specific_run_image) {
        this.specific_run_image = specific_run_image;
    }
}
