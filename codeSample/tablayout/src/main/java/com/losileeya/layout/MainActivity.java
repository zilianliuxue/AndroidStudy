package com.losileeya.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-22
 * Time: 12:57
 * 类描述：
 *
 * @version :
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private Button button2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        button.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                startActivity(new Intent(this,ExampleActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this,DefineActivity.class));
                break;
        }
    }
}
