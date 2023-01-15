package ru.ablyuzin.springboot.booksaccounting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ablyuzin.springboot.booksaccounting.models.Book;
import ru.ablyuzin.springboot.booksaccounting.models.User;
import ru.ablyuzin.springboot.booksaccounting.services.BookService;
import ru.ablyuzin.springboot.booksaccounting.services.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public BooksController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping()
    public String index(@RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "per_page", required = false, defaultValue = "10") Integer perPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear,
                        Model model) {
        if (page == null || perPage == null) {
            model.addAttribute("books", bookService.findAll(sortByYear));
        } else {
            model.addAttribute("books", bookService.findWithPagination(page, perPage, sortByYear));
            model.addAttribute("currentPage", page);
        }
        return "book/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("user") User user) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("users", userService.findAll());
        return "book/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "book/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "book/new";
        }
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "book/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("book") Book book) {
        bookService.update(book, id);
        return "redirect:/books/{id}";
    }

    @PatchMapping("/{id}/add-reader")
    public String addReader(@PathVariable("id") int id, @ModelAttribute("user") User user) {
        bookService.addReader(user, id);
        return "redirect:/books/{id}";
    }

    @PatchMapping("/{id}/remove-reader")
    public String removeReader(@PathVariable int id, @ModelAttribute("user") User user) {
        bookService.removeReader(id);
        return "redirect:/books/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "find-by", required = false) String findBy,
                         @RequestParam(value = "title", required = false) String title,
                         Model model, RedirectAttributes redirAttrs) {
        if (findBy == null) {
            return searchBy(title, model, bookService.findAllByTitle(title), redirAttrs);
        } else if (findBy.equals("title")) {
            return searchBy(title, model, bookService.findAllByTitle(title), redirAttrs);
        } else if (findBy.equals("reader")) {
            return searchBy(title, model, bookService.findAllByReader(title), redirAttrs);
        }
        return "book/search";
    }

    private String searchBy(String title, Model model, List<Book> books, RedirectAttributes redirAttrs) {
        if (title == null) {
            return "book/search";
        } else if (title.equals("")) {
            redirAttrs.addFlashAttribute("searchMessage", "Для поиска необходимо ввести название книги!");
            return "redirect:/books/search";
        }
        model.addAttribute("books", books);
        return "book/search";
    }
}