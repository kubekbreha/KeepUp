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

package com.grizzly.keepup.chat;

import java.util.Date;

/**
 * Created by kubek on 1/17/18.
 */

/**
 * Chat message adapter
 */
public class ChatMessage {

    private String mMessageText;
    private String mMessageUser;
    private Long mMessageTime;

    public ChatMessage(String messageText, String messageUser) {
        this.mMessageText = messageText;
        this.mMessageUser = messageUser;
        mMessageTime = new Date().getTime();
    }

    public ChatMessage() {
    }

    public String getmMessageText() {
        return mMessageText;
    }

    public String getmMessageUser() {
        return mMessageUser;
    }

    public Long getmMessageTime() {
        return mMessageTime;
    }

    public void setmMessageText(String messageText) {
        this.mMessageText = messageText;
    }

    public void setmMessageUser(String messageUser) {
        this.mMessageUser = messageUser;
    }

    public void setmMessageTime(Long messageTime) {
        this.mMessageTime = messageTime;
    }
}
