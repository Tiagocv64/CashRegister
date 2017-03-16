package com.creation.tiagocv.cashregister;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tcver on 15/03/2017.
 */

public class RegistersAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String[]> mList;

    public RegistersAdapter(Context c, ArrayList<String[]> s) {
        mContext = c;
        mList = s;
    }

    public int getCount() {
        return mList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new CardView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View cardView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            cardView =  View.inflate( parent.getContext(), R.layout.card_view_registers, null);

            TextView textViewRegisterMedalNumber = (TextView) cardView.findViewById(R.id.text_view_medal_number);
            TextView textViewRegisterCaixa = (TextView) cardView.findViewById(R.id.text_view_caixa);
            TextView textViewRegisterVendas = (TextView) cardView.findViewById(R.id.text_view_vendas);
            TextView textViewRegisterLucro = (TextView) cardView.findViewById(R.id.text_view_lucro);

            textViewRegisterMedalNumber.setText("#" + mList.get(position)[0]);
            textViewRegisterCaixa.setText(mList.get(position)[1]);
            textViewRegisterVendas.setText("Vendas: " + mList.get(position)[2]);
            textViewRegisterLucro.setText("Lucro: " + mList.get(position)[3]);

            cardView.setPadding(8, 8, 8, 8);
        } else {
            cardView =  convertView;
        }


        return cardView;
    }
}