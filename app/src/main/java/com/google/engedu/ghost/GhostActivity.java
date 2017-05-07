/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    Button challengeButton;
    Button restartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        InputStream wordListStream ;

        try {
            wordListStream = getAssets().open("words.txt");
            dictionary = new FastDictionary(wordListStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        challengeButton = (Button) findViewById(R.id.challenge);
        restartButton = (Button) findViewById(R.id.restart);

        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView status = (TextView) findViewById(R.id.gameStatus);
                TextView ghostText = (TextView) findViewById(R.id.ghostText);
                String wordFragment = ghostText.getText().toString();
                if(wordFragment.length() >= dictionary.MIN_WORD_LENGTH && dictionary.isWord(wordFragment)){
                    status.setText(wordFragment +" is a word. User Victory!");
                }else{
                    String possible = dictionary.getAnyWordStartingWith(wordFragment);
                    if(possible == null){
                        status.setText("No words can be made. User Victory!");
                    }else{
                        status.setText("A word can still be spelled. The next letter would be " + possible + ". Computer Victory!");
                    }
                }
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart(view);
            }
        });

        onStart(null);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);

        TextView ghostText = (TextView) findViewById(R.id.ghostText);
        TextView status = (TextView) findViewById(R.id.gameStatus);

        savedInstanceState.putString("ghostText", ghostText.getText().toString());
        savedInstanceState.putString("status", status.getText().toString());

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        TextView ghostText = (TextView) findViewById(R.id.ghostText);
        TextView status = (TextView) findViewById(R.id.gameStatus);

        ghostText.setText(savedInstanceState.getString("ghostText"));
        status.setText(savedInstanceState.getString("status"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {

        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView ghostText = (TextView) findViewById(R.id.ghostText);
        String wordFragment = ghostText.getText().toString();

        if(dictionary.isWord(wordFragment) && wordFragment.length() >= dictionary.MIN_WORD_LENGTH){
            label.setText("You spelled " + wordFragment + ". Computer Victory!");
        }else{
            String possibleWord = dictionary.getAnyWordStartingWith(wordFragment);
            if(possibleWord == null){
                label.setText("Computer Challenges You. There are no words available. Computer Victory!");
            }else{
                char[] charArray = possibleWord.toCharArray();
                wordFragment += possibleWord;
                ghostText.setText(wordFragment);
                label.setText(USER_TURN);
            }
            userTurn = true;

        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {


        if((keyCode < 65 || keyCode > 90 ) && userTurn == true){

            TextView word = (TextView) findViewById(R.id.ghostText);
            String wordFragment = word.getText().toString();
            wordFragment += event.getDisplayLabel();
            wordFragment = wordFragment.toLowerCase();
            word.setText(wordFragment);

            TextView status = (TextView) findViewById(R.id.gameStatus);
            status.setText(COMPUTER_TURN);
            userTurn = false;
            //Delay so that the user can understand the computer made its turn
            Handler timerHandler = new Handler();
            timerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn();
                }
            }, 500);

        }

        return super.onKeyUp(keyCode, event);
    }
}
