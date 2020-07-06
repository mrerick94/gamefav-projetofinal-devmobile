package com.example.projetofinal.control.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpService {

    private static final String URL_BASE_RAWG = "https://api.rawg.io/api/games?page_size=20";
    private static final String URL_BASE_IMGBB = "https://api.imgbb.com/1/upload?expiration=1209600&key=";
    private static final String API_KEY_IMGBB = "5e53f759b1303f0095827fee90bdb2f6";


    //Acho que da pra fazer mais enxuto se pensar um pouco mais
    public static String getGameList(String pesquisa, String proximaPagina) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        String urlPesquisa = URL_BASE_RAWG + "&search=";
        if (pesquisa != null && !pesquisa.equals("")) {
            if (proximaPagina != null) {
                Request request = builder.url(proximaPagina).get().addHeader("User-Agent", "MobileDevelopmentClassProject").build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } else {
                String pesquisaEncoded = encodePesquisa(pesquisa);
                if (pesquisaEncoded != null) {
                    urlPesquisa += pesquisaEncoded;
                    Request request = builder.url(urlPesquisa).get().addHeader("User-Agent", "MobileDevelopmentClassProject").build();
                    Response response = client.newCall(request).execute();
                    return response.body().string();
                }
            }
        } else if (proximaPagina != null) {
            Request request = builder.url(proximaPagina).get().addHeader("User-Agent", "MobileDevelopmentClassProject").build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        Request request = builder.url(URL_BASE_RAWG).get().addHeader("User-Agent", "MobileDevelopmentClassProject").build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String postImage(String encodedImage) throws IOException {
        String url = URL_BASE_IMGBB + API_KEY_IMGBB;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("image", encodedImage).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).post(body).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //encoda a pesquisa digitada pelo usuario para poder inserir ela na url
    private static String encodePesquisa(String pesquisa) {
        try {
            return URLEncoder.encode(pesquisa, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
