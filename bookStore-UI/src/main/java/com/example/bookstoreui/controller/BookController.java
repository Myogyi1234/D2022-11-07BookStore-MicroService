package com.example.bookstoreui.controller;

import com.example.bookstoreui.ds.AccountInfo;
import com.example.bookstoreui.ds.Book;
import com.example.bookstoreui.ds.Cart;
import com.example.bookstoreui.ds.TransportInfo;
import com.example.bookstoreui.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;
import java.util.Set;

@Controller
@RequestMapping("/bookstore")
public class BookController {

    @Autowired
    private BookService bookService;
    @Value("http://localhost:8099/")
    private String paymentUrl;
    @Value("http://localhost:8091/")
    private String transportUrl;
    private RestTemplate template=new RestTemplate();
    @Autowired
    private Cart cart;
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        bookService.findAllBooks()
                .forEach(System.out::println);

        return "success!";
    }
    @GetMapping("/book/delete/{id}")
    public String deleteBookFromCart(@PathVariable("id") int id,Model model){
        cart.removeBookFromCart(bookService.findBooksByID(id));

        return "redirect:/bookstore/book/cart/view";
    }
    @PostMapping("/book/checkout")
    public String handleCheckout(@ModelAttribute Book book){
     //   book.getQuantity().forEach(System.out::println);
        Set<Book> bookSet=cart.getBookSet();
        int i=0;
        for (Book book1:bookSet){
            book1.setItemCount(book.getQuantity().get(i));
            i++;
        }
     //   double totalPrice = getTotalPrice();
        cart.setBookSet(bookSet);

     //   template.postForEntity(paymentUrl+"payment/checkout/1",String.valueOf(totalPrice),String.class);
        return "redirect:/bookstore/account-info";
    }

    private double getTotalPrice() {
        ;
        return cart.getBookSet().stream().map(b->b.getItemCount()*b.getPrice()).mapToDouble(d->d).sum();
    }

    @GetMapping("/book/cart/clear")
    public String clearCart(){
        cart.clearCart();
        return "redirect:/bookstore/book/cart/view";
    }
    @GetMapping("/account-info")
    public String paymentInfoForm(Model model){
        model.addAttribute("accountInfo",new AccountInfo());
        return "accountInfo-form";
    }

    @PostMapping("/account-info")
    public String processAccountInfo(AccountInfo accountInfo, BindingResult result,
                                     @ModelAttribute Set<Book>bookSet, RedirectAttributes redirectAttributes){

        if (result.hasErrors()){
          //  return "accountInfo-form";
        }
        AccountInfo account=new AccountInfo();
        account.setAccountNumber(accountInfo.getAccountNumber());
        account.setName(accountInfo.getName());
        account.setTotalAmount(getTotalPrice());
   ResponseEntity response= template.postForEntity(paymentUrl+"payment/checkout",account, String.class);
        System.out.println("============================"+response.getStatusCode());

        if (response.getStatusCode().equals(HttpStatus.CREATED)){
            var transportInfo=new TransportInfo(account.getName(),generateOrderId(),bookSet,getTotalPrice());
            ResponseEntity transportResponse=template.postForEntity(
                    transportUrl +"transport/transport-create",transportInfo,String.class);
            if (transportResponse.getStatusCode().equals(HttpStatus.CREATED)){
                redirectAttributes.addFlashAttribute("success",true);
            }

        }else {
            throw  new IllegalArgumentException("CheckOut Error!");
        }

        return  "redirect:/";
    }
    private String generateOrderId(){
        return "AMZ000"+new Random().nextInt(10000)+1000;

    }
    record TransportInfo2(String name,String orderId,
                         Set<Book>bookSet ){

    }


    @GetMapping({"/", "/home", "/index"})
    public String index(Model model) {
       model.addAttribute("success",model.containsAttribute("success"));
        model.addAttribute("books", bookService.findAllBooks());
        return "home";
    }

    @GetMapping("/book/{id}")
    public String detailBooks(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookService.findBooksByID(id));
        return "book-details";
    }

    @GetMapping("/book/cart/{id}")
    public String addToCart(@PathVariable("id") int id) {
        cart.addToCart(bookService.findBooksByID(id));
        return "redirect:/bookstore/book/" + id;
    }
    @GetMapping("/book/cart/view")
    public String viewCart(@ModelAttribute Book book){
        //model.addAttribute("book", new Book());
        //model.addAttribute("books",cart.getBookSet());
        return "cart-view";
    }
    @ModelAttribute("books")
    public Set<Book> getAllBooksFromCart(){
        return cart.getBookSet();
    }

    @ModelAttribute("cartSize")
    public int cartSize() {
        return cart.cartSize();
    }
}
