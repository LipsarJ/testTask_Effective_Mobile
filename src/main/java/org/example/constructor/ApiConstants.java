package org.example.constructor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {
    // Текстовки для успешных запросов
    public static final String HTTP_OK_200_DESCRIPTION = "Успешное выполнение операции";


    public static final String PUBLIC_API_PREFIX = "api/v1/public";
    public static final String PRIVATE_API_PREFIX = "api/v1/private";


    public static final String API_TASK_PATH = "/task";
    public static final String API_COMMENT_PATH = "/comment";

    public static final String TASK_ID = "taskId";
    public static final String COMMENT_ID = "commentId";

    public static final String TASK_ID_PATH = "/{" + TASK_ID + "}";
    public static final String COMMENT_ID_PATH = "/{" + COMMENT_ID + "}";

    public static final String PUBLIC_V1_TASKS = PUBLIC_API_PREFIX + API_TASK_PATH;
    public static final String PUBLIC_V1_COMMENTS = PUBLIC_V1_TASKS + TASK_ID_PATH + API_COMMENT_PATH;

    public static final String PRIVATE_V1_TASKS = PRIVATE_API_PREFIX + API_TASK_PATH;
    public static final String PRIVATE_V1_COMMENTS = PRIVATE_V1_TASKS + TASK_ID_PATH + API_COMMENT_PATH;
}
