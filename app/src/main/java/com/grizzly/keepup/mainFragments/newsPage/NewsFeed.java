package com.grizzly.keepup.mainFragments.newsPage;

import java.util.Date;

/**
 * Created by kubek on 1/27/2018.
 */

public class NewsFeed {

    private Long mRunDate;
    private Long mReversedTimestamp;
    private String mSpecificRunImage;
    private String distance;
    private int time;

    public NewsFeed(String specificRunImage, String distance, int time) {
        this.mSpecificRunImage = specificRunImage;
        this.distance = distance;
        this.time = time;
        mRunDate = new Date().getTime();
        mReversedTimestamp = - new Date().getTime();
    }

    public NewsFeed() {

    }

    public Long getmReversedTimestamp() {
        return mReversedTimestamp;
    }

    public void setmReversedTimestamp(Long reversedTimestamp) {
        this.mReversedTimestamp = reversedTimestamp;
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

    public Long getmRunDate() {
        return mRunDate;
    }

    public String getmSpecificRunImage() {
        return mSpecificRunImage;
    }

    public void setmRunDate(Long mRunDate) {
        this.mRunDate = mRunDate;
    }

    public void setmSpecificRunImage(String mSpecificRunImage) {
        this.mSpecificRunImage = mSpecificRunImage;
    }
}
