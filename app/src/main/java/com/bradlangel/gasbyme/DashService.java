package com.bradlangel.gasbyme;


import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by bradlangel on 7/17/14.
 * Interface for retrofit calls to Dash-Api
 */
public interface DashService {

    /*
     * Asynchronous call
     */
    @GET("/gas/price/latlng/{latitude}/{longitude}/{sort}")
    void getGasStations(@Path("latitude") String latitude,
                                     @Path("longitude") String longitude,
                                     @Path("sort") String sort,
                                     Callback<List<GasStation>> callback);

    /*
     * Synchronous call
     */
    @GET("/gas/price/latlng/{latitude}/{longitude}/{sort}")
    List<GasStation> listGasStations(@Path("latitude") String latitude,
                                     @Path("longitude") String longitude,
                                     @Path("sort") String sort);
}
