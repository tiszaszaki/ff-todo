package hu.feketefamily.fftodo.constants;

import java.util.HashMap;
import java.util.Map;

public class TodoCommon {
	public static final int maxBoardNameLength = 64;
	public static final int maxBoardDescriptionLength = 1024;
	public static final int maxBoardAuthorLength = 128;

	public static final int maxTodoNameLength = 128;
	public static final int maxTodoDescriptionLength = 1024;

	public static final int maxTaskNameLength = 32;

	public static final int todoPhaseMin = 0;
	public static final int todoPhaseMax = 2;

	public static String getTodoPhaseName(Integer idx)
	{
		String result = "";
		Map<Integer, String> todoPhaseNames = new HashMap<>();

		todoPhaseNames.put(0, "Backlog");
		todoPhaseNames.put(1, "In progress");
		todoPhaseNames.put(2, "Done");

		if ((idx >= todoPhaseMin) && (idx <= todoPhaseMax))
			result = todoPhaseNames.get(idx);

		return result;
	}

	public static final String fieldTruncateStr = "...";

	public static final String todoCloneSuffix = " (cloned)";

	public static final String boardPath = "/ff-todo/board";
	public static final String todoPath = "/ff-todo/todo";
	public static final String taskPath = "/ff-todo/task";
	public static final String pivotPath = "/ff-todo/pivot";

	public static String boardTodoPath(Long id) {
		return boardPath + "/" + id + "/todo";
	}

	public static String todoTaskPath(Long id) {
		return todoPath + "/" + id + "/task";
	}
}
