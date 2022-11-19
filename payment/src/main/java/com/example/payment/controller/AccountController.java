package com.example.payment.controller;

import com.example.payment.dao.AccountDao;
import com.example.payment.ds.Account;
import com.example.payment.ds.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/payment")
public class AccountController {
    @Autowired
    private AccountDao accountDao;

    /*
    customer-id -account , owner - id, total amount
     */
    //curl -X POST -H "Content-Type: application/json" -d '{"name":"Thuza","totalAmount":50,"accountNumber":" Thuza64261000"}' localhost:8099/payment/checkout
    @Transactional
    @PostMapping(value = "/checkout",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity checkout(
             @RequestBody AccountInfo accountInfo) {
        Optional<Account> accountOptional = accountDao.findAccountByNameAndAccountNumber(accountInfo.getName(), accountInfo.getAccountNumber());
        if (!accountOptional.isPresent()) {
            return ResponseEntity.notFound().build();

        }
        Account userAccount = accountOptional.get();
        Account ownerAccount = accountDao.findById(2).get();
            if (accountInfo.getTotalAmount()>userAccount.getAmount()){
                return ResponseEntity.badRequest().body("Insufficient Amount");
            }else {

                transferAmount(accountInfo.getTotalAmount(), userAccount, ownerAccount);
                return ResponseEntity
                    .status(HttpStatus.CREATED).build();

        }
    }

    private void transferAmount(double totalAmount, Account userAccount, Account ownerAccount) {
        userAccount.setAmount(userAccount.getAmount() - totalAmount);
        ownerAccount.setAmount(ownerAccount.getAmount() + totalAmount);
    }


    public double toDouble(String amount){
        return Double.parseDouble(amount);
        /*
         private Integer id;
    private String name;
    private double amount;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;
         */
    }
    //curl -X POST -H "Content-Type: application/json" -d '{"name":"Thaw Thaw","amount":5000,"accountType":"CASH"}' localhost:8099/payment/register
    @PostMapping(value = "/register",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity register(@RequestBody Account account){
        account.setAccountNumber(generateId(account.getName()));
        return ResponseEntity.ok(accountDao.save(account));


    }
    public String generateId(String name){
        return removeWhiteSpace(name) + (( new Random().nextInt(10000) +10000));
    }

    private static String removeWhiteSpace(String name) {
        String n1="";
        if (name.contains(" ")){
            String [] names= name.split(" ");

            for (String s:names){
                n1+=s;

            }
        }else {
            n1= name;
            return n1;
        }
        return n1.trim();
    }
}
