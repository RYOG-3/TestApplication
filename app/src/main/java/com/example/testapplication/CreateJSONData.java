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
import java.util.List;

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

    private JSONObject defaultJSONObject;

    private JSONObject defaultRouterJSONObject;
    private JSONObject defaultSwitchJSONObject;
    private JSONObject defaultInterfaceJSONObject_Router;
    private JSONObject defaultInterfaceJSONObject_Switch;

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
            this.defaultJSONObject = new JSONObject(str);
            this.defaultRouterJSONObject = new JSONObject(jsonObject.getJSONArray("Routers").getJSONObject(0).toString());
            this.defaultSwitchJSONObject = new JSONObject(jsonObject.getJSONArray("Switches").getJSONObject(0).toString());
            this.defaultInterfaceJSONObject_Router = new JSONObject(jsonObject.getJSONArray("Routers").getJSONObject(0).getJSONArray("interface").getJSONObject(0).toString());
            this.defaultInterfaceJSONObject_Switch = new JSONObject(jsonObject.getJSONArray("Switches").getJSONObject(0).getJSONArray("interface").getJSONObject(0).toString());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 機器に設定したデータを全てJSONとして書き込む
     */
    public boolean setData() {
        try {
            System.out.println(jsonObject);

            // ルータの追加
            JSONArray array = jsonObject.getJSONArray("Routers");
            ArrayList<Router> routers = MainActivity.getAvailableRouters();
            for (int i = 0; i < routers.size(); i++) { // JSONArray オブジェクトは拡張for文は使えない
                if (routers.get(i) != null) {
                    System.out.println("TEST" + i);
                    array.getJSONObject(i).put("management_ip_address", routers.get(i).getManagement_ipAddress());
                    array.getJSONObject(i).put("hostname", routers.get(i).getHostname().getText());
                    array.getJSONObject(i).put("ip_domain_lookup", routers.get(i).getIp_domain_lookup());
                    array.getJSONObject(i).put("banner_motd", routers.get(i).getBanner());
                    array.getJSONObject(i).put("service_password-encryption", routers.get(i).getService_password());
                    array.getJSONObject(i).put("ip_domain-name", routers.get(i).getDomain_name());
                    array.getJSONObject(i).put("enable_secret", routers.get(i).getEnable_secret());
                    array.getJSONObject(i).put("line_console_password", routers.get(i).getPassword());
                    array.getJSONObject(i).put("logging_synchronous", routers.get(i).getLogging());

                    List<Interface> interfaces = routers.get(i).getInterfaces();
                    JSONArray interfaces_json = array.getJSONObject(i).getJSONArray("interface");
                    for (int j = 0; j < interfaces.size(); j++) {
                        interfaces_json.getJSONObject(j).put("name", interfaces.get(j).getInterfaceType().toString());
                        interfaces_json.getJSONObject(j).put("ipv4", interfaces.get(j).getIPv4());
                        interfaces_json.getJSONObject(j).put("subnet_mask", interfaces.get(j).getSubnet_mask());
                        interfaces_json.getJSONObject(j).put("shutdown", interfaces.get(j).getShutdown());
                        if (interfaces.size() != interfaces_json.length()) {
                            addInterface(array.getJSONObject(i), "Router");
                        }
                    }

                    System.out.println("END" + i);
                }
                if (i != routers.size() - 1) { // 二台目以降の設定データがあれば逐次 JSON データを追加する
                    addConfigList(jsonObject, "Router");
                }
            }

            // スイッチの追加
            array = jsonObject.getJSONArray("Switches");
            ArrayList<Switch> switchs = MainActivity.getAvailableSwitches();
            for (int i = 0; i < switchs.size(); i++) { // JSONArray オブジェクトは拡張for文は使えない
                if (switchs.get(i) != null) {
                    System.out.println("TEST" + i);
                    array.getJSONObject(i).put("hostname", switchs.get(i).getHostname().getText());
                    array.getJSONObject(i).put("ip_domain_lookup", switchs.get(i).getIp_domain_lookup());
                    array.getJSONObject(i).put("banner_motd", switchs.get(i).getBanner());
                    array.getJSONObject(i).put("service_password-encryption", switchs.get(i).getService_password());
                    array.getJSONObject(i).put("ip_domain-name", switchs.get(i).getDomain_name());
                    array.getJSONObject(i).put("enable_secret", switchs.get(i).getEnable_secret());
                    array.getJSONObject(i).put("line_console_password", switchs.get(i).getPassword());
                    array.getJSONObject(i).put("logging_synchronous", switchs.get(i).getLogging());

                    List<VLAN> vlanList = switchs.get(i).getVlanList();
                    JSONArray interfaces = array.getJSONObject(i).getJSONArray("interface");
                    for (int j = 0; j < vlanList.size(); j++) {
                        if (j == 0) {
                            interfaces.getJSONObject(j).put("name", vlanList.get(j).getInterfaceType());
                            interfaces.getJSONObject(j).put("switch_port_mode", vlanList.get(j).getSwitchPortMode());
                            interfaces.getJSONObject(j).getJSONArray("vlan_ID").put(vlanList.get(j).getVlanID());
                            interfaces.getJSONObject(j).getJSONArray("vlan_name").put(vlanList.get(j).getVlanName());
                        } else {
                            for (int k = 0; k < interfaces.length(); k++) {
                                if (interfaces.getJSONObject(k).get("name") == vlanList.get(j).getInterfaceType()) {
                                    interfaces.getJSONObject(k).getJSONArray("vlan_ID").put(vlanList.get(j).getVlanID());
                                    interfaces.getJSONObject(k).getJSONArray("vlan_name").put(vlanList.get(j).getVlanName());
                                    break;
                                } else {
                                    addInterface(array.getJSONObject(i), "Switch");

                                    interfaces.getJSONObject(k+1).put("name", vlanList.get(j).getInterfaceType());
                                    interfaces.getJSONObject(k+1).put("switch_port_mode", vlanList.get(j).getSwitchPortMode());
                                    interfaces.getJSONObject(k+1).getJSONArray("vlan_ID").put(vlanList.get(j).getVlanID());
                                    interfaces.getJSONObject(k+1).getJSONArray("vlan_name").put(vlanList.get(j).getVlanName());
                                    break;
                                }
                            }
                        }
                    }
                    System.out.println("END" + i);
                }
                if (i != switchs.size() - 1) { // 二台目以降の設定データがあれば逐次 JSON データを追加する
                    addConfigList(jsonObject, "Switch");
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

    public void addConfigList(JSONObject jsonObject, String type) {
        try {
            if (type == "Router") {
                jsonObject.accumulate("Routers", defaultRouterJSONObject);
            } else if (type == "Switch") {
                jsonObject.accumulate("Switches", defaultSwitchJSONObject);

            } else {
                System.out.println("JSONデータは追加されませんでした");
            }

        } catch (JSONException e) {

        }
    }

    public void addInterface(JSONObject jsonObject, String type) {
        try {
            if (type == "Router") {
                jsonObject.accumulate("interface", defaultInterfaceJSONObject_Router);
            } else if (type == "Switch") {
                jsonObject.accumulate("interface", defaultInterfaceJSONObject_Switch);
            } else {
                System.out.println("JSONデータは追加されませんでした");
            }


        } catch (JSONException e) {

        }
    }
}
