package com.example.transporation.ds;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter

public class TransportInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String orderId;
    @OneToMany(mappedBy = "transportInfo")
    private Set<Book> books=new HashSet<>();
    private double total;

    public TransportInfo() {

    }

    public TransportInfo(String name, String orderId, double total) {
        this.name = name;

        this.books = books;
        this.total = total;
    }

    public void addBook(Book book){
        book.setTransportInfo(this);
        books.add(book);
    }
}
