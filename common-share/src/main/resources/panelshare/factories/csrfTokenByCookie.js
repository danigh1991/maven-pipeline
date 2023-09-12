angular.module('emixApp.factories').factory('XSRFInterceptor', ['$cookieStore', function ($cookieStore) {

    var XSRFInterceptor = {

        request: function (config) {

            var token = $cookieStore.get('XSRF-TOKEN');

            if (token) {
                config.headers['X-XSRF-TOKEN'] = token;
            }
            return config;
        }
    };
    return XSRFInterceptor;
}]);
