package com.mobileappscompany.training.myhomeworkthreadapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Subscription subscription;
    private Observable<Integer> observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onAdd(View view) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                int totalOfNum = recoverIntValue();

                int []numbers = new int[totalOfNum];

                for (int index = 0; index < totalOfNum; index++) {
                    numbers[index] = getRandomNumber(100);
                }

                final StringBuilder sb = new StringBuilder();
                for (int number : numbers) {
                    sb.append(number + ",");
                }
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(sb.toString());
                    }
                });
            }
        }.start();
    }

    public void onAverage(View view) {
        new AsyncTask<Integer, Void, String>() {
            @Override
            protected String doInBackground(Integer... params) {
                int totalOfNum = params[0];

                double []numbers = new double[totalOfNum];

                for (int index = 0; index < totalOfNum; index++) {
                    numbers[index] = getRandomNumber(100);
                }

                double total = 0;
                for (double number : numbers) {
                    total += number;
                }
                return String.valueOf(total/((double)totalOfNum));
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                textView.setText(s);
            }
        }.execute(recoverIntValue());
    }

    public void onHigh(View view) {
        Intent i = new Intent(this, MyIntentService.class);
        i.putExtra("number",recoverIntValue());
        startService(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusExecuition(String highestValue) {
        textView.setText(highestValue);
    }

    public void onLow(View view) {
        observable = Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(100);
                return recoverIntValue();
            }
        });

        configureSubscription();
    }

    private void configureSubscription() {
        subscription = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer totalOfNum) {
                        Integer minNumber = 10000;
                        for (int index = 0; index < totalOfNum; index++) {
                            int nextNumber = MainActivity.getRandomNumber(10000);
                            minNumber = minNumber < nextNumber ? minNumber : nextNumber;
                        }
                        textView.setText(minNumber.toString());
                    }
                });
    }

    public int recoverIntValue() {
        String numString = editText.getText().toString();
        if(numString.trim().isEmpty()) {
            showToastMessage("Enter a number please.");
            throw new RuntimeException();
        }
        return Integer.valueOf(numString) * 1000;
    }

    public static int getRandomNumber(int maxNum) {
        return (int)(Math.random()*maxNum);
    }

    private void showToastMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
