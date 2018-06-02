package com.example.enrico.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.enrico.todolist.data.DataHandler;
import com.example.enrico.todolist.detail.DetailActivity;
import com.example.enrico.todolist.detail.DetailFragment;
import com.example.enrico.todolist.dialog.CategoryDialogFragment;
import com.example.enrico.todolist.dialog.GiveUpCurrentEditing;
import com.example.enrico.todolist.eventObjects.PassToDetailFragment;
import com.example.enrico.todolist.eventObjects.RefreshAdapter;
import com.example.enrico.todolist.eventObjects.RefreshFragment;
import com.example.enrico.todolist.eventObjects.UpdateHighlight;
import com.example.enrico.todolist.json.JsonHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements CategoryDialogFragment.CategoryDialogListener, GiveUpCurrentEditing.CancelButtonClick {
    private static final String TAG = "ClickedItem";
    private boolean mIsTablet;
    private FloatingActionButton fab;
    public FrameLayout detailContainer;
    private DetailFragment detailFragment;
    private DataHandler dataHandler = DataHandler.getInstance();
    private JsonHelper jsonHelper = JsonHelper.getInstance();
    RefreshAdapter refreshAdapter = new RefreshAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog();
            }
        });
        detailContainer = (FrameLayout) findViewById(R.id.detail_category_container);
        mIsTablet = (detailContainer != null);
        dataHandler.setIsTablet(mIsTablet);

        detailFragment = new DetailFragment();

        jsonHelper.getMapFromPrefs(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        jsonHelper.saveMapToPrefs(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(refreshAdapter);
    }

    @Override
    protected void onDestroy() {
        jsonHelper.saveMapToPrefs(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // TODO: 1/1/18 Add functionality here
            case R.id.sort:
                dataHandler.setIsSorted(true);
                dataHandler.sortList();
                RefreshAdapter refreshAdapter = new RefreshAdapter();
                EventBus.getDefault().post(refreshAdapter);
                removeFragment();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayDialog() {
        CategoryDialogFragment dialog = new CategoryDialogFragment();
        dialog.show(getSupportFragmentManager(), "CategoryDialogFragment");
    }

    @Override
    public void onPositiveResult(String rawCategory) {
        if (!TextUtils.isEmpty(rawCategory)) {
            String category = rawCategory.trim();
            if (!TextUtils.isEmpty(category)) {
                if (!dataHandler.getCategoryList().contains(category)) {
                    if (dataHandler.getIsSorted()) {
                        dataHandler.addCategoryByAlpha(category);
                    } else {
                        dataHandler.addCategoryByTime(category);
                    }
                    dataHandler.addTitleOnly(category);
                    EventBus.getDefault().post(refreshAdapter);         //pass an arbitrary non-null view object so that the fragment can refresh the view
                } else {
                    Toast.makeText(this, R.string.duplicate_entry, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.empty_note, Toast.LENGTH_SHORT).show();
            }
        } else {
            // TODO: 12/23/17 Make this message more informative
            Toast.makeText(this, R.string.empty_note, Toast.LENGTH_SHORT).show();
        }

        UpdateHighlight update = new UpdateHighlight();
        EventBus.getDefault().post(update);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void itemClicked(String category) {
        dataHandler.setClickedItem(category);
        if (!mIsTablet && !dataHandler.getActionMode()) {
            Intent intent = new Intent(this, DetailActivity.class);
            startActivity(intent);
        } else {
            if (getSupportFragmentManager().findFragmentById(R.id.detail_category_container) == null && mIsTablet) {
                detailFragment = null;
                detailFragment = new DetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_category_container, detailFragment)
                        .commit();
            } else {
                //notify DetailFragment to update views
                EventBus.getDefault().post(this);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void destroyedActionMode(CategoryItemsAdapter adapter) {
        removeFragment();
    }

    public void doneClicked(View view) {
        PassToDetailFragment pass = new PassToDetailFragment();
        EventBus.getDefault().post(pass);
    }

    public void cancelClicked(View view) {
        GiveUpCurrentEditing dialogEdit = new GiveUpCurrentEditing();
        dialogEdit.show(getSupportFragmentManager(), "GiveUpCurrentEditing");
    }

    @Override
    public void refreshFragment() {
        RefreshFragment refreshFragment = new RefreshFragment();
        EventBus.getDefault().post(refreshFragment);
    }

    private void removeFragment(){
        if (getSupportFragmentManager().findFragmentById(R.id.detail_category_container) != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(detailFragment)
                    .commit();
        }
    }
}
