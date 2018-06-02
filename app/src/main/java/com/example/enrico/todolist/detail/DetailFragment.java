package com.example.enrico.todolist.detail;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enrico.todolist.MainActivity;
import com.example.enrico.todolist.R;
import com.example.enrico.todolist.data.DataHandler;
import com.example.enrico.todolist.dialog.GiveUpCurrentEditing;
import com.example.enrico.todolist.eventObjects.PassToDetailFragment;
import com.example.enrico.todolist.eventObjects.RefreshAdapter;
import com.example.enrico.todolist.eventObjects.RefreshFragment;
import com.example.enrico.todolist.eventObjects.UpdateHighlight;
import com.example.enrico.todolist.json.JsonHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private static final String TAG = "Clicked";
    private EditText title;
    private EditText detail;
    private String currentTitle;
    private String currentDetail;
    private DataHandler dataHandler = DataHandler.getInstance();
    private int currentIndex = dataHandler.getClickedIndex();
    private int titleInputType;
    private int detailInputType;
    private JsonHelper jsonHelper = JsonHelper.getInstance();

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        title = view.findViewById(R.id.editTitle);
        detail = view.findViewById(R.id.editDetail);


        //get current title
        currentTitle = dataHandler.getClickedItem();
        //get current detail
        currentDetail = dataHandler.getMap().get(currentTitle);

        setItems();

        titleInputType = title.getInputType();
        detailInputType = detail.getInputType();


        if(dataHandler.getEditMode()){
            title.setInputType(View.LAYER_TYPE_NONE);
            detail.setInputType(View.LAYER_TYPE_NONE);
        }

        return view;
    }

    private void setItems() {
        if (dataHandler.getClickedItem() != null) {
            String clickedItem = dataHandler.getClickedItem();
            title.setText(clickedItem);
            detail.setText(dataHandler.getMap().get(clickedItem));
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editMode) {
            if(!dataHandler.getEditMode()){
                //is not in edit mode
                title.setInputType(View.LAYER_TYPE_NONE);
                detail.setInputType(View.LAYER_TYPE_NONE);
                dataHandler.setEditMode(true);
            }else{
                //is in edit mode
                title.setInputType(titleInputType);
                detail.setInputType(detailInputType);
                dataHandler.setEditMode(false);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void done(PassToDetailFragment pass) {
        saveNewItem();
        jsonHelper.saveMapToPrefs(getActivity());
        Toast.makeText(getContext(), "Map: " + dataHandler.getMap().size() +
                "List: " + dataHandler.getCategoryList().size(), Toast.LENGTH_SHORT).show();

        UpdateHighlight update = new UpdateHighlight();
        EventBus.getDefault().post(update);

        RefreshAdapter refresh = new RefreshAdapter();
        EventBus.getDefault().post(refresh);   //refreshes the adapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addedNewCategory(UpdateHighlight update){
        int position = dataHandler.getCategoryList().indexOf(title.getText().toString());
        dataHandler.setSelectedPosition(position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(MainActivity mainActivity) {
        setItems();
    }

    private void saveNewItem() {
        currentTitle = dataHandler.getClickedItem();
        String currentText = title.getText().toString();
        String value = detail.getText().toString();
        if (!TextUtils.isEmpty(currentText)) {
            if (currentText.equals(currentTitle)) {
                dataHandler.updateDetail(currentText, value);
                Toast.makeText(getContext(), R.string.note_saved, Toast.LENGTH_SHORT).show();
            } else {
                currentIndex = dataHandler.getClickedIndex();
                dataHandler.removeTodo(currentTitle);
                dataHandler.addTodo(currentText, value);
                dataHandler.getCategoryList().remove(currentTitle);
                dataHandler.addCategoryByTime(currentText);
                Log.i("CheckItems", "map"+dataHandler.getMap().toString());
                Log.i("CheckItems", "list: " + dataHandler.getCategoryList().toString());
                Toast.makeText(getContext(), R.string.note_saved, Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(currentText);
            }
        } else {
            if (!currentDetail.equals(value)) {
                dataHandler.updateDetail(currentTitle, value);
                Toast.makeText(getContext(), R.string.note_saved, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshFragment(RefreshFragment refreshFragment) {
        setItems();
    }
}
