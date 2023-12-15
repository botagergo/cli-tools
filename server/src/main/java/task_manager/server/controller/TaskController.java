package task_manager.server.controller;

import com.google.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import task_manager.core.data.Task;
import task_manager.logic.filter.FilterCriterionException;
import task_manager.logic.use_case.task.PropertyConverterException;
import task_manager.logic.use_case.task.TaskUseCase;
import task_manager.logic.use_case.task.TaskUseCaseException;
import task_manager.server.ProblemDetails;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(path = "${apiPrefix}/tasks")
@Log4j2
public class TaskController {

	@Inject
	public TaskController(TaskUseCase taskUseCase, PropertyManager propertyManager) {
		this.taskUseCase = taskUseCase;
		this.propertyManager = propertyManager;
	}

	@GetMapping
	public Object getTasks() {
		try {
			return taskUseCase.getTasks(null, null, null, null, null);
		} catch (PropertyException | PropertyConverterException e) {
			return null;
		} catch (IOException e) {
			return handleInternalServerError(e);
		} catch (FilterCriterionException | TaskUseCaseException e) {
			throw new RuntimeException(e);
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
			if (propertyManager.hasProperty(task, "uuid")) {
				handleUuidInPostRequest();
			}
			return taskUseCase.addTask(task);
		} catch (IOException e) {
			return handleInternalServerError(e);
		}
	}

	@PutMapping(consumes = "application/json")
	public Object putTask(@RequestBody Task task) throws TaskUseCaseException {
		try {
			if (!propertyManager.hasProperty(task, "uuid")) {
				handleNoUuidInPutRequest();
			}
			taskUseCase.modifyTask(task);
			return ResponseEntity.ok().build();
		} catch (IOException e) {
			return handleInternalServerError(e);
		}
	}

	@DeleteMapping(value = "{uuid}", consumes = "application/json")
	public Object deleteTask(@PathVariable String uuid) throws TaskUseCaseException {
		try {
			taskUseCase.deleteTask(UUID.fromString(uuid));
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
