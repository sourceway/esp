package eu.sourceway.esp.handler;

import eu.sourceway.esp.MultiAjaxView;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MultiAjaxViewHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private static final String ESP_VIEW_REGISTRY = "ESP_VIEW_REGISTRY";

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return MultiAjaxView.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        MultiAjaxView multiAjaxView = (MultiAjaxView) returnValue;
        mavContainer.setViewName(multiAjaxView.getViewName());
        mavContainer.addAttribute(ESP_VIEW_REGISTRY, multiAjaxView.getRegistry());
    }
}
