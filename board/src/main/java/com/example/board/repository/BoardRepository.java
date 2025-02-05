package com.example.board.repository;

import com.example.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    //update board_table set board_hits=board_hits+1 where id=?
    //엔티티를 기준으로 쿼리를 수행하므로 엔티티 이름이 와야한다.
    @Modifying // @Query 어노테이션으로 쿼리 작업할때는 @Modifying 어노테이션을 필수로 붙여야한다.
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id")
    void updateHits(@Param("id") Long id);
}
