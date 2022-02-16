package com.nameless.spin_off.entity.movie;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.ViewedPostByIp;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.StaticVariable.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseTimeEntity {

    @Id
    @Column(name="movie_id")
    private Long id;

    private String title;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<FollowedMovie> followingMembers = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<Post> taggedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ViewedMovieByIp> viewedMovieByIps = new ArrayList<>();

    private Double postScore;
    private Double viewScore;
    private Double followScore;
    private Double popularity;

    //==연관관계 메소드==//
    private void addViewedMovieByIp(String ip) {
        ViewedMovieByIp viewedMovieByIp = ViewedMovieByIp.createViewedMovieByIp(ip);

        updateViewCount();
        this.viewedMovieByIps.add(viewedMovieByIp);
        viewedMovieByIp.updateMovie(this);
    }

    public void addTaggedPosts(Post post) {
        updatePostCount();
        this.taggedPosts.add(post);
        post.updateMovie(this);
    }

    public void addFollowingMembers(FollowedMovie followedMovie) {
        updateFollowCount();
        this.followingMembers.add(followedMovie);
    }

    //==생성 메소드==//
    public static Movie createMovie(Long id, String title, String imageUrl) {

        Movie movie = new Movie();
        movie.updateId(id);
        movie.updateTitle(title);
        movie.updateImageUrl(imageUrl);
        movie.updateCountToZero();

        return movie;
    }

    //==수정 메소드==//

    public void updateCountToZero() {
        this.viewScore = 0.0;
        this.postScore = 0.0;
        this.followScore = 0.0;
        this.popularity = 0.0;
    }

    public void updatePopularity() {
        this.popularity = viewScore + postScore + followScore;
    }

    private void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private void updateTitle(String title) {
        this.title = title;
    }

    private void updateId(Long id) {
        this.id = id;
    }

    //==비즈니스 로직==//
    public boolean insertViewedMovieByIp(String ip) {

        if (isNotAlreadyIpView(ip)) {
            addViewedMovieByIp(ip);
            return true;
        } else {
            return false;
        }
    }

    public void updateFollowCount() {

        LocalDateTime currentTime = LocalDateTime.now();
        FollowedMovie followedMovie ;
        int j = 0, i = followingMembers.size() - 1;
        double result = 0, total = 1 * MOVIE_FOLLOW_COUNT_SCORES.get(0);

        while (i > -1) {
            followedMovie = followingMembers.get(i);
            if (isInTimeFollowedMovie(currentTime, followedMovie, j)) {
                result += 1;
                i--;
            } else {
                if (j == MOVIE_FOLLOW_COUNT_SCORES.size() - 1) {
                    break;
                }
                total += MOVIE_FOLLOW_COUNT_SCORES.get(j) * result;
                result = 0;
                j++;
            }
        }
        followScore = total + MOVIE_FOLLOW_COUNT_SCORES.get(j) * result;

        updatePopularity();
    }

    public void updateViewCount() {

        LocalDateTime currentTime = LocalDateTime.now();
        ViewedMovieByIp viewedMovieByIp ;
        int j = 0, i = viewedMovieByIps.size() - 1;
        double result = 0, total = 1 * MOVIE_VIEW_COUNT_SCORES.get(0);

        while (i > -1) {
            viewedMovieByIp = viewedMovieByIps.get(i);
            if (isInTimeViewedMovie(currentTime, viewedMovieByIp, j)) {
                result += 1;
                i--;
            } else {
                if (j == MOVIE_VIEW_COUNT_SCORES.size() - 1) {
                    break;
                }
                total += MOVIE_VIEW_COUNT_SCORES.get(j) * result;
                result = 0;
                j++;
            }
        }
        viewScore = total + MOVIE_VIEW_COUNT_SCORES.get(j) * result;

        updatePopularity();
    }

    public void updatePostCount() {

        LocalDateTime currentTime = LocalDateTime.now();
        Post post ;
        int j = 0, i = taggedPosts.size() - 1;
        double result = 0, total = 1 * MOVIE_POST_COUNT_SCORES.get(0);

        while (i > -1) {
            post = taggedPosts.get(i);
            if (isInTimeTaggedPost(currentTime, post, j)) {
                result += 1;
                i--;
            } else {
                if (j == MOVIE_POST_COUNT_SCORES.size() - 1) {
                    break;
                }
                total += MOVIE_POST_COUNT_SCORES.get(j) * result;
                result = 0;
                j++;
            }
        }
        postScore = total + MOVIE_POST_COUNT_SCORES.get(j) * result;

        updatePopularity();
    }
    //==조회 로직==//
    public Boolean isNotAlreadyIpView(String ip) {

        LocalDateTime currentTime = LocalDateTime.now();

        List<ViewedMovieByIp> views = viewedMovieByIps.stream()
                .filter(viewedMovieByIp -> viewedMovieByIp.getIp().equals(ip))
                .collect(Collectors.toList());

        if (views.isEmpty()) {
            return true;
        }
        return ChronoUnit.MINUTES
                .between(views.get(views.size() - 1).getCreatedDate(), currentTime) >= VIEWED_BY_IP_MINUTE;
    }

    private boolean isInTimeFollowedMovie(LocalDateTime currentTime, FollowedMovie followedMovie, int j) {
        return ChronoUnit.DAYS
                .between(followedMovie.getCreatedDate(), currentTime) >= MOVIE_FOLLOW_COUNT_DAYS.get(j) &&
                ChronoUnit.DAYS
                        .between(followedMovie.getCreatedDate(), currentTime) < MOVIE_FOLLOW_COUNT_DAYS.get(j + 1);
    }

    private boolean isInTimeViewedMovie(LocalDateTime currentTime, ViewedMovieByIp viewedMovieByIp, int j) {
        return ChronoUnit.DAYS
                .between(viewedMovieByIp.getCreatedDate(), currentTime) >= MOVIE_VIEW_COUNT_DAYS.get(j) &&
                ChronoUnit.DAYS
                        .between(viewedMovieByIp.getCreatedDate(), currentTime) < MOVIE_VIEW_COUNT_DAYS.get(j + 1);
    }

    private boolean isInTimeTaggedPost(LocalDateTime currentTime, Post post, int j) {
        return ChronoUnit.DAYS
                .between(post.getCreatedDate(), currentTime) >= MOVIE_POST_COUNT_DAYS.get(j) &&
                ChronoUnit.DAYS
                        .between(post.getCreatedDate(), currentTime) < MOVIE_POST_COUNT_DAYS.get(j + 1);
    }

}
