package com.lkjuhkmnop.textquest.tqmanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity
public class DBGame {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "game_id")
    private int gameId;

    @ColumnInfo(name = "quest_id")
    private int questId;

    @ColumnInfo(name = "game_title")
    private String gameTitle;

    @ColumnInfo(name = "game_last_passage_pid", defaultValue = "-1")
    private int gameLastPassagePid = -1;

    @ColumnInfo(name = "game_time")
    private long gameTimestamp;

    @ColumnInfo(name = "game_char_properties_json")
    private String gameCharPropertiesJson;

//    Constructor to create new game
    public DBGame(int questId, String gameTitle, long gameTimestamp) {
        this.questId = questId;
        this.gameTitle = gameTitle;
        this.gameTimestamp = gameTimestamp;
    }

//    Constructor to update existing game
    @Ignore
    public DBGame(int gameId, int gameLastPassagePid, long gameTimestamp, String gameCharPropertiesJson) {
        this.gameId = gameId;
        this.gameLastPassagePid = gameLastPassagePid;
        this.gameTimestamp = gameTimestamp;
        this.gameCharPropertiesJson = gameCharPropertiesJson;
    }

    //    public DBGame(int questId, String gameTitle, int gameLastPassagePid, long gameTimestamp, String gameCharPropertiesJson) {
//        this.questId = questId;
//        this.gameTitle = gameTitle;
//        this.gameLastPassagePid = gameLastPassagePid;
//        this.gameTimestamp = gameTimestamp;
//        this.gameCharPropertiesJson = gameCharPropertiesJson;
//    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public int getGameLastPassagePid() {
        return gameLastPassagePid;
    }

    public void setGameLastPassagePid(int gameLastPassagePid) {
        this.gameLastPassagePid = gameLastPassagePid;
    }

    public long getGameTimestamp() {
        return gameTimestamp;
    }

    public void setGameTimestamp(long gameTimestamp) {
        this.gameTimestamp = gameTimestamp;
    }

    public String getGameCharPropertiesJson() {
        return gameCharPropertiesJson;
    }

    public void setGameCharPropertiesJson(String gameCharPropertiesJson) {
        this.gameCharPropertiesJson = gameCharPropertiesJson;
    }
}
