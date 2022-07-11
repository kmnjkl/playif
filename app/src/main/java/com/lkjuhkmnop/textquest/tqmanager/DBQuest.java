package com.lkjuhkmnop.textquest.tqmanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class DBQuest {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "quest_id")
    private int questId;

    @ColumnInfo(name = "quest_cloud_id")
    private String questCloudId;

    @ColumnInfo(name = "quest_uploader_user_id")
    private String questUploaderUserId;

    @ColumnInfo(name = "quest_title")
    private String questTitle;

    @ColumnInfo(name = "character_properties")
    private String characterProperties;

    @ColumnInfo(name = "character_parameters")
    private String characterParameters;

    @ColumnInfo(name = "quest_json")
    private String questJson;


    public DBQuest(String questCloudId, String questTitle, String characterProperties, String characterParameters, String questJson) {
        this.questCloudId = questCloudId;
        this.questTitle = questTitle;
        this.characterProperties = characterProperties;
        this.characterParameters = characterParameters;
        this.questJson = questJson;
    }

    @Ignore
    public DBQuest(String questCloudId, String questUploaderUserId, String questTitle, String characterProperties, String characterParameters, String questJson) {
        this.questCloudId = questCloudId;
        this.questUploaderUserId = questUploaderUserId;
        this.questTitle = questTitle;
        this.characterProperties = characterProperties;
        this.characterParameters = characterParameters;
        this.questJson = questJson;
    }

    @Ignore
    public DBQuest(String questCloudId, String questUploaderUserId, String questTitle) {
        this.questCloudId = questCloudId;
        this.questUploaderUserId = questUploaderUserId;
        this.questTitle = questTitle;
    }

    @Ignore
    public DBQuest(String questCloudId, String questUploaderUserId) {
        this.questCloudId = questCloudId;
        this.questUploaderUserId = questUploaderUserId;
    }

    @Ignore
    public DBQuest() {}

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public String getQuestCloudId() {
        return questCloudId;
    }

    public void setQuestCloudId(String questCloudId) {
        this.questCloudId = questCloudId;
    }

    public String getQuestUploaderUserId() {
        return questUploaderUserId;
    }
    public void setQuestUploaderUserId(String questUploaderUserId) {
        this.questUploaderUserId = questUploaderUserId;
    }

    public String getQuestTitle() {
        return questTitle;
    }

    public void setQuestTitle(String questTitle) {
        this.questTitle = questTitle;
    }

    public String getQuestJson() {
        return questJson;
    }

    public void setQuestJson(String questJson) {
        this.questJson = questJson;
    }

    public String getCharacterProperties() {
        return characterProperties;
    }

    public void setCharacterProperties(String characterProperties) {
        this.characterProperties = characterProperties;
    }

    public String getCharacterParameters() {
        return characterParameters;
    }

    public void setCharacterParameters(String characterParameters) {
        this.characterParameters = characterParameters;
    }
}
