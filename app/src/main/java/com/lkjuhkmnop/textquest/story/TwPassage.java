package com.lkjuhkmnop.textquest.story;

import java.util.List;

public class TwPassage {
//    Twison json attributes
    public String text;
    public TwLink[] links;
    public String name;
    public int pid;
    public TwPosition position;

    @Override
    public String toString() {
        return "story.TwPassage{" +
                "\n\ttext='" + text + '\'' +
                ",\n\t links=" + links +
                ",\n\t name='" + name + '\'' +
                ",\n\t pid=" + pid +
                ",\n\t position=" + position +
                "}\n";
    }
}
