package com.lkjuhkmnop.textquest.tqmanager;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.room.Update;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lkjuhkmnop.textquest.story.TQCharacter;
import com.lkjuhkmnop.textquest.story.TQQuest;
import com.lkjuhkmnop.textquest.story.TQStory;
import com.lkjuhkmnop.textquest.tools.Tools;

import java.util.HashMap;

/**
 * Class to manage local database.
 */
public class TQManager {
//    /===== singleton pattern implementation to use AppDatabase to work with app's database using Room =====\
    private static final String DATABASE_NAME = "textquest";
    private static volatile AppDatabase APP_DATABASE_INSTANCE;

    private static AppDatabase getAppDatabaseInstance(Context context) {
        if (APP_DATABASE_INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (APP_DATABASE_INSTANCE == null) {
                    APP_DATABASE_INSTANCE
                            = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return APP_DATABASE_INSTANCE;
    }
//    \===== singleton pattern implementation to use AppDatabase to work with app's database using Room =====/

    /* MANAGE DATABASE*/
    /* QUESTS TABLE*/
    /**
     * Class to add a new quest to the local library in new thread.
     * @see TQManager#addQuest
     * */
    private static class AddQuest extends Thread {
//        Title of new quest
        private String title;
        private String cloudId;
//        Quest's author
        private String author;
//        Character's properties and parameters in the new quest
        private HashMap<String, String> characterProperties;
        private HashMap<String, String> characterParameters;
//        Json from Twine (twison) with quest
        private String twineJson;
//        Activity from which TQManager invoked
        private Context context;
        private ContentResolver contentResolver;

        /**
         * Constructor to set quest's parameters.
         * @see TQManager#addQuest
         * */
        public AddQuest(String title, String author, HashMap<String, String> characterProperties, HashMap<String, String> characterParameters, String twineJson, Context context, ContentResolver contentResolver) {
            this.title = title;
            this.author = author;
            this.characterProperties = characterProperties;
            this.characterParameters = characterParameters;
            this.twineJson = twineJson;
            this.context = context;
            this.contentResolver = contentResolver;
        }

        public AddQuest(String title, String cloudId, String author, HashMap<String, String> characterProperties, HashMap<String, String> characterParameters, String twineJson, Context context, ContentResolver contentResolver) {
            this.title = title;
            this.cloudId = cloudId;
            this.author = author;
            this.characterProperties = characterProperties;
            this.characterParameters = characterParameters;
            this.twineJson = twineJson;
            this.context = context;
            this.contentResolver = contentResolver;
        }

        /**
         * When thread is started, add quest with parameters specified using {@link AddQuest} class's constructor {@link AddQuest#AddQuest}
         * */
        @Override
        public void run() {
            super.run();
//            Replace bad attribute "creator-version" (you can't use '-' in fields names in Java) with "creator_version"
            String correctedJson = twineJson.replaceAll("\"creator-version\":", "\"creator_version\":");
            DBQuestsDao questDao = getAppDatabaseInstance(context).questsDao();
            questDao.insert(new DBQuest(cloudId, author, title, Tools.getGson().toJson(characterProperties), Tools.getGson().toJson(characterParameters), correctedJson));
        }
    }
    /**
     * Method to add a new quest to the local library.
     * It uses {@link TQManager}'s inner class {@link AddQuest} to set parameters of the new quest (title, author, etc.) and start a new thread to use app's database (using Room).
     * */
    public void addQuest(String title, String author, HashMap<String, String> characterProperties, HashMap<String, String> characterParameters, String twineJson, Context context, ContentResolver contentResolver) {
        AddQuest aq = new AddQuest(title, author, characterProperties, characterParameters, twineJson, context, contentResolver);
        aq.start();
    }
    public void addQuest(String title, String cloudId, String author, HashMap<String, String> characterProperties, HashMap<String, String> characterParameters, String twineJson, Context context, ContentResolver contentResolver) {
        AddQuest aq = new AddQuest(title, cloudId, author, characterProperties, characterParameters, twineJson, context, contentResolver);
        aq.start();
    }


    private static class UpdateQuest extends Thread {
        Context context;
        private DBQuest quest;

        public UpdateQuest(Context context, DBQuest quest) {
            this.context = context;
            this.quest = quest;
        }

        @Override
        public void run() {
            super.run();
            DBQuestsDao questsDao = getAppDatabaseInstance(context).questsDao();
//            questsDao.update(quest.getQuestId(), quest.getQuestCloudId(), quest.getQuestUploaderUserId(), quest.getQuestTitle(), quest.getCharacterProperties(), quest.getCharacterParameters(), quest.getQuestJson());
            questsDao.update(quest);
        }
    }
    public void updateQuest(Context context, DBQuest quest) throws InterruptedException {
        UpdateQuest uq = new UpdateQuest(context, quest);
        uq.start();
        uq.join();
    }


    private static class UpdateQuestCloudInfo extends Thread {
        Context context;
        private int localQuestId;
        private String newCloudId, newUploaderUserId;

        public UpdateQuestCloudInfo(Context context, int localQuestId) {
            this.context = context;
            this.localQuestId = localQuestId;
        }

        public UpdateQuestCloudInfo(Context context, int localQuestId, String newCloudId, String newUploaderUserId) {
            this.context = context;
            this.localQuestId = localQuestId;
            this.newCloudId = newCloudId;
            this.newUploaderUserId = newUploaderUserId;
        }

        @Override
        public void run() {
            super.run();
            DBQuestsDao questsDao = getAppDatabaseInstance(context).questsDao();
//            Log.d("LKJD", "TQM: UpdateQuestCloudInfo.run() updating local quest: localQuestId=" + localQuestId + "; newCloudId=" + newCloudId + "; newUploaderUserId=" + newUploaderUserId + "  EXECUTING");
            if (newCloudId == null && newUploaderUserId == null) {
//                Log.d("LKJD", "TQM: UpdateQuestCloudInfo.run() updating local quest: localQuestId=" + localQuestId + "; newCloudId=" + newCloudId + "; newUploaderUserId=" + newUploaderUserId + "  SETTING NULL CLOUD DATA");
                questsDao.updateCloudInfo(localQuestId);
            } else {
//                Log.d("LKJD", "TQM: UpdateQuestCloudInfo.run() updating local quest: localQuestId=" + localQuestId + "; newCloudId=" + newCloudId + "; newUploaderUserId=" + newUploaderUserId + "  SETTING NOT NULL CLOUD DATA");
                questsDao.updateCloudInfo(localQuestId, newCloudId, newUploaderUserId);
            }
        }
    }
    public void updateQuestCloudInfo(Context context, DBQuest quest) throws InterruptedException {
        UpdateQuestCloudInfo uq = new UpdateQuestCloudInfo(context, quest.getQuestId(), quest.getQuestCloudId(), quest.getQuestUploaderUserId());
        uq.start();
        uq.join();
    }
    public void updateQuestCloudInfo(Context context, int questId) throws InterruptedException {
//        Log.d("LKJD", "TQM: updateQuestCloudInfo: quest_id=" + questId + "  METHOD INVOKED");
        UpdateQuestCloudInfo uq = new UpdateQuestCloudInfo(context, questId);
        uq.start();
        uq.join();
//        Log.d("LKJD", "TQM: updateQuestCloudInfo: quest_id=" + questId + "  METHOD FINISH EXECUTING");
//        DBQuest quest = getQuestById(context, questId);
//        quest.setQuestCloudId(null);
//        quest.setQuestUploaderUserId(null);
//        updateQuest(context, quest);
    }


    /**
     * Class to get quest by id from app's database.
     * @see TQManager#getQuestById(Context, int)
     * */
    private static class GetQuestById extends Thread {
        private Context context;
        private int questId;
        private DBQuest resQuest;

        public GetQuestById(Context context, int questId) {
            this.context = context;
            this.questId = questId;
        }

        @Override
        public void run() {
            super.run();
            DBQuestsDao questDao = getAppDatabaseInstance(context).questsDao();
            resQuest = questDao.getQuestById(questId);
        }
    }
    /**
     * Returns story (quest) by quest_id from app's database.
     * @see GetQuestById
     * */
    public DBQuest getQuestById(Context context, int questId) throws InterruptedException {
        GetQuestById gqbi = new GetQuestById(context, questId);
        gqbi.start();
        gqbi.join();
        return gqbi.resQuest;
    }


    /**
     * Class to get quest by id from app's database.
     * @see TQManager#getSimpleQuestById
     * */
    private static class GetSimpleQuestById extends Thread {
        private Context context;
        private int questId;
        private DBQuest resQuest;

        public GetSimpleQuestById(Context context, int questId) {
            this.context = context;
            this.questId = questId;
        }

        @Override
        public void run() {
            super.run();
            DBQuestsDao questDao = getAppDatabaseInstance(context).questsDao();
            resQuest = questDao.getSimpleQuestById(questId);
        }
    }
    /**
     * Returns quest with specified quest_id from app's database.
     * @see GetSimpleQuestById
     * */
    public DBQuest getSimpleQuestById(Context context, int questId) throws InterruptedException {
        GetSimpleQuestById gsqbi = new GetSimpleQuestById(context, questId);
        gsqbi.start();
        gsqbi.join();
        return gsqbi.resQuest;
    }


    /**
     * Class to get an array of all quests (without json) from app's database in new thread.
     * @see TQManager#getQuestsArray
     * */
    private static class GetQuestsArray extends Thread {
        /**
         * Context is needed to use Room.
         * @see #GetQuestsArray
         * @see #run()
         * @see TQManager#getAppDatabaseInstance
         * */
        private final Context context;
        /**
         * Array to save query results.
         * @see
         * */
        private DBQuest[] resQuestsArray;

        public GetQuestsArray(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            super.run();
            DBQuestsDao questDao = getAppDatabaseInstance(context).questsDao();
            resQuestsArray = questDao.getAllQuestsArray();
        }
    }
    /**
     * Returns an array of all quests from app's database (without json). Method uses {@link GetQuestsArray} to access the database in new thread.
     * @see GetQuestsArray
     * */
    public DBQuest[] getQuestsArray(Context context) throws InterruptedException {
        GetQuestsArray gqa = new GetQuestsArray(context);
        gqa.start();
        gqa.join();
        return gqa.resQuestsArray;
    }


    /**
     * Class to delete a quest from the app's database in new thread.
     * @see #deleteQuestById
     * */
    private static class DeleteQuestById extends Thread {
        /**
         * Context is needed to use Room.
         * @see #DeleteQuestById
         * @see #run()
         * @see TQManager#getAppDatabaseInstance
         * */
        private final Context context;
        /**
         * Id of the quest to delete.
         * @see #DeleteQuestById
         * @see #run
         * */
        private int id;

        /**
         * Constructor sets {@link #context} (needed to get the AppDatabase instance) and {@link #id} of the quest to delete.
         * @see #run
         * */
        public DeleteQuestById(Context context, int id) {
            this.context = context;
            this.id = id;
        }

        @Override
        public void run() {
            super.run();
            DBQuestsDao questDao = getAppDatabaseInstance(context).questsDao();
            questDao.deleteQuestsByIds(id);
        }
    }
    /**
     * Method to delete a quest from the app's database by id.
     * It uses {@link DeleteQuestById} to set the {@link DeleteQuestById#context} and the {@link DeleteQuestById#id} of the quest to delete and to start a new thread to use the app's database (using Room).
     * @see DeleteQuestById
     * */
    public void deleteQuestById(Context context, int id) throws InterruptedException {
        DeleteQuestById dqbi = new DeleteQuestById(context, id);
        dqbi.start();
        dqbi.join();
    }


    /* GAMES TABLE */
    /**
     * Class to get all started games form the app's database in new thread (using Room).
     * @see TQManager#getGames(Context) 
     * */
    private static class GetGames extends Thread {
        /**
         * Context is needed to use Room.
         * @see GetGames#GetGames(Context)
         * @see TQManager#getAppDatabaseInstance(Context)
         * */
        private Context context;
        /**
         * List to save games from app's database and to have access to this information from {@link TQManager#getGames(Context)} method.
         * @see GetGames#run()
         * @see TQManager#getGames(Context)
         * */
        private DBGame[] gamesList;

        /**
         * Constructor to set the context.
         * @see GetGames#context
         * @see TQManager#getGames(Context)
         * */
        public GetGames(Context context) {
            this.context = context;
        }

        /**
         * When thread is started, get games from app's database and save result to {@link GetGames#gamesList} field.
         * @see TQManager#getGames(Context)
         * */
        @Override
        public void run() {
            super.run();
//        Get 'tqgame' table's DAO
            DBGamesDao gameDao = getAppDatabaseInstance(context).gamesDao();
//        Get all games from database table and save result to GetGames.gamesList
            this.gamesList = gameDao.getAllGames();
        }
    }
    /**
     * Returns an array of TQGame instances with information about started games.
     * It uses {@link TQManager}'s inner class {@link GetGames} to set the context to use Room and start a new thread to use app's database (using Room).
     * @see GetQuestById
     * */
    public DBGame[] getGames(Context context) throws InterruptedException {
        GetGames gg = new GetGames(context);
        gg.start();
        gg.join();
        return gg.gamesList == null ? null : gg.gamesList;
    }


    /** Class to add new game to the app's database in new thread (using Room).*/
    private static class AddGame extends Thread {
        private Context context;
        private DBGame game;

        public AddGame(Context context, DBGame game) {
            this.context = context;
            this.game = game;
        }

        @Override
        public void run() {
            super.run();
            DBGamesDao gamesDao = getAppDatabaseInstance(context).gamesDao();
            gamesDao.insert(game);
        }
    }
    /** Method to add a new game to the app's database in new thread.
     * It uses {@link AddGame} to set a new game and to add it to the database.
     * */
    public void addGame(Context context, DBGame game) throws InterruptedException {
        AddGame ag = new AddGame(context, game);
        ag.start();
        ag.join();
    }


    /**
     * Class to get game from database by title.
     * */
    private static class GetGameByTitle extends Thread {
        private Context context;
        private String title;
        private DBGame resGame;

        public GetGameByTitle(Context context, String title) {
            this.context = context;
            this.title = title;
        }

        @Override
        public void run() {
            super.run();
            DBGamesDao gamesDao = getAppDatabaseInstance(context).gamesDao();
            resGame = gamesDao.getGameByTitle(title);
        }
    }
    public DBGame getGameByTitle(Context context, String title) throws InterruptedException {
        GetGameByTitle ggbt = new GetGameByTitle(context, title);
        ggbt.start();
        ggbt.join();
        return ggbt.resGame;
    }

    private static class DeleteGameById extends Thread {
        private Context context;
        private int gameId;

        public DeleteGameById(Context context, int gameId) {
            this.context = context;
            this.gameId = gameId;
        }

        @Override
        public void run() {
            super.run();
            DBGamesDao gamesDao = getAppDatabaseInstance(context).gamesDao();
            gamesDao.deleteGamesByIds(gameId);
        }
    }
    public void deleteGameById(Context context, int gameId) throws InterruptedException {
        DeleteGameById dgbi = new DeleteGameById(context, gameId);
        dgbi.start();
        dgbi.join();
    }


    private static class UpdateGame extends Thread {
        private Context context;
        private DBGame game;

        public UpdateGame(Context context, DBGame game) {
            this.context = context;
            this.game = game;
        }

        @Override
        public void run() {
            super.run();
            DBGamesDao gamesDao = getAppDatabaseInstance(context).gamesDao();
            gamesDao.updateGame(game.getGameId(), game.getGameLastPassagePid(), game.getGameTimestamp(), game.getGameCharPropertiesJson());
        }
    }
    public void updateGame(Context context, DBGame game) throws InterruptedException {
        UpdateGame ug = new UpdateGame(context, game);
        ug.start();
        ug.join();
    }

    public TQStory getStoryByGameTitle(Context context, String gameTitle) throws InterruptedException, JsonProcessingException {
        DBGame game = getGameByTitle(context, gameTitle);
        DBQuest quest = getQuestById(context, game.getQuestId());
        String charProps = game.getGameCharPropertiesJson() == null ? quest.getCharacterProperties() : game.getGameCharPropertiesJson();
        TQCharacter character = new TQCharacter(charProps, quest.getCharacterParameters());
        TQQuest tqQuest = Tools.getGson().fromJson(quest.getQuestJson(), TQQuest.class);
        TQStory story;
        if (game.getGameLastPassagePid() == -1) {
            story = new TQStory(game.getGameId(), quest.getQuestTitle(), quest.getQuestUploaderUserId(), character, tqQuest);
        } else {
            story = new TQStory(game.getGameId(), quest.getQuestTitle(), quest.getQuestUploaderUserId(), character, tqQuest, game.getGameLastPassagePid());
        }
        return story;
    }
}
