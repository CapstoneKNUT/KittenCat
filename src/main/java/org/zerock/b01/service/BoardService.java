package org.zerock.b01.service;

import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface BoardService {

    // 리뷰 등록
    Long register(BoardDTO boardDTO);

    // 리뷰 읽기
    BoardDTO readOne(Long bno);

    // 리뷰 수정
    void modify(BoardDTO boardDTO);

    // 리뷰 삭제
    void remove(Long bno);

    // 리뷰 목록 조회
    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);

    // 게시글의 이미지와 댓글의 숫자까지 처리
    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    default Board dtoToEntity(BoardDTO boardDTO) {
        Board board = Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .build();

        if (boardDTO.getFileNames() != null) {
            boardDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            });
        }
        return board;
    }

    default BoardDTO entityToDTO(Board board) {
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();

        List<String> fileNames = board.getImageSet().stream().sorted()
                .map(boardImage -> boardImage.getUuid() + "_" + boardImage.getFileName())
                .collect(Collectors.toList());

        boardDTO.setFileNames(fileNames);
        return boardDTO;
    }
}
