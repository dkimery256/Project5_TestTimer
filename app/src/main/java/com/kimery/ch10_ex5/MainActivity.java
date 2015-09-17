package com.kimery.ch10_ex5;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {


    private TextView messageTextView;
    volatile boolean stop = false;
    private final String URL_STRING = "http://rss.cnn.com/rss/cnn_tech.rss";
    private final String FILENAME = "news_feed.xml";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTextView = (TextView) findViewById(R.id.messageTextView);

    }

    private void updateView(final long elapsedMillis) {
        // UI changes need to be run on the UI thread

        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis / 10000;

            @Override
            public void run() {
                if (!stop) {
                    messageTextView.setText("File Downloaded: " + elapsedSeconds + " time(s)");
                }
            }
        });

    }

    public void startButton(View view) {
        stop = false;
        final long startMillis = System.currentTimeMillis();
        final Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                if (!stop) {
                    long elapsedMillis = System.currentTimeMillis() - startMillis;
                    downloadFile();
                    updateView(elapsedMillis);
                }else{
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0, 10000);
    }

    public void stopButton(View view) {
        stop = true;
        String message = messageTextView.getText().toString();
        if(!message.equals("File Downloaded: 0 time(s)"))
            messageTextView.setText("File Downloaded: Stopped");
    }

    protected void onPause(){
        super.onPause();
        stop = true;
    }

    public void downloadFile() {
        try{
            // get the URL
            URL url = new URL(URL_STRING);

            // get the input stream
            InputStream in = url.openStream();

            // get the output stream
            FileOutputStream out =
                    openFileOutput(FILENAME, Context.MODE_PRIVATE);

            // read input and write output
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1)
            {
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            out.close();
            in.close();
        }
        catch (IOException e) {
            Log.e("News reader", e.toString());
        }
    }

}






