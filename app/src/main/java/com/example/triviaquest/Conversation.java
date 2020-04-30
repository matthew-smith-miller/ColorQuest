package com.example.triviaquest;

public class Conversation {
    private int mImage;
    private int mBackgroundColor;
    private int mLayoutUponComplete;
    ConversationElement[] conversationElements;
    int elementIndex;
    Boolean isActive;

    Conversation(
            int image,
            int backgroundColor,
            int layoutUponComplete,
            ConversationElement[] conversationElements) {
        this.mImage = image;
        this.mBackgroundColor = backgroundColor;
        this.mLayoutUponComplete = layoutUponComplete;
        this.conversationElements = conversationElements;
        isActive = false;
    }

    int getImage() {
        return mImage;
    }
    int getBackgroundColor() {
        return mBackgroundColor;
    }
    int getLayoutUponComplete() {
        return mLayoutUponComplete;
    }
}
