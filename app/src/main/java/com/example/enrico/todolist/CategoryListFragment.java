package com.example.enrico.todolist;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.enrico.todolist.data.DataHandler;
import com.example.enrico.todolist.divider.HorizontalDividerItemDecoration;
import com.example.enrico.todolist.eventObjects.RefreshAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class CategoryListFragment extends Fragment {

    private static final String TAG = "RefreshedAdapter";
    public RecyclerView recyclerView;
    public CategoryItemsAdapter adapter;
    public View view;
    public ScrollView scrollView;
    private Parcelable recyclerViewState;
    private DataHandler dataHandler = DataHandler.getInstance();

    public CategoryListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_fragment, container, false);
        recyclerView = view.findViewById(R.id.categoryItems);
        scrollView = view.findViewById(R.id.scroll);

        //set recyclerView divider
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        Drawable divider = getResources().getDrawable(R.drawable.category_divider);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration(divider));

        adapter = new CategoryItemsAdapter(getContext(), (ArrayList<String>) dataHandler.getCategoryList(), CategoryListFragment.this, getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void checkModeState() {
        //save state
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        dataHandler.setNumItems(recyclerView.getAdapter().getItemCount());
        recyclerView.setAdapter(adapter);

        //restore state
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickedDone(RefreshAdapter refreshAdapter) {
        checkModeState();
    }
}
