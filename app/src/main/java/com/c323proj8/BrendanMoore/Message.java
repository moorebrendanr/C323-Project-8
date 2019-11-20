package com.c323proj8.BrendanMoore;

/**
 * This class represents a message.
 */
public class Message {
    String text;
    String sentBy;
    String sentTo;

    /**
     * Construct a Message
     * @param text the message text
     * @param sentBy who the message was sent by
     * @param sentTo who the message was sent to
     */
    public Message(String text, String sentBy, String sentTo) {
        this.text = text;
        this.sentBy = sentBy;
        this.sentTo = sentTo;
    }
}
