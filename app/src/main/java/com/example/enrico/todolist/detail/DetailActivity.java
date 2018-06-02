package com.example.enrico.todolist.detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.enrico.todolist.R;
import com.example.enrico.todolist.dialog.GiveUpCurrentEditing;
import com.example.enrico.todolist.eventObjects.PassToDetailFragment;
import com.example.enrico.todolist.eventObjects.RefreshFragment;

import org.greenrobot.eventbus.EventBus;

public class DetailActivity extends AppCompatActivity implements GiveUpCurrentEditing.CancelButtonClick{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void doneClicked(View view) {
        PassToDetailFragment pass = new PassToDetailFragment();
        EventBus.getDefault().post(pass);
        finish();
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
}
