package com.nameless.spin_off.service.hashtag;

import com.nameless.spin_off.StaticVariable;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.movie.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.nameless.spin_off.StaticVariable.HASHTAG_FOLLOW_COUNT_SCORES;
import static com.nameless.spin_off.StaticVariable.HASHTAG_SCORE_FOLLOW_RATES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaHashtagServiceTest {

    @Autowired HashtagService hashtagService;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 해시태그_조회수_증가() throws Exception{

        //given
        Hashtag hashtag = hashtagRepository.save(Hashtag.createHashtag(""));
        Hashtag hashtag2 = hashtagRepository.save(Hashtag.createHashtag(""));
        Hashtag hashtag3 = hashtagRepository.save(Hashtag.createHashtag(""));

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        for (int i = 0; i < 10; i++) {
            hashtagService.insertViewedHashtagByIp("" + i, hashtag.getId());
            hashtagService.insertViewedHashtagByIp("" + i % 2, hashtag2.getId());
            hashtagService.insertViewedHashtagByIp("" + i % 3, hashtag3.getId());
        }

        Hashtag tag = hashtagRepository.getById(hashtag.getId());
        Hashtag tag2 = hashtagRepository.getById(hashtag2.getId());
        Hashtag tag3 = hashtagRepository.getById(hashtag3.getId());

        //then
        assertThat(tag.getViewScore()).isEqualTo(tag.getViewedHashtagByIps().size() * StaticVariable.MOVIE_VIEW_COUNT_SCORES.get(0));
        assertThat(tag.getViewScore()).isEqualTo(10 * StaticVariable.MOVIE_VIEW_COUNT_SCORES.get(0));
        assertThat(tag2.getViewScore()).isEqualTo(tag2.getViewedHashtagByIps().size() * StaticVariable.MOVIE_VIEW_COUNT_SCORES.get(0));
        assertThat(tag2.getViewScore()).isEqualTo(2 * StaticVariable.MOVIE_VIEW_COUNT_SCORES.get(0));
        assertThat(tag3.getViewScore()).isEqualTo(tag3.getViewedHashtagByIps().size() * StaticVariable.MOVIE_VIEW_COUNT_SCORES.get(0));
        assertThat(tag3.getViewScore()).isEqualTo(3 * StaticVariable.MOVIE_VIEW_COUNT_SCORES.get(0));
    }



    @Test
    public void 멤버_팔로우_해시태그() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Hashtag hashtag = Hashtag.createHashtag("팔로우_해시태그");
        Long hashtagId = hashtagRepository.save(hashtag).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId);

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(memberId);
        Hashtag newHashtag = hashtagRepository.getById(hashtagId);
        //then
        assertThat(newMember.getId()).isEqualTo(memberId);
        assertThat(newMember.getFollowedHashtags().size()).isEqualTo(1);
        assertThat(newMember.getFollowedHashtags().iterator().next().getHashtag().getId()).isEqualTo(hashtagId);
        assertThat(newHashtag.getFollowingMembers().size()).isEqualTo(1);
        assertThat(newHashtag.getFollowScore()).isEqualTo(HASHTAG_FOLLOW_COUNT_SCORES.get(0) * HASHTAG_SCORE_FOLLOW_RATES);
    }

    @Test
    public void 멤버_팔로우_해시태그_예외처리() throws Exception{

        //given

        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Hashtag hashtag = Hashtag.createHashtag("팔로우_해시태그");
        Long hashtagId = hashtagRepository.save(hashtag).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId);

        System.out.println("멤버함수");

        //then
        assertThatThrownBy(() -> hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId))
                .isInstanceOf(AlreadyFollowedHashtagException.class);
        assertThatThrownBy(() -> hashtagService.insertFollowedHashtagByHashtagId(0L, hashtagId))
                .isInstanceOf(NotExistMemberException.class);
        assertThatThrownBy(() -> hashtagService.insertFollowedHashtagByHashtagId(memberId, 0L))
                .isInstanceOf(NotExistHashtagException.class);
    }
}