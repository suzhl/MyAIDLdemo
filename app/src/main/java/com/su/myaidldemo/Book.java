package com.su.myaidldemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 苏照亮 on 2017/11/3.
 */

public class Book implements Parcelable {

    public String bookId;
    public String bookName;
    public String author;

    public Book(String bookId, String bookName, String author) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
    }

    protected Book(Parcel in) {
        bookId = in.readString();
        bookName = in.readString();
        author = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return "ID: " + bookId + ", BookName: " + bookName + "Author: " + author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeString(bookName);
        dest.writeString(author);
    }
}
