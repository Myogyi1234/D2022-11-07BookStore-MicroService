package com.example.transporation.controller;

import com.example.transporation.dao.BookDao;
import com.example.transporation.dao.TransportInfoDao;
import com.example.transporation.ds.Book;
import com.example.transporation.ds.TransportInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transport")
public class TransportController {
    @Autowired
    private TransportInfoDao transportInfoDao;

    @Autowired
    private BookDao bookDao;
    @PostMapping(value = "/transport-create",consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity createTransportInfo(@RequestBody TransportInfo transportInfo){
        TransportInfo tp1=new TransportInfo(transportInfo.getName(),
                transportInfo.getOrderId(),transportInfo.getTotal());
        for (Book book:transportInfo.getBooks()){
            tp1.addBook(bookDao.save(book));
        }
        transportInfoDao.save(transportInfo);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

}
