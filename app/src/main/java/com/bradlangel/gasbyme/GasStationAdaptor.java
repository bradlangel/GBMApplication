package com.bradlangel.gasbyme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by bradlangel on 7/21/14.
 */
public class GasStationAdaptor extends BaseAdapter {

    Context context;
    List<GasStation> gasStationList;
    String preference;

    //Max length of String(distance, price) to be displayed on ListView output
    final int maxLength = 2;


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


        convertView = mInflater.inflate(R.layout.list_item, null);
        holder = new ViewHolder();

        holder.gas_pic = (ImageView) convertView.findViewById(R.id.gas_pic);
        holder.station_name = (TextView) convertView.findViewById(R.id.station_name);
        holder.distance = (TextView) convertView.findViewById(R.id.distance);
        holder.price = (TextView) convertView.findViewById(R.id.price);


        GasStation gasStationPosition = gasStationList.get(position);


        //Use Picasso to place images into ImageView
        Picasso.with(context)
                .load(gasStationPosition.getImageUrl())
                .resize(70, 70)
                .into(holder.gas_pic);



        //Place data into text view variables
        holder.station_name.setText(gasStationPosition.getLongName());

        holder.distance.setText(Double.toString(round(gasStationPosition.getDistance(), maxLength)) + " mi");

        /*
         * Use preferences to find correct price to place in text view variable
         */
        if(preference.equals("regular")) {

            Double regularPrice =  gasStationPosition.getRegular();
            //If we want regular gas price but station has none then set holder to null
            if(regularPrice == 0.0) {
                holder.price.setText("No Regular");
            } else {
                holder.price.setText("$" + Double.toString(round(regularPrice, maxLength)));
            }
        } else if(preference.equals("premium")) {

            Double premiumPrice =  gasStationPosition.getPremium();
            //If we want premium gas price but station has none then set holder to null
            if(premiumPrice == 0.0) {
                holder.price.setText("No Premium");
            } else {
                holder.price.setText("$" + Double.toString(round(premiumPrice, maxLength)));
            }
        } else if(preference.equals("plus")) {

            Double plusPrice =  gasStationPosition.getPlus();
            //If we want plus gas price but station has none then set holder to null
            if(plusPrice == 0.0) {
                holder.price.setText("No Plus");
            } else {
                holder.price.setText("$" + Double.toString(round(plusPrice, maxLength)));
            }
        } else if(preference.equals("diesel")) {

            Double dieselPrice =  gasStationPosition.getDiesel();
            //If we want diesel gas price but station has none then set holder to null
            if(dieselPrice == 0.0) {
                holder.price.setText("No Diesel");
            } else {
                holder.price.setText("$" + Double.toString(round(dieselPrice, maxLength)));
            }
        } else {

            holder.price.setText("BAD PREFERENCE!: " + preference);
        }



        convertView.setTag(holder);

        return convertView;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}