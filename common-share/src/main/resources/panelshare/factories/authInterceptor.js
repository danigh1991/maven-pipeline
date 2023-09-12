'use strict';

angular.module('emixApp.factories').factory('authInterceptor', ['$q', '$injector', '$location', 'authService',
    function authInterceptor($q, $injector, $location, authService) {
        var replays = [];
        var refreshTokenPromise;

        return {
            response: onSuccess,
            request: request,
            responseError: responseError
        };

        //////////

        // Add authorization token to headers
        function request(config) {
            config.headers = config.headers || {};
            var _accessToken = authService.getTokenFromStorage();

            config.headers.Authorization = _accessToken ? 'Bearer ' + _accessToken : null;

            // check Language Conflict (change language in another tab)
            if(sessionStorage.getItem(siteConfig.langCookieName) && $.cookie(siteConfig.langCookieName) !== sessionStorage.getItem(siteConfig.langCookieName)) {
                $.cookie(siteConfig.langCookieName, sessionStorage.getItem(siteConfig.langCookieName), { path: '/'});
                console.log("your language cookie sysc with tab language");
                //return $q.reject('change language in another tab');
            }
            // check Language Conflict (change language in another tab)

            return config;
        }

        function onSuccess(response) {
            return response;
        }

        var refreshTokenCallCount = 0;
        var lastRefreshTokenCallUrl = '';
        function responseError(response){
            var inFlightAuthRequest = null;
            if(response.config.url === Emix.Api.Account.refreshToken){
                //console.log(JSON.stringify(response));
                $injector.get("$http").get(Emix.Api.Account.logout);
                authService.clearAllToken();
                authService.closeAllModals();
                window.location = Emix.Pages.login;
            }else{
                switch (response.status) {
                    case 401:
                        if(lastRefreshTokenCallUrl === response.config.url) {
                            refreshTokenCallCount++;
                            if(refreshTokenCallCount > 1){
                                authService.clearAllToken();
                            }
                        }else{
                            lastRefreshTokenCallUrl = response.config.url;
                            refreshTokenCallCount = 1;
                        }
                        authService.clearToken();
                        var refreshTokenValue = authService.getRefreshToken();
                        if(refreshTokenValue) {
                            var deferred = $q.defer();
                            if (!inFlightAuthRequest) {
                                var $http = $injector.get("$http");
                                inFlightAuthRequest = $http({
                                    method: "POST",
                                    url: Emix.Api.Account.refreshToken,
                                    data: {data: refreshTokenValue},
                                    cache: false
                                });
                            }
                            inFlightAuthRequest.then(function (r) {
                                inFlightAuthRequest = null;
                                //console.log(JSON.stringify(r));
                                if(r) {
                                    authService.setToken(r.data.returnObject.access_token);
                                    authService.setTokenInStorage(r.data.returnObject.access_token);
                                    $injector.get("$http")(response.config).then(function (resp) {
                                        deferred.resolve(resp);
                                    }, function (resp) {
                                        deferred.reject(resp);
                                    });
                                }
                            }, function (error) {
                                inFlightAuthRequest = null;
                                deferred.reject();
                                authService.clearAllToken();
                                authService.closeAllModals();
                                window.location = Emix.Pages.login;
                            });
                        }else{
                           return $q.reject(response);
                        }
                        return deferred.promise;

                    default:
                        return $q.reject(response);
                }
                //return response || $q.when(response);
            }
        }
        // Intercept 401s and redirect you to login
        /*function responseError(response) {
            if (response.status === 401 && $cookies.get('token')) {
                return checkAuthorization(response);
            }

            return $q.reject(response);

            /////////

            function checkAuthorization(res) {
                return $q(function(resolve, reject) {

                    var replay = {
                        success: function(){
                            $injector.get('$http')(res.config).then(resolve, reject);
                        },

                        cancel: function(){
                            reject(res);
                        }
                    };

                    replays.push(replay);

                    if (!refreshTokenPromise) {
                        refreshTokenPromise = $injector.get('Auth') // REFRESH TOKEN HERE
                            .refreshToken()
                            .then(clearRefreshTokenPromise)
                            .then(replayRequests)
                            .catch(cancelRequestsAndRedirect);
                    }
                });

                ////////////

                function clearRefreshTokenPromise(auth) {
                    refreshTokenPromise = null;
                    return auth;
                }

                function replayRequests(auth) {
                    replays.forEach(function(replay) {
                        replay.success();
                    });

                    replays.length = 0;

                    return auth;
                }

                function cancelRequestsAndRedirect() {

                    refreshTokenPromise = null;
                    replays.forEach(function(replay) {
                        replay.cancel();
                    });

                    replays.length = 0;

                    $cookies.remove('token');
                    var $state = $injector.get('$state');

                    // SET YOUR LOGIN PAGE
                    $state.go('login');
                }
            }
        }*/
}]);