package com.grizzly.keepup.mainFragments.Map;

import com.google.firebase.auth.FirebaseAuth;

import java.util.TimerTask;

/**
 * Created by kubek on 3/28/2018.
 */

public class SendMinuteData  extends TimerTask {

    public SendMinuteData(){
    }

    public void run() {
        MapFragment.minutes++;
        //System.out.println("second");
    }


}
