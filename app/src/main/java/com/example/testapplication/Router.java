package com.example.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * カスタムビューを作ってみた
 * これでImageViewとルータのクラスをまとめることができたはず
 */

public class Router extends AppCompatImageView implements Device {
    private float x, y; // 画像の中心座標
    private int router_ID; // ルータの番号
    private boolean isSet = false; // 設定済かどうか
    private String management_ipAddress; // 管理用のIPアドレス（管理サーバーからネットワーク機器に送信するのに必要）
    private TextView hostname; // ルータのホスト名
    private boolean ip_domain_lookup; // ip domain-lookupが有効か無効か(有効がtrue)
    private String banner; // バナー名
    private boolean service_password; // service password-encryptionが有効か無効か
    private String domain_name; // ドメイン名
    private String enable_secret; // 特権モードのパスワード
    private String password; // コンソールのパスワード
    private boolean logging; // logging synchronousが有効か無効か
    private List<Interface> interfaces; // ルータが保持するインターフェースのリスト
    private boolean rip; // RIP プロトコルが有効か（デフォルトでは false）
    private boolean ospf; // OSPF プロトコルが有効か（デフォルトでは false）
    private Cable cable; // 接続しているケーブル

    public Router(Context context, float x, float y, int router_ID) {
        super(context);
        this.x = x;
        this.y = y;
        this.router_ID = router_ID;
        hostname = new TextView(context);
        hostname.setText("Router" + router_ID);
        hostname.setTextColor(Color.RED);
        hostname.setWidth(200);
        hostname.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setImageResource(R.drawable.router);
        this.setId(1000+router_ID);
        this.management_ipAddress = "";
        interfaces = new ArrayList<>();
        rip = false;
        ospf = false;
        cable = null;
    }

    public void setID(int router_ID) {
        this.router_ID = router_ID;
    }

    public void setManagement_ipAddress(String management_ipAddress) { this.management_ipAddress = management_ipAddress; }
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
        return router_ID;
    }

    public TextView getHostname() {
        return hostname;
    }

    /**
     * ネットワーク機器の基本情報やルーティングプロトコルに関するセッターたち
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

    public void setRIP(boolean rip) {this.rip = rip;}

    public void setOSPF(boolean ospf) {this.ospf = ospf;}

    public void setCable(Cable cable) {this.cable = cable;}

    /**
     * ネットワーク機器の基本情報やルーティングプロトコルに関するゲッターたち
     */
    public String getManagement_ipAddress() { return management_ipAddress; }
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

    public boolean getRIP() { return rip; }

    public boolean getOSPF() { return ospf; }

    public Cable getCable() {
        return cable;
    }

    public void addInterface(Interface router_interface) {
        interfaces.add(router_interface);
    }

    public List<Interface> getInterfaces() { return interfaces; }

}
