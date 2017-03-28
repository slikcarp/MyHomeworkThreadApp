package com.mobileappscompany.training.myhomeworkthreadapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;


public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int totalOfNum = intent.getIntExtra("number", 1);

            int maxNumber = 0;
            for (int index = 0; index < totalOfNum; index++) {
                int nextNumber = MainActivity.getRandomNumber(10000);
                maxNumber = maxNumber > nextNumber ? maxNumber : nextNumber;
            }

            EventBus.getDefault().post(String.valueOf(maxNumber));
        }
    }
}
