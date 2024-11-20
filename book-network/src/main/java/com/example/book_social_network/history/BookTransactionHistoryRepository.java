package com.example.book_social_network.history;

import com.example.book_social_network.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory ,Integer> {
    @Query("""
          select history
          from BookTransactionHistory history
          where history.user.id = :userId
          """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);
    @Query("""
          select history
          from BookTransactionHistory history
          where history.book.owner.id = :userId
          """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);
    @Query("""
          select
          (count(*) > 0) as isBorrowed
          from BookTransactionHistory bookTransactionHistory
          where bookTransactionHistory.user.id = :userId
          and bookTransactionHistory.book.id = :bookId
          and bookTransactionHistory.returnApproved = false
        """)
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer id);
    @Query("""
           SELECT transaction
           from BookTransactionHistory transaction
           where transaction.user.id = :userId
           and transaction.book.id = :bookId
           and transaction.returned = false
           and transaction.returnApproved = false
           """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer id);

    @Query("""
           SELECT transaction
           from BookTransactionHistory transaction
           where transaction.book.owner.id = :userId
           and transaction.book.id = :bookId
           and transaction.returned = true
           and transaction.returnApproved = false
           """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer id);
}
