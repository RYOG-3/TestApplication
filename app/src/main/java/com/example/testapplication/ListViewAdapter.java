package com.example.testapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * VLANのリストを管理するためのアダプタークラスをカスタマイズした継承クラス
 */
public class ListViewAdapter extends BaseAdapter {
    static class ViewHolder {
        TextView vlanIdView;
        TextView vlanNameView;
    }

    private final LayoutInflater inflater;
    private final int itemLayoutId;
    private final List<Integer> vlanIds;
    private final List<String> vlanNames;

    ListViewAdapter(Context context, int itemLayoutId, List<Integer> vlanIds, List<String> vlanNames) {
        super();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemLayoutId = itemLayoutId;
        this.vlanIds = vlanIds;
        this.vlanNames = vlanNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // 最初だけ View を inflate して、それを再利用する
        if (convertView == null) {
            // activity_main.xml に list.xml を inflate して convertView とする
            convertView = inflater.inflate(itemLayoutId, parent, false);
            // ViewHolder を生成
            holder = new ViewHolder();
            holder.vlanIdView = convertView.findViewById(R.id.vlan_number);
            holder.vlanNameView = convertView.findViewById(R.id.vlan_name);
            convertView.setTag(holder);
        }
        // holder を使って再利用
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        // holder の vlanIdView にセット
        holder.vlanIdView.setText(Integer.toString(vlanIds.get(position)));
        // 現在の position にあるファイル名リストを holder の textView にセット
        holder.vlanNameView.setText(vlanNames.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        // texts 配列の要素数
        return vlanIds.size();
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
