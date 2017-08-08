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

    function loadContent(target, url) {
        var $ajax = $.ajax({
            url: url,
            dataType: "html",
            headers: {
                'X-ESP-AJAX-REQUEST': true
            },
            beforeSend: config.beforeSendCallback
        }).fail(function (xhr) {
            $(target).html(xhr.responseText);
            updateCsrf(xhr);
        }).done(function (data, textStatus, request) {
            $("<div>" + data + "</div>").find("div[data-esp-target-selector]").each(function () {
                var $this = $(this);
                var selector = $this.attr("data-esp-target-selector");
                if (selector === '$default$') {
                    $(target).html($this.html());
                } else {
                    $(selector).html($this.html());
                }
            });

            var redirectedLocation = request.getResponseHeader("X-ESP-CURRENT-URL");
            if (history.state.url !== redirectedLocation) {
                history.replaceState({url: redirectedLocation}, null, redirectedLocation);
            }
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

    function updateCsrf(request) {
        var csrfToken = request.getResponseHeader("X-ESP-CSRF-TOKEN");
        if (csrfToken !== undefined) {
            $("input[name=_csrf]").val(csrfToken);
        }
    }

    function goToUrl(href, content) {
        if ('pushState' in window.history) {
            history.pushState({url: href}, null, href);

            if (content === undefined) {
                loadContent(config.contentSelector, href);
            } else {
                $(config.contentSelector).html(content);
            }
        } else {
            window.location = href;
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

    function formSubmit(form, doneHandler, failHandler) {
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
        });
        if (typeof(failHandler) === "function") {
            $ajax.fail(failHandler);
        }
        if (typeof(doneHandler) === "function") {
            $ajax.done(doneHandler);
        }
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

        var form = $(e.target);
        var targetUrl = form.attr('action');
        formSubmit(form, function (data, textStatus, request) {
            goToUrl(request.getResponseHeader("X-ESP-CURRENT-URL"), data);
        }, function (e) {
            goToUrl(targetUrl, e.responseText);
        });

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
        goToUrl(href);
        return false;
    });

    var currentLocation = document.location.toString();
    history.replaceState({url: currentLocation}, null, currentLocation);

    $(window).on("popstate", function (e) {
        if (history.state !== null) {
            loadContent(config.contentSelector, history.state.url);
        }
    });
}(jQuery));
