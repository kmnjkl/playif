package com.lkjuhkmnop.textquest.playactivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lkjuhkmnop.textquest.R;

import java.util.HashMap;

public class CharParamsAdapter extends RecyclerView.Adapter<CharParamsAdapter.ViewHolder> {
    HashMap<String, String> charParamsData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView charParamName, charParamValue;
        public ViewHolder(View itemView) {
            super(itemView);

            charParamName = (TextView) itemView.findViewById(R.id.char_param_name);
            charParamValue = (TextView) itemView.findViewById(R.id.char_param_value);
        }

        public void setCharParamNameText(String text) {
            charParamName.setText(text);
        }

        public void setCharParamValueText(String text) {
            charParamValue.setText(text);
        }
    }

    public CharParamsAdapter(HashMap<String, String> charParamsData) {
        this.charParamsData = charParamsData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.char_param_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String charParamKey = charParamsData.keySet().toArray()[position].toString();
        holder.setCharParamNameText(charParamKey);
        holder.setCharParamValueText(charParamsData.get(charParamKey));
    }

    @Override
    public int getItemCount() {
        return charParamsData == null ? 0 : charParamsData.size();
    }
}
