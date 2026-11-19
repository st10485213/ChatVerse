package chatverse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private final String jsonFilePath;
    private final List<Message> sentMessages = new ArrayList<>();
    private final List<Message> disregardedMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIDs = new ArrayList<>();

    public MessageManager() {
        this("messages.json");
    }

    public MessageManager(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
        loadStoredMessagesFromJson();
    }

    public void addSentMessage(Message msg) {
        if (msg == null) return;
        msg.setFlag("Sent");
        sentMessages.add(msg);
        if (msg.getMessageHash() != null) messageHashes.add(msg.getMessageHash());
        if (msg.getMessageID() != null) messageIDs.add(msg.getMessageID());
    }

    public void addDisregardedMessage(Message msg) {
        if (msg == null) return;
        msg.setFlag("Disregard");
        disregardedMessages.add(msg);
    }

    public void addStoredMessage(Message msg) {
        if (msg == null) return;
        msg.setFlag("Stored");
        storedMessages.add(msg);
        if (msg.getMessageHash() != null) messageHashes.add(msg.getMessageHash());
        if (msg.getMessageID() != null) messageIDs.add(msg.getMessageID());
        saveMessageToJson(msg);
    }

    public void saveMessageToJson(Message msg) {
        if (msg == null) return;
        String line = "{\"messageID\":\"" + escapeJson(msg.getMessageID()) + "\","
                + "\"numMessagesSent\":" + msg.getNumMessagesSent() + ","
                + "\"sender\":\"" + escapeJson(msg.getSender()) + "\","
                + "\"recipient\":\"" + escapeJson(msg.getRecipient()) + "\","
                + "\"messageText\":\"" + escapeJson(msg.getMessageText()) + "\","
                + "\"messageHash\":\"" + escapeJson(msg.getMessageHash()) + "\","
                + "\"flag\":\"" + escapeJson(msg.getFlag()) + "\"}";

        try (PrintWriter out = new PrintWriter(new FileWriter(jsonFilePath, true))) {
            out.println(line);
        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    public void rewriteJsonFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(jsonFilePath, false))) {
            for (Message msg : storedMessages) {
                String line = "{\"messageID\":\"" + escapeJson(msg.getMessageID()) + "\","
                        + "\"numMessagesSent\":" + msg.getNumMessagesSent() + ","
                        + "\"sender\":\"" + escapeJson(msg.getSender()) + "\","
                        + "\"recipient\":\"" + escapeJson(msg.getRecipient()) + "\","
                        + "\"messageText\":\"" + escapeJson(msg.getMessageText()) + "\","
                        + "\"messageHash\":\"" + escapeJson(msg.getMessageHash()) + "\","
                        + "\"flag\":\"" + escapeJson(msg.getFlag()) + "\"}";
                out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error updating JSON file: " + e.getMessage());
        }
    }

    public List<Message> loadStoredMessagesFromJson() {
        storedMessages.clear();
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            return storedMessages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Message msg = new Message();
                msg.setMessageID(extractJsonString(line, "messageID"));
                msg.setNumMessagesSent(extractJsonInt(line, "numMessagesSent"));
                msg.setSender(extractJsonString(line, "sender"));
                msg.setRecipient(extractJsonString(line, "recipient"));
                msg.setMessageText(extractJsonString(line, "messageText"));
                msg.setMessageHash(extractJsonString(line, "messageHash"));
                msg.setFlag(extractJsonString(line, "flag"));

                if (msg.getMessageText() != null) {
                    storedMessages.add(msg);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }
        return storedMessages;
    }

    // a. Display the sender, recipient, and message of all stored messages
    public String displayStoredSendersAndRecipients() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            sb.append(i + 1).append(". ")
              .append("Sender: ").append(m.getSender())
              .append(" | Recipient: ").append(m.getRecipient())
              .append(" | Message: ").append(m.getMessageText())
              .append("\n");
        }
        return sb.toString().trim();
    }

    // b. Display the longest stored message
    public String displayLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) {
            String currentText = m.getMessageText() != null ? m.getMessageText() : "";
            String longestText = longest.getMessageText() != null ? longest.getMessageText() : "";
            if (currentText.length() > longestText.length()) {
                longest = m;
            }
        }
        return longest.getMessageText();
    }

    // c. Search for a message ID and display the corresponding recipient and message
    public String searchByMessageID(String targetId) {
        if (targetId == null) return "Message ID not found.";
        List<Message> pool = new ArrayList<>();
        pool.addAll(sentMessages);
        pool.addAll(storedMessages);

        for (Message m : pool) {
            if (targetId.equals(m.getMessageID())) {
                return "Recipient: " + m.getRecipient() + "\nMessage: " + m.getMessageText();
            }
        }
        return "Message ID not found.";
    }

    // d. Search for all the messages stored/sent for a particular recipient
    public String searchByRecipient(String recipient) {
        if (recipient == null) return "No messages found for recipient.";
        StringBuilder sb = new StringBuilder();
        List<Message> pool = new ArrayList<>();
        pool.addAll(sentMessages);
        pool.addAll(storedMessages);

        for (Message m : pool) {
            if (recipient.equals(m.getRecipient())) {
                sb.append(m.getMessageText()).append("\n");
            }
        }
        return sb.length() == 0 ? "No messages found for recipient." : sb.toString().trim();
    }

    // e. Delete a message using the message hash
    public String deleteByMessageHash(String hash) {
        if (hash == null) return "Message hash not found.";

        for (int i = 0; i < sentMessages.size(); i++) {
            Message m = sentMessages.get(i);
            if (hash.equals(m.getMessageHash())) {
                sentMessages.remove(i);
                messageHashes.remove(hash);
                messageIDs.remove(m.getMessageID());
                return "Message: \"" + m.getMessageText() + "\" successfully deleted.";
            }
        }

        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (hash.equals(m.getMessageHash())) {
                storedMessages.remove(i);
                messageHashes.remove(hash);
                messageIDs.remove(m.getMessageID());
                rewriteJsonFile();
                return "Message: \"" + m.getMessageText() + "\" successfully deleted.";
            }
        }

        return "Message hash not found.";
    }

    // f. Display a report that lists the full details of all stored/sent messages
    public String displayReport() {
        if (storedMessages.isEmpty() && sentMessages.isEmpty()) {
            return "No messages available for report.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Message Hash: ").append(m.getMessageHash()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getMessageText()).append("\n")
              .append("------------------------------\n");
        }
        for (Message m : sentMessages) {
            sb.append("Message Hash: ").append(m.getMessageHash()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getMessageText()).append("\n")
              .append("------------------------------\n");
        }
        return sb.toString().trim();
    }

    public List<Message> getSentMessages() { return sentMessages; }
    public List<Message> getDisregardedMessages() { return disregardedMessages; }
    public List<Message> getStoredMessages() { return storedMessages; }
    public List<String> getMessageHashes() { return messageHashes; }
    public List<String> getMessageIDs() { return messageIDs; }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    private String unescapeJson(String value) {
        if (value == null) return null;
        return value.replace("\\\"", "\"")
                    .replace("\\\\", "\\")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r");
    }

    private String extractJsonString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();

        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                sb.append('\\').append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return unescapeJson(sb.toString());
    }

    private int extractJsonInt(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();

        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        try {
            return Integer.parseInt(json.substring(start, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}