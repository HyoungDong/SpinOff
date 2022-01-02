package com.nameless.spin_off.domain.post;

import com.nameless.spin_off.domain.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostBlock {

    @Id
    @GeneratedValue
    @Column(name="postblock_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postblockedmember_id")
    @NotNull
    private Member postBlockedMember;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static PostBlock createPostBlock(Member member, Member postBlockedMember) {
        PostBlock postBlock = new PostBlock();
        postBlock.updateMember(member);
        postBlock.updatePostBlockedMember(postBlockedMember);

        return postBlock;

    }

    //==수정 메소드==//
    private void updatePostBlockedMember(Member postBlockedMember) {
        this.postBlockedMember = postBlockedMember;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}