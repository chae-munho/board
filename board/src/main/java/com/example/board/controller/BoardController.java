package com.example.board.controller;
import com.example.board.dto.BoardDTO;
import com.example.board.entity.BoardEntity;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor // final 필드에 대한 생성자를 생성 di
@RequestMapping(value = "/board") // 기본 경로를 /board로 설정
public class BoardController {

    private final BoardService boardService;
    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) {  //@ModelAttribute는 @RequestMapping 메소드가 붙여진 Controller 클래스에 지원된다.
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO);
        return "index";
    }
    @GetMapping("/")
    public String findAll(Model model) {
        // DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "list";
    }
    @GetMapping("/{id}")
    public String findById(@PathVariable(value = "id") Long id, Model model, @PageableDefault(page = 1) Pageable pageable) {
        /*
            해당 게시글의 조회수를 하나 올리고 게시글 데이터를 가져와 detail.html에 출력
         */
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber()); // detail.html에 페이지 넘버를 넘기기 위한 것
        return "detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO board =  boardService.update(boardDTO);
        model.addAttribute("board", board);
        //return "redirect:/board" + boardDTO.getId();  // 해당 코드로 리턴하면 조회수가 또 증가함
        return "detail"; // 조회수 증가 반영이 안됨
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        boardService.delete(id);
        return "redirect:/board/"; //list.html으로 리디렉션
    }
    //  /board/paging?page=1   @PageableDefault(page = 1)은 기본으로 page=1로 설정하게 해준다
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        //pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        // page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 3 4 5
        // 현재 사용자가 7페이지 7 8 9
        // 보여지는 페이지 갯수 8개
        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
     
        return "paging";


    }

}

