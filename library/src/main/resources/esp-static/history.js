(function ($) {
    if (!('pushState' in window.history)) {
        return true;
    }
    var hasPushState = 'pushState' in window.history;

    var defaults = {
        externalUrlPattern: /^(\w+:)?\/\//,
        contentSelector: "#content",
        beforeSendCallback: null,
        alwaysCallback: null,
        failCallback: null,
        doneCallback: null
    };

    var config = defaults;

    window.ESP = {
        options: function (options) {
            config = $.extend({}, defaults, options);
        }
    };

    if (!hasPushState) {
        // Session history management not available, ESP will do nothing.
        return;
    }

    function isValid(el, tagName, attribute) {
        return el.tagName.toUpperCase() === tagName.toUpperCase() && $(el).attr(attribute) !== undefined;
    }

    function getTargetUrl(el) {
        if (isValid(el, 'A', 'href')) {
            return $(el).attr('href');
        }
        if (isValid(el, 'FORM', 'action')) {
            return $(el).attr('action');
        }
        return '';
    }

    $.expr[':'].external = function (el) {
        var href = getTargetUrl(el);
        return href !== undefined && href.search(config.externalUrlPattern) !== -1;
    };

    $.expr[':'].internal = function (el) {
        if (isValid(el, 'A', 'href') || isValid(el, 'FORM', 'action')) {
            return !$.expr[':'].external(el);
        }
        return false;
    };

    function loadContent(url) {
        var $ajax = $.ajax({
            url: url,
            dataType: "html",
            headers: {
                'X-ESP-AJAX-REQUEST': true
            },
            beforeSend: config.beforeSendCallback
        }).fail(function (xhr) {
            setContent(url, xhr.responseText);
            updateCsrf(xhr);
        }).done(function (data, textStatus, request) {
            setContent(request.getResponseHeader("X-ESP-CURRENT-URL"), data);
            updateCsrf(request);
        });
        if (typeof(config.alwaysCallback) === "function") {
            $ajax.always(config.alwaysCallback);
        }
        if (typeof(config.doneCallback) === "function") {
            $ajax.done(config.doneCallback);
        }
        if (typeof(config.failCallback) === "function") {
            $ajax.fail(config.failCallback);
        }
    }

    function setContent(url, data) {
        history.pushState({url: url}, null, url);

        $("<div>" + data + "</div>").find("div[data-esp-target-selector]").each(function () {
            var $this = $(this);
            var selector = $this.attr("data-esp-target-selector");
            if (selector === '$default$') {
                $(config.contentSelector).html($this.html());
            } else {
                $(selector).html($this.html());
            }
        });
    }

    function updateCsrf(request) {
        var csrfToken = request.getResponseHeader("X-ESP-CSRF-TOKEN");
        if (csrfToken !== undefined) {
            $("input[name=_csrf]").val(csrfToken);
        }
    }

    function extractFormData(formMethod, form) {
        if (formMethod === "post") {
            // use FormData for POST forms, so we can support file uploads
            var data = new FormData();
            // set values of normal fields
            $.each(form.serializeArray(), function (i, el) {
                data.append(el.name, el.value);
            });
            // set fields for file upload
            form.find("input[type='file']").each(function (i, element) {
                if (element.files.length > 0) {
                    var fieldName = $(element).attr('name');
                    $.each(element.files, function (i, file) {
                        data.append(fieldName, file);
                    });
                }
            });
            return data;
        } else {
            // if not POST assume GET and just serialize the form to text
            var fields = [];
            $.each(form.serializeArray(), function (i, el) {
                if (el.value !== "") {
                    fields.push(el.name + '=' + el.value);
                }
            });
            return fields.join("&");
        }
    }

    function formSubmit(form) {
        var targetUrl = form.attr('action');
        var formMethod = form.attr('method');
        if (formMethod === undefined) {
            formMethod = 'post';
        }

        var data = extractFormData(formMethod, form);

        var $ajax = $.ajax({
            'type': formMethod,
            'url': targetUrl,
            'data': data,
            dataType: "html",
            processData: false,
            contentType: false,
            headers: {
                'X-ESP-AJAX-REQUEST': true
            },
            beforeSend: config.beforeSendCallback
        }).done(function (data, textStatus, request) {
            setContent(request.getResponseHeader("X-ESP-CURRENT-URL"), data);
        }).fail(function (e) {
            setContent(targetUrl, e.responseText);
        });

        if (typeof(config.alwaysCallback) === "function") {
            $ajax.always(config.alwaysCallback);
        }
        if (typeof(config.doneCallback) === "function") {
            $ajax.done(config.doneCallback);
        }
        if (typeof(config.failCallback) === "function") {
            $ajax.fail(config.failCallback);
        }
    }

    $(document).on("submit", "form:not([data-esp-ajax='false']):internal", function (e) {
        e.preventDefault();
        formSubmit($(e.target));
        return false;
    });

    $(document).on("click", "a:not([data-esp-ajax='false']):internal", function (e) {
        var href = $(this).attr('href');
        if (href.indexOf('#') === 0) {
            return true;
        }
        // Ensure middle, control and command clicks act normally
        if (e.which === 2 || e.metaKey || e.ctrlKey) {
            return true;
        }
        loadContent(href);
        return false;
    });

    var currentLocation = document.location.toString();
    history.replaceState({url: currentLocation}, null, currentLocation);

    $(window).on("popstate", function (e) {
        if (history.state !== null) {
            loadContent(history.state.url);
        }
    });
}(jQuery));
