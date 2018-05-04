package eu.sourceway.esp.interceptor;

import eu.sourceway.esp.configuration.EspProperties;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EspAjaxLayoutInterceptor extends HandlerInterceptorAdapter {

    public static final String ESP_AJAX_LAYOUT = "esp:ajax";

    private final EspProperties properties;

    public EspAjaxLayoutInterceptor(EspProperties properties) {
        this.properties = properties;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null || !modelAndView.hasView() || isLayoutDisabled(handler)) {
            return;
        }
        String originalViewName = modelAndView.getViewName();
        if (isRedirectOrForward(originalViewName)) {
            return;
        }

        if (isAjaxRequest(request)) {
            modelAndView.setViewName(ESP_AJAX_LAYOUT);
            // Unfortunately we have to set the Cache-Control header
            // due to Chrome only using the cached response
            // if we come back to our page from an external site
            // see: https://stackoverflow.com/a/8568402/1659588
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("X-ESP-CURRENT-URL", buildCurrentUrl(request));
            if (hasCsrfToken()) {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
                if (csrfToken != null) {
                    response.addHeader("X-ESP-CSRF-TOKEN", csrfToken.getToken());
                }
            }
        } else {
            modelAndView.setViewName(getLayoutName(handler));
        }

        modelAndView.addObject(properties.getViewAttributeName(), originalViewName);
        modelAndView.addObject("ESP_VIEW_VAR_NAME", properties.getViewAttributeName());
        modelAndView.addObject("ESP_FRAGMENT_NAME", properties.getContentFragmentName());
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return null != request.getHeader("X-ESP-AJAX-REQUEST");
    }

    private boolean hasCsrfToken() {
        try {
            Class.forName("org.springframework.security.web.csrf.CsrfToken");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private String buildCurrentUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            return request.getRequestURI();
        } else {
            return request.getRequestURI() + "?" + queryString;
        }
    }

    private boolean isLayoutDisabled(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Layout layout = getMethodOrTypeAnnotation(handlerMethod);
        return layout != null && layout.disabled();
    }

    private String getLayoutName(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Layout layout = getMethodOrTypeAnnotation(handlerMethod);
        if (layout == null || layout.value().trim().isEmpty()) {
            return properties.getDefaultLayout();
        } else {
            return layout.value();
        }
    }

    private Layout getMethodOrTypeAnnotation(HandlerMethod handlerMethod) {
        Layout layout = handlerMethod.getMethodAnnotation(Layout.class);
        if (layout == null) {
            return handlerMethod.getBeanType().getAnnotation(Layout.class);
        }
        return layout;
    }

    private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }
}
