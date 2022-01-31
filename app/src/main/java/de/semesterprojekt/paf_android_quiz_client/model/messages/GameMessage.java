package de.semesterprojekt.paf_android_quiz_client.model.messages;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.semesterprojekt.paf_android_quiz_client.model.User;

/**
 * Creates an GameMessageobject from JSONString-Data received from the server via Websocket
 */
public class GameMessage {

    private String category;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int correctAnswer;
    User user;
    User opponent;
    private int userScore;
    private int opponentScore;
    private Map<Integer, String> answers = new HashMap<>();


    public GameMessage(String category, String question, String answer1, String answer2, String answer3, String answer4, int correctAnswer, User user, User opponent, int userScore, int opponentScore) {
        this.userScore = userScore;
        this.opponentScore = opponentScore;
        this.category = category;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAnswer = correctAnswer;
        this.user = user;
        this.opponent = opponent;
    }

//BODY: {
// "category":"Kultur",
// "question":"Wer oder was ist \"Debussy\"?",
// "answer1":"ein Theaterstück von Roger Vontobel",
// "answer2":"ein Notenschlüssel aus Asien","answer3":"eine Gitarre aus Kolumbien",
// "answer4":"ein französischer Komponist des Impressionismus",
// "correctAnswer":4,
// "user":{"userName":"alf","profileImage":"default10.png"},
// "opponent":{"userName":"feli","profileImage":"default9.png"},
// "userScore":0,
// "opponentScore":0,
// "type":"GAME_MESSAGE"}


    // Getter & Setter

    public Map<Integer, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, String> answers) {
        this.answers = answers;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public User getUser() {
        return user;
    }

    public String getUserName() {
        return user.getUserName();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getOpponent() {
        return opponent;
    }
    public String getOpponentName() {
        return opponent.getUserName();
    }

    public void setOpponent(User opponent) {
        this.opponent = opponent;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    @NonNull
    @Override
    public String toString() {
        return "category " + this.category + "\n" +
                "question " + this.question + "\n" +
                "answer1 " + this.answer1 + "\n" +
                "answer2 " + this.answer2 + "\n" +
                "answer4 " + this.answer2 + "\n" +
                "correctAnswer " + this.correctAnswer + "\n" +
                "user " + this.user.toString() + "\n" +
                "opponent " + this.opponent.toString() + "\n" +
                "userScore " + this.userScore + "\n" +
                "opponentScore " + this.opponentScore;
    }
}
