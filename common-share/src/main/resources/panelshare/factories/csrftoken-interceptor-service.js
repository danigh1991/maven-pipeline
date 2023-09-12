'use strict';

angular.module('emixApp.factories').factory('CsrfTokenInterceptorService', ['$q', function CsrfTokenInterceptorService($q) {
        // Private constants.
        //var CSRF_TOKEN_HEADER = 'X-CSRF-TOKEN',
        var CSRF_TOKEN_HEADER = 'X-XSRF-TOKEN',
            CSRF_TOKEN_HEADER_FOR_SEND = 'X-XSRF-TOKEN',
            HTTP_TYPES_TO_ADD_TOKEN = ['DELETE', 'POST', 'PUT'];

        // Private properties.
        var token;

        // Public interface.
        return {
            response: onSuccess,
            responseError: onFailure,
            request: onRequest,
        };

        // Private functions.
        function onFailure(response) {
            if (response.status === 403) {
                console.log('Request forbidden. Ensure CSRF token is sent for non-idempotent requests.');
            }
            else {
                var newToken = response.headers(CSRF_TOKEN_HEADER);
                if (newToken) {
                    token = newToken;
                    var date = new Date();
                    var minutes = 20;
                    date.setTime(date.getTime() + (minutes * 60 * 1000));
                    $.cookie(CSRF_TOKEN_HEADER_FOR_SEND, token, { expires: date, path: '/' });
                }
            }
            return $q.reject(response);
        }

        function onRequest(config) {

            // check Language Conflict (change language in another tab)
            if(sessionStorage.getItem(siteConfig.langCookieName) && $.cookie(siteConfig.langCookieName) !== sessionStorage.getItem(siteConfig.langCookieName)) {
                $.cookie(siteConfig.langCookieName, sessionStorage.getItem(siteConfig.langCookieName), { path: '/'});
                console.log("your language cookie sysc with tab language");
                //return $q.reject('change language in another tab');
            }
            // check Language Conflict (change language in another tab)

            if (HTTP_TYPES_TO_ADD_TOKEN.indexOf(config.method.toUpperCase()) !== -1) {
                config.headers[CSRF_TOKEN_HEADER_FOR_SEND] = $.cookie(CSRF_TOKEN_HEADER_FOR_SEND);//token;
            }            
            return config;
        }

        function onSuccess(response) {

            var newToken = response.headers(CSRF_TOKEN_HEADER);            
            if (newToken) {
                token = newToken;
                var date = new Date();
                var minutes = 20;
                date.setTime(date.getTime() + (minutes * 60 * 1000));
                $.cookie(CSRF_TOKEN_HEADER_FOR_SEND, token, { expires: date, path: '/' });
            }

            return response; 
        }
    }]);