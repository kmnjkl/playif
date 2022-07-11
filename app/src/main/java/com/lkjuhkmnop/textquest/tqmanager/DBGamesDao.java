package com.lkjuhkmnop.textquest.tqmanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DBGamesDao {
    @Query("SELECT * FROM DBGame")
    DBGame[] getAllGames();

    @Query("SELECT * FROM DBGame WHERE game_id = (:gameId)")
    DBGame getGameById(int gameId);

    @Query("SELECT * FROM DBGame WHERE game_title = (:gameTitle)")
    DBGame getGameByTitle(String gameTitle);

    @Query("UPDATE DBGame SET game_last_passage_pid = (:gameLastPassagePid), game_time = (:gameTimestamp), game_char_properties_json = (:gameCharPropertiesJson) WHERE game_id = (:gameId)")
    void updateGame(int gameId, int gameLastPassagePid, long gameTimestamp, String gameCharPropertiesJson);

    @Insert
    void insert(DBGame... games);

    @Query("DELETE FROM DBGame WHERE game_id IN (:ids)")
    void deleteGamesByIds(int... ids);

    @Delete
    void deleteGames(DBGame... games);
}
