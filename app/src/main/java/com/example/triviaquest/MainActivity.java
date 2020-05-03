package com.example.triviaquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String ACTIVE_DUNGEON_INDEX_KEY = "activeDungeonIndex";
    private static final String CONVERSATION_INDEX_KEY = "conversationIndex";
    private static final String QUESTION_INDEX_KEY = "questionIndex";
    private static final String NAME_KEY = "name";
    private static final String SCORE_KEY = "score";
    public String name;
    private static final String QUESTIONS_ATTEMPTED_KEY = "questionsAttempted";
    private static final String COLORS_ACQUIRED_KEY = "colorsAcquired";
    private static final String CURRENT_SCREEN = "currentScreen";
    public Dungeon[] dungeons;
    public Dungeon activeDungeon;
    public int activeDungeonIndex;
    public int conversationIndex;
    public int questionIndex;
    public int score;
    public int questionsAttempted;
    public int[] colorsAcquired;
    public String currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dungeons = buildDungeons();
        activeDungeon = dungeons[0];
        colorsAcquired = buildColorsAcquired();
        currentScreen = "main";

        if (savedInstanceState != null) {
            activeDungeonIndex = savedInstanceState.getInt(ACTIVE_DUNGEON_INDEX_KEY);
            conversationIndex = savedInstanceState.getInt(CONVERSATION_INDEX_KEY);
            questionIndex = savedInstanceState.getInt(QUESTION_INDEX_KEY);
            name = savedInstanceState.getString(NAME_KEY);
            score = savedInstanceState.getInt(SCORE_KEY);
            questionsAttempted = savedInstanceState.getInt(QUESTIONS_ATTEMPTED_KEY);
            colorsAcquired = savedInstanceState.getIntArray(COLORS_ACQUIRED_KEY);
            switchScreen(savedInstanceState.getString(CURRENT_SCREEN));
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_SCREEN, currentScreen);
        outState.putInt(ACTIVE_DUNGEON_INDEX_KEY, activeDungeonIndex);
        outState.putInt(CONVERSATION_INDEX_KEY, conversationIndex);
        outState.putInt(QUESTION_INDEX_KEY, questionIndex);
        outState.putString(NAME_KEY, name);
        outState.putInt(SCORE_KEY, score);
        outState.putInt(QUESTIONS_ATTEMPTED_KEY, questionsAttempted);
        outState.putIntArray(COLORS_ACQUIRED_KEY, colorsAcquired);
    }

    /**
     * Called by buttons, initiates conversation
     *
     * @param view is the button
     */
    public void enterDungeon(View view) {
        switchScreen("conversation");
    }

    /**
     * Sets content view as conversation layout and customizes image
     * Then sets up the conversation element
     * conversationIndex is NOT set to 0 at start so progress is maintained on orientation change
     */
    private void setUpConversationLayout() {
        if (activeDungeonIndex < dungeons.length) {
            activeDungeon = dungeons[activeDungeonIndex];
        }
        if (activeDungeon != null) {
            setContentView(R.layout.conversation);
            ((ImageView) findViewById(R.id.interlocutor_image)).setImageResource(
                    activeDungeon.getImage());
            if (conversationIndex < activeDungeon.conversationElements.length) {
                setConversationElement();
            }
        }
    }

    /**
     * This method configures the conversation screen according to the needs
     * of the conversation element - i.e., input or output
     */
    public void setConversationElement() {
        if (conversationIndex < activeDungeon.conversationElements.length) {
            ConversationElement conversationElement =
                    activeDungeon.conversationElements[conversationIndex];
            if (conversationElement.getType().equals("output")) {
                TextView conversationOutput = (TextView) findViewById(R.id.conversation_output);
                conversationOutput.setVisibility(View.VISIBLE);
                conversationOutput.setText(checkForMerge(conversationElement.getContent()));
                findViewById(R.id.conversation_input).setVisibility(View.GONE);
            } else if (conversationElement.getType().equals("input")) {
                EditText conversationInput = (EditText) findViewById(
                        R.id.conversation_input);
                conversationInput.setVisibility(View.VISIBLE);
                conversationInput.setText("");
                conversationInput.setHint(conversationElement.getContent());
                findViewById(R.id.conversation_output).setVisibility(View.GONE);
            }
        }
    }

    /**
     * Checks that an input has content and then applies it to target field
     * Increments conversationIndex counter then checks if additional elements exist
     * If not, shifts to questions if they exist, or returns to map
     *
     * @param view comes from the layout on button press
     */
    public void advanceConversation(View view) {
        hideKeyboard(view);
        boolean passedValidation = false;
        if (activeDungeon.conversationElements[conversationIndex].getType().equals("input")) {
            EditText conversationInput = (EditText) findViewById(
                    R.id.conversation_input);
            if (conversationInput.getText().toString().matches("")) {
                Toast toast = Toast.makeText(
                        this, "Please enter a response", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                passedValidation = true;
                setValueToInputTarget(
                        conversationInput.getText().toString(),
                        activeDungeon.conversationElements[conversationIndex].getInputTarget());
            }
        } else {
            passedValidation = true;
        }
        if (passedValidation) {
            conversationIndex++;
            if (conversationIndex < activeDungeon.conversationElements.length) {
                setConversationElement();
            } else if (questionIndex < activeDungeon.questions.length) {
                switchScreen("question");
            } else {
                activeDungeonIndex++;
                switchScreen("map");
            }
        }
    }

    /**
     * Sets value to variable from an input-type conversation element
     *
     * @param content     is the content entered by the user
     * @param inputTarget is the target variable
     */
    private void setValueToInputTarget(String content, String inputTarget) {
        switch (inputTarget) {
            case "name":
                name = content;
                //Space for future value setting
        }
    }

    /**
     * Replaces content in conversation elements at pre-defined spots
     * This method exists because dialogue is defined at the beginning
     *
     * @param content is the original dialogue string
     * @return is the dialogue string with values merged in
     */
    private String checkForMerge(String content) {
        if (content.contains("$$$name")) {
            return content.replace("$$$name", name);
        } else {
            return content;
        }
    }

    /**
     * Sets content view to question and customizes image
     * Then sets up individual question
     * conversationIndex set to 0 since a conversation has just ended
     * questionIndex is NOT set to 0 at start so progress is maintained on orientation change
     */
    private void setUpQuestionLayout() {
        conversationIndex = 0;
        if (activeDungeonIndex < dungeons.length) {
            activeDungeon = dungeons[activeDungeonIndex];
        }
        if (activeDungeon != null) {
            setContentView(R.layout.question);
            ((ImageView) findViewById(R.id.interlocutor_image_small)).setImageResource(
                    activeDungeon.getImage());
            if (questionIndex < activeDungeon.questions.length) {
                setQuestion();
            }
        }
    }

    /**
     * This method configures the question screen according to the needs
     * of the question: text, radio, or checkbox
     * This also sets the background color (e.g., red for the red question)
     */
    public void setQuestion() {
        if (questionIndex < activeDungeon.questions.length) {
            Question question = activeDungeon.questions[questionIndex];
            findViewById(R.id.question_background).setBackgroundResource(
                    question.getBackgroundColor());
            ((TextView) findViewById(R.id.speech_bubble)).setText(question.getContent());
            EditText answerText = (EditText) findViewById(R.id.answer_text);
            RadioGroup answerRadioGroup = (RadioGroup) findViewById(R.id.answer_radio_group);
            LinearLayout answerCheckboxGroup = (LinearLayout) findViewById(R.id.answer_checkbox_group);
            System.out.println(answerRadioGroup);
            switch (question.getType()) {
                case "text":
                    answerText.setVisibility(View.VISIBLE);
                    answerText.setText("");
                    answerRadioGroup.setVisibility(View.GONE);
                    answerCheckboxGroup.setVisibility(View.GONE);
                    break;
                case "radio":
                    answerText.setVisibility(View.GONE);
                    answerRadioGroup.setVisibility(View.VISIBLE);
                    answerCheckboxGroup.setVisibility(View.GONE);
                    if (question.options != null) {
                        String[] splitOptions = question.options.split(",");
                        for (int i = 0; i < splitOptions.length; i++) {
                            RadioButton radioButton = (RadioButton) findViewById(
                                    getResources().getIdentifier(
                                            "answer_radio_" + i,
                                            "id",
                                            getPackageName()));
                            radioButton.setText(splitOptions[i]);
                        }
                    }
                    break;
                case "checkbox":
                    answerText.setVisibility(View.GONE);
                    answerRadioGroup.setVisibility(View.GONE);
                    answerCheckboxGroup.setVisibility(View.VISIBLE);
                    if (question.options != null) {
                        String[] splitOptions = question.options.split(",");
                        for (int i = 0; i < splitOptions.length; i++) {
                            CheckBox checkBox = (CheckBox) findViewById(
                                    getResources().getIdentifier(
                                            "answer_checkbox_" + i,
                                            "id",
                                            getPackageName()));
                            checkBox.setText(splitOptions[i]);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Checks that an answer has been entered, then checks if correct
     * Increments questionIndex counter then checks if additional questions exist
     * If not, returns to map
     *
     * @param view comes from the layout on button press
     */
    public void advanceQuestions(View view) {
        hideKeyboard(view);
        boolean passedValidation = false;
        EditText answerText = (EditText) findViewById(R.id.answer_text);
        RadioGroup answerRadioGroup = (RadioGroup) findViewById(R.id.answer_radio_group);
        LinearLayout answerCheckboxGroup = (LinearLayout) findViewById(R.id.answer_checkbox_group);
        Question question = activeDungeon.questions[questionIndex];
        switch (question.getType()) {
            case "text":
                if (answerText.getText().toString().matches("")) {
                    Toast toast = Toast.makeText(
                            this, "Please enter a response", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    passedValidation = true;
                    checkSingleAnswer(answerText.getText().toString());
                }
                break;
            case "radio":
                if (findViewById(answerRadioGroup.getCheckedRadioButtonId()) != null) {
                    passedValidation = true;
                    RadioButton radioButton = (RadioButton) findViewById(
                            answerRadioGroup.getCheckedRadioButtonId());
                    radioButton.setChecked(false);
                    checkSingleAnswer(radioButton.getText().toString());
                    break;
                }
            case "checkbox":
                Set<String> userResponses = new HashSet<String>();
                for (int i = 0; i < 4; i++) {
                    CheckBox checkBox = (CheckBox) findViewById(
                            getResources().getIdentifier(
                                    "answer_checkbox_" + i,
                                    "id",
                                    getPackageName()));
                    if (checkBox.isChecked()) {
                        userResponses.add(checkBox.getText().toString());
                        checkBox.setChecked(false);
                    }
                }
                if (userResponses.size() > 0) {
                    passedValidation = true;
                    int countCorrect = 0;
                    String[] answerComponents = question.getAnswer().split(",");
                    for (String answerComponent : answerComponents) {
                        if (userResponses.contains(answerComponent)) {
                            countCorrect++;
                        }
                    }
                    if (countCorrect == answerComponents.length) {
                        registerResponse(true);
                    } else {
                        registerResponse(false);
                    }
                    break;
                }
        }
        if (passedValidation) {
            questionIndex++;
            if (questionIndex < activeDungeon.questions.length) {
                setQuestion();
            } else {
                activeDungeonIndex++;
                switchScreen("map");
            }
        }
    }

    /**
     * Checks response on text or radio where there is a single correct answer
     * Answer checker for checkboxes is embedded in advanceQuestions method
     *
     * @param userResponse is user response string
     */
    private void checkSingleAnswer(String userResponse) {
        Question question = activeDungeon.questions[questionIndex];
        if (userResponse.toLowerCase().equals(question.getAnswer().toLowerCase())) {
            registerResponse(true);
        } else {
            registerResponse(false);
        }
    }

    /**
     * Generates a toast to indicate correct/incorrect
     * Also increments questionsAttempted and, if correct, score
     *
     * @param isCorrect is passed from answer check
     */
    private void registerResponse(boolean isCorrect) {
        String message;
        if (isCorrect) {
            message = "Correct!";
            score++;
            colorsAcquired[questionsAttempted] =
                    activeDungeon.questions[questionIndex].getBackgroundColor();
        } else {
            message = "Derp, that is incorrect";
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        questionsAttempted++;
    }

    /**
     * Sets content view to map
     * Sets both conversationIndex and questionIndex to 0 since one or both have ended
     * Then updates roads and dungeon buttons to allow forward progress
     */
    private void returnToMapLayout() {
        activeDungeon = null;
        conversationIndex = 0;
        questionIndex = 0;
        setContentView(R.layout.map_scrollable);

        //Future: should scroll to activeDungeon
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        if (questionsAttempted == 9) {
            switchScreen("final_result");
        } else {
            Toast.makeText(this,
                    "You have recovered " + score + " out of " + questionsAttempted + " colors so far",
                    Toast.LENGTH_SHORT).show();

            for (int i = 1; i <= activeDungeonIndex; i++) {
                View road = findViewById(
                        getResources().getIdentifier(
                                "img_road_" + i,
                                "id",
                                getPackageName()));
                if (road != null) {
                    road.setBackgroundResource(getResources().getIdentifier(
                            "img_road_" + i + "_normal",
                            "drawable",
                            getPackageName()));
                }
                Button completedDungeonButton = (Button) findViewById(
                        getResources().getIdentifier(
                                "btn_dungeon_" + (i - 1),
                                "id",
                                getPackageName()));
                if (completedDungeonButton != null) {
                    completedDungeonButton.setEnabled(false);
                    completedDungeonButton.setBackgroundResource(R.drawable.btn_completed);
                }
            }
            Button activeDungeonButton = (Button) findViewById(
                    getResources().getIdentifier(
                            "btn_dungeon_" + activeDungeonIndex,
                            "id",
                            getPackageName()));
            if (activeDungeonButton != null) {
                activeDungeonButton.setEnabled(true);

            }
        }
    }

    /**
     * Sets contentView to final result and displays colors on badges
     * for all the colors that the user recovered
     */
    private void displayFinalResult() {
        setContentView(R.layout.final_result);
        for (int i = 0; i < colorsAcquired.length; i++) {
            View view = findViewById(
                    getResources().getIdentifier(
                            "color_" + i,
                            "id",
                            getPackageName()));
            view.setBackgroundResource(colorsAcquired[i]);
        }
        ((TextView) findViewById(R.id.final_result_message)).setText(
                "Congratulations! You recovered " +
                        score +
                        " out of " +
                        questionsAttempted +
                        " colors"
        );
    }

    /**
     * For hiding keyboard
     *
     * @param view is from the button clicked
     */
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(
                Context.INPUT_METHOD_SERVICE
        );
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Handles switch screen requests and stores value in currentScreen variable
     * to assist with restoration after orientation change
     * I suspect this method wouldn't need to exist if I used multiple activities
     *
     * @param targetScreen refers to the target screen
     */
    private void switchScreen(String targetScreen) {
        switch (targetScreen) {
            case "map":
                returnToMapLayout();
                break;
            case "conversation":
                setUpConversationLayout();
                break;
            case "question":
                setUpQuestionLayout();
                break;
            case "final_result":
                displayFinalResult();
                break;
            default:
                setContentView(R.layout.activity_main);
        }
        currentScreen = targetScreen;
    }

    /**
     * Close the app
     *
     * @param view is from button clicked
     */
    public void finishActivity(View view) {
        this.finish();
    }

    /**
     * Builds out instances of the Dungeon object
     * A Dungeon includes conversation elements and/or questions, so it is used for the intro
     * conversation with the "You must help us" spirit (who has no questions) as well as for
     * the evil spirits, who have both conversation elements and questions
     * Question extends ConversationElement, and includes a color id which is used
     * both for background setting on question layout and for tracking which colors
     * the user has recovered
     *
     * @return is the list of Dungeons
     */
    private Dungeon[] buildDungeons() {
        return new Dungeon[]{
                new Dungeon(
                        R.drawable.intro_speaker,
                        new ConversationElement[]{
                                new ConversationElement(getString(R.string.output),
                                        getString(R.string.dungeon_0_conv_0),
                                        null),
                                new ConversationElement(getString(R.string.output),
                                        getString(R.string.dungeon_0_conv_1),
                                        null),
                                new ConversationElement(getString(R.string.output),
                                        getString(R.string.dungeon_0_conv_2),
                                        null),
                                new ConversationElement(getString(R.string.input),
                                        getString(R.string.dungeon_0_conv_3),
                                        getString(R.string.name)),
                                new ConversationElement(getString(R.string.output),
                                        getString(R.string.dungeon_0_conv_4),
                                        null)},
                        new Question[]{}),
                new Dungeon(
                        R.drawable.img_interlocutor_roy,
                        new ConversationElement[]{
                                new ConversationElement(getString(
                                        R.string.output),
                                        getString(R.string.dungeon_1_conv_0),
                                        null),
                                new ConversationElement(getString(
                                        R.string.output),
                                        getString(R.string.dungeon_1_conv_1),
                                        null),
                                new ConversationElement(getString(
                                        R.string.output),
                                        getString(R.string.dungeon_1_conv_2),
                                        null)},
                        new Question[]{
                                new Question(getString(
                                        R.string.radio),
                                        getString(R.string.dungeon_1_ques_0),
                                        getString(R.string.dungeon_1_ques_0_a),
                                        getString(R.string.dungeon_1_ques_0_o),
                                        R.color.colorRed),
                                new Question(
                                        getString(R.string.text),
                                        getString(R.string.dungeon_1_ques_1),
                                        getString(R.string.dungeon_1_ques_1_a),
                                        null,
                                        R.color.colorOrange),
                                new Question(
                                        getString(R.string.radio),
                                        getString(R.string.dungeon_1_ques_2),
                                        getString(R.string.dungeon_1_ques_2_a),
                                        getString(R.string.dungeon_1_ques_2_o),
                                        R.color.colorYellow)}),
                new Dungeon(
                        R.drawable.img_interlocutor_gbiv,
                        new ConversationElement[]{
                                new ConversationElement(
                                        getString(R.string.output),
                                        getString(R.string.dungeon_2_conv_0),
                                        null),
                                new ConversationElement(
                                        getString(R.string.output),
                                        getString(R.string.dungeon_2_conv_1),
                                        null),
                                new ConversationElement(
                                        getString(R.string.output),
                                        getString(R.string.dungeon_2_conv_2),
                                        null)},
                        new Question[]{
                                new Question(
                                        getString(R.string.text),
                                        getString(R.string.dungeon_2_ques_0),
                                        getString(R.string.dungeon_2_ques_0_a),
                                        null,
                                        R.color.colorGreen),
                                new Question(
                                        getString(R.string.text),
                                        getString(R.string.dungeon_2_ques_1),
                                        getString(R.string.dungeon_2_ques_1_a),
                                        null,
                                        R.color.colorBlue),
                                new Question(
                                        getString(R.string.checkbox),
                                        getString(R.string.dungeon_2_ques_2),
                                        getString(R.string.dungeon_2_ques_2_a),
                                        getString(R.string.dungeon_2_ques_2_o),
                                        R.color.colorIndigo),
                                new Question(
                                        getString(R.string.radio),
                                        getString(R.string.dungeon_2_ques_3),
                                        getString(R.string.dungeon_2_ques_3_a),
                                        getString(R.string.dungeon_2_ques_3_o),
                                        R.color.colorViolet)}),
                new Dungeon(
                        R.drawable.img_interlocutor_bw,
                        new ConversationElement[]{
                                new ConversationElement(
                                        getString(R.string.output),
                                        getString(R.string.dungeon_3_conv_0),
                                        null),
                                new ConversationElement(
                                        getString(R.string.output),
                                        getString(R.string.dungeon_3_conv_1),
                                        null),
                                new ConversationElement(
                                        getString(R.string.output),
                                        getString(R.string.dungeon_3_conv_2),
                                        null)},
                        new Question[]{
                                new Question(
                                        getString(R.string.radio),
                                        getString(R.string.dungeon_3_ques_0),
                                        getString(R.string.dungeon_3_ques_0_a),
                                        getString(R.string.dungeon_3_ques_0_o),
                                        R.color.colorWhite),
                                new Question(
                                        getString(R.string.text),
                                        getString(R.string.dungeon_3_ques_1),
                                        getString(R.string.dungeon_3_ques_1_a),
                                        null,
                                        R.color.colorBlack)})};
    }

    /**
     * Builds array to store colors recovered by user
     * All values set to default color so that on final result screen they will be gray
     * if user didn't recover that color
     *
     * @return is the array
     */
    private int[] buildColorsAcquired() {
        return new int[]{
                R.color.colorRoadDisabled,
                R.color.colorRoadDisabled,
                R.color.colorRoadDisabled,
                R.color.colorRoadDisabled,
                R.color.colorRoadDisabled,
                R.color.colorRoadDisabled,
                R.color.colorRoadDisabled,
                R.color.colorRoadDisabled,
                R.color.colorRoadDisabled
        };
    }
}
