package hu.feketefamily.fftodo.constants;

public class ErrorMessages {
	public static final String CONSTRAINT_VIOLATION_MESSAGE = "Constraint violation occurred";
	public static String BOARD_NOT_EXIST_MESSAGE(Long id, String name)
	{
		return "Board (id: " + id + ", name: \"" + name + "\") does not exist!";
	}
	public static String TODO_NOT_EXIST_MESSAGE(Long id, String name)
	{
		return "Todo (id: " + id + ", name: \"" + name + "\") does not exist!";
	}
	public static String TASK_NOT_EXIST_MESSAGE(Long id, String name)
	{
		return "Task (id: " + id + ", name: \"" + name + "\") does not exist!";
	}
	public static String TODO_PHASE_NOT_EXIST(Integer idx) { return "Phase index is out of range."; }
}
