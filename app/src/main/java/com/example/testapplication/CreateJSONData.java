package com.example.testapplication;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 送信するJSONデータを作成するクラス
 * タブレット上で登録したネットワーク機器への設定情報を書き出す
 */
public class CreateJSONData {
    private Context context;
    private AssetManager assetManager;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private JSONObject jsonObject;

    public CreateJSONData(Context context) {
        this.context = context;
        this.assetManager = context.getResources().getAssets();
        try {
            this.inputStream = assetManager.open("dataList.json");
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
            String str = "";
            String line_str;
            while ((line_str = this.bufferedReader.readLine()) != null) {
                str += line_str;
            }
            this.jsonObject = new JSONObject(str);

            this.inputStream.close();
            this.bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 設定したデータを全てJSONとして書き込む
     */
    public boolean setData() {
        try {
            System.out.println(jsonObject);
            JSONArray array = jsonObject.getJSONArray("Routers");
            ArrayList<Router> routers = MainActivity.getAvailableRouters();
            for (int i = 0; i < routers.size(); i++) { // JSONArray オブジェクトは拡張for文は使えない
                if (routers.get(i) != null) {
                    System.out.println("TEST" + i);
                    array.getJSONObject(i).put("hostname", routers.get(i).getHostname().getText());
                    array.getJSONObject(i).put("ip_domain_lookup", routers.get(i).getIp_domain_lookup());
                    array.getJSONObject(i).put("banner_motd", routers.get(i).getBanner());
                    array.getJSONObject(i).put("service_password-encryption", routers.get(i).getService_password());
                    array.getJSONObject(i).put("ip_domain-name", routers.get(i).getDomain_name());
                    array.getJSONObject(i).put("enable_secret", routers.get(i).getEnable_secret());
                    array.getJSONObject(i).put("line_console_password", routers.get(i).getPassword());
                    array.getJSONObject(i).put("logging_synchronous", routers.get(i).getLogging());
                    System.out.println("END" + i);
                }
                if (i != routers.size() - 1) { // JSONデータを追加する
                    addConfigList(jsonObject);
                }
            }
            System.out.println(jsonObject);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("何も配置されていません");
            return false;
        }
    }

    /**
     * 作成したJSONの文字列を返すゲッター
     *
     * @return JSONの文字列データ
     */
    public String getJson() {
        return jsonObject.toString();
    }

    public void addConfigList(JSONObject jsonObject) {
        try {
            JSONObject json = new JSONObject("{" +
                    "\"hostname\": \"\"," +
                    "\"ip_domain_lookup\": false," +
                    "\"banner_motd\": \"\"," +
                    "\"service_password-encryption\": false," +
                    "\"ip_domain-name\": \"\"," +
                    "\"enable_secret\": \"\"," +
                    "\"line_console_password\": \"\"," +
                    "\"logging_synchronous\": false," +
                    "\"ipv4\": 0," +
                    "\"subnet_mask\": 0," +
                    "\"ipv6\": 0," +
                    "\"prefix\": 0," +
                    "\"description\": \"\"" +
                    "}");
            jsonObject.accumulate("Routers", json);
        } catch (JSONException e) {

        }
    }
}
