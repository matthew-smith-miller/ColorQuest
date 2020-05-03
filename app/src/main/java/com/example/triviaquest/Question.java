package com.example.triviaquest;

import androidx.annotation.Nullable;

public class Question extends ConversationElement {
    public String options;
    private String mAnswer;
    private int mBackgroundColor;

    Question(String type,
             String content,
             String answer,
             @Nullable String options,
             int backgroundColor) {
        super(type, content, null);
        mAnswer = answer;
        this.options = options;
        mBackgroundColor = backgroundColor;
    }

    String getAnswer() {
        return mAnswer;
    }

    int getBackgroundColor() {
        return mBackgroundColor;
    }
}
