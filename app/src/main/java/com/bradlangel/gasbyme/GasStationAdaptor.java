package com.bradlangel.gasbyme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bradlangel on 7/21/14.
 */
public class GasStationAdaptor extends BaseAdapter {

    Context context;
    List<GasStation> gasStationList;
    String preference;

    //Max length of String(distance, price) to be displayed on ListView output
    final int maxLength = 4;

    GasStationAdaptor(Context context, List<GasStation> gasStationList, String preference) {
        this.context = context;
        this.gasStationList = gasStationList;
        this.preference = preference;
    }

    @Override
    public int getCount() {
        return gasStationList.size();
    }

    @Override
    public GasStation getItem(int position) {
        return gasStationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return gasStationList.indexOf(getItem(position));
    }

    /* private viewHolder class */
    private class ViewHolder {
        ImageView gas_pic;
        TextView station_name;
        TextView distance;
        TextView price;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();

            holder.gas_pic = (ImageView) convertView.findViewById(R.id.gas_pic);
            holder.station_name = (TextView) convertView.findViewById(R.id.station_name);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.price = (TextView) convertView.findViewById(R.id.price);


            GasStation gasStationPosition = gasStationList.get(position);


            //TODO: Set imageResource! Use picassa
            holder.station_name.setText(gasStationPosition.getLongName());
            holder.distance.setText(Double.toString(gasStationPosition.getDistance()).substring(0, maxLength) + " mi");

            //See which gas price to get
            if(preference.equals("regular")) {

                Double regularPrice =  gasStationPosition.getRegular();
                //If we want regular gas price but station has none then set holder to null
                if(regularPrice == null) {
                    holder = null;
                } else {
                    holder.price.setText("$" + Double.toString(regularPrice).substring(0, maxLength));
                }
            } else if(preference.equals("premium")) {

                Double premiumPrice =  gasStationPosition.getPremium();
                //If we want premium gas price but station has none then set holder to null
                if(premiumPrice == null) {
                    holder = null;
                } else {
                    holder.price.setText("$" + Double.toString(premiumPrice).substring(0, maxLength));
                }
            } else if(preference.equals("plus")) {

                Double plusPrice =  gasStationPosition.getPlus();
                //If we want plus gas price but station has none then set holder to null
                if(plusPrice == null) {
                    holder = null;
                } else {
                    holder.price.setText("$" + Double.toString(plusPrice).substring(0, maxLength));
                }
            } else if(preference.equals("diesel")) {

                Double dieselPrice =  gasStationPosition.getDiesel();
                //If we want diesel gas price but station has none then set holder to null
                if(dieselPrice == null) {
                    holder = null;
                } else {
                    holder.price.setText("$" + Double.toString(dieselPrice));
                }
            } else {
                holder.price.setText("BAD PREFERENCE!: " + preference);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        return convertView;
    }
}