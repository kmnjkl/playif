package com.lkjuhkmnop.textquest.gamesactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.lkjuhkmnop.textquest.R;
import com.lkjuhkmnop.textquest.tools.Tools;
import com.lkjuhkmnop.textquest.tqmanager.DBGame;

public class GamesActivity extends AppCompatActivity {
    private RecyclerView gamesResView;

    private GamesAdapter gamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TextQuest);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        gamesResView = findViewById(R.id.games_recycler_view);

        DBGame[] games = new DBGame[0];
        try {
            games = Tools.tqManager().getGames(getApplicationContext());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gamesAdapter = new GamesAdapter(this, getApplicationContext(), games);
        gamesResView.setLayoutManager(new LinearLayoutManager(this));
        gamesResView.setAdapter(gamesAdapter);
    }

    public void reloadGamesList() throws InterruptedException {
        gamesAdapter.setGames(Tools.tqManager().getGames(getApplicationContext()));
        gamesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            reloadGamesList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}