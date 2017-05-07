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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class TrieNode {

    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(TrieNode root, String s) {

        TrieNode node = root;
        int i = 0;
        int n = s.length();
        char[] charList = s.toCharArray();

        while(i<n){
            if(node.children.containsKey(charList[i]+"") == true){
                node = node.children.get(charList[i]+"");
                i++;
            }else{
                break;
            }

        }

        while(i<n){

            TrieNode newNode = new TrieNode();
            node.children.put(charList[i] + "", newNode);
            node = newNode;
            i++;
            if (i - n == 0) {
                newNode.isWord = true;
            }

        }
    }

    public boolean isWord(String s) {
        if(s.length() == 0){
            return isWord;
        }else{
            if(children.containsKey(s.charAt(0)+"")){
                Boolean bool = children.get(s.charAt(0)+"").isWord(s.substring(1));
                return bool;
            }else{
                return false;
            }
        }

    }

    public String getGoodWordStartingWith(String s) {

        if(s.isEmpty()){
            Random random = new Random();
            return ((char)(random.nextInt(26) + 'a')) + "";
        }

        if(s.length() == 1){

            boolean check = true;
            if(children.containsKey(s)){
                if(children.get(s).children.isEmpty()){
                    return null;
                }
                while(check){

                    Log.d("Tag", "hi");
                    ArrayList<Character> list = new ArrayList<Character>();
                    for(int i = 0; i<26; i++){
                        list.add((char)(i + 'a'));
                    }

                    for(int i = 0; i<list.size(); i++){
                        if(!children.get(s).children.containsKey(list.get(i) + "")){
                            Log.d("Tag", "" + i);
                            list.remove(i);
                        }else if(children.get(s).children.get(list.get(i) + "").isWord){
                            list.remove(i);
                        }
                    }
                    Log.d("Tag", list.toString());
                    if(list.size() > 0) {
                        Random random = new Random();
                        int num = random.nextInt(list.size());
                        return list.get(num) + "";
                    }else{

                        while(check){

                            Random random = new Random();
                            String temp = (char)(random.nextInt(26) + 'a') + "";
                            if(children.get(s).children.containsKey(temp)){
                                return temp;
                            }
                        }
                    }
                }
            }
        }

        if(s.length() > 1){
            String word = "";
            if(children.containsKey(s.charAt(0)+"")){
                word = children.get(s.charAt(0)+"").getAnyWordStartingWith(s.substring(1));
                return word;
            }else{
                return null;
            }
        }

        return null;

    }

    public String getAnyWordStartingWith(String s) {
        if(s.isEmpty()){
            Random random = new Random();
            return ((char)(random.nextInt(26) + 'a')) + "";
        }

        if(s.length() == 1){

            boolean check = true;
            if(children.containsKey(s)){
                if(children.get(s).children.isEmpty()){
                    return null;
                }
                while(check){
                    Random random = new Random();
                    String temp = (char)(random.nextInt(26) + 'a') + "";
                    if(children.get(s).children.containsKey(temp)){
                        return temp;
                    }
                }
            }
        }

        if(s.length() > 1){
            String word = "";
            if(children.containsKey(s.charAt(0)+"")){
                word = children.get(s.charAt(0)+"").getAnyWordStartingWith(s.substring(1));
                return word;
            }else{
                return null;
            }
        }

        return null;
    }


}
