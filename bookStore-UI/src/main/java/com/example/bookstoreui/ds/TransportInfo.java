package com.example.bookstoreui.ds;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
@AllArgsConstructor
@Data
public class TransportInfo {
    private String name;
    private String orderId;
    private Set<Book>books;
    private double total;

    public TransportInfo() {
    }
}
