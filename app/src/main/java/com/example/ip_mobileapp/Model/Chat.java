package com.example.ip_mobileapp.Model;

import com.example.ip_mobileapp.Model.User;

import java.util.List;

public class Chat
{
    public Integer getId()
    {
        return id;
    }

    public User getOtherUser()
    {
        return otherUser;
    }

    public List<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(List<Message> messages)
    {
        this.messages = messages;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setOtherUser(User otherUser)
    {
        this.otherUser = otherUser;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", otherUser=" + otherUser +
                ", messages=" + messages +
                '}';
    }

    private Integer id;
    private User otherUser;
    private List<Message> messages;
}
