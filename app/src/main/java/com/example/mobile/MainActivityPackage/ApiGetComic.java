package com.example.mobile.MainActivityPackage;

import android.os.AsyncTask;

import com.example.mobile.MainActivityPackage.interfaces.GetComic;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiGetComic extends AsyncTask<Void, Void, Void> {
    String data;
    GetComic getComic;

    public ApiGetComic(GetComic getComic) {
        this.getComic = getComic;
        this.getComic.start();
    }
    @Override
    protected Void doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.myjson.online/v1/records/473ae232-80d7-4337-9f66-89d760776a3e")
                .build();
        data = null;
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            data = body.string();
        } catch (IOException e) {
            data = null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        if (data == null) {
            this.getComic.isError();
        } else {
            this.getComic.end(data);
        }
    }
}
