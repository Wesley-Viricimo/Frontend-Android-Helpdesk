package com.example.helpdesk.api.interceptor;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class Interceptor implements okhttp3.Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "token")
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
