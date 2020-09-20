package com.hosle.log.logtofile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mDisplayView;
    private Button mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplayView = findViewById(R.id.text_show);
        mAddButton = findViewById(R.id.btn_add_log);

        LogToFile.setFileName("simple_log_to_file_demo.log");

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newLogString = "Add a new log record";
                addLog(newLogString);
                mDisplayView.setText(mDisplayView.getText() + "\n" + newLogString);
            }
        });
    }

    private void addLog(String content) {
        Log.i("simple_log_to_file_demo", content);
        LogToFile.logToFile(content);
    }
}