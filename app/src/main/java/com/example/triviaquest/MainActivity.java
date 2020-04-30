package com.example.triviaquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public Map<String,Conversation> conversations;
    public Conversation activeConversation;
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversations = new HashMap<String,Conversation>();
        buildConversations();
        setContentView(R.layout.activity_main);
    }

    public void beginGame(View view) {
        setContentView(R.layout.conversation);
        setConversation(conversations.get("peasant"));
    }

    private void setConversation(Conversation conversation) {
        if (conversation != null) {
            activeConversation = conversation;
            activeConversation.isActive = true;
            ImageView interlocutor = (ImageView) findViewById(R.id.interlocutor_image);
            interlocutor.setImageResource(activeConversation.getImage());
            findViewById(R.id.conversation_background).setBackgroundColor(
                    activeConversation.getBackgroundColor());
            setConversationElement(0);
        }
    }

    public void advanceConversation (View view) {
        if (activeConversation.conversationElements[
                activeConversation.elementIndex].getType().equals("input")) {
            EditText conversationInput = (EditText) findViewById(
                    R.id.conversation_input);
            if (conversationInput.getText().toString().matches("")) {
                Toast toast = Toast.makeText(
                        this,"Please enter a response", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                setValueToInputTarget(
                        conversationInput.getText().toString(),
                        activeConversation.conversationElements[
                                activeConversation.elementIndex].getInputTarget());
                setConversationElement(1);
            }
        } else {
            setConversationElement(1);
        }
    }

    public void setConversationElement (int increment) {
        activeConversation.elementIndex += increment;
        if (activeConversation.elementIndex < activeConversation.conversationElements.length) {
            ConversationElement conversationElement = activeConversation.conversationElements[
                    activeConversation.elementIndex];
            if (conversationElement.getType().equals("output")) {
                TextView conversationOutput = (TextView) findViewById(R.id.conversation_output);
                conversationOutput.setVisibility(View.VISIBLE);
                conversationOutput.setText(conversationElement.getContent());
                findViewById(R.id.conversation_input).setVisibility(View.GONE);
            } else if (conversationElement.getType().equals("input")) {
                EditText conversationInput = (EditText) findViewById(
                        R.id.conversation_input);
                conversationInput.setVisibility(View.VISIBLE);
                conversationInput.setHint(conversationElement.getContent());
                findViewById(R.id.conversation_output).setVisibility(View.GONE);
            }
        } else {
            setContentView(activeConversation.getLayoutUponComplete());
        }
    }

    private void setValueToInputTarget(String content, String inputTarget) {
        switch (inputTarget) {
            case "name":
                name = content;
            //Space for future value setting
        }
    }

    private void buildConversations() {
        conversations.put("peasant", new Conversation(
            R.drawable.ice_monster,
            R.color.colorPrimary,
            R.layout.map,
            new ConversationElement[] {
                new ConversationElement("output","Hello adventurer!",null),
                new ConversationElement("output","You must help us", null),
                new ConversationElement("output","Say, what is your name?", null),
                new ConversationElement("input","Enter name", "name"),
                new ConversationElement("output","We know you are a great trivia warrior and we will be eternally grateful!", "name")} ) );
        conversations.put("ice_monster", new Conversation(
            R.drawable.ice_monster,
            R.color.colorD1Primary,
            R.layout.question,
            new ConversationElement[] {
                new ConversationElement("output","Well, it's the famous adventurer", null),
                new ConversationElement("output","You are a fool for coming here", null),
                new ConversationElement("output","Prepare to freeze", null) } ) );
    }
}
