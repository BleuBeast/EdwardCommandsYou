package com.example.edwardcommandsyou;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivityFragment extends Fragment {
    private SecureRandom random; // for randomizing button flashes
    private Handler handler; // for delaying flashes of buttons
    private TextView yourTurnTextView; // for displaying if it is your turn or Edward's
    private TextView levelTextView; // for displaying level number
    private LinearLayout[] buttonLinearLayouts; // array of all the rows of buttons in the game
   // private int stepsTakenInObeying; // how far the player has gotten in the command sequence
    private int roundProgress; // how far the player is into the round of sequences
    private ArrayList<Button> commandSequence; // sequence of buttons for a sequence of commands
    private boolean inReverse;
    private int roundLength;
    private int commandLength=1;
    private int steps = 0;
    public View view;
    private boolean isYourTurn = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_main, container, false);

        //yourTurnTextView = (TextView) view.findViewById(yourTurnTextView.getId());

        random = new SecureRandom();
        handler = new Handler();
        //stepsTakenInObeying = 0;
        roundProgress = 0;
        commandSequence = new ArrayList<>();

        levelTextView = (TextView) view.findViewById(R.id.levelTextView);
        updateLevelText();

        yourTurnTextView = (TextView) view.findViewById(R.id.yourTurnTextView);
        //yourTurnTextView.setText(R.string.your_turn);

        buttonLinearLayouts = new LinearLayout[3];
        buttonLinearLayouts[0] = (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        buttonLinearLayouts[1] = (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        buttonLinearLayouts[2] = (LinearLayout) view.findViewById(R.id.row3LinearLayout);

        for (LinearLayout buttonLinearLayout : buttonLinearLayouts) {
            for (int column = 0; column < buttonLinearLayout.getChildCount(); column++) {
                Button button = (Button) buttonLinearLayout.getChildAt(column);
                button.setOnClickListener(gameButtonsListener);
            }
        }
        Button button1 = (Button) view.findViewById(R.id.button1);
        Button button2 = (Button) view.findViewById(R.id.button2);
        Button button3 = (Button) view.findViewById(R.id.button3);
        Button button4 = (Button) view.findViewById(R.id.button4);
        Button button5 = (Button) view.findViewById(R.id.button5);
        Button button6 = (Button) view.findViewById(R.id.button6);
        button1.setBackgroundResource(R.color.buttonColor1);
        button2.setBackgroundResource(R.color.buttonColor2);
        button3.setBackgroundResource(R.color.buttonColor3);
        button4.setBackgroundResource(R.color.buttonColor4);
        button5.setBackgroundResource(R.color.buttonColor5);
        button6.setBackgroundResource(R.color.buttonColor6);

        createCommandSequence();
        animateCommand();

        changeTurn();
        System.out.println(yourTurnTextView.getText());

        return view;
    }

    private void changeTurn () //Used to tell the user on screen whose turn it is
    {
        System.out.println("I have made it to change turn");
        if (!isYourTurn) yourTurnTextView.setText(R.string.your_turn);
        if (isYourTurn) yourTurnTextView.setText(R.string.edwards_turn);
        isYourTurn = !isYourTurn;
    }

    private void updateLevelText()
    {
        String message = "Current Score: " + (commandLength-1);
        levelTextView.setText(message);
    }

    public void updateInReverse(SharedPreferences sharedPreferences) {
        String yes_no = sharedPreferences.getString("in_reverse", null);
        switch (yes_no) {
            case "yes":
                inReverse = true;
                break;
            case "no":
                inReverse = false;
                break;
        }
    }

    public void updateRoundLength(SharedPreferences sharedPreferences) {
        String length = sharedPreferences.getString("number_of_rounds", null);
        roundLength = Integer.parseInt(length);
    }

    public void updateCommandLength(SharedPreferences sharedPreferences) {
        String length = sharedPreferences.getString("command_lengths", null);
        commandLength = Integer.parseInt(length);
    }

    // responds to buttons being pressed
    private OnClickListener gameButtonsListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            System.out.println("Button clicked");
            System.out.println(commandLength);
            Button userInput = ((Button) view);
            if (userInput.equals(commandSequence.get(steps)))
            {
                System.out.println("Right answer");
                steps++;
                if (commandLength==steps)
                {
                    System.out.println("Made it through the round");
                    commandLength++;
                    steps = 0;
                    updateLevelText();
                    createCommandSequence();
                    animateCommand();
                }
            }
            else
            {
                enableButtons(false);
                // Display some message saying incorrect
                // print (you made it x rounds!)
//                DialogFragment gameResults = new GameResults();
//                use FragmentManager to display the DialogFragment
//                gameResults.setCancelable(false);
//                gameResults.show(getFragmentManager(), "quiz results");

                String message = getString(R.string.end_message) + " " + commandLength + " " + getString(R.string.new_game);
                commandSequence.clear();
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                commandLength = 1;
                steps = 0;
                createCommandSequence();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateLevelText();
                        animateCommand();
                    }
                }, 3500);
                System.out.println("Wrong answer");

                // Dr. Porter fix
                //(MainActivityFragment) getFragmentManager().findFragmentById(R.id.quizFragment)).resetQuiz();

            }

