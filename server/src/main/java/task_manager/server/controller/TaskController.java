package task_manager.server.controller;

import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import com.google.inject.Inject;
import lombok.extern.log4j.Log4j2;
import task_manager.logic.use_case.PropertyDescriptorUseCase;
import task_manager.logic.use_case.TaskUseCase;
import task_manager.server.ProblemDetails;
import task_manager.data.Task;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyDescriptorCollection;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;

@RestController
@RequestMapping(path = "${apiPrefix}/tasks")
@Log4j2
public class TaskController {

	@Inject
	public TaskController(PropertyDescriptorUseCase propertyDescriptorUseCase,
		TaskUseCase taskUseCase, PropertyManager propertyManager)
		throws IOException {
		PropertyDescriptorCollection propertyDescriptors =
			propertyDescriptorUseCase.getPropertyDescriptors();

		if (propertyDescriptors.get("uuid") == null) {
			propertyDescriptorUseCase.createPropertyDescriptor(
				new PropertyDescriptor("name", PropertyDescriptor.Type.String, false, ""));
			propertyDescriptorUseCase.createPropertyDescriptor(
				new PropertyDescriptor("uuid", PropertyDescriptor.Type.UUID, false, ""));
			propertyDescriptorUseCase.createPropertyDescriptor(
				new PropertyDescriptor("done", PropertyDescriptor.Type.Boolean, false, false));
			propertyDescriptorUseCase.createPropertyDescriptor(
				new PropertyDescriptor("tags", PropertyDescriptor.Type.UUID, true, List.of()));
			propertyDescriptorUseCase.createPropertyDescriptor(
				new PropertyDescriptor("status", PropertyDescriptor.Type.UUID, false, null));
		}

		this.taskUseCase = taskUseCase;
		this.propertyManager = propertyManager;
	}

	@GetMapping
	public Object getTasks(@RequestParam(value = "query", defaultValue = "") String query) {
		try {
			return taskUseCase.getTasks(query);
		} catch (PropertyException e) {
			return null;
		}
		catch (IOException e) {
			return handleInternalServerError(e);
		}
	}

	@GetMapping("{uuid}")
	public Object getTask(@PathVariable("uuid") String uuid) {
		try {
			Task task = taskUseCase.getTask(UUID.fromString(uuid));
			if (task == null) {
				return handleTaskNotFound(uuid);
			}
			return task;
		} catch (IllegalArgumentException e) {
			return handleInvalidUUID(uuid, e);
		} catch (IOException e) {
			return handleInternalServerError(e);
		}
	}

	@PostMapping(consumes = "application/json")
	public Object postTask(@RequestBody Task task) {
		try {
			if (propertyManager.hasRawProperty(task, "uuid")) {
				handleUuidInPostRequest();
			}
			return taskUseCase.addTask(task);
		} catch (IOException e) {
			return handleInternalServerError(e);
		}
	}

	@PutMapping(consumes = "application/json")
	public Object putTask(@RequestBody Task task) {
		try {
			if (!propertyManager.hasRawProperty(task, "uuid")) {
				handleNoUuidInPutRequest();
			}
			UUID uuid = task.getUUID();
			if (taskUseCase.modifyTask(task) == null) {
				return handleTaskNotFound(uuid.toString());
			}
			return ResponseEntity.ok().build();
		} catch (IOException e) {
			return handleInternalServerError(e);
		}
	}

	@DeleteMapping(value = "{uuid}", consumes = "application/json")
	public Object deleteTask(@PathVariable String uuid) {
		try {
			boolean result = taskUseCase.deleteTask(UUID.fromString(uuid));
			if (!result) {
				return handleTaskNotFound(uuid);
			}
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return handleInvalidUUID(uuid, e);
		} catch (IOException e) {
			return handleInternalServerError(e);
		}
	}

	private <T> T handleInvalidUUID(String uuid, Exception e) {
		log.debug("Invalid UUID in request: {}", uuid);
		throw new ErrorResponseException(HttpStatus.BAD_REQUEST,
			ProblemDetails.invalidUUID(uuid), e);
	}

	private <T> T handleTaskNotFound(String uuid) {
		log.debug("Task not found: {}", uuid);
		throw new ErrorResponseException(HttpStatus.NOT_FOUND,
			ProblemDetails.taskNotFound(uuid), null);
	}

	private <T> T handleInternalServerError(Exception e) {
		log.error("{}\n{}", e.toString(), ExceptionUtils.getStackTrace(e));
		throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR,
			ProblemDetails.internalServerError(), e);
	}

	@SuppressWarnings("UnusedReturnValue")
	private <T> T handleUuidInPostRequest() {
		log.debug("UUID was found in POST request");
		throw new ErrorResponseException(HttpStatus.BAD_REQUEST,
			ProblemDetails.uuidInPostRequest(), null);
	}

	@SuppressWarnings("UnusedReturnValue")
	private <T> T handleNoUuidInPutRequest() {
		log.debug("UUID was not found in PUT request");
		throw new ErrorResponseException(HttpStatus.BAD_REQUEST,
			ProblemDetails.noUuidInPutRequest(), null);
	}

	TaskUseCase taskUseCase;

	private final PropertyManager propertyManager;

}
