/*
angular.module('emixApp.services').service('topupHttpServices', ['httpServices', '$http', function (httpServices, $http) {
    'use strict';
    httpServices.getTopUpRequestWrappers = function () {
        return $http({
            method: "GET",
            url: Emix.Api.Application.topup.getTopUpRequestWrappers,
            params: {},
            cache: false,
            headers: this.getHeaders()
        });
    };
}]);*/
