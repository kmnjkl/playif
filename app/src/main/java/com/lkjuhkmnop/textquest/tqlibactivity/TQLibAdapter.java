package com.lkjuhkmnop.textquest.tqlibactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lkjuhkmnop.textquest.R;
import com.lkjuhkmnop.textquest.libraryactivity.LibraryAdapter;
import com.lkjuhkmnop.textquest.tools.Tools;
import com.lkjuhkmnop.textquest.tqmanager.CloudManager;
import com.lkjuhkmnop.textquest.tqmanager.DBQuest;

import java.util.HashMap;

public class TQLibAdapter extends RecyclerView.Adapter<TQLibAdapter.ViewHolder> {
    private Context context;
    private DBQuest[] questsData;

    public TQLibAdapter(Context context, DBQuest[] questsData) {
        this.context = context;
        this.questsData = questsData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView questCid, questTitle, questAuthor;
        private ImageView downloadBtn;

        private String tqCidText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questCid = itemView.findViewById(R.id.tqlib_quest_cid);
            questTitle = itemView.findViewById(R.id.tqlib_quest_title);
            questAuthor = itemView.findViewById(R.id.tqlib_quest_author);
            downloadBtn = itemView.findViewById(R.id.tqlib_download_button);
        }

        public void setQuestCidText() {
            questCid.setText(itemView.getResources().getText(R.string.tqlib_quest_cid_prefix) + " " + tqCidText);
        }

        public void setQuestCidText(String cidText) {
            tqCidText = cidText;
            setQuestCidText();
        }

        public void setTitleText(String title) {
            questTitle.setText(title);
        }

        public void setAuthorText(String author) {
            questAuthor.setText(author);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tqlib_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setQuestCidText(questsData[position].getQuestCloudId());
        holder.setTitleText(questsData[position].getQuestTitle());
        Tools.cloudManager().getUserDisplayName(questsData[position].getQuestUploaderUserId(), new CloudManager.OnCMResponseListener<String>() {
            @Override
            public void onCMResponse(CloudManager.CMResponse<String> response) {
                holder.setAuthorText(response.getData());
            }
        });

        holder.downloadBtn.setOnClickListener(v -> {
            DBQuest quest = questsData[position];
            Tools.tqManager().addQuest(quest.getQuestTitle(), quest.getQuestCloudId(), quest.getQuestUploaderUserId(), Tools.getGson().fromJson(quest.getCharacterProperties(), HashMap.class), Tools.getGson().fromJson(quest.getCharacterParameters(), HashMap.class), quest.getQuestJson(), context, context.getContentResolver());
        });
    }

    @Override
    public int getItemCount() {
        return questsData == null ? 0 : questsData.length;
    }
}
