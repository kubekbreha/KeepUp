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
