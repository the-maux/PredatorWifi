package com.myWifi.app.ViewController.View.Adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.myWifi.app.MainActivityToFragment;

/**
 * Created by maxim_000 on 29/10/2015.
 */
public class            SlideMenuClickListener  implements ListView.OnItemClickListener {
    private             MainActivityToFragment activity;

    public              SlideMenuClickListener(MainActivityToFragment activity) {
        this.activity = activity;
    }

    @Override
    public void         onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) position = 1;
            activity.displayView(position);
    }
}