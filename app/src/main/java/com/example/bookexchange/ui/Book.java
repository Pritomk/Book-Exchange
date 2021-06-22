package com.example.bookexchange.ui;

import org.bson.types.ObjectId;

public class Book {
    public ObjectId id;
    public String name;
    public String author;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Book(ObjectId id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }
    public Book() {

    }
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
