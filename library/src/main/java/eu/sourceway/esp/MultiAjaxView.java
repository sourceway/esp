package eu.sourceway.esp;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

public class MultiAjaxView {

    private final Map<String, String> registry = new LinkedHashMap<>();
    private final String viewName;

    public MultiAjaxView(String viewName) {
        this.viewName = requireNonNull(viewName, "viewName may not be null");
    }

    public void registerView(String selector, String viewName) {
        requireNonNull(selector, "selector may not be null");
        requireNonNull(viewName, "viewName may not be null");
        registry.put(selector, viewName);
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, String> getRegistry() {
        return unmodifiableMap(registry);
    }
}
