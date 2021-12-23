package de.semesterprojekt.paf_android_quiz_client.model;

/**
 * Creates an GameMessageobject from JSONString-Data received from the server via Websocket
 */
public class GameMessageObject {

    private int userScore;
    private int opponentScore;
    private String category;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    User user;
    User opponent;

    public GameMessageObject() {

    }

    public GameMessageObject(String category, String question, String answer1, String answer2, String answer3, String answer4, int userScore, int opponentScore, User user, User opponent) {
        this.userScore = userScore;
        this.opponentScore = opponentScore;
        this.category = category;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.user = user;
        this.opponent = opponent;
    }

    // {"category":"Essen & Trinken","question":"Aus welchem Land kommt der Gouda?","answer":["Ghana","Niederlande","Frankreich","Luxemburg"],"userScore":0,"opponentScore":0,"user":{"userId":0,"userName":"Bernd","profileImage":null,"ready":false},"opponent":{"userId":0,"userName":"Beate","profileImage":null,"ready":false}}


    // Getter & Setter
    public User getUser() {
        return user;
    }

    public User getOpponent() {
        return opponent;
    }

    public void setUser(User user) {
        this.user = user;
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
}
