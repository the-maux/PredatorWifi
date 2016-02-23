package com.myWifi.app;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import com.larvalabs.svgandroid.SVGParser;
import com.myWifi.app.ViewController.Controler.Networking.InfoConfNetWork;
import com.myWifi.app.ViewController.Controler.Predator.LinkWifiPredator;
import com.myWifi.app.ViewController.Model.MyTextView;
import com.myWifi.app.ViewController.Model.NavDrawerItem;
import com.myWifi.app.ViewController.Model.Networking.ClientNatif;
import com.myWifi.app.ViewController.View.Adapter.NavDrawerListAdapter;
import com.myWifi.app.ViewController.View.Adapter.SlideMenuClickListener;
import com.myWifi.app.ViewController.Model.Predator.ClientPredator;
import com.myWifi.app.ViewController.Model.Predator.StackClientPredator;
import com.myWifi.app.ViewController.View.*;

import java.util.ArrayList;
import java.util.List;

public class                            MainActivityToFragment extends AppCompatActivity {
    private MyTextView                  TitleActionBar;
    private DrawerLayout                mDrawerLayout = null;
    private ListView                    mDrawerList = null;
    private String[]                    navMenuTitles = null;
    private ArrayList<NavDrawerItem>    navDrawerItems = null;
    private NavDrawerListAdapter        adapter = null;
    private List<Fragment>              myStack = new ArrayList<>();
    private List<String>                myStackTitre = new ArrayList<>();
    public StackClientPredator          myListClient = null;
    private ClientPredator              actualClientPredator = null;
    private LinkWifiPredator            linkWifiPredator = null;
    public boolean                      Predator = false;
    public InfoConfNetWork              infoConfNetWork;
    public ClientNatif                  clientNatif = null;
    private Menu                        menu;
    private MediaRouteActionProvider    mediaRouteActionProvider;
    private MenuItem                    mediaRouteMenuItem;