/*            // this section should check if correct button has been pressed
            int index = !inReverse ? stepsTakenInObeying : (commandLength - 1 - stepsTakenInObeying);
            boolean isCorrect = commandSequence.get(index).equals(button);

            // the button has been pressed so another step has been taken in obeying the sequence
            ++stepsTakenInObeying;
            if (isCorrect) {
                // if a correct button was pressed and we are at the end of a command
                if (stepsTakenInObeying == commandLength) {
                    // if a correct button was pressed and we are at the end of a round
                    if (roundProgress == roundLength) {
                        // reset round counter
                        roundProgress = 0;
                    }
                    // reset step in command counter
                    stepsTakenInObeying = 0;
                }
            }
            else {

            }*/
        }
    };

    /*public static class GameResults extends DialogFragment
    {

        // create an AlertDialog and return it
        @Override
        public Dialog onCreateDialog(Bundle bundle)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String endMessage = getString(R.string.end_message) + commandLength;
            builder.setMessage(endMessage);

            // "Reset Quiz" Button
            builder.setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            commandLength = 1;
                            steps = 0;
                            createCommandSequence();
                            animateCommand();
                            //(MainActivityFragment) getFragmentManager().findFragmentById(R.id.gameFragment)).resetGame();
                        }
                    }
            );

            return builder.create(); // return the AlertDialog
        }
    }
*/

    /*private void animateAlphaBlink(Button button) {
        Animation animation = new AlphaAnimation(1,0);
        //animation.setRepeatCount(5);
        animation.setDuration(500);
        button.startAnimation(animation);
    }*/

    // two runnable's run at same time so first delay must be less than second
    private void animateColorBlink(final Button button) {
        final Drawable background = button.getBackground();
        // delay then light up button by changing color
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setBackgroundResource(R.color.lightup);
            }
        }, 500);
        // delay then change to back to normal color
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setBackground(background);
            }
        }, 1000);
    }

    private void enableButtons(boolean enable) {
        for (LinearLayout buttonLinearLayout : buttonLinearLayouts) {
            for (int column = 0; column < buttonLinearLayout.getChildCount(); column++) {
                Button button = (Button) buttonLinearLayout.getChildAt(column);
                button.setEnabled(enable);
            }
        }
    }

    /*private void reset()
    {
        commandSequence.clear();
        commandLength=1;
    }*/

    // This doesn't work for animateAlphaBlink or animateColorBlink
    // All the buttons flash at once
    // lights up the buttons in a sequence
    private void animateCommand() {
        enableButtons(false);
        changeTurn();
        for (int i = 0; i < commandSequence.size(); i++) {
            final int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateColorBlink(commandSequence.get(finalI));
                }
            }, (i*1500));
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeTurn();
                enableButtons(true);
            }
        }, 1500*commandSequence.size());
        System.out.println("Animation complete");

    }

    // creates a sequence of buttons for a command of given length
    private void createCommandSequence() {
        commandSequence.clear();
        for (int i = 0; i < commandLength; i++) {
            int whichRow = random.nextInt(3);
            int whichColumn = random.nextInt(2);
            Button button = (Button) buttonLinearLayouts[whichRow].getChildAt(whichColumn);
            commandSequence.add(button);
        }
        System.out.println("Command sequence created");/*
        for ( int i = 0 ; i<commandLength ; i++)
        {
            System.out.println(commandSequence.get(i).getSolidColor());
        }*/
    }
}
