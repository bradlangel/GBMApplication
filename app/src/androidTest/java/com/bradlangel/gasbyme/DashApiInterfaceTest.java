package com.bradlangel.gasbyme;

import org.junit.Test;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by bradlangel on 7/17/14.
 */
public class DashApiInterfaceTest {

    private final static String latitude = "40.686647";
    private final static String longitude = "-73.991809";
    private final static String sort = "regular";
    private final static String key = "Authorization";
    private final static String value ="Bearer JnTSgfNZqIU7bix-YdUuwFBXU89x7EkqqohzzIWgjX6lY6m6qrH5WOCKn4Wr3yutXFoX7TBE7nOqmKga-Emq4TWwdKprNIp3ICzCQ8Lwg-ntPKViOoz8nQ";



    @Test
    public void callToAPI() {
//Create Rest Adaptor; explicitly call Okhttp

        RestAdapter restAdapterT =  new RestAdapter.Builder()
                .setEndpoint("https://dash.by/api/v1")
                .build();

        DashService dashApiServiceT = restAdapterT.create(DashService.class);


        //Each call on the generated dashApiService makes an HTTP request to the remote web server.
        List<GasStation> gasStationsT = dashApiServiceT.listGasStations(latitude, longitude, sort, key, value);

        String gasLocationT = gasStationsT.get(0).getLongName();

    }
}
