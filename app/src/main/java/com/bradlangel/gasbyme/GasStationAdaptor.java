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
 * Adapter to display Gas Station information
 */
public class GasStationAdaptor extends BaseAdapter {

    Context context;
    List<GasStation> gasStationList;
    String preference;

    //Max length of String(e.g. distance, price) to be displayed on ListView output
    static final int distmaxLength = 2;
    static final int maxLength = 3;

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
        TextView  address;
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
        holder.address = (TextView) convertView.findViewById(R.id.address);
        holder.distance = (TextView) convertView.findViewById(R.id.distance);
        holder.price = (TextView) convertView.findViewById(R.id.price);


        GasStation gasStationPosition = gasStationList.get(position);


        //Use Picasso to place images into ImageView
        Picasso.with(context)
                .load(gasStationPosition.getImageUrl())
                .into(holder.gas_pic);

        //Place address into text view variables
        holder.address.setText(gasStationPosition.getAddress());

        /*
         * Use preferences to find correct price to place in text view variable
         */

        if(preference.equals(this.context.getString(R.string.premium))) {
            Double premiumPrice =  gasStationPosition.getPremium();
            //If we want premium gas price but station has none then set holder to null
            if(premiumPrice == 0.0) {
                holder.price.setText(this.context.getString(R.string.no_premium));
                convertView.setTag(holder);
                return convertView;
            } else {
                holder.price.setText("$" + Double.toString(round(premiumPrice, maxLength)));
            }
        } else if(preference.equals(this.context.getString(R.string.plus))) {
            Double plusPrice =  gasStationPosition.getPlus();
            //If we want plus gas price but station has none then set holder to null
            if(plusPrice == 0.0) {
                holder.price.setText(this.context.getString(R.string.no_plus));
                convertView.setTag(holder);
                return convertView;
            } else {
                holder.price.setText("$" + Double.toString(round(plusPrice, maxLength)));
            }
        } else if(preference.equals(this.context.getString(R.string.diesel))) {
            Double dieselPrice =  gasStationPosition.getDiesel();
            //If we want diesel gas price but station has none then set holder to null
            if(dieselPrice == 0.0) {
                holder.price.setText(this.context.getString(R.string.no_diesel));
                convertView.setTag(holder);
                return convertView;
            } else {
                holder.price.setText("$" + Double.toString(round(dieselPrice, maxLength)));
            }
        } else {

            Double regularPrice =  gasStationPosition.getRegular();
            //If we want regular gas price but station has none then set holder to null
            if(regularPrice == 0.0) {
                holder.price.setText(this.context.getString(R.string.no_regular));
                convertView.setTag(holder);
                return convertView;
            } else {
                holder.price.setText("$" + Double.toString(round(regularPrice, maxLength)));
            }
        }


        //Place distance into text view variables
        holder.distance.setText(Double.toString(round(gasStationPosition.getDistance(), distmaxLength)) + this.context.getString(R.string.miles));


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