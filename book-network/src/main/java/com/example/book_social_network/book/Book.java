package com.example.book_social_network.book;


import com.example.book_social_network.common.BaseEntity;
import com.example.book_social_network.feedback.Feedback;
import com.example.book_social_network.history.BookTransactionHistory;
import com.example.book_social_network.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Book extends BaseEntity {

    private String title ;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    // bookCover is a string that will contain the path to the book cover image
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    @Transient
    public double getRate(){
        if(feedbacks == null || feedbacks.isEmpty()){
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        double roundedRate = Math.round(rate * 10.0) / 10.0;
        return roundedRate;
    }



}
