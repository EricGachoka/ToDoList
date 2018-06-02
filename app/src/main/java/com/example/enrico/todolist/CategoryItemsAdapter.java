package com.example.enrico.todolist;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.enrico.todolist.checks.MonitorChecks;
import com.example.enrico.todolist.data.DataHandler;
import com.example.enrico.todolist.eventObjects.RefreshAdapter;
import com.example.enrico.todolist.eventObjects.UpdateHighlight;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

class CategoryItemsAdapter extends RecyclerView.Adapter<CategoryItemsAdapter.ViewHolder>
        implements ActionMode.Callback {
    private static final String TAG = "ClickedItem";
    private ArrayList<String> list;
    private Context mContext;
    private View view;
    private CategoryListFragment fragment;
    private FragmentActivity activity;
    private ActionMode actionMode = null;
    private static Menu actionMenu;
    private DataHandler dataHandler = DataHandler.getInstance();

    CategoryItemsAdapter(Context context, ArrayList<String> list, CategoryListFragment fragment, FragmentActivity activity) {
        this.mContext = context;
        this.list = list;
        this.fragment = fragment;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.category_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.categoryText.setText(list.get(position));
        holder.toggle(position);
        if (MonitorChecks.checkMode) {
            holder.iconDelete.setVisibility(View.VISIBLE);
            startActionMode();
        }
        if (MonitorChecks.checkAll) {
            holder.iconDelete.setChecked(true);
        }

        //prevent checkBoxes from misbehaving
        holder.setIsRecyclable(false);

        //give color to selected item
        if ((dataHandler.getIsTablet()) && (position == dataHandler.getSelectedPosition())) {
            holder.parent.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        // TODO: 1/3/18 handle view recycling and coloring of rows
        if (holder.parent.getSolidColor() == Color.GRAY) {
            Log.i(TAG, "GrayColor" + holder.getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMenu = menu;
        MainActivity mainActivity = (MainActivity) mContext;
        mainActivity.getMenuInflater().inflate(R.menu.menu, menu);
        mode.setTitle(mainActivity.getString(R.string.delete_categories));
        actionMode = mode;
        activity.findViewById(R.id.fab).setVisibility(View.GONE);
        final CheckBox checkBox = (CheckBox) menu.findItem(R.id.remove_all).getActionView();
        MenuItem bin = menu.findItem(R.id.remove);

        bin.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ArrayList<Integer> checks = MonitorChecks.getChecks();
                ArrayList<String> items = new ArrayList<>();
                for (Integer check : checks) {
                    String key = dataHandler.getCategoryList().get(check);
                    items.add(key);
                    dataHandler.removeTodo(key);
                }
                dataHandler.getCategoryList().removeAll(items);
                MonitorChecks.getChecks().clear();

                UpdateHighlight update = new UpdateHighlight();
                EventBus.getDefault().post(update);

                RefreshAdapter refreshAdapter = new RefreshAdapter();
                EventBus.getDefault().post(refreshAdapter);
                return false;
            }
        });

        if (MonitorChecks.checkAll) {
            checkBox.setChecked(true);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    MonitorChecks.checkAll = true;
                    MonitorChecks.singleModeCheck = false;
//                    MonitorChecks.currentPosition = fragment.recyclerView.getVerticalScrollbarPosition();
                    fragment.checkModeState();
                } else {
                    if (!MonitorChecks.singleModeCheck) {
                        MonitorChecks.checkAll = false;
                        if (!MonitorChecks.checks.isEmpty()) {
                            MonitorChecks.clearChecks();
                        }
//                    MonitorChecks.currentPosition = fragment.recyclerView.getVerticalScrollbarPosition();
                        fragment.checkModeState();
                    }
                }
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove:
                actionMode.finish();
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (actionMode != null) {
            actionMode = null;
            MonitorChecks.checkMode = false;
            MonitorChecks.getChecks().clear();
            MonitorChecks.checkAll = false;
            activity.findViewById(R.id.fab).setVisibility(View.VISIBLE);
            dataHandler.setActionMode(false);
            fragment.checkModeState();
            EventBus.getDefault().post(this);       //event to notify the mainActivity that the action mode has been destroyed
        }
    }

    private void startActionMode() {
        if (actionMode == null) {
            actionMode = activity.startActionMode(this);
        }
    }

        class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        CheckBox iconDelete;
        LinearLayout parent;

        ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            iconDelete = itemView.findViewById(R.id.delete);
            categoryText = itemView.findViewById(R.id.categoryItem);

            categoryText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(categoryText.getText().toString());
                    dataHandler.setSelectedPosition(getAdapterPosition());
                    RefreshAdapter refreshAdapter = new RefreshAdapter();
                    EventBus.getDefault().post(refreshAdapter);   //refresh the recyclerview
                }
            });

            categoryText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dataHandler.setActionMode(true);
                    EventBus.getDefault().post(categoryText.getText().toString());
                    dataHandler.setSelectedPosition(getAdapterPosition());
                    MonitorChecks.checkMode = true;
                    MonitorChecks.setCurrentPosition(getAdapterPosition());
                    RefreshAdapter refreshAdapter = new RefreshAdapter();
                    EventBus.getDefault().post(refreshAdapter);   //refresh the recyclerview
                    return true;
                }
            });
        }

        //handle toggling of checkboxes
        public void toggle(final int position) {
            if (MonitorChecks.getChecks().contains(position)) {
                iconDelete.setChecked(true);
            } else {
                iconDelete.setChecked(false);
            }

            iconDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        if (!MonitorChecks.checks.contains(position)) {
                            MonitorChecks.addCheck(position);
                            if (dataHandler.getNumItems() == MonitorChecks.getChecks().size()) {
                                CheckBox check = (CheckBox) actionMenu.findItem(R.id.remove_all).getActionView();
                                check.setChecked(true);
                                MonitorChecks.checkAll = true;
                            }
                        }
                    } else {
                        if (MonitorChecks.checks.contains(position)) {
                            MonitorChecks.removeCheck(position);
                            CheckBox check = (CheckBox) actionMenu.findItem(R.id.remove_all).getActionView();
                            MonitorChecks.singleModeCheck = true;
                            MonitorChecks.checkAll = false;
                            check.setChecked(false);
                        }
                    }
                }
            });
        }

    }
}
