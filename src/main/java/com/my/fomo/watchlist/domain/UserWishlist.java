package com.my.fomo.watchlist.domain;

import com.my.fomo.auth.domain.User;
import com.my.fomo.stock.domain.Stock;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_wishlist",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stock_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserWishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    public static UserWishlist of(User user, Stock stock) {
        UserWishlist wishlist = new UserWishlist();
        wishlist.user = user;
        wishlist.stock = stock;
        return wishlist;
    }
}
