package com.dotincorpkr.englidot;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wjddk on 2017-02-06.
 */

public class TagData {

    @SerializedName("id")
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SerializedName("wrongAnswer")
    String wrongAnswer;

    public String getWrongAnswer() {
        return wrongAnswer;
    }

    public void setWrongAnswer(String wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
    }


    @SerializedName("testTime")
    private String testTime;

    public String getTestTime() {
        return testTime;
    }
    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    @SerializedName("testResult")
    private String testResult;

    public String getTestResult() {
            return testResult;
        }
    public void setTestResult(String testResult) {
            this.testResult = testResult;
        }

    //여기까지 Alphabet Review Data를 위한 TagData

    @SerializedName("word")
    String word;

    public String getWord(){
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }

    @SerializedName("wordClass1")
    String wordClass1;

    public String getWordClass1(){
        return wordClass1;
    }
    public void setWordClass1(String wordClass1) {
        this.word = wordClass1;
    }

    @SerializedName("mean1")
    String mean1;

    public String getMean1(){
        return mean1;
    }
    public void setMean1(String wordClass1) {
        this.mean1 = mean1;
    }

    @SerializedName("Tword")
    String Tword;

    @SerializedName("Tmean1")
    String Tmean1;

    @SerializedName("Tmean2")
    String Tmean2;

    @SerializedName("Tbraille")
    String Tbraille;

    public String getTword() {
        return Tword;
    }

    public void setTword(String tword) {
        Tword = tword;
    }

    public String getTmean1() {
        return Tmean1;
    }

    public void setTmean1(String tmean1) {
        Tmean1 = tmean1;
    }

    public String getTmean2() {
        return Tmean2;
    }

    public void setTmean2(String tmean2) {
        Tmean2 = tmean2;
    }

    public String getTbraille() {
        return Tbraille;
    }

    public void setTbraille(String tbraille) {
        Tbraille = tbraille;
    }

}