package com.mountblue.blogapplication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.*;
@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "excerpt")
    private String excerpt;
    @Column(name = "content")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="author",nullable=false)
    private Users author;
    @Column(name = "published_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishedAt;
    @Column(name = "is_published")
    private boolean isPublished;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post",
                cascade = CascadeType.ALL,
                orphanRemoval = true)
    private  List<Comments> comments=new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name= "post_tags",
                joinColumns = @JoinColumn(name= "post_id"),
                inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tags> tags=new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addComments(Comments comment) {
        comments.add(comment);
        comment.setPost(this);
    }
    public void removeComments(Comments comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
    public void addTags(Tags tag) {
        tags.add(tag);
        tag.getPosts().add(this);
    }
    public void removeTags(Tags tag) {
        tags.remove(tag);
        tag.getPosts().remove(this);
    }
    public void clearTags() {
        for (Tags tag : new HashSet<>(tags)) {
            tag.getPosts().remove(this);
        }
        tags.clear();
    }
    public Post(String title, String excerpt, String content, Users author, boolean isPublished) {
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.author = author;
        this.isPublished = isPublished;
    }
}
