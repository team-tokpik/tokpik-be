package org.example.tokpik_be.term.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "terms")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer mainCategory;

    @Column(nullable = false)
    private Integer subCategory;

    @Column(nullable = true)
    private String contentTitle;

    @Column(nullable = false)
    private String content;

    public Term(String title, Integer mainCategory, Integer subCategory,
        String contentTitle, String content){
        this.title = title;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.contentTitle = contentTitle;
        this.content = content;
    }
}
