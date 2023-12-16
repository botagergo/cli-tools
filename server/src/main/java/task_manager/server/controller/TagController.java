package task_manager.server.controller;

import com.google.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task_manager.task_logic.use_case.label.LabelUseCase;
import task_manager.server.ProblemDetails;

@RestController
@RequestMapping(path = "${apiPrefix}/tags")
@Log4j2
public class TagController {

	@Inject
	public TagController(LabelUseCase labelUseCase) {
		this.labelUseCase = labelUseCase;
	}

	/*
	@GetMapping
	public Object getTags(@RequestParam(value = "query", defaultValue = "") String query) {
		try {
			if (!query.isEmpty()) {
				return labelUseCase.findTag(query);
			} else {
				return labelUseCase.getTags();
			}
		} catch (Exception e) {
			return handleInternalServerError(e);
		}
	}

	@GetMapping("{uuid}")
	public Object getTag(@PathVariable("uuid") String uuid) {
		try {
			Tag tag = labelUseCase.getTag(UUID.fromString(uuid));
			if (tag == null) {
				handleTagNotFound(uuid);
			}
			return tag;
		} catch (IllegalArgumentException e) {
			return handleInvalidUUID(uuid, e);
		} catch (Exception e) {
			return handleInternalServerError(e);
		}
	}

	@PostMapping(consumes = "application/json")
	public Object postTag(@RequestBody Tag tag) {
		try {
			if (tag.uuid() != null) {
				handleUuidInPostRequest();
			}
			return labelUseCase.addTag(tag.name());
		} catch (Exception e) {
			return handleInternalServerError(e);
		}
	}

	@PutMapping(consumes = "application/json")
	public Object putTag(@RequestBody Tag tag) {
		try {
			UUID uuid = tag.uuid();
			if (uuid == null) {
				return handleNoUuidInPutRequest();
			}

			if (labelUseCase.update(tag) == null) {
				return handleTagNotFound(uuid.toString());
			}
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return handleInternalServerError(e);
		}
	} */

	@DeleteMapping(value = "{uuid}", consumes = "application/json")
	public Object deleteTag(@PathVariable String uuid) {
		try {
			// boolean result = labelUseCase.delete(UUID.fromString(uuid));
			boolean result = false;
			if (!result) {
				return handleTagNotFound(uuid);
			}
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return handleInvalidUUID(uuid, e);
		} catch (Exception e) {
			return handleInternalServerError(e);
		}
	}

	private <T> T handleInvalidUUID(String uuid, Exception e) {
		log.debug("Invalid UUID in request: {}", uuid);
		throw new ErrorResponseException(HttpStatus.BAD_REQUEST,
			ProblemDetails.invalidUUID(uuid), e);
	}

	private <T> T handleTagNotFound(String uuid) {
		log.debug("Tag not found: {}", uuid);
		throw new ErrorResponseException(HttpStatus.NOT_FOUND,
			ProblemDetails.tagNotFound(uuid), null);
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


	private <T> T handleNoUuidInPutRequest() {
		log.debug("UUID was not found in PUT request");
		throw new ErrorResponseException(HttpStatus.BAD_REQUEST,
			ProblemDetails.noUuidInPutRequest(), null);
	}

	final LabelUseCase labelUseCase;

}
