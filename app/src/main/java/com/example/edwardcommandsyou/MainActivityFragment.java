package com.example.edwardcommandsyou;

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

import androidx.fragment.app.Fragment;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {
    private SecureRandom random; // for randomizing button flashes
    private Handler handler; // for delaying flashes of buttons
    private TextView yourTurnTextView; // for displaying if it is your turn or Edward's
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

        yourTurnTextView = (TextView) view.findViewById(R.id.yourTurnTextView);
        //yourTurnTextView.setText(R.string.your_turn);
        changeTurn();
        System.out.println(yourTurnTextView.getText());

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
        return view;
    }

    private void changeTurn () //Used to tell the user on screen whose turn it is
    {
        if (!isYourTurn) yourTurnTextView.setText(R.string.your_turn);
        if (isYourTurn) yourTurnTextView.setText(R.string.edwards_turn);
        isYourTurn = !isYourTurn;
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
                    createCommandSequence();
                    animateCommand();
                }
            }
            else
            {
                // Display some message saying incorrect
                // print (you made it x rounds!)
                System.out.println("Wrong answer");
                commandLength = 1;
                steps = 0;
                createCommandSequence();
                animateCommand();
                // Says how many you made it
            }


//            // this section should check if correct button has been pressed
//            int index = !inReverse ? stepsTakenInObeying : (commandLength - 1 - stepsTakenInObeying);
//            boolean isCorrect = commandSequence.get(index).equals(button);
//
//            // the button has been pressed so another step has been taken in obeying the sequence
//            ++stepsTakenInObeying;
//            if (isCorrect) {
//                // if a correct button was pressed and we are at the end of a command
//                if (stepsTakenInObeying == commandLength) {
//                    // if a correct button was pressed and we are at the end of a round
//                    if (roundProgress == roundLength) {
//                        // reset round counter
//                        roundProgress = 0;
//                    }
//                    // reset step in command counter
//                    stepsTakenInObeying = 0;
//                }
//            }
//            else {
//
//            }
        }
    };

    private void animateAlphaBlink(Button button) {
        Animation animation = new AlphaAnimation(1,0);
        //animation.setRepeatCount(5);
        animation.setDuration(500);
        button.startAnimation(animation);
    }

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

    private void reset()
    {
        commandSequence.clear();
        commandLength=1;
    }

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
                enableButtons(true);
                changeTurn();
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
