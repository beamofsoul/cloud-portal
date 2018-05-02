package com.moraydata.general.primary.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ErrorProperties.IncludeStacktrace;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

/**
 * @ClassName ExceptionController
 * @Description A controller to handler all exceptions
 * @author MingshuJian
 * @Date 2017年8月28日 下午3:15:32
 * @version 1.0.0
 */
@Controller
@EnableConfigurationProperties({ ServerProperties.class })
public class ExceptionController implements ErrorController {

	private static final String ERROR_PATH = "/error"; //the path of the error page.
	
	@Autowired
	private ErrorAttributes errorAttributes; // the current error attributes.
	
	@Autowired
	private ServerProperties serverProperties; // the current server properties.
	
	@GetMapping(value = ERROR_PATH, produces = TEXT_HTML_VALUE)
	public ModelAndView handleHtmlError(HttpServletRequest request) {
		return new ModelAndView(ERROR_PATH, getErrorAttributes(request));
	}

	@GetMapping(value = ERROR_PATH, produces = APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> handleJsonError(HttpServletRequest request) {
		Map<String, Object> attibutes = getErrorAttributes(request);
		return new ResponseEntity<Map<String, Object>>(attibutes, HttpStatus.valueOf(Integer.valueOf(attibutes.get("status").toString())));
	}

	/**
	 * Determine if error attributes should include stack trace information.
	 * @param HttpServletRequest request - the source request
	 * @return boolean - stack trace information should be included or not
	 */
	protected boolean isIncludeStackTrace(final HttpServletRequest request) {
		IncludeStacktrace includeStacktrace = serverProperties.getError().getIncludeStacktrace();
		return ErrorProperties.IncludeStacktrace.ALWAYS.equals(includeStacktrace) ? true : 
			ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM.equals(includeStacktrace) ? getTraceValue(request) : false;
	}

	/**
	 * Returns a Map of the error attributes. The map can be used as the model of an error page ModelAndView, or returned as a ResponseBody.
	 * @param HttpServletRequest request - the source request
	 * @return Map<String, Object> - a map of error attributes
	 */
	private Map<String, Object> getErrorAttributes(final HttpServletRequest request) {
		return errorAttributes.getErrorAttributes(new DispatcherServletWebRequest(request), isIncludeStackTrace(request));
	}

	/**
	 * If there is a parameter "trace" in request and the value of it is true, then return true.
	 * Otherwise, return false.
	 * @param HttpServletRequest request - the source request
	 * @return boolean - a boolean of whether trace or not
	 */
	private boolean getTraceValue(final HttpServletRequest request) {
		String trace = request.getParameter("trace");
		return StringUtils.isNotBlank(trace) ? trace.toLowerCase().equals(Boolean.TRUE.toString()) : Boolean.FALSE.booleanValue();
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}
}
