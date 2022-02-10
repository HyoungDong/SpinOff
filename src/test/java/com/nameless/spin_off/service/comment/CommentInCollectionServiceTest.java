package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.comment.NotSearchCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CommentInCollectionServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired LikedPostRepository likedPostRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInCollectionRepository commentInCollectionRepository;
    @Autowired CommentInCollectionService commentInCollectionService;
    @Autowired EntityManager em;

    @Test
    public void saveCommentInCollectionByCommentVO() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Collection collection = Collection.createDefaultCollection(member);
        collectionRepository.save(collection);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        CommentInCollection comment = commentInCollectionService.insertCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(member.getId(), collection.getId(), null, "야스히로 라할살"));

        System.out.println("컬렉션업로드");
        Collection newCollection = collectionRepository.getById(collection.getId());

        //then
        assertThat(newCollection.getCommentCount()).isEqualTo(newCollection.getCommentInCollections().size());
        assertThat(newCollection.getCommentInCollections().get(newCollection.getCommentInCollections().size() - 1)).isEqualTo(comment);
    }

    @Test
    public void 대댓글_테스트() throws Exception{
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        CommentInCollection parent = CommentInCollection.createCommentInCollection(mem, "야스히로 라할살", null);
        col.addCommentInCollection(parent);
        commentInCollectionRepository.save(parent);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수1");
        CommentInCollection childComment1 = commentInCollectionService.insertCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(mem.getId(), col.getId(), parent.getId(), "요지스타 라할살"));

        System.out.println("서비스함수2");
        CommentInCollection childComment2 = commentInCollectionService.insertCommentInCollectionByCommentVO(new CreateCommentInCollectionVO(mem.getId(), col.getId(), parent.getId(), "슈퍼스타검흰 라할살"));

        System.out.println("부모댓글업로드");
        CommentInCollection parentComment = commentInCollectionRepository.findById(parent.getId()).get();

        System.out.println("컬렉션업로드");
        Collection collection = collectionRepository.getById(col.getId());

        //then
        assertThat(collection.getCommentCount()).isEqualTo(collection.getCommentInCollections().size());
        assertThat(collection.getCommentInCollections().size()).isEqualTo(3);
        assertThat(parentComment.getChildren().size()).isEqualTo(2);
        assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment1);
        assertThat(parentComment.getChildren().get(1)).isEqualTo(childComment2);
        assertThat(childComment1.getParent()).isEqualTo(parentComment);
        assertThat(childComment2.getParent()).isEqualTo(parentComment);
    }

    @Test
    public void 댓글_저장시_파라미터_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);

        CreateCommentInCollectionVO commentInCollectionVO1 =
                new CreateCommentInCollectionVO(0L, 0L, 0L, "");

        CreateCommentInCollectionVO commentInCollectionVO2 =
                new CreateCommentInCollectionVO(mem.getId(), 0L, 0L, "");

        CreateCommentInCollectionVO commentInCollectionVO3 =
                new CreateCommentInCollectionVO(mem.getId(), col.getId(), 0L, "");

        //when

        //then
        assertThatThrownBy(() -> commentInCollectionService.insertCommentInCollectionByCommentVO(commentInCollectionVO1))
                .isInstanceOf(NotSearchMemberException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInCollectionService.insertCommentInCollectionByCommentVO(commentInCollectionVO2))
                .isInstanceOf(NotSearchCollectionException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInCollectionService.insertCommentInCollectionByCommentVO(commentInCollectionVO3))
                .isInstanceOf(NotSearchCommentInCollectionException.class);//.hasMessageContaining("")

    }

}