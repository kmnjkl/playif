package com.lkjuhkmnop.textquest.story;

import com.lkjuhkmnop.textquest.tools.MathExpressionEvaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TQStory {
    private int gameId;
    private String title;
    private String author;
    private TQCharacter character;
    private final TQQuest tqquest;
//    current passage which should be processed in current stage of the story
    private TwPassage currentPassage;
    private String currentPassageCleanText;
    private final MathExpressionEvaluator mathExpressionEvaluator = new MathExpressionEvaluator();

    public TQStory(int gameId, String title, String author, TQCharacter character, TQQuest tqquest) {
        this.gameId = gameId;
        this.title = title;
        this.author = author;
        this.character = character;
        this.tqquest = tqquest;
//        Set the start passage as current passage
//        we can find the start passage by it's pid specified in json attribute in the TQ json file
        this.currentPassage = this.getPassageByPid(this.tqquest.startnode);
//        Initialize mathExpressionEvaluator
//        set start character properties as variables
        for (Map.Entry<String, String> prop: character.properties.entrySet()) {
            try {
                mathExpressionEvaluator.setVariable(prop.getKey(), Double.parseDouble(prop.getValue()));
            } catch (NullPointerException | NumberFormatException ignored) {}
        }
    }

    public TQStory(int gameId, String title, String author, TQCharacter character, TQQuest tqquest, int startPid) {
        this.gameId = gameId;
        this.title = title;
        this.author = author;
        this.character = character;
        this.tqquest = tqquest;
        this.currentPassage = this.getPassageByPid(startPid);
//        Initialize mathExpressionEvaluator
//        set start character properties as variables
        for (Map.Entry<String, String> prop: character.properties.entrySet()) {
            try {
                mathExpressionEvaluator.setVariable(prop.getKey(), Double.parseDouble(prop.getValue()));
            } catch (NullPointerException | NumberFormatException ignored) {}
        }
    }

//    SPECIAL ACTIONS
    public void restart() {
        currentPassage = getPassageByPid(tqquest.startnode);
    }

//    GET PASSAGE
//    Method finds passage with specified pid
    public TwPassage getPassageByPid(int pid) {
        for (TwPassage passage : this.tqquest.passages) {
            if (passage.pid == pid) {
                return passage;
            }
        }
        return null;
    }

//    Method finds passage with specified name
    public TwPassage getPassageByName(String name) {
        for (TwPassage passage : this.tqquest.passages) {
            if (passage.name.equals(name)) {
                return passage;
            }
        }
        return null;
    }

//    PASSAGE PROCESSING
    public int getCurrentPassagePid() {
        return this.currentPassage.pid;
    }

    public String getCurrentPassageName() {
        return this.currentPassage.name;
    }

//    Method returns passage's text without character parameters, links and replaces TQ keywords with corresponding values
//    method change's character parameters
    public String processCurrentPassage() {
//        Passage's text
        String text = currentPassage.text;

//        Change character parameters
        String charParamsString = text.substring(0, text.indexOf("\n") + 1);
        charParamsString = charParamsString.trim();
        String[] charParamsSplit = charParamsString.split(" ");
        int cpi = 0;
        for (Map.Entry<String, String> param : this.character.parameters.entrySet()) {
            param.setValue(charParamsSplit[cpi].trim());
            cpi++;
        }

//        Delete first line (with character parameters)
        text = text.substring(text.indexOf("\n") + 1);

        StringBuffer sbtext = new StringBuffer(text);
//        System.out.println("sbtext: " + sbtext.charAt(sbtext.length()-1) + "\n" + sbtext.charAt(sbtext.length()) + "\n");
//        Firstly replace TQ keywords because they can be used in links
//        Replace TQ keywords
//        1. Perform actions with character properties specified in currentPassage text
        Pattern propActPattern = Pattern.compile("@@.+?@@");
        Matcher propActMatcher = propActPattern.matcher(sbtext);
        String propActExpression;
        String propName;
        Double propNewVal;
        int findStart = 0;
        while (propActMatcher.find(findStart)) {
            findStart = propActMatcher.start();
            propActExpression = sbtext.substring(propActMatcher.start() + 2, propActMatcher.end() - 2);
            sbtext.delete(propActMatcher.start(), propActMatcher.end() + 1);
            propName = propActExpression.substring(0, propActExpression.indexOf('='));
            if (character.properties.containsKey(propName)) {
                propActExpression = propActExpression.substring(propActExpression.indexOf('=') + 1);
                propNewVal = mathExpressionEvaluator.evaluate(propActExpression);
//            Save the new value of the character property
                try {
//                If the value must be integer (we can use parseInt without exception)
                    Integer.parseInt(character.properties.get(propName));
                    character.properties.replace(propName, Integer.toString(propNewVal.intValue()));
                } catch (NumberFormatException e) {
//                If value must be double
                    character.properties.replace(propName, propNewVal.toString());
                }
                mathExpressionEvaluator.setVariable(propName, propNewVal);
            }
        }
        text = sbtext.toString();
//        2. Replace character properties and parameters
        for (Map.Entry<String, String> property : this.character.properties.entrySet()) {
            text = text.replaceAll("@" + property.getKey() + "@", property.getValue());
        }
        for (Map.Entry<String, String> parameter : this.character.parameters.entrySet()) {
            text = text.replaceAll("@" + parameter.getKey() + "@", parameter.getValue());
        }

        sbtext = new StringBuffer(text);
//        Delete links
        int i_start = sbtext.indexOf("[[");
        int i_end = sbtext.indexOf("]]");
        while (i_start >= 0 && i_end >= 0) {
//            delete link
            sbtext.delete(i_start, i_end + 2);
//            find next link
            i_start = sbtext.indexOf("[[");
            i_end = sbtext.indexOf("]]");
        }

//        Delete all empty lines and return clean text
        this.currentPassageCleanText = sbtext.toString().replaceAll("(?m)^[ \t]*\r?\n", "");
        return this.currentPassageCleanText;
    }

    public TwLink[] getCurrentPassageLinks() {
        return this.currentPassage.links;
    }

    public String getCurrentPassageCleanText() {
        return currentPassageCleanText;
    }

    //    Method receives a link's number and changes currentPassage
    public void goByLinkNumber(int linkNumber) {
        this.currentPassage = this.getPassageByPid(this.currentPassage.links[linkNumber-1].pid);
    }

//    CHARACTER
    public HashMap<String, String> getCurrentCharacterProperties() {
    return character.properties;
}
    public HashMap<String, String> getCurrentCharacterParameters() {
        return character.parameters;
    }

//    STORY STATE
//    Method checks if this passage is last (the game ends on it)
    public boolean isEnd() {
        return currentPassage.links == null || currentPassage.links.length == 0 || currentPassage.name.equals("end") || currentPassage.name.equals("last");
    }

//    INFORMATION
    public int getGameId() {
        return gameId;
    }
}
