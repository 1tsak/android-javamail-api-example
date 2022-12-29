package com.a2v10.javamailapiexample;

import static com.a2v10.javamailapiexample.HTMLUtils.extractPlainTextFromHtml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;


public class MainActivity extends AppCompatActivity {
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//      You have to create app password first visit this for more instruction https://support.google.com/accounts/answer/185833?hl=en
        String username = "your email";
        String password = "your app password";

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Fetching mails. Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

//        Call Async Task FetchMail
        new FetchMail().execute(username, password);

    }

    // Setting RecyclerView
    public void setRecyclerView(ArrayList<String> subject) {
        ArrayList<MailModel> mailModels = new ArrayList<>();
        for (String str : subject) {
            Log.d("mail", str);
            try {
                JSONObject mailObject = new JSONObject(str);
                mailModels.add(new MailModel(mailObject.getString("body"), mailObject.getString("subject")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getApplicationContext(), mailModels);
        recyclerView.setAdapter(recyclerAdapter);
        progressDialog.dismiss();

    }

    //  Async task to fetch mail using java mail api on background thread
    private class FetchMail extends AsyncTask<String, Integer, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> mails = new ArrayList<String>();
            String host = "pop.gmail.com";// I tried google's pop
            String mailStoreType = "pop3";

            String username = params[0]; //passed in through the execute() method
            String password = params[1]; //passed in through the execute() method

            try {
                // create properties field
                Properties properties = new Properties();
                properties.put("mail.store.protocol", "pop3");
                properties.put("mail.pop3.host", host);
                properties.put("mail.pop3.port", "995");
                properties.put("mail.pop3.starttls.enable", "true");
                Session emailSession = Session.getDefaultInstance(properties);

                Store store = emailSession.getStore("pop3s");

                store.connect(host, username, password);

                Folder emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        System.in));

                Message[] messages = emailFolder.getMessages();

                System.out.println("messages.length---" + messages.length);
                for (int i = 0; i < 25; i++) {
                    Message message = messages[i];

                    System.out.println("---------------------------------");
                    String subjectS = message.getSubject().toString();
                    Object content = message.getContent();
                    // Get the content of the message
                    String body = "";

                    if (content instanceof String) {
                        // The content is a simple string, so we can return it as is
                        body = (String) content;
                    } else if (content instanceof Multipart) {
                        // The content is a multipart message, so we need to process each part
                        Multipart multipart = (Multipart) content;
//                        for (int j = 0; j < multipart.getCount(); j++) {
                        Part part = multipart.getBodyPart(0);
                        String contentType = part.getContentType();
                        if (contentType.startsWith("text/plain")) {
                            // This part is plain text, so we can return it as is
                            body = (String) part.getContent();
                        } else if (contentType.startsWith("text/html")) {
                            // This part is HTML, so we need to use an HTML parser to extract the plain text
                            String html = (String) part.getContent();
                            body = extractPlainTextFromHtml(html);
                        }
//                        }
                    } else if (content instanceof MimeBodyPart) {
                        // The content is a single MIME body part, so we can process it directly
                        MimeBodyPart bodyPart = (MimeBodyPart) content;
                        String contentType = bodyPart.getContentType();
                        if (contentType.startsWith("text/plain")) {
                            // This part is plain text, so we can return it as is
                            body = (String) bodyPart.getContent();
                        } else if (contentType.startsWith("text/html")) {
                            // This part is HTML, so we need to use an HTML parser to extract the plain text
                            String html = (String) bodyPart.getContent();
                            body = extractPlainTextFromHtml(html);
                        }
                    }

                    try {
                        String mailObject = String.format("{subject: \"%s\", body: \"%s\"}", subjectS, body);
                        mails.add(mailObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    String line = reader.readLine();
                    if ("YES".equals(line)) {
                        message.writeTo(System.out);
                    } else if ("QUIT".equals(line)) {
                        break;
                    }
                }

                emailFolder.close(false);
                store.close();

            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("mytag", "done!" + mails.size());

//            return fetch();
            return mails;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            setRecyclerView(strings);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }


}
