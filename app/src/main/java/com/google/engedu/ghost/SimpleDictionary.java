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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {

        if(prefix == null){
            return words.get(new Random(System.currentTimeMillis()).nextInt(words.size()));
        }else{

            String newWord = binarySearch(words, prefix);
            return newWord;
        }
    }

    @Override
    public String getGoodWordStartingWith(String prefix, int size) {

        String selected = null;
        ArrayList<String> evenWords = new ArrayList<String>();
        ArrayList<String> oddWords = new ArrayList<String>();
        ArrayList<String> tempWords = new ArrayList<String>(words);

        String newWord = "";
        do{

            newWord = binarySearch(tempWords, prefix);
            if(newWord == null){
                break;
            }
            if(newWord.length()%2 == 0){
                 evenWords.add(newWord);
            }else{
                oddWords.add(newWord);
            }

            tempWords.remove(newWord);

        }while(newWord != null);

        if(size%2 == 0 && evenWords.size()!=0){
            selected = evenWords.get(new Random().nextInt(evenWords.size()));
        }else if (oddWords.size() != 0 && oddWords.size()!=0){
            selected = oddWords.get(new Random().nextInt(oddWords.size()));
        }

        if(selected == null){
            selected = binarySearch(words, prefix);
        }

        return selected;
    }

    public String binarySearch(List<String> wordList, String prefix){

        int max = wordList.size()-1;
        int middle = max/2;
        if(max == -1){
            return null;
        }
        if(max == 0 && !wordList.get(0).startsWith(prefix)){
            return null;
        }

        String newWord = "";

        if(wordList.get(middle).startsWith(prefix)){
            return wordList.get(middle);
        }else{

            int checker = prefix.compareTo(wordList.get(middle));
            if(checker < 0){

                List<String> newList = wordList.subList(0, middle);
                newWord = binarySearch(newList, prefix);

            }else{

                List<String> newList = wordList.subList(middle, max);
                newWord = binarySearch(newList, prefix);
            }

        }

        return newWord;
    }

}
