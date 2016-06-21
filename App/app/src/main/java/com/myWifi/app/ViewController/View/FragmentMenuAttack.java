package com.myWifi.app.ViewController.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.myWifi.app.R;


public class                FragmentMenuAttack extends android.support.v4.app.Fragment {
    private final String    TAG = "FragmentMenuAttack";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_menu_attack, container, false);
        Log.w("FragmentMenuAttack", "createView");

        rootView.findViewById(R.id.KarmaButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                TODO:
                        1. Sync avec le server
                        2. ListView ProbeRequest Sniffed
                        3. OnClickedProbeRequest
                        4. Launch a new AP with new new APNAME
                        5. Sync with Server
                        6. Go to Fragment wifi Predator
                 */
            }
        });
        rootView.findViewById(R.id.evillTwinAttack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                TODO:
                 */
            }
        });
        rootView.findViewById(R.id.networkAudit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                TODO:
                 */
            }
        });
        return rootView;
    }
}
