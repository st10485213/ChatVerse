package chatverse;

import java.util.Random;

public class Message {
    private String messageID;
    private int numMessagesSent;
    private String recipient;
    private String messageText;
    private String messageHash;

    // Default constructor
    public Message() {}

    // Main constructor
    public Message(int messageNumber, String recipient, String messageText) {
        this.numMessagesSent = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
    }

    // Autogenerates a 10-digit random message ID
    private String generateMessageID() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    // Validates that Message ID is no more than 10 characters
    public boolean checkMessageID() {
        return this.messageID != null && this.messageID.length() <= 10;
    }

    // Validates recipient cell number length and international code
    public boolean checkRecipientCell() {
        return this.recipient != null && 
               this.recipient.length() <= 12 && 
               this.recipient.startsWith("+");
    }

    // Checks message length requirement
    public boolean checkMessageLength() {
        return this.messageText != null && this.messageText.length() <= 250;
    }

    // Autogenerates Message Hash: FIRST2DIGITS:MSGNUNBER:FIRSTWORDLASTWORD
    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2 || messageText == null || messageText.trim().isEmpty()) {
            return "";
        }

        String firstTwoDigits = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].toUpperCase();
        String lastWord = words[words.length - 1].toUpperCase();

        return firstTwoDigits + ":" + numMessagesSent + ":" + firstWord + lastWord;
    }

    // Handles user options for sending, disregarding, or storing a message
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                return "Message successfully sent";
            case 2:
                return "Press 0 to delete the message";
            case 3:
                return storeMessage();
            default:
                return "Invalid selection.";
        }
    }

    // Returns formatted full details of a sent message
    public String printMessages() {
        return "Message ID: " + messageID + "\n" +
               "Message Hash: " + messageHash + "\n" +
               "Recipient: " + recipient + "\n" +
               "Message: " + messageText;
    }

    // Returns total count of messages sent
    public int returnTotalMessagess(int totalSent) {
        return totalSent;
    }

    // Stores message in JSON format
    public String storeMessage() {
        // Minimal JSON string construction using Java's standard string formatting
        String jsonOutput = String.format(
            "{\n  \"messageID\": \"%s\",\n  \"messageHash\": \"%s\",\n  \"recipient\": \"%s\",\n  \"message\": \"%s\"\n}",
            messageID, messageHash, recipient, messageText
        );
        System.out.println("JSON Representation:\n" + jsonOutput);
        return "Message successfully stored";
    }

    // Getters and Setters
    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
}