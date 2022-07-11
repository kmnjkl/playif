package com.lkjuhkmnop.textquest.libraryactivity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lkjuhkmnop.textquest.R;
import com.lkjuhkmnop.textquest.tools.Tools;
import com.lkjuhkmnop.textquest.tqmanager.CloudManager;
import com.lkjuhkmnop.textquest.tqmanager.DBGame;
import com.lkjuhkmnop.textquest.tqmanager.DBQuest;

import java.util.Calendar;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private Context context;
    private LibraryActivity libraryActivity;
    private DBQuest[] questsData;

    public void setQuestsData(DBQuest[] questsData) {
        this.questsData = questsData;
    }

    public void changeQuestData(int position, DBQuest quest) {
        questsData[position] = quest;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView questId, questTitle, questAuthor;
        ImageView qNewGame, qCloudButton, qSettings, qDelete;
        String qIdText = "", qIdAddedText = "";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            questId = itemView.findViewById(R.id.tqlib_quest_cid);
            questTitle = itemView.findViewById(R.id.tqlib_quest_title);
            questAuthor = itemView.findViewById(R.id.tqlib_quest_author);
            qNewGame = itemView.findViewById(R.id.tqlib_download_button);
            qCloudButton = itemView.findViewById(R.id.tqlib_quest_cloud_button);
            qSettings = itemView.findViewById(R.id.tqlib_quest_settings);
            qDelete = itemView.findViewById(R.id.tqlib_quest_delete);
        }

        public void setIdText() {
            questId.setText(itemView.getResources().getString(R.string.lib_quest_id_prefix) + " " + qIdText + qIdAddedText);
        }

        public void setIdText(String idText) {
            qIdText = idText;
            setIdText();
        }

        public void setIdAddedText(String qIdAddedText) {
            this.qIdAddedText = qIdAddedText;
            setIdText();
        }

        public void setTitleText(String title) {
            questTitle.setText(title);
        }

        public void setAuthorText(String author) {
            questAuthor.setText(author);
        }

        public void setCloudButtonVisibility(int visibility) {
            qCloudButton.setVisibility(visibility);
        }

        public void setCloudButtonImage(int resourceId) {
            qCloudButton.setImageResource(resourceId);
        }

        public void setCloudButtonOnClickListener(View.OnClickListener onClickListener) {
            qCloudButton.setOnClickListener(onClickListener);
        }

        public View getItemView() {
            return itemView;
        }
    }

    private void matchQuest(int position, ViewHolder lqViewHolder) {
        Log.d("LKJD", "mathQuest");
        if (questsData[position].getQuestCloudId() != null && !questsData[position].getQuestCloudId().equals("")
                && questsData[position].getQuestUploaderUserId() != null && !questsData[position].getQuestUploaderUserId().equals("")) {
            Log.d("LKJD", "cloud matchQuest");
            Tools.cloudManager().matchQuest(questsData[position], response -> {
                if (response.getResponseCode() == CloudManager.OK && response.getData() == CloudManager.QUEST_MATCH) {
                    Log.d("LKJD", "ok matchQuest: " + response);
                    displayUploaded(position, lqViewHolder);
                } else {
                    Log.d("LKJD", "no matchQuest: " + response);
                    questsData[position].setQuestCloudId(null);
                    questsData[position].setQuestUploaderUserId(null);
                    try {
                        Tools.tqManager().updateQuest(context, questsData[position]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    enableUpload(position, lqViewHolder);
                }
            });
        } else {
            Log.d("LKJD", "no cloud mathQuest needed");
            enableUpload(position, lqViewHolder);
        }
    }

    private void enableUpload(int position, ViewHolder holder) {
        holder.setIdAddedText("");
        holder.setAuthorText("Anonymous");
        holder.setCloudButtonImage(R.drawable.ic_cloud_upload);
        holder.setCloudButtonVisibility(View.VISIBLE);
//            Set on click listener for the cloud upload button
        holder.qCloudButton.setOnClickListener(v -> {
            try {
                Tools.cloudManager().uploadQuest(context, questsData[position].getQuestId(),
                        response -> {
                            libraryActivity.reloadQuestsList(position, questsData[position]);
                            matchQuest(position, holder);
                        });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            holder.setCloudButtonVisibility(View.GONE);
        });
    }

    private void displayUploaded(int position, ViewHolder holder) {
        holder.setCloudButtonImage(R.drawable.ic_cloud_delete);
        holder.setCloudButtonVisibility(View.VISIBLE);
        holder.setCloudButtonOnClickListener(v -> {
            Log.d("LKJD", "LIB/CLOUD: lib: quest " + questsData[position].getQuestId() + ": delete cloud quest (" + questsData[position].getQuestCloudId() + ") BUTTON CLICKED");
            try {
                Tools.cloudManager().deleteQuest(context, questsData[position], response -> {
                    libraryActivity.reloadQuestsList(position, questsData[position]);
                    matchQuest(position, holder);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            holder.setCloudButtonVisibility(View.GONE);
        });

//        Set author's name
        Tools.cloudManager().getUserDisplayName(questsData[position].getQuestUploaderUserId(), new CloudManager.OnCMResponseListener<String>() {
            @Override
            public void onCMResponse(CloudManager.CMResponse<String> response) {
                holder.setAuthorText(response.getData());
            }
        });
//        Append quest id
        String questCloudId = questsData[position].getQuestCloudId();
        holder.setIdAddedText("\n[CId: " + questCloudId.substring(0, 4) + "..." + questCloudId.substring(questCloudId.length() - 3) + "]");
    }

    public LibraryAdapter(Context context, LibraryActivity libraryActivity,  DBQuest[] quests) {
        this.context = context;
        this.libraryActivity = libraryActivity;
        this.questsData = quests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lib_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Set quests description
        holder.setIdText(String.valueOf(questsData[position].getQuestId()));
        holder.setTitleText(questsData[position].getQuestTitle());

//        Set click listeners
//        For description
        holder.getItemView().findViewById(R.id.tqlib_description).setOnClickListener(v -> {
            Toast.makeText(v.getContext(), questsData[position].getQuestTitle(), Toast.LENGTH_SHORT).show();
            matchQuest(position, holder);
        });

//        For the new game button
        ImageView addButton = holder.qNewGame;
        addButton.setOnClickListener(v -> {
            String newGameTitle = questsData[position].getQuestTitle();
            try {
                while (Tools.tqManager().getGameByTitle(context, newGameTitle) != null) {
                    newGameTitle = newGameTitle + "_n";
                }
                DBGame newGame = new DBGame(questsData[position].getQuestId(), newGameTitle, Calendar.getInstance().getTimeInMillis());
                Tools.tqManager().addGame(context, newGame);
                Tools.startPlayActivity(libraryActivity, addButton, newGameTitle);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

//        For the settings button

//        For the delete button
        holder.qDelete.setOnClickListener(v -> {
            try {
                Tools.tqManager().deleteQuestById(context, questsData[position].getQuestId());
                libraryActivity.reloadQuestsList();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        matchQuest(position, holder);
    }

    @Override
    public int getItemCount() {
        return questsData == null ? 0 : questsData.length;
    }
}
