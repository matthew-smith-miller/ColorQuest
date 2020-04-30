package com.example.triviaquest;

import androidx.annotation.Nullable;

public class ConversationElement {
    private String mType; //input or output
    private String mContent;
    private String mInputTarget;

    ConversationElement(String type, String content, @Nullable String inputTarget) {
        this.mType = type;
        this.mContent = content;
        this.mInputTarget = inputTarget;
    }

    String getType() {
        return mType;
    }

    String getContent() {
        return mContent;
    }

    public String getInputTarget() {
        return mInputTarget;
    }
}
