package task_manager.server;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ProblemDetails {

    public static ProblemDetail invalidUUID(Object uuid) {
        ProblemDetail pd =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid UUID: " + uuid);
        pd.setTitle("Invalid UUID");
        pd.setType(URI.create("/task-manager/error/invalid-uuid"));
        return pd;
    }

    public static ProblemDetail taskNotFound(String uuid) {
        ProblemDetail pd =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "Task not found with UUID: " + uuid);
        pd.setTitle("Task not found");
        pd.setType(URI.create("/task-manager/error/task-not-found"));
        return pd;
    }

    public static ProblemDetail internalServerError() {
        ProblemDetail pd =
            ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal server error");
        pd.setType(URI.create("/task-manager/error/internal-server-error"));
        return pd;
    }

    public static ProblemDetail uuidInPostRequest() {
        ProblemDetail pd =
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("UUID in POST request");
        pd.setType(URI.create("/task-manager/error/uuid-in-post-request"));
        return pd;
    }

    public static ProblemDetail noUuidInPutRequest() {
        ProblemDetail pd =
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("No UUID in PUT request");
        pd.setType(URI.create("/task-manager/error/no-uuid-in-put-request"));
        return pd;
    }

    public static ProblemDetail propertyTypeMismatch() {
        ProblemDetail pd =
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("UUID in POST request");
        pd.setType(URI.create("/task-manager/error/uuid-in-post-request"));
        return pd;
    }

    public static ProblemDetail tagNotFound(String uuid) {
        ProblemDetail pd =
            ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "Tag not found with UUID: " + uuid);
        pd.setTitle("Tag not found");
        pd.setType(URI.create("/task-manager/error/tag-not-found"));
        return pd;
    }

}
