package hu.feketefamily.fftodo.constants;

public class TodoCommon {
	public static final int maxBoardNameLength = 64;
	public static final int maxBoardDescriptionLength = 1024;
	public static final int maxBoardAuthorLength = 128;

	public static final int maxTodoNameLength = 128;
	public static final int maxTodoDescriptionLength = 1024;

	public static final int maxTaskNameLength = 32;

	public static final int phaseMin = 0;
	public static final int phaseMax = 2;

	public static final String todoCloneSuffix = " (cloned)";

	public static final String boardPath = "/ff-todo/board";
	public static final String todoPath = "/ff-todo/todo";
	public static final String taskPath = "/ff-todo/task";

	public static String boardTodoPath(Long id) {
		return boardPath + "/" + id + "/todo";
	}

	public static String todoTaskPath(Long id) {
		return todoPath + "/" + id + "/task";
	}
}
