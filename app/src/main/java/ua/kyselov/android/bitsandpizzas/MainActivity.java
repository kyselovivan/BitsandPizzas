package ua.kyselov.android.bitsandpizzas;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;


public class MainActivity extends Activity {

    private ShareActionProvider shareActionProvider;
    private String[] titles;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private int currentPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList.setAdapter(new ArrayAdapter<String>(
                        this, android.R.layout.simple_list_item_activated_1, titles)
        );
        if(savedInstanceState!=null){
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitile(currentPosition);
        }
        else {
            selectItem(0);
        }
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener(){
            public void onBackStackChanged(){
                FragmentManager fragMan = getFragmentManager();
                Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                if(fragment instanceof TopFragment){
                    currentPosition = 0;
                }
                if(fragment instanceof PizzaFragment){
                    currentPosition = 1;
                }
                if(fragment instanceof PastaFragment){
                    currentPosition = 2;
                }
                if(fragment instanceof StoresFragment){
                    currentPosition = 3;
                }
                setActionBarTitile(currentPosition);
                drawerList.setItemChecked(currentPosition,true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider)menuItem.getActionProvider();
        setIntent("This is example text");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_create_order:
                Intent intent = new Intent(this, OrderActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_share:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setIntent(String text){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            drawerLayout.closeDrawer(drawerList);
        }
    }

    private void selectItem(int position){
        Fragment fragment;
        switch (position){
            case 1:
                fragment = new PizzaFragment();
                break;
            case 2:
                fragment = new PastaFragment();
                break;
            case 3:
                fragment = new StoresFragment();
                break;
            default:
                fragment = new TopFragment();
                break;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment,"visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        setActionBarTitile(position);

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(drawerList);
    }

    private void setActionBarTitile(int position){
        String title;
        if(position == 0){
            title = getResources().getString(R.string.app_name);
        }
        else{
            title = titles[position];
        }
        getActionBar().setTitle(title);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position", currentPosition);
    }
}
