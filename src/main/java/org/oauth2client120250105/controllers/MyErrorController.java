package org.oauth2client120250105.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.oauth2client120250105.dtos.ErrorObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
public class MyErrorController implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(
            value = "/error",
            method = {RequestMethod.GET, RequestMethod.POST,
                    RequestMethod.DELETE, RequestMethod.PUT})
    public String handleError(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        // Получаем атрибуты ошибки
        Map<String, Object> errorAttributes = getErrorAttributes(request);
        // Добавляем в атрибуты ошибки метод запроса
        errorAttributes.put("requestMethod", request.getMethod());
        // Создаем объект ошибки
        ErrorObject errorObject = new ErrorObject(
                response.getStatus(),
                errorAttributes);

        // Помещаем объект ошибки в модель
        model.addAttribute("errorObject", errorObject);

        // Возвращаем страницу ошибки
        return "error";
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);
        return errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.STATUS,
                        ErrorAttributeOptions.Include.ERROR,
                        ErrorAttributeOptions.Include.EXCEPTION,
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.BINDING_ERRORS,
                        ErrorAttributeOptions.Include.STACK_TRACE,
                        ErrorAttributeOptions.Include.PATH)
        );
    }
}