    /**
     * Parametrage de l'ActionBar
     */
    private void                        initActioBar() {
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater inflator = LayoutInflater.from(this);
        //Set le text dans l'ActionBar
        TitleActionBar = ((MyTextView) (inflator.inflate(R.layout.action_bar, null)).findViewById(R.id.title));
        //Set limage Button pour display le navDrawer
        //getSupportActionBar().setHomeAsUpIndicator(SVGParser.getSVGFromResource(getResources(), R.raw.menu_blc).createPictureDrawable());
        //Set la couleur du Background de la BarActivity
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF222222")));
        TitleActionBar.setText(this.getTitle());
        //Set la view avec la Gravity des elements
        getSupportActionBar().setCustomView(
                inflator.inflate(R.layout.action_bar, null),
                new android.support.v7.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
    }

    /**
     * Permer de creer le NavDrawer depuis un ArrayString dans String.xml
     * SVGParser est un objet pour afficher les image .SVG qui sont vectorielle
     */
    public void                         acualizeNavDrawer() {
        navDrawerItems = new ArrayList<>();
        navMenuTitles = getResources().getStringArray(R.array.Menu); //load le TableauString
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0],
                SVGParser.getSVGFromResource(getResources(), R.raw.wifi).createPictureDrawable()));//load titre & img
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],
                SVGParser.getSVGFromResource(getResources(), R.raw.security_network).createPictureDrawable()));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2],
                SVGParser.getSVGFromResource(getResources(), R.raw.security_network).createPictureDrawable()));
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener(this));//handler click menu
        mDrawerList.addHeaderView(View.inflate(getApplicationContext(), R.layout.header_nav_drawer, null));//logo au dessus du menu
        /*        * Load l'adapter et donc le mDrawerList        */
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        adapter.notifyDataSetChanged();
        mDrawerList.setAdapter(adapter);
        mDrawerList.setDividerHeight(10);
        mDrawerList.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg));
        mDrawerList.invalidate();
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void                      onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Background
        setContentView(LayoutInflater.from(this).inflate(R.layout.drawer_layout, null));
        initActioBar();
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        infoConfNetWork = new InfoConfNetWork(this);
        acualizeNavDrawer();
    }

    /**
     * Remplace la vue actuel par la nouvelle
     * Ajoute l'ancienne � la StackFragment en cas de backTouch
     * @param fragment
     * @param position
     */
    private void                        switch_screen(Fragment fragment, int position) {
        try {
            // remplace la frame
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack("tag")
                    .commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            //ferme le navDrawer apres le click
            mDrawerLayout.closeDrawer(mDrawerList);
            if (position == 1 || position == 0) {
                //si on a tout depile de la Stack on la clear pour la memoire
                myStack.clear();
                myStackTitre.clear();
            }
            //ajoute le Fragment � la stackFragment et sont titre pour gerer le backTouch
            myStack.add(fragment);
            myStackTitre.add(navMenuTitles[position - 0x1]);
        } catch (IllegalStateException e) {
            Log.w("Error MainActivity", "FragmentStack or FragmentManager corrupted");
            finishActivity(0);
        }
        //Gestion du clavier avec le BackTouch
         if (getCurrentFocus() != null) {
         ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE))
             .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
         }
    }

    /**
     * retourne un Fragment en rapport � sa position pour les fonctionnalites tableau
     * @param position
     * @return Fragment
     */
    private     Fragment                getFragmentByPosition(int position) {
        Fragment                        fragment = null;
        switch (position) {
            case 0:
                fragment = new FragmentWifiDetails();
                break;
            case 1:
                fragment = new FragmentWifiDetails();
                break;
            case 2:
                fragment = new FragmentLinkWifiPredator();
                break;
            case 3:
                fragment = new FragmentDiscoverClientNatif();
                break;
            case 4:
                fragment = new FragmentDetailClientNatif();
                break;
            case 5:
                fragment = (actualClientPredator != null) ? new FragmentClientPredatorDetail() : new FragmentWifiDetails();
                break;



        }
        Predator = (fragment.getClass() == FragmentLinkWifiPredator.class);
        Log.w("ActivityToFragment", "getFragment-> " + position + " bool Predator : " + Predator);
        return fragment;
    }

    /**
     * Set le titre de la Page & produit les decalages
     * @param position
     */
    public void                         displayView(int position) {
        TitleActionBar.setText(navMenuTitles[(position == 0) ? position : position - 0x1]);
        switch (position) {
            case 0:
                position = 0;
                break;
            case 1:
                position = 1;
                break;
            case 2:
                position = 2;
                break;
            default:
                break;
        }
        if (position < 0) position = 0;
        Log.w("ActivityToFragment", "goToSwitchScreen-> " + position);
        switch_screen(getFragmentByPosition(position), position);
    }

    @Override
    public void                         onBackPressed() {
        if (myStack.size() >= 0x2) {
            {
                // load le fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, myStack.get(myStack.size() - 0x2))
                        .commit();
                // Load le Titre du Fragment dans l'ActionBar
                TitleActionBar.setText(myStackTitre.get(myStack.size() - 0x2));
                myStackTitre.remove(myStackTitre.size() - 0x1);
                myStack.remove(myStack.size() - 0x1);
            }
        }
    }
    @Override
    public boolean                      onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                else
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    public ClientPredator               getActualClientPredator() {
        return actualClientPredator;
    }
    public void                         setActualClientPredator(ClientPredator actualClientPredator) {
        this.actualClientPredator = actualClientPredator;
    }
    public LinkWifiPredator             getLinkWifiPredator() {
        return linkWifiPredator;
    }
    @Override
    public boolean                      onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        mediaRouteMenuItem.setVisible(false);
        return true;
    }
    public void                         initMenuForAirplay(MediaRouteSelector mediaRouteSelector) {
        mediaRouteMenuItem.setVisible(true);
        mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mediaRouteSelector);
    }
}
