package com.lkjuhkmnop.textquest.gamesactivity;

import android.content.Context;
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
import com.lkjuhkmnop.textquest.tqmanager.DBGame;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {
    private GamesActivity gamesActivity;
    private Context context;
    private DBGame[] games;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView gameTitle, gameTime;
        ImageView gameNewGame, gameSettings, gameDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            gameTitle = itemView.findViewById(R.id.game_title);
            gameTime = itemView.findViewById(R.id.game_time);
            gameNewGame = itemView.findViewById(R.id.game_new_game);
            gameSettings = itemView.findViewById(R.id.game_settings);
            gameDelete = itemView.findViewById(R.id.game_delete);
        }

        public void setTitle(String title) {
            gameTitle.setText(title);
        }

        public void setTime(String time) {
            gameTime.setText(time);
        }

        public View getItemView() {
            return itemView;
        }
    }

    public GamesAdapter(GamesActivity gamesActivity, Context context, DBGame[] games) {
        this.gamesActivity = gamesActivity;
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.games_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setTitle(games[position].getGameTitle());
        holder.setTime((new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.ROOT)).format(new Date(games[position].getGameTimestamp())));

//        Set click listeners
//        For description
        holder.getItemView().findViewById(R.id.game_description).setOnClickListener(v -> {
            Tools.startPlayActivity(gamesActivity, v, games[position].getGameTitle());
        });

//        For the new game button
        holder.getItemView().findViewById(R.id.game_new_game).setOnClickListener(v -> Toast.makeText(v.getContext(), games[position].getGameTitle() + "\nNEW GAME", Toast.LENGTH_SHORT).show());

//        For the delete button
        holder.getItemView().findViewById(R.id.game_delete).setOnClickListener(v -> {
            try {
                Tools.tqManager().deleteGameById(context, games[position].getGameId());
                gamesActivity.reloadGamesList();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return games == null ? 0 : games.length;
    }

    public void setGames(DBGame[] games) {
        this.games = games;
    }

    public DBGame[] getGames() {
        return games;
    }
}
