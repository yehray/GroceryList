package com.example.yehray.grocerylist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;

public class ItemAdapter extends ArrayAdapter<Item> {
    ArrayList<Item> items = null;
    Context context;

    public ItemAdapter(Context context, ArrayList<Item> resource) {
        super(context, R.layout.item, resource);
        this.context = context;
        this.items = resource;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.item, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        TextView qty = (TextView) convertView.findViewById(R.id.textQtyUnit);
        TextView category = (TextView) convertView.findViewById(R.id.textSection);

        name.setText(items.get(position).getName());
        qty.setText(""+items.get(position).getQuantity()+" "+items.get(position).getUnit());
        category.setText(items.get(position).getCategory());

        if(position > 0) {
            if ((items.get(position - 1).getCategory()).equals(items.get(position).getCategory())) {
                category.setVisibility(View.GONE);
            }
            else {
                category.setVisibility(View.VISIBLE);
            }
        } else {
            category.setVisibility(View.VISIBLE);
        }

        if(items.get(position).getValue() > 0)
            cb.setChecked(true);
        else
            cb.setChecked(false);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton v,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                CheckBox checkBox=(CheckBox)v;
                if (isChecked) {
                    items.get(position).setValue(1);
                }
                else {
                    items.get(position).setValue(0);
                }
            }
        });


        return convertView;
    }


}
