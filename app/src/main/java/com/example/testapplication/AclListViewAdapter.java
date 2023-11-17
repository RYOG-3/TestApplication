package com.example.testapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AclListViewAdapter extends BaseAdapter {
    static class ViewHolder {
        TextView permissionView;
        TextView ipaddressView;
        TextView wildcardView;
    }

    private final LayoutInflater inflater;
    private final int itemLayoutId;
    private final List<Boolean> permissions;
    private final List<String> aclLists_ipaddress;
    private final List<String> aclLists_wildcard;

    AclListViewAdapter(Context context, int itemLayoutId, List<Boolean> permissions, List<String> aclLists_ipaddress, List<String> aclLists_wildcard) {
        super();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemLayoutId = itemLayoutId;
        this.permissions = permissions;
        this.aclLists_ipaddress = aclLists_ipaddress;
        this.aclLists_wildcard = aclLists_wildcard;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AclListViewAdapter.ViewHolder holder;
        // 最初だけ View を inflate して、それを再利用する
        if (convertView == null) {
            // activity_main.xml に list.xml を inflate して convertView とする
            convertView = inflater.inflate(itemLayoutId, parent, false);
            // ViewHolder を生成
            holder = new AclListViewAdapter.ViewHolder();
            holder.permissionView = convertView.findViewById(R.id.permission);
            holder.ipaddressView = convertView.findViewById(R.id.ipaddress_acl);
            holder.wildcardView = convertView.findViewById(R.id.wildcard_acl);
            convertView.setTag(holder);
        }
        // holder を使って再利用
        else {
            holder = (AclListViewAdapter.ViewHolder) convertView.getTag();
        }

        // holder の 各View にセット
        if (permissions.get(position)) {
            holder.permissionView.setText("Permit");
        } else {
            holder.permissionView.setText("Deny");
        }
        holder.ipaddressView.setText(aclLists_ipaddress.get(position));
        // 現在の position にあるファイル名リストを holder の textView にセット
        holder.wildcardView.setText(aclLists_wildcard.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        // texts 配列の要素数
        return permissions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
