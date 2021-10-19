package hu.feketefamily.fftodo.unittests;

import hu.feketefamily.fftodo.model.repository.BoardRepository;
import hu.feketefamily.fftodo.service.BoardService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class BoardServiceUnitTests {
	private static final Long TEST_BOARD_ID = 1L;

	@InjectMocks
	private BoardService boardService;

	@Mock
	private BoardRepository boardRepository;

	@Test
	void shouldRemoveExistingBoard() {
		when(boardRepository.existsById(TEST_BOARD_ID)).thenReturn(true);
		doNothing().when(boardRepository).deleteById(TEST_BOARD_ID);

		boardService.removeBoard(TEST_BOARD_ID);

		verify(boardRepository).deleteById(TEST_BOARD_ID);
	}

	@Test
	void shouldNotRemoveNonExistingBoard() {
		when(boardRepository.existsById(TEST_BOARD_ID)).thenReturn(false);

		boardService.removeBoard(TEST_BOARD_ID);

		verify(boardRepository, times(0)).deleteById(TEST_BOARD_ID);
	}
}
