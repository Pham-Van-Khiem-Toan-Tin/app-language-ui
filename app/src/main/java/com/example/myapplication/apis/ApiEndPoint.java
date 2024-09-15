package com.example.myapplication.apis;

public class ApiEndPoint {
    public static final String SOCKET_URL = "http://192.168.101.4:8000";
    public static final String URL = SOCKET_URL + "/";
    public static final String BASE_URL = SOCKET_URL + "/api/";
    public static final String LOGIN = BASE_URL + "login";
    public static final String REGISTER = BASE_URL + "register";
    public static final String RESET_PASSWORD = BASE_URL + "password/";
    public static final String RESET_PASSWORD_EMAIL = RESET_PASSWORD + "reset";
    public static final String RESET_PASSWORD_CODE = RESET_PASSWORD + "verify";
    public static final String RESET_PASSWORD_CHANGE = RESET_PASSWORD + "change";
    public static final String TRANSLATE = BASE_URL + "translate";
    public static final String STATISTICAL = BASE_URL + "statistical";
    public static final String VOCABULARY = BASE_URL + "vocabulary/";
    public static final String VOCABULARY_ALL = VOCABULARY + "all";
    public static final String VOCABULARY_TOPIC = VOCABULARY + "topic/";
    public static final String VOCABULARY_TOPIC_COMPLETE = VOCABULARY_TOPIC + "complete";
    public static final String GRAMMAR = BASE_URL + "grammar/";
    public static final String GRAMMAR_ALL = GRAMMAR + "all";
    public static final String GRAMMAR_COMPLETE = GRAMMAR + "complete";
    public static final String EXERCISE = BASE_URL + "exercise/";
    public static final String EXERCISE_ALL = EXERCISE + "all";
    public static final String EXERCISE_COMPLETE = EXERCISE + "complete";

}
