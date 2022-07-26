package com.example.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

public class Switch extends AppCompatImageView implements Device {
    private float x, y; // 画像の中心座標
    private int switch_ID; // スイッチの番号
    private boolean isSet = false; // 設定済かどうか
    private int[] vlan; // 設定しているVLANの番号
    private TextView hostname; // スイッチのホスト名
    private boolean ip_domain_lookup; // ip domain-lookupが有効か無効か(有効がtrue)
    private String banner; // バナー名
    private boolean service_password; // service password-encryptionが有効か無効か
    private String domain_name; // ドメイン名
    private String enable_secret; // 特権モードのパスワード
    private String password; // コンソールのパスワード
    private boolean logging; // logging synchronousが有効か無効か

    public Switch(Context context, float x, float y, int switch_ID) {
        super(context);
        this.x = x;
        this.y = y;
        this.switch_ID = switch_ID;
        hostname = new TextView(context);
        hostname.setText("Switch" + switch_ID);
        hostname.setTextColor(Color.RED);
        hostname.setWidth(200);
        hostname.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setImageResource(R.drawable.resource_switch);
        this.setId(2000+switch_ID);
        ip_domain_lookup = false;
        banner = "";
        service_password = false;
        domain_name = "";
        enable_secret = "";
        password = "";
        logging = false;
    }

    public void setID(int switch_ID) {
        this.switch_ID = switch_ID;
    }

    public void setHostname(TextView hostname) {
        this.hostname = hostname;
    }

    public float getCenterX() {
        return x;
    }

    public float getCenterY() {
        return y;
    }

    public boolean getIsSet() {
        return isSet;
    }

    public int getID() {
        return switch_ID;
    }

    public TextView getHostname() {
        return hostname;
    }

    /**
     * ネットワーク機器の基本情報に関するセッターたち
     */
    public void setIp_domain_lookup(boolean ip_domain_lookup) {
        this.ip_domain_lookup = ip_domain_lookup;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void setService_password(boolean service_password) {
        this.service_password = service_password;
    }

    public void setDomain_name(String domain_name) {
        this.domain_name = domain_name;
    }

    public void setEnable_secret(String enable_secret) {
        this.enable_secret = enable_secret;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    /**
     * ネットワーク機器の基本情報のゲッターたち
     */
    public boolean getIp_domain_lookup() {
        return ip_domain_lookup;
    }

    public String getBanner() {
        return banner;
    }

    public boolean getService_password() {
        return service_password;
    }

    public String getDomain_name() {
        return domain_name;
    }

    public String getEnable_secret() {
        return enable_secret;
    }

    public String getPassword() {
        return password;
    }

    public boolean getLogging() {
        return logging;
    }
}
