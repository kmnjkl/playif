package com.lkjuhkmnop.textquest.questmanageactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lkjuhkmnop.textquest.R;
import com.lkjuhkmnop.textquest.tools.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Objects;

public class QuestManageActivity extends AppCompatActivity implements CharPAddDialog.CharPDataAddDialogListener {
    public static final int CHAR_PARAMETER = 0;
    public static final int CHAR_PROPERTY = 1;
//    To use in intents when starting QuestManageActivity (to put extra with action type)
        public static final String ACTION_EXTRA_NAME = "action";
//    Action types for QuestManageActivity (to use as extra's in intents)
        public static final int ACTION_ADD_QUEST = 1;
    public static final int ACTION_REDACT_QUEST = 2;
    private int action;

    private EditText questTitle;
    private Button questJsonFileButton, questManageOk;
    private ImageView[] questAddCharPDataButtons;
    private RecyclerView questCharPropertiesRecView, questCharParametersRecView;
    private CharPDataAdapter[] charPDataAdapters;
//    private TextView tw;

    private static final int PICK_JSON_FILE_CODE = 1;

    private static final String ADD_CHAR_P_DATA_ITEM_DIALOG_TAG = "ADD_CHAR_P_DATA_ITEM_DIALOG_TAG";

    private String questJson;
    private LinkedHashMap<String, String> questCharacterProperties = new LinkedHashMap<>();
    private LinkedHashMap<String, String> questCharacterParameters = new LinkedHashMap<>();
    private LinkedHashMap<String, String>[] questCharPData = new LinkedHashMap[2];

    {
        questCharPData[CHAR_PROPERTY] = questCharacterProperties;
        questCharPData[CHAR_PARAMETER] = questCharacterParameters;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TextQuest);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_manage);

        charPDataAdapters = new CharPDataAdapter[2];
        questAddCharPDataButtons = new ImageView[2];

        questTitle = (EditText) findViewById(R.id.quest_title);
        questJsonFileButton = (Button) findViewById(R.id.quest_json_file_button);
        questAddCharPDataButtons[CHAR_PROPERTY] = (ImageView) findViewById(R.id.quest_add_character_property);
        questAddCharPDataButtons[CHAR_PARAMETER] = (ImageView) findViewById(R.id.quest_add_character_parameter);
        questCharPropertiesRecView = (RecyclerView) findViewById(R.id.quest_character_properties_recycler_view);
        questCharParametersRecView = (RecyclerView) findViewById(R.id.quest_character_parameters_recycler_view);
        questManageOk = (Button) findViewById(R.id.quest_manage_ok_button);
//        tw = findViewById(R.id.textView);


//        Set click listener for "ok" button (save new quest or update existing one)
        questManageOk.setOnClickListener(v -> {
            boolean ok = true;
            if (questJson == null ? true : questJson.equals("")) {
                ok = false;
                questJsonFileButton.setText(getText(R.string.quest_button_json_file_choose_warning));
                questJsonFileButton.setTextColor(getColor(R.color.warningText));
            }
            if (questTitle.getText().toString().equals("")) {
                ok = false;
                questTitle.setBackgroundColor(getColor(R.color.warning));
                questTitle.setHint(getText(R.string.quest_title_empty_warning));
                questTitle.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(questTitle, InputMethodManager.SHOW_IMPLICIT);
            }
            if (ok) {
                if (action == ACTION_ADD_QUEST) {
//                    Add new quest
//                    Author is empty if the user haven't signed in and populated with the users ID otherwise
                    String author;
                    if (Tools.authTools().getUser() == null) {
                        author = "";
                    } else {
                        author = Tools.authTools().getUser().getUid();
                    }
                    Tools.tqManager().addQuest(questTitle.getText().toString(), author, questCharacterProperties, questCharacterParameters, questJson, getApplicationContext(), getContentResolver());
                    finish();
                } else if (action == ACTION_REDACT_QUEST) {
//                    Update quest
                }
            }
        });


//        Set layout manager and adapter for character properties RecycleView
        charPDataAdapters[CHAR_PROPERTY] = new CharPDataAdapter(this, questCharacterProperties);
        questCharPropertiesRecView.setLayoutManager(new LinearLayoutManager(this));
        questCharPropertiesRecView.setAdapter(charPDataAdapters[CHAR_PROPERTY]);

//        Set layout manager and adapter for character parameters RecycleView
        charPDataAdapters[CHAR_PARAMETER] = new CharPDataAdapter(this, questCharacterParameters);
        questCharParametersRecView.setLayoutManager(new LinearLayoutManager(this));
        questCharParametersRecView.setAdapter(charPDataAdapters[CHAR_PARAMETER]);

//        Action type (add new quest / manage existing quest settings)
//        Received as extra in intent
        action = getIntent().getIntExtra(ACTION_EXTRA_NAME, ACTION_ADD_QUEST);

//        Click listener for json file button
        questJsonFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            startActivityForResult(intent, PICK_JSON_FILE_CODE);
        });

//        Click listener for add character property button
        questAddCharPDataButtons[CHAR_PROPERTY].setOnClickListener(v -> {
//          Show a dialog to get property name
            CharPAddDialog charPropDialog = new CharPAddDialog(CHAR_PROPERTY);
            charPropDialog.show(getSupportFragmentManager(), ADD_CHAR_P_DATA_ITEM_DIALOG_TAG);
        });

//        Click listener for add character parameter button
        questAddCharPDataButtons[CHAR_PARAMETER].setOnClickListener(v -> {
//          Show a dialog to get property name
            CharPAddDialog charParamDialog = new CharPAddDialog(CHAR_PARAMETER);
            charParamDialog.show(getSupportFragmentManager(), ADD_CHAR_P_DATA_ITEM_DIALOG_TAG);
        });
    }

//    User choose json file
//    Read file and save json in questJson field
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_JSON_FILE_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            StringBuilder stringBuilder = new StringBuilder();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException ignored) {}
            questJson = stringBuilder.toString();
//            tw.setText(questJson);
        }
    }

    public void notifyCharPDataRecViewAdapter(int charPDataType) {
        charPDataAdapters[charPDataType].notifyDataSetChanged();
//        questCharPropertiesRecView.
//        questCharPropertiesRecView.requestLayout();
//        questCharPropertiesRecView.
//        questCharPropertiesRecView.setAdapter(charPropertiesAdapter);
    }

//    Add character property with specified name
    @Override
    public void onCharPDataAddDialogPositiveClick(int charPDataType, String charPropAddName) {
        if (questCharacterProperties.containsKey(charPropAddName) || questCharacterParameters.containsKey(charPropAddName)) {
            Toast.makeText(this, getString(R.string.char_p_data_add_name_used), Toast.LENGTH_LONG).show();
        } else {
            questCharPData[charPDataType].put(charPropAddName, "");
            notifyCharPDataRecViewAdapter(charPDataType);
        }
        questAddCharPDataButtons[charPDataType].requestFocus();
//        RecyclerView.ViewHolder addedItemViewHolder = questCharPropertiesRecView.findViewHolderForAdapterPosition(0);
//        Log.d("dialog", "end");
//        addedItemViewHolder.requestValueFocus();
    }

//    User cancel adding a new char. prop.
    @Override
    public void onCharPDataAddDialogNegativeClick() {
        Toast.makeText(this, getText(R.string.char_p_data_add_cancel_msg), Toast.LENGTH_SHORT).show();

    }
}