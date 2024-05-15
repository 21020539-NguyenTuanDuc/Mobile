package com.example.mobile.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuizModel {
    private String id;
    private String date_id;
    private String question;
    private List<String> answer;
    private String true_answer;
    private String detailed_answer;

    public QuizModel() {
        id = "";
        date_id = "";
        question = "";
        answer = new ArrayList<>();
        true_answer = "";
        detailed_answer = "";
    }

    public QuizModel(String id, String date_id, String question,
                     List<String> answer, String true_answer, String detailed_answer) {
        this.id = id;
        this.date_id = date_id;
        this.question = question;
        this.answer = answer;
        this.true_answer = true_answer;
        this.detailed_answer = detailed_answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate_id() {
        return date_id;
    }

    public void setDate_id(String date_id) {
        this.date_id = date_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public String getTrue_answer() {
        return true_answer;
    }

    public void setTrue_answer(String true_answer) {
        this.true_answer = true_answer;
    }

    public String getDetailed_answer() {
        return detailed_answer;
    }

    public void setDetailed_answer(String detailed_answer) {
        this.detailed_answer = detailed_answer;
    }

    public int noOfTrueAnswer() {
        for (int i = 0; i < this.answer.size(); ++i) {
            if (this.answer.get(i).equals(this.true_answer)) {
                return i;
            }
        }
        Log.e("There is not the true answer", id);
        return -1;
    }
}
