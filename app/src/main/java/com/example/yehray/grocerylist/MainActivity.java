package com.example.yehray.grocerylist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import edu.gatech.seclass.GroceryListManager.db.DatabaseAccess;


public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private FloatingActionButton btnAdd;
    private DatabaseAccess databaseAccess;
    private ArrayList<GroceryList> groceryLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.databaseAccess = DatabaseAccess.getInstance(this);

        this.listView = (ListView) findViewById(R.id.listMainView);
        this.btnAdd  = (FloatingActionButton) findViewById(R.id.fab2);

        groceryLists = new ArrayList<GroceryList>();

        GroceryListAdapter adapter = new GroceryListAdapter(this, groceryLists);
        this.listView.setAdapter(adapter);

        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClicked();
            }
        });

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroceryList groceryList = groceryLists.get(position);
                TextView txtMemo = (TextView) view.findViewById(R.id.txtMemo);
                if (groceryList.isFullDisplayed()) {
                    txtMemo.setText(groceryList.getShortText());
                    groceryList.setFullDisplayed(false);
                } else {
                    txtMemo.setText(groceryList.getText());
                    groceryList.setFullDisplayed(true);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseAccess.open();
        this.groceryLists = databaseAccess.getAllGroceryLists();
        databaseAccess.close();
        GroceryListAdapter adapter = new GroceryListAdapter(this, groceryLists);
        this.listView.setAdapter(adapter);
    }

    public void onAddClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New List");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = inflater.inflate(R.layout.list_title_add, null);

        final EditText input = (EditText) addView.findViewById(R.id.textTitleNew);

        input.setMaxLines(1);
        input.setSingleLine(true);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(100);
        input.setFilters(filterArray);


        input.setHint("Enter name of list.");
        builder.setView(addView);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(input.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(),"Please enter a valid list title.", Toast.LENGTH_LONG).show();
                    return;
                }
                GroceryList newList = new GroceryList();
                newList.setText(input.getText().toString());
                databaseAccess.open();
                databaseAccess.save(newList);
                databaseAccess.close();

                ArrayAdapter<GroceryList> adapter = (ArrayAdapter<GroceryList>) listView.getAdapter();
                adapter.add(newList);
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alert = builder.create();

        alert.show();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    alert.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    alert.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        alert.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

    public void onGoClicked() {
        Intent intent = new Intent(this, EditListActivity.class);
        startActivity(intent);
    }

    public void onDeleteClicked(GroceryList groceryList) {
        databaseAccess.open();
        databaseAccess.delete(groceryList);
        databaseAccess.close();

        ArrayAdapter<GroceryList> adapter = (ArrayAdapter<GroceryList>) listView.getAdapter();
        adapter.remove(groceryList);
        adapter.notifyDataSetChanged();
    }

    public void onEditClicked(GroceryList groceryList) {
        try {
            Intent intent = new Intent(this, EditListActivity.class);
            intent.putExtra("MEMO", groceryList);
            startActivity(intent);
        } catch (Exception ex) {
            String test = ex.getMessage();
            String blah = test;

        }
    }

    private class GroceryListAdapter extends ArrayAdapter<GroceryList> {


        public GroceryListAdapter(Context context, List<GroceryList> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.grocerylist, parent, false);
            }

            ImageView btnEdit = (ImageView) convertView.findViewById(R.id.btnEdit);
            ImageView btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
            TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            TextView txtMemo = (TextView) convertView.findViewById(R.id.txtMemo);

            final GroceryList groceryList = groceryLists.get(position);
            groceryList.setFullDisplayed(false);
            txtDate.setText(groceryList.getDate());
            txtMemo.setText(groceryList.getShortText());
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditClicked(groceryList);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClicked(groceryList);
                }
            });
            return convertView;
        }
    }
}