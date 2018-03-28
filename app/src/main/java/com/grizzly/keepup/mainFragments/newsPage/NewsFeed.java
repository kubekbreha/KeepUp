/*
* Copyright 2018 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.grizzly.keepup.mainFragments.newsPage;

import java.util.Date;
import java.util.List;

/**
 * Created by kubek on 1/27/2018.
 */

/**
 * News feed adapter.
 */
public class NewsFeed {

    private Long runDate;
    private Long reversedTimestamp;
    private String specificRunImage;
    private String distance;
    private String userId;
    private int time;
    private List<Integer> minuteTimes;
    private String pushID;


    /**
     * Constructor for setting.
     */
    public NewsFeed(String specificRunImage, String distance, int time, String userId, List<Integer> times, String pushID) {
        this.specificRunImage = specificRunImage;
        this.distance = distance;
        this.time = time;
        this.userId = userId;
        runDate = new Date().getTime();
        reversedTimestamp = - new Date().getTime();
        this.minuteTimes = times;
        this.pushID = pushID;
    }

    /**
     * Constructor for getting.
     */
    public NewsFeed() {
    }


    /**
     * Getters and setters for variables.
     */
    public Long getReversedTimestamp() {
        return reversedTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setReversedTimestamp(Long reversedTimestamp) {
        this.reversedTimestamp = reversedTimestamp;
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

    public Long getRunDate() {
        return runDate;
    }

    public String getSpecificRunImage() {
        return specificRunImage;
    }

    public void setRunDate(Long runDate) {
        this.runDate = runDate;
    }

    public void setSpecificRunImage(String specificRunImage) {
        this.specificRunImage = specificRunImage;
    }

    public List<Integer> getMinuteTimes() {
        return minuteTimes;
    }

    public void setMinuteTimes(List<Integer> minuteTimes) {
        this.minuteTimes = minuteTimes;
    }

    public String getPushID() {
        return pushID;
    }

    public void setPushID(String pushID) {
        this.pushID = pushID;
    }
}
