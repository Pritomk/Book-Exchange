package com.example.bookexchange.ui;

import org.bson.types.ObjectId;

public class BookItem {
    String _id;
    String bookName;
    String price;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public BookItem(String _id, String bookName, String price) {
        this._id = _id;
        this.bookName = bookName;
        this.price = price;
    }
}
