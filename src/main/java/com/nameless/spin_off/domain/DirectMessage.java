package com.nameless.spin_off.domain;

import com.nameless.spin_off.domain.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectMessage {

    @Id
    @GeneratedValue
    @Column(name="directmessage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivemember_id")
    @NotNull
    private Member receivedMember;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //==연관관계 메소드==//

    //==생성 메소드==//

    //==수정 메소드==//

    //==비즈니스 로직==//

    //==조회 로직==//

}
