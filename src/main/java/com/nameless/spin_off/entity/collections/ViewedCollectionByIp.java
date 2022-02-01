package com.nameless.spin_off.entity.collections;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewedCollectionByIp extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="viewed_collection_by_ip_id")
    private Long id;

    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    @NotNull
    private Collection collection;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static ViewedCollectionByIp createViewedCollectionsByIp(String ip, Collection collection) {
        ViewedCollectionByIp viewedCollectionByIp = new ViewedCollectionByIp();
        viewedCollectionByIp.updateCollections(collection);
        viewedCollectionByIp.updateIp(ip);

        return viewedCollectionByIp;

    }

    //==수정 메소드==//
    public void updateCollections(Collection collection) {
        this.collection = collection;
    }

    private void updateIp(String ip) {
        this.ip = ip;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
}
