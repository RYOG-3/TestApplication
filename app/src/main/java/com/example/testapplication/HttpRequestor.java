package com.example.testapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import androidx.core.util.Consumer;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 非同期通信HTTPリクエスト実行クラス<br />
 * <p>[使用方法]</p>
 * new HttpRequestor(
 *　 this,
 *　 "http://xxxx.xx",
 *　 "処理中メッセージ",
 *　 b -> {
 *　 // rはバイト配列のレスポンスデータ。
 *　 // 正常時の処理を記載。
 *　 },
 *　 e -> {
 *　 // eはエクセプション。
 *　 // エラー時の処理を記載。
 *　 }
 * ).execute();
 */
public class HttpRequestor extends AsyncTask<Void, Void, byte[]> {
    private Context context = null;
    private String url = null;
    private String message = null;
    private Consumer<byte[]> callback = null;
    private Consumer<Exception> errorCallback = null;
    private Exception exception = null;
    private ProgressDialog progressDialog = null;

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param url 通信先URL
     * @param message 処理中メッセージ
     * @param callback 正常時のコールバック関数(Consumer<byte[]>)
     * @param errorCallback エラー時のコールバック関数(Consumer<Exception>)
     */
    public HttpRequestor(Context context, String url, String message, Consumer<byte[]> callback, Consumer<Exception> errorCallback) {
        this.context = context;
        this.url = url;
        this.message = message;
        this.callback = callback;
        this.errorCallback = errorCallback;
}

    @Override
    protected void onPreExecute() {
        // 砂時計表示
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setMessage(this.message);
        progressDialog.setCancelable(false);
        this.progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        HttpURLConnection con = null;
        try {
            // HTTPリクエスト
            final URL url = new URL(this.url);
            con = (HttpURLConnection) url.openConnection();
            con.connect();

            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                final InputStream in = con.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte [] buffer = new byte[1024];
                while(true) {
                    int len = in.read(buffer);
                    if(len < 0) {
                        break;
                    }
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
                return out.toByteArray();
            } else {
                throw new IOException("HTTP status:" + status);
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString(), e);
            this.exception = e;
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }


    @Override
    protected void onPostExecute(byte[] response) {
        super.onPostExecute(response);
        // コールバック処理実行
        if (this.exception == null) {
            callback.accept(response);
        } else {
            errorCallback.accept(this.exception);
        }
        // 砂時計解除
        this.progressDialog.dismiss();
    }
}
