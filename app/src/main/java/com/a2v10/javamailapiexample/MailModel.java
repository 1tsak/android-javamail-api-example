package com.a2v10.javamailapiexample;

import org.json.JSONObject;

public class MailModel {
    String Content;
    String Subject;

    public MailModel(String content, String subject) {
        Content = content;
        Subject = subject;
    }

    public String getContent() {
        return Content;
    }

    public String getSubject() {
        return Subject;
    }
}
