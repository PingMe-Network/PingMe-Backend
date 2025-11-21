package me.huynhducphu.PingMe_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.authorization.Role;
import me.huynhducphu.PingMe_Backend.model.blog.Blog;
import me.huynhducphu.PingMe_Backend.model.blog.BlogComment;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;
import me.huynhducphu.PingMe_Backend.model.constant.AuthProvider;
import me.huynhducphu.PingMe_Backend.model.constant.Gender;
import me.huynhducphu.PingMe_Backend.model.constant.UserStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Admin 8/3/2025
 **/
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String password;

    private String address;

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Blog> blogs;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BlogComment> blogComments;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
