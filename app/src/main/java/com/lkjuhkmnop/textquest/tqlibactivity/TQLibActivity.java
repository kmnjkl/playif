package com.lkjuhkmnop.textquest.tqlibactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.lkjuhkmnop.textquest.R;
import com.lkjuhkmnop.textquest.tools.Tools;
import com.lkjuhkmnop.textquest.tqmanager.CloudManager;
import com.lkjuhkmnop.textquest.tqmanager.DBQuest;

import java.util.List;

public class TQLibActivity extends AppCompatActivity {
    private RecyclerView tqlibRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TextQuest);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tqlib);

        tqlibRecView = findViewById(R.id.tqlib_recycler_view);

        Tools.cloudManager().getCloudQuests(new CloudManager.OnCMResponseListener<List<DBQuest>>() {
            @Override
            public void onCMResponse(CloudManager.CMResponse<List<DBQuest>> response) {
                DBQuest[] quests = new DBQuest[0];
                quests = response.getData().toArray(quests);

                TQLibAdapter adapter = new TQLibAdapter(getApplicationContext(), quests);
                tqlibRecView.setLayoutManager(new LinearLayoutManager(getParent()));
                tqlibRecView.setAdapter(adapter);
            }
        });
    }
}