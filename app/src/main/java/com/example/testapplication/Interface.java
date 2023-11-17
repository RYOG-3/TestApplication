package com.example.testapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * ルータとスイッチのインターフェースに関する情報を保持するクラス
 */
public class Interface {
    private InterfaceType interfaceType;
    private String ipv4;
    private String subnet_mask;
    private String ipv6;
    private int prefix;
    private String description;
    private boolean shutdown;
    private int cableID; // 繋がっているケーブルのID
    private List<VLAN> vlanList; // インターフェースに登録されている VLAN リスト

    public Interface(InterfaceType interfaceType, String ipv4, String subnet_mask, boolean shutdown, int cableID) {
        this.interfaceType = interfaceType;
        this.ipv4 = ipv4;
        this.subnet_mask = subnet_mask;
        this.shutdown = shutdown;
        this.cableID = cableID;
    }

    public Interface(InterfaceType interfaceType, boolean shutdown, int cableID) { // スイッチで使用するコンストラクタ
        this.interfaceType = interfaceType;
        this.shutdown = shutdown;
        this.cableID = cableID;
        this.vlanList = new ArrayList<>();
    }

    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    public String getIPv4() {
        return ipv4;
    }

    public String getSubnet_mask() {
        return subnet_mask;
    }

    public boolean getShutdown() { return shutdown; }

    public int getConnectedCableID() {
        return cableID;
    }

    public void setInterfaceType(InterfaceType interfaceType) {
        this.interfaceType = interfaceType;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public void setSubnet_mask(String subnet_mask) {
        this.subnet_mask = subnet_mask;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
