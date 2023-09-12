'use strict';

angular.module('emixApp.factories').factory('authService', [
    function authService() {
        var accessTokenKey = 'accessToken';
        var refreshTokenKey = 'refreshToken';
        return {
            setToken: function (token) {
                //$.cookie(accessTokenKey, token, {path: '/'});
            },
            getToken: function () {
                return $.cookie(accessTokenKey);
            },
            setTokenInStorage: function (token) {
                localStorage.setItem(accessTokenKey, token);
            },
            getTokenFromStorage: function () {
                return localStorage.getItem(accessTokenKey);
            },
            setRefreshToken: function (token) {
                localStorage.setItem(refreshTokenKey, token);
            },
            getRefreshToken: function () {
                return localStorage.getItem(refreshTokenKey);
            },
            clearAllToken: function () {
                $.removeCookie(accessTokenKey, {path: '/'});
                localStorage.removeItem(refreshTokenKey);
                localStorage.removeItem(accessTokenKey);
            },
            clearToken: function () {
                $.removeCookie(accessTokenKey, {path: '/'});
                localStorage.removeItem(accessTokenKey);
            },
            isLoggedIn: function () {
                return !($.cookie(accessTokenKey) === null || $.cookie(accessTokenKey) === undefined);
            },
            closeAllModals: function (){
                $('.modal').modal('hide');
                $('.modal').remove();
                $('.modal-backdrop').remove();
            },
        }
    }]);