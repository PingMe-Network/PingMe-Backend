package me.huynhducphu.ping_me.model.blog;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.huynhducphu.ping_me.model.User;
import me.huynhducphu.ping_me.model.common.BaseEntity;
import me.huynhducphu.ping_me.model.constant.BlogCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin 9/15/2025
 *
 **/
@Entity
@Table(name = "blogs")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Blog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String title;

    private String imgPreviewUrl;

    @Column(columnDefinition = "VARCHAR(150)")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private BlogCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    private List<BlogComment> blogComments;

    @Column(nullable = false)
    private Boolean isApproved = false;

    public Blog(String title, String description, String content, BlogCategory category) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.category = category;
        this.isApproved = false;
        this.blogComments = new ArrayList<>();
    }
}
