package com.lkjuhkmnop.textquest.questmanageactivity;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lkjuhkmnop.textquest.R;

import java.util.LinkedHashMap;

public class CharPDataAdapter extends RecyclerView.Adapter<CharPDataAdapter.ViewHolder> {
    private QuestManageActivity questManageActivity;
    private LinkedHashMap<String, String> charPData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView charPName, charPValueLabel;
        EditText charPValue;
        ImageView charPDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            charPName = itemView.findViewById(R.id.char_p_data_name);
            charPValueLabel = itemView.findViewById(R.id.char_p_data_value_label);
            charPValue = itemView.findViewById(R.id.char_p_data_value);
            charPDelete = itemView.findViewById(R.id.char_p_data_delete);
        }

        public void setCharPNameText(String text) {
            charPName.setText(text);
        }

        public void setCharPValueText(String text) {
            charPValue.setText(text);
        }

        public boolean requestValueFocus() {
            return charPValue.requestFocus();
        }

        public View getItemView() {
            return itemView;
        }
    }

    public CharPDataAdapter(QuestManageActivity questManageActivity, LinkedHashMap<String, String> charPData) {
        this.questManageActivity = questManageActivity;
        this.charPData = charPData;
    }

    @NonNull
    @Override
    public CharPDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quest_character_p_data_item, parent, false);
        return new CharPDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharPDataAdapter.ViewHolder holder, int position) {
//        if (position == charProps.size()) {
//            return;
//        }
//        Set character p. data name
        String charPName = charPData.keySet().toArray()[position].toString();
        holder.setCharPNameText(charPName);
//        Set char. p. data value (empty string in new property)
        holder.setCharPValueText(charPData.get(charPName));

//        Save character p. data value when it's text (in EditText) changed
        EditText charPValueET = holder.getItemView().findViewById(R.id.char_p_data_value);
        charPValueET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String changeCharPName = charPData.keySet().toArray()[position].toString();
                charPData.replace(changeCharPName, s.toString());
            }
        });

//        Delete char. p. data on corresponding button click
        ImageView charPDelete = holder.getItemView().findViewById(R.id.char_p_data_delete);
        charPDelete.setOnClickListener(v -> {
            Context context = holder.getItemView().getContext();
            String deleteCharPName = charPData.keySet().toArray()[position].toString();
            Toast.makeText(context, context.getText(R.string.char_p_data_delete_msg) + " " + deleteCharPName, Toast.LENGTH_SHORT).show();
            charPData.remove(deleteCharPName);
            notifyDataSetChanged();
        });

        if (position == getItemCount()-1) {
            holder.requestValueFocus();
        }
    }

    @Override
    public int getItemCount() {
        return charPData == null ? 0 : charPData.size();
    }


}

