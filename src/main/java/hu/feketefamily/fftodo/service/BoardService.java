package hu.feketefamily.fftodo.service;

import hu.feketefamily.fftodo.constants.TodoCommon;
import hu.feketefamily.fftodo.exception.NotExistException;
import hu.feketefamily.fftodo.model.entity.Board;
import hu.feketefamily.fftodo.model.repository.BoardRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Date;
import java.util.Set;

import static hu.feketefamily.fftodo.constants.ErrorMessages.BOARD_NOT_EXIST_MESSAGE;

@Log4j2
@Service
@Validated
public class BoardService {
	@Autowired
	private BoardRepository boardRepository;

	public Board getBoard(Long id) {
		return boardRepository.findById(id).orElseThrow(() -> new NotExistException(BOARD_NOT_EXIST_MESSAGE(id, "")) );
	}

	public Set<Long> getBoardsId() {
		return boardRepository.getAllIds();
	}

	public Board addBoard(@Valid Board board)
	{
		Date now=new Date(); Board newBoard;
		board.setDateCreated(now);
		board.setReadonlyTodos(false);
		board.setReadonlyTasks(false);
		newBoard = boardRepository.save(board);
		log.info("Saved new Board: {{}}", newBoard.toString());
		return newBoard;
	}

	public void removeBoard(Long id) {
		if (boardRepository.existsById(id)) {
			log.info("Deleting Board with id: {{}}", id);
			boardRepository.deleteById(id);
		} else {
			log.warn("No Boards were deleted with id {{}}", id);
		}
	}

	@Transactional
	public void updateBoard(Long id, @Valid Board patchedBoard) {
		if (boardRepository.updateById(id, patchedBoard.getName(), patchedBoard.getDescription(), patchedBoard.getAuthor()) >= 1)
		{
			log.info("Successfully updated Board with id {{}}: {}", id, patchedBoard);
		}
		else
		{
			log.warn("No Boards were updated with id {{}}", id);
		}
	}

	public Integer getNameMaxLength() {
		Integer maxLength=TodoCommon.maxBoardNameLength;
		log.info("Querying maximum name length setting for all Boards: {}", maxLength);
		return maxLength;
	}
	public Integer getDescriptionMaxLength() {
		Integer maxLength=TodoCommon.maxBoardDescriptionLength;
		log.info("Querying maximum description length setting for all Boards: {}", maxLength);
		return maxLength;
	}
	public Integer getAuthorMaxLength() {
		Integer maxLength=TodoCommon.maxBoardAuthorLength;
		log.info("Querying maximum author length setting for all Boards: {}", maxLength);
		return maxLength;
	}

	public Boolean isReadonlyTodos(Long id) {
		Board board = getBoard(id);
		Boolean readonly = board.getReadonlyTodos();
		log.info("Querying Read-only Todos setting for Board with id {{}}: {}", id, readonly);
		return readonly;
	}

	@Transactional
	public void setReadonlyTodos(Long id, Boolean readonly) {
		if (boardRepository.updateReadonlyTodos(id, readonly) >= 1)
		{
			log.info("Successfully updated Read-only Todos setting for Board with id {{}} to {}", id, readonly);
		}
	}

	public Boolean isReadonlyTasks(Long id) {
		Board board = getBoard(id);
		Boolean readonly = board.getReadonlyTasks();
		log.info("Querying Read-only Tasks setting for Board with id {{}}: {}", id, readonly);
		return readonly;
	}

	@Transactional
	public void setReadonlyTasks(Long id, Boolean readonly) {
		if (boardRepository.updateReadonlyTasks(id, readonly) >= 1)
		{
			log.info("Successfully updated Read-only Tasks setting for Board with id {{}} to {}", id, readonly);
		}
	}
}
