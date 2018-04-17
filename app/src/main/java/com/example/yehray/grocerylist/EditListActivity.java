package com.example.yehray.grocerylist;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Comparator;
import java.util.concurrent.Callable;

import android.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;

import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.gatech.seclass.GroceryListManager.db.DatabaseAccess;

public class EditListActivity extends AppCompatActivity {
    ArrayList<Item> shoppingList = null;
    ItemAdapter adapter = null;
    ListView lv = null;
    EditText et = null;
    private GroceryList groceryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        et = (EditText) findViewById(R.id.textTitle);
        lv = (ListView) findViewById(R.id.listView);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.shoppingList = new ArrayList<Item>();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            groceryList = (GroceryList) bundle.get("MEMO");
            if(groceryList != null) {
                this.et.setText(groceryList.getText());
                this.shoppingList = groceryList.getItems();

            }
        }

        sortItems();
        registerForContextMenu(lv);
        adapter = new ItemAdapter(this, shoppingList);

        lv.setAdapter(adapter);


    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Integer pos = info.position;
        if(item.getTitle()=="Edit") {
            Showdialog(pos);
        }
        if(item.getTitle()=="Delete") {
            String itemName = shoppingList.get(pos).getName();
            removeItem(itemName, pos);
        }
        return true;
    }

    public void goBack() {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu with items in action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Action handlers for items in Menu bar
        int id = item.getItemId();


        if(id == android.R.id.home) {
            saveGroceryList();
            goBack();
            return true;
        }

        if (id == R.id.action_sort) {
            sortItems();

            lv.setAdapter(adapter);
            return true;
        }

        if (id == R.id.action_add) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Item");

            ArrayAdapter<String> spinnerAdapter = getSpinnerAdapter();

            ArrayAdapter<String> autoAdapter = getAutoAdapter(null);

            LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View search_View = inflater.inflate(R.layout.item_add, null);

            final Spinner spinner = (Spinner) search_View.findViewById(R.id.categorySpinner);
            // Creating adapter for spinner


            // attaching data adapter to spinner
            spinner.setAdapter(spinnerAdapter);

            final AutoCompleteTextView autoText = (AutoCompleteTextView) search_View.findViewById(R.id.autoName);

            autoText.setAdapter(autoAdapter);
            autoText.setThreshold(1);

            autoText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View arg0) {
                    autoText.showDropDown();
                }
            });

            /////
            builder.setView(search_View);

            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        shoppingList.add(new Item(formatText(autoText.getText().toString()), 0, 1, spinner.getSelectedItem().toString(), "EACH"));
                        sortItems();
                        lv.setAdapter(adapter);
                        saveGroceryList();
                    } catch (Exception ex) {
                        String msg = ex.getMessage();
                    }
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

            autoText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        if (spinner.getSelectedItemPosition() > 0) {
                            alert.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    } else {
                        alert.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    if (position > 0) {
                        String selectedCat = parentView.getItemAtPosition(position).toString();
                        ArrayAdapter<String> autoAdapter = getAutoAdapter(selectedCat);
                        autoText.setAdapter(autoAdapter);

                        if (autoText.getText().toString().length() > 0) {
                            alert.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
                        }

                    }
                    else {
                        alert.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            alert.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
            return true;
        }

        //TODO: instead of clearing out the fields, this should just uncheck the checkboxes
        if (id == R.id.action_clear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reset Check List?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for(Item item : shoppingList) {
                        item.setValue(0);
                    }
                    lv.setAdapter(adapter);
                    saveGroceryList();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            lv.setAdapter(adapter);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Showdialog(final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Item Quantity");

        ArrayAdapter<String> spinnerAdapter = getUnitSpinnerAdapter();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View editView = inflater.inflate(R.layout.qty_unit_edit, null);

        final Spinner spinner = (Spinner) editView.findViewById(R.id.unitSpinner);
        // Creating adapter for spinner


        // attaching data adapter to spinner
        spinner.setAdapter(spinnerAdapter);

        String currentSpinnerValue =  shoppingList.get(position).getUnit();
        int spinnerPosition = spinnerAdapter.getPosition(currentSpinnerValue);

        //set the default according to value
        spinner.setSelection(spinnerPosition);

        //final EditText inputName = (EditText) search_View.findViewById(R.id.textName);
        final NumberPicker inputQty = (NumberPicker) editView.findViewById(R.id.numberPickerEdit);
        inputQty.setMaxValue(50);
        inputQty.setMinValue(0);
        inputQty.setValue(shoppingList.get(position).getQuantity());

        /////
        builder.setView(editView);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    shoppingList.get(position).setQuantity(inputQty.getValue());
                    shoppingList.get(position).setUnit(spinner.getSelectedItem().toString());

                    lv.setAdapter(adapter);
                    saveGroceryList();
                } catch (Exception ex) {
                    String msg = ex.getMessage();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public ArrayAdapter<String> getAutoAdapter(String category) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        String[] autoItems = databaseAccess.getDistinctItems(category);
        databaseAccess.close();
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, autoItems);

        return autoAdapter;
    }

    public ArrayAdapter<String> getUnitSpinnerAdapter(){
        try {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            String[] units = databaseAccess.getDistinctUnits();
            databaseAccess.close();

            // Creating adapter for spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, units);

            // Drop down layout style - list view with radio button
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            return spinnerAdapter;
        } catch (Exception ex) {
            String msg = ex.getMessage();
            return new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {});
        }
    }
    public ArrayAdapter<String> getSpinnerAdapter() {
        try {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            String[] categories = databaseAccess.getDistinctCategories();
            databaseAccess.close();


            // Creating adapter for spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

            // Drop down layout style - list view with radio button
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            return spinnerAdapter;
        } catch (Exception ex) {
            String msg = ex.getMessage();
            return new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {});
        }


    }

    public static String formatText(String original)
    {
        if (original.isEmpty())
            return original;

        return original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
    }

    public void saveGroceryList() {
        try {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            if (groceryList == null) {
                // Add new memo
                GroceryList temp = new GroceryList();
                temp.setText(et.getText().toString());
                temp.setItems(shoppingList);
                databaseAccess.save(temp);
            } else {
                // Update the memo
                groceryList.setText(et.getText().toString());
                groceryList.setItems(shoppingList);
                databaseAccess.update(groceryList);
            }
            databaseAccess.close();
        } catch (Exception ex) {
            String test = ex.getMessage();
        }

    }

    public void onCancelClicked() {
        this.finish();
    }

    public static void storeListToLocal( ArrayList<Item> items, Context context)
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(items);

        editor.putString("myArray", json);
        editor.commit();
    }

    public static ArrayList getListFromLocal( Context context)
    {
        //SharedPreferences WordSearchGetPrefs = dan.getSharedPreferences("dbArrayValues",Activity.MODE_PRIVATE);
        //Set<Item> tempSet = new HashSet<Item>();
        //tempSet = WordSearchGetPrefs.get
        //tempSet = WordSearchGetPrefs.getStringSet("myArray", tempSet);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("myArray", null);
        Type type = new TypeToken<ArrayList<Item>>() {}.getType();
        ArrayList<Item> items = gson.fromJson(json, type);

        return items;
    }

    public void sortItems()
    {
        Collections.sort(shoppingList, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return (item1.getCategory() + item1.getName()).compareTo((item2.getCategory() + item2.getName()));
            }
        });
    }

    public void removeItem(String selectedItem, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove " + selectedItem + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shoppingList.remove(position);
                sortItems();
                lv.setAdapter(adapter);
                saveGroceryList();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        lv.setAdapter(adapter);
        saveGroceryList();
        goBack();
    }
}