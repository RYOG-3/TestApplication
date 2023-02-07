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
    public void setData() {
        try {
            System.out.println(jsonObject);
            JSONArray array = jsonObject.getJSONArray("Routers");
            for (int i = 0; i < array.length(); i++) { // JSONArray オブジェクトは拡張for文は使えない
                array.getJSONObject(i).put("hostname", MainActivity.routers.get(i).getHostname().getText());
                array.getJSONObject(i).put("ip_domain_lookup", MainActivity.routers.get(i).getIp_domain_lookup());
                array.getJSONObject(i).put("banner_motd", MainActivity.routers.get(i).getBanner());
                array.getJSONObject(i).put("service_password-encryption", MainActivity.routers.get(i).getService_password());
                array.getJSONObject(i).put("ip_domain-name", MainActivity.routers.get(i).getDomain_name());
                array.getJSONObject(i).put("enable_secret", MainActivity.routers.get(i).getEnable_secret());
                array.getJSONObject(i).put("line_console_password", MainActivity.routers.get(i).getPassword());
                array.getJSONObject(i).put("logging_synchronous", MainActivity.routers.get(i).getLogging());
            }
            System.out.println(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("何も配置されていません");
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

}
