package com.losileeya.coordinatorlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-23
 * Time: 12:58
 * 类描述：
 *
 * @version :
 */
public class CollasingActivity extends AppCompatActivity {
    private ImageView backdrop;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsing_toolbar;
    private AppBarLayout appbar;
    private CoordinatorLayout main_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collapsing_layout);
        initView();
    }

    private void initView() {
        backdrop = (ImageView) findViewById(R.id.backdrop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        main_content = (CoordinatorLayout) findViewById(R.id.main_content);
        // Title
        collapsing_toolbar.setTitle("collapsing");
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { //返回按钮的点击事件
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       // Menu item click 的監聽事件一樣要設定在 setSupportActionBar 才有作用
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    msg += "Click edit";
                    break;
                case R.id.action_share:
                    msg += "Click share";
                    break;
                case R.id.action_settings:
                    msg += "Click setting";
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(CollasingActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
