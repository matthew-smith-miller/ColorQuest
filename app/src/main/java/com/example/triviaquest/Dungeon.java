package com.example.triviaquest;

public class Dungeon {
    private int mImage;
    ConversationElement[] conversationElements;
    Question[] questions;

    Dungeon(
            int image,
            ConversationElement[] conversationElements,
            Question[] questions) {
        this.mImage = image;
        this.conversationElements = conversationElements;
        this.questions = questions;
    }

    int getImage() {
        return mImage;
    }
}
