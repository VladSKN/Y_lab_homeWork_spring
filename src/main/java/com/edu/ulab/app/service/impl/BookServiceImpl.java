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
        bookEntity.setUserId(userRepository.getUserById(bookDto.getUserId()).getId());
        bookEntity.setId(bookId.getAndIncrement());
        bookRepository.insertBook(bookEntity);

        BookDto bookEntityToToBookDto = bookEntityToBookDtoMapper.bookEntityToToBookDto(bookEntity);

        log.info("createBook from BookServiceImpl successfully: {}", bookEntityToToBookDto);
        return bookEntityToToBookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookEntity bookEntity = bookEntityToBookDtoMapper.bookDtoToBookEntity(bookDto);
        bookRepository.updateBook(bookEntity);

        log.info("updateBook from BookServiceImpl successfully: {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        BookEntity bookById = bookRepository.findBookById(id);
        BookDto bookDto = bookEntityToBookDtoMapper.bookEntityToToBookDto(bookById);

        log.info("getBookById from BookServiceImpl successfully: {}", bookDto);
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteBookById(id);

        log.info("deleteBookById from BookServiceImpl successfully: {}", id);
    }

    @Override
    public List<BookEntity> findBookByUserId(long userId) {

        List<BookEntity> bookByUserId = bookRepository.findBookByUserId(userId);

        log.info("findBookByUserId from BookServiceImpl successfully: {}", bookByUserId);
        return bookByUserId;
    }

    @Override
    public void deleteBookByUserId(long userId) {
        bookRepository.deleteBookByUserId(userId);

        log.info("deleteBookByUserId from BookServiceImpl successfully: {}", userId);
    }
}
