package com.example.notificationservice;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MailHogMessage {
    private Content content;

    public MailHogMessage(JSONObject jsonMsg) {
        if (jsonMsg.has("Content")) {
            JSONObject contentJson = jsonMsg.getJSONObject("Content");
            this.content = new Content(contentJson);
        } else {
            System.out.println("Warning: 'Content' object not found in MailHog message JSON: " + jsonMsg);
            this.content = new Content(new JSONObject());
        }
    }

    public Content getContent() { return content; }

    public static class Content {
        private Map<String, List<String>> Headers;
        private String Body;

        public Content(JSONObject contentJson) {
            this.Headers = new HashMap<>();
            this.Body = "";

            if (contentJson != null) {
                if (contentJson.has("Headers")) {
                    JSONObject headersJson = contentJson.getJSONObject("Headers");

                    for (String key : headersJson.keySet()) {
                        Object valueObj = headersJson.get(key);
                        if (valueObj instanceof JSONArray) {
                            JSONArray headerValuesArray = (JSONArray) valueObj;
                            List<String> headerValuesList = new ArrayList<>();
                            for (int i = 0; i < headerValuesArray.length(); i++) {
                                Object item = headerValuesArray.get(i);
                                if (item != null) {
                                    headerValuesList.add(item.toString());
                                } else {
                                    headerValuesList.add(null);
                                }
                            }
                            this.Headers.put(key, headerValuesList);
                        } else {
                            System.out.println("Warning: Header value for key '" + key + "' is not an array: " + valueObj + ". Adding as single-item list.");
                            this.Headers.put(key, List.of(valueObj != null ? valueObj.toString() : "null"));
                        }
                    }
                }

                if (contentJson.has("Body")) {
                    this.Body = contentJson.getString("Body");
                }
            }
        }

        public Map<String, List<String>> getHeaders() { return Headers; }
        public String getBody() { return Body; }
    }
}

public class MailHogClient {

    public static List<MailHogMessage> getMessages(String host, int port) {
        String url = "http://" + host + ":" + port + "/api/v2/messages";

        HttpResponse<String> response = Unirest.get(url).asString();
        if (response.isSuccess()) {
            JSONObject json = new JSONObject(response.getBody());
            JSONArray items = json.getJSONArray("items");

            List<MailHogMessage> messages = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject itemJson = items.getJSONObject(i);
                MailHogMessage msg = new MailHogMessage(itemJson);
                messages.add(msg);
            }
            return messages;
        } else {
            System.out.println("Failed to fetch messages from MailHog API: " + response.getStatus() + " - " + response.getBody());
        }
        return List.of();
    }
}