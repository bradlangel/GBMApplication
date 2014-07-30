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


    @Test
    public void callToAPI() {
//Create Rest Adaptor; explicitly call Okhttp

        RestAdapter restAdapterT =  new RestAdapter.Builder()
                .setEndpoint("https://dash.by/api/v1")
                .build();

        DashService dashApiServiceT = restAdapterT.create(DashService.class);


    }
}
