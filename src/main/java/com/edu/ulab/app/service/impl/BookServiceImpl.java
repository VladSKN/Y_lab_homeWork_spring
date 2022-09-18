package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.mapper.BookEntityToBookDtoMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.BookRepository;
import com.edu.ulab.app.storage.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final BookEntityToBookDtoMapper bookEntityToBookDtoMapper;

    private static final AtomicLong bookId = new AtomicLong(System.currentTimeMillis());

    public BookServiceImpl(BookRepository bookRepository,
                           UserRepository userRepository,
                           BookEntityToBookDtoMapper bookEntityToBookDtoMapper) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookEntityToBookDtoMapper = bookEntityToBookDtoMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        // сгенерировать идентификатор
        // создать книгу
        // вернуть сохраненного книгу со всеми необходимыми полями id
        BookEntity bookEntity = bookEntityToBookDtoMapper.bookDtoToBookEntity(bookDto);
        bookEntity.setUser(userRepository.getUserById(bookDto.getUserId()));
        bookEntity.setId(bookId.getAndIncrement());
        bookRepository.insertBook(bookEntity);

        return bookEntityToBookDtoMapper.bookEntityToToBookDto(bookEntity);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookEntity bookEntity = bookEntityToBookDtoMapper.bookDtoToBookEntity(bookDto);
        bookRepository.updateBook(bookEntity);
        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        BookEntity bookById = bookRepository.findBookById(id);
        return bookEntityToBookDtoMapper.bookEntityToToBookDto(bookById);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteBookById(id);
    }

    @Override
    public List<BookEntity> findBookByUserId(long userId) {
        return bookRepository.findBookByUserId(userId);
    }

    @Override
    public void deleteBookByUserId(long userId) {
        bookRepository.deleteBookByUserId(userId);
    }
}
